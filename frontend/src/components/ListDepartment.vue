<template>
  <div>
    <v-container>
      <div v-if="hasAdministrationRole() && departments.length > 0">
        <v-row justify="center">
          <v-col sm="12" md="8" lg="6" xl="4">
            <h1>Feuerwehren</h1>
            <div v-for="department in departments" :key="department.id">
              <h2>{{ department.name }}</h2>
              <EditDepartment :department="department" class="mb-8" />
            </div>
          </v-col>
        </v-row>
      </div>
    </v-container>

    <div v-if="hasAdministrationRole()">
      <hr />
      <v-row justify="center" class="add-new-department">
        <v-col sm="12" md="8" lg="6" xl="4">
          <h1>Feuerwehr hinzufügen</h1>
          <AddDepartment
            :onDepartmentCreated="onDepartmentCreated"
            class="mb-8"
          />
        </v-col>
      </v-row>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Watch } from "vue-property-decorator";
import EditDepartment from "./EditDepartment.vue";
import AddDepartment from "./AddDepartment.vue";

import { hasAdministrationRole } from "../services/authentication";
import {
  // eslint-disable-next-line no-unused-vars
  Department,
  getDepartments,
  getMyDepartment,
  updateDepartment,
} from "../services/department";

// eslint-disable-next-line no-unused-vars
import { changePassword, User, getMe } from "../services/user";

@Component({
  components: { EditDepartment, AddDepartment },
})
export default class ListDepartment extends Vue {
  myDepartment: Department = {} as Department;
  departments: Department[] = [];
  loadingChangeDepartmentId = -1;

  password = "";
  repeatPassword = "";
  passwordLoading = false;
  passwordSuccess = false;
  user: User = {} as User;
  showPasswordError = false;

  hasAdministrationRole = hasAdministrationRole;

  updateDepartment(departmentId: string) {
    console.log(departmentId);
    const department = [this.myDepartment, ...this.departments].find(
      (dep) => dep.id === departmentId
    );
    if (department) {
      updateDepartment(department);
    }
  }

  updateUser() {
    if (this.password !== this.repeatPassword) {
      this.showPasswordError = true;
      return;
    }
    this.passwordLoading = true;
    changePassword({ ...this.user, password: this.password })
      .then(() => {
        this.passwordLoading = false;
        this.passwordSuccess = true;
        this.password = "";
        this.repeatPassword = "";
        setTimeout(() => (this.passwordSuccess = false), 2000);
      })
      .catch((error) => {
        console.log(error);
        this.passwordLoading = false;
        this.$toast(error);
      });
  }

  onDepartmentCreated(department: Department) {
    if (this.departments.find((dep) => dep.id === department.id)) {
      return;
    }
    this.departments.push(department);
  }

  @Watch("password")
  onPasswordChange() {
    this.showPasswordError = false;
  }
  @Watch("repeatPassword")
  onRepeatPasswordChange() {
    this.showPasswordError = false;
  }

  mounted() {
    getMe().then((user) => (this.user = user));
    getMyDepartment()
      .then((department) => {
        this.myDepartment = department;
      })
      .then(() => {
        if (hasAdministrationRole()) {
          return getDepartments();
        }
      })
      .then((departments) => {
        this.departments = (departments || []).filter(
          (department) => department.id !== this.myDepartment.id
        );
      });
  }
}
</script>

<style scoped lang="scss">
.add-new-department {
  background: #ddf2ff;
}
</style>
