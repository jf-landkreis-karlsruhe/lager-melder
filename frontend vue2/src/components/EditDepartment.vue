<template>
  <div v-if="!error">
    <form v-on:submit.prevent="updateDepartment()">
      <v-text-field
        v-model="department.leaderName"
        label="Jugendwart"
        required
      />
      <v-text-field
        type="email"
        v-model="department.leaderEMail"
        label="Jugendwart Email"
        required
      />
      <v-container>
        <v-row justify="end">
          <v-dialog v-model="dialogOpen" persistent max-width="500">
            <template
              v-slot:activator="{ on, attrs }"
              v-if="hasAdministrationRole()"
            >
              <v-btn
                rounded
                color="primary"
                dark
                v-bind="attrs"
                v-on="on"
                class="mb-2"
              >
                Registriernugsmail versenden
              </v-btn>
            </template>
            <v-card>
              <form v-on:submit.prevent="sendRegistrationEmail">
                <v-card-title class="headline">
                  Registriernugsmail versenden
                </v-card-title>
                <v-card-text v-if="!emailSent">
                  Willst du wirklich?
                  <ol>
                    <li>Ein neues Passwort für {{ department.name }} setzen</li>
                    <li>
                      Das neue Passwort an {{ department.leaderEMail }} schicken
                    </li>
                  </ol>
                </v-card-text>
                <v-card-text v-if="emailSent">
                  <v-icon medium>mdi-check</v-icon> Registrierungsmail
                  erfolgreich versendet
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn rounded text @click="closeModal"> Schließen </v-btn>
                  <v-btn
                    color="primary"
                    v-if="!emailSent"
                    :loading="sendingEmail"
                    type="submit"
                  >
                    Senden
                  </v-btn>
                </v-card-actions>
              </form>
            </v-card>
          </v-dialog>
          <v-btn
            rounded
            color="primary"
            :loading="loading"
            type="submit"
            class="ml-3"
          >
            <span v-if="saved">
              <v-icon medium>mdi-check</v-icon> Gespeichert
            </span>
            <span v-if="!saved"> Speichern </span>
          </v-btn>
        </v-row>
      </v-container>
    </form>
    <form v-on:submit.prevent="updateRole()">
      <v-container v-if="user.role !== 'ADMIN'">
        <v-row align="center" justify="space-between">
          <div>
            <v-text-field
              type="text"
              v-model="user.username"
              label="Benutzername"
              hint="Read only"
              readonly
              required
            />
          </div>
          <div>
            <v-select
              v-model="user.role"
              :items="rolesList"
              item-text="text"
              item-value="value"
              label="Role"
            ></v-select>
          </div>
          <div>
            <button type="sumbit" :loading="roleLoading">
              <v-icon medium class="mr-2"> mdi-content-save </v-icon>
            </button>
          </div>
        </v-row>
        <hr class="mt-16 mb-8" />
      </v-container>
    </form>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";
import {
  hasAdministrationRole,
  Roles,
  rolesText,
} from "@/services/authentication";

import {
  // eslint-disable-next-line no-unused-vars
  Department,
  updateDepartment,
} from "../services/department";

import {
  // eslint-disable-next-line no-unused-vars
  User,
  sendRegistrationMail,
  userForDepartment,
  updateRole,
} from "../services/user";

@Component({})
export default class EditDepartment extends Vue {
  @Prop() department!: Department;
  error = false;
  hasAdministrationRole = hasAdministrationRole;

  loading: boolean = false;
  saved: boolean = false;
  roleLoading: boolean = false;

  sendingEmail: boolean = false;
  emailSent: boolean = false;
  dialogOpen: boolean = false;
  user: User = {} as User;
  rolesList = [
    { value: Roles.USER, text: rolesText(Roles.USER) },
    {
      value: Roles.SPECIALIZED_FIELD_DIRECTOR,
      text: rolesText(Roles.SPECIALIZED_FIELD_DIRECTOR),
    },
  ];

  closeModal() {
    this.dialogOpen = false;
    this.sendingEmail = false;
    this.emailSent = false;
  }

  sendRegistrationEmail() {
    this.sendingEmail = true;
    sendRegistrationMail(this.user.id).then(() => {
      this.emailSent = true;
      this.sendingEmail = false;
    });
  }

  updateDepartment() {
    this.loading = true;
    updateDepartment(this.department)
      .then(() => {
        this.loading = false;
        this.saved = true;
        setTimeout(() => (this.saved = false), 2000);
      })
      .catch(() => {
        this.loading = false;
      });
  }

  updateRole() {
    if (this.user.role) {
      this.roleLoading = true;
      updateRole(this.user.id, this.user.role)
        .then((updatedUser) => {
          this.user = updatedUser;
          this.roleLoading = false;
          this.$toast.success(
            `Rolle ${rolesText(updatedUser.role)} für ${
              updatedUser.username
            } gepeichert.`
          );
        })
        .catch(() => {
          this.roleLoading = false;
          this.$toast.error(
            `Rolle ${rolesText(this.user.role)} konnte für ${
              this.user.username
            } nicht gepeichert werden.`
          );
        });
    }
  }

  mounted() {
    userForDepartment(this.department.id)
      .then((user) => (this.user = user))
      .catch(() => (this.error = true));
  }
}
</script>

<style scoped></style>
