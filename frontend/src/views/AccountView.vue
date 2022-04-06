<template>
  <v-container>
    <v-row justify="center">
      <v-col sm="12" md="8" lg="6" xl="4">
        <div v-if="myDepartment.id">
          <h2>Deine Feuerwehr - {{ myDepartment.name }}</h2>
          <EditDepartment :department="myDepartment" class="mb-8" />
        </div>
      </v-col>
    </v-row>

    <v-row justify="center">
      <v-col sm="12" md="8" lg="6" xl="4">
        <h2>Passwort</h2>
        <form v-on:submit.prevent="updateUser()" class="mb-8">
          <v-text-field
            v-model="password"
            label="Passwort"
            hint="Mindestlänge 8 Zeichen"
            required
          />
          <v-text-field
            v-model="repeatPassword"
            label="Passwort wiederholen"
            required
          />
          <v-alert v-if="showPasswordError" type="error">
            Die Passwörter sind nicht gleich.
          </v-alert>

          <v-container>
            <v-row justify="end">
              <v-btn
                :color="passwordSuccess ? 'success' : 'primary'"
                :loading="passwordLoading"
                type="submit"
                rounded
              >
                <div v-if="passwordSuccess">
                  <v-icon medium>mdi-check</v-icon> geändert
                </div>
                <div v-if="!passwordSuccess">Passwort ändern</div>
              </v-btn>
            </v-row>
          </v-container>
        </form>
      </v-col>
    </v-row>

    <hr class="mt-16 mb-8" />
    <v-row justify="center">
      <v-col sm="12" md="8" lg="6" xl="4">
        <div
          v-if="isLoggedIn"
          class="d-flex justify-space-between align-center"
        >
          <p class="mb-0">Du bist eingeloggt:</p>
          <v-btn rounded color="error" @click="logout"> Ausloggen </v-btn>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import {
  hasAdministrationRole,
  logout,
  isLoggedIn,
} from "@/services/authentication";
import {
  Department,
  getDepartments,
  getMyDepartment,
} from "@/services/department";
import EditDepartment from "../components/EditDepartment.vue";
import { changePassword, getMe, User } from "@/services/user";
import Vue from "vue";
import { Component, Watch } from "vue-property-decorator";

@Component({ name: "AccountView", components: { EditDepartment } })
export default class AccountView extends Vue {
  myDepartment: Department = {} as Department;
  departments: Department[] = [];
  loadingChangeDepartmentId = -1;
  password = "";
  repeatPassword = "";
  passwordLoading = false;
  passwordSuccess = false;
  user: User = {} as User;
  showPasswordError = false;
  loggedIn = false;
  hasAdministrationRole = hasAdministrationRole;

  private get isLoggedIn(): boolean {
    return isLoggedIn();
  }

  updateUser() {
    console.log("update user!");
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
      });
  }

  onDepartmentCreated(department: Department) {
    if (this.departments.find((dep) => dep.id === department.id)) {
      return;
    }
    this.departments.push(department);
  }

  logout() {
    logout();
    this.loggedIn = false;
    document.location.assign("/login");
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
