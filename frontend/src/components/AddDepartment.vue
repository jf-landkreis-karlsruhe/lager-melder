<template>
  <div class="mb-10">
    <form v-on:submit.prevent="addDepartmentAndUser()">
      <v-text-field
        v-model="departmentName"
        label="Name der Feuerwehr"
        required
      />
      <v-text-field v-model="leaderName" label="Jugendwart" required />
      <v-text-field
        v-model="leaderMail"
        type="email"
        label="Jugendwart Email"
        required
      />
      <v-text-field v-model="username" label="Benutzername" required />
      <v-alert v-if="error" type="error">
        Es gab einen Fehler beim erzeugen der Feuerwehr oder des Benutzers.
      </v-alert>
      <v-row justify="end">
        <v-btn color="primary" :loading="loading" type="submit">
          <span v-if="created">
            <v-icon medium>mdi-check</v-icon> Erstellt
          </span>
          <span v-if="!created"> Erstellen </span>
        </v-btn>
      </v-row>
    </form>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";

import {
  // eslint-disable-next-line no-unused-vars
  Department
} from "../services/department";

import {
  // eslint-disable-next-line no-unused-vars
  User,
  registerNewDepartmentAndUser
} from "../services/user";

@Component({})
export default class AddDepartment extends Vue {
  @Prop() onDepartmentCreated!: (department: Department) => void;

  departmentName = "";
  leaderName = "";
  leaderMail = "";
  username = "";

  loading: boolean = false;
  created: boolean = false;
  error: boolean = false;

  addDepartmentAndUser() {
    this.loading = true;
    this.error = false;

    const departmentWithUserReguest = {
      departmentName: this.departmentName,
      leaderName: this.leaderName,
      leaderEMail: this.leaderMail,
      username: this.username
    };
    registerNewDepartmentAndUser(departmentWithUserReguest)
      .then(departmentWithUser => {
        this.loading = false;
        this.created = true;
        this.onDepartmentCreated({
          id: departmentWithUser.departmentId,
          name: departmentWithUser.departmentName,
          leaderName: departmentWithUser.leaderName,
          leaderEMail: departmentWithUser.leaderEMail
        });
        this.departmentName = "";
        this.leaderName = "";
        this.leaderMail = "";
        this.username = "";
      })
      .catch((e: any) => {
        console.log("error", e);
        this.loading = false;
        this.error = true;
      });
  }
}
</script>

<style scoped></style>
