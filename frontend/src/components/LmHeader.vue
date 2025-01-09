<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { AuthenticationChangedEvent, getTokenData, hasAdministrationRole, isLoggedIn } from '../services/authentication'
import { pathNeedsAuthentication } from '@/router'

let timeoutId = 0
const loggedIn = ref<boolean>(false)
const router = useRouter()

onMounted(() => {
  window.addEventListener('focus', checkToken)
  checkToken()
  window.addEventListener(AuthenticationChangedEvent, checkToken)
})

onBeforeUnmount(() => {
  timeoutId && clearTimeout(timeoutId)
  window.removeEventListener('focus', checkToken)
  window.removeEventListener(AuthenticationChangedEvent, checkToken)
})

const checkToken = () => {
  const token = getTokenData()
  if (!token) {
    loggedIn.value = false
    if (pathNeedsAuthentication(window.location.pathname)) {
      router.push('/login')
    }
    return
  }
  loggedIn.value = true
  const secondsUntilTokenExpires = token.exp - new Date().getTime() / 1000
  timeoutId = window.setTimeout(() => checkToken, secondsUntilTokenExpires * 1000)
}
</script>

<template>
  <header>
    <div class="d-flex justify-center align-center hero-image-container">
      <router-link :to="isLoggedIn() ? '/teilnehmer' : ''">
        <img alt="Zeltlager logo" class="hero-image" src="../assets/logo.png" />
      </router-link>
    </div>
    <v-container>
      <nav id="nav" v-if="loggedIn">
        <v-container fluid class="nav-bar">
          <v-row justify="space-between" align="center" class="nav-bar__row">
            <ul class="pa-0 nav-bar__list">
              <li class="nav-item">
                <router-link to="/teilnehmer">Teilnehmer</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/files">Anmeldeunterlagen</router-link>
              </li>
              <li class="nav-item admin" v-if="hasAdministrationRole()">
                <router-link to="/feuerwehr">Feuerwehren</router-link>
              </li>
              <li class="nav-item admin" v-if="hasAdministrationRole()">
                <router-link to="/planung"> Planung</router-link>
              </li>
              <li class="nav-item admin" v-if="hasAdministrationRole()">
                <router-link to="/anwesend"> Anwesend </router-link>
              </li>
              <li class="nav-item admin" v-if="hasAdministrationRole()">
                <router-link to="/einstellungen"> Einstellungen </router-link>
              </li>
            </ul>

            <router-link to="/account" class="account">
              <v-icon size="medium" color="blue-darken-2" icon="mdi-account"></v-icon>
              <span class="account__link pl-1">Mein Profil</span>
            </router-link>
          </v-row>
        </v-container>
      </nav>
    </v-container>
  </header>
</template>

<style scoped lang="scss">
header {
  margin-bottom: 30px;

  .hero-image-container {
    border-bottom: #0077c0 6px solid;

    .hero-image {
      max-width: 100%;
      max-height: 200px;
      margin-top: 8px;
    }
  }

  .nav-bar {
    margin-top: 12px;
    font-weight: 500;

    .nav-bar__row {
      gap: 12px;

      .nav-bar__list {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: 12px;

        .nav-item {
          display: inline-block;

          a {
            text-decoration: none;
            color: #303030;
            border-radius: 16px;
            border: 2px solid #d3e9f8;
            transition: padding 0.2s ease-in-out;
            padding: 0 0.5rem;
            background: linear-gradient(to left, #d3e9f8 50%, #95caee 50%) right;
            background-size: 200%;
            transition: background 0.25s ease-in-out;

            &:hover,
            &:active {
              border: 2px solid #95caee;
              background-position: left;
            }
            &.router-link-active {
              background: linear-gradient(to left, #95caee 50%, #95caee 50%) right;
            }
          }

          &.admin {
            a {
              border: 2px solid var(--lm-c-accent);
              background: linear-gradient(to left, var(--lm-c-accent) 50%, #ecd032 50%) right;
              background-size: 200%;

              &:hover {
                border: 2px solid #ecd032;
                background-position: left;
              }

              &.router-link-active {
                background: linear-gradient(to left, #ecd032 50%, #ecd032 50%);
              }
            }
          }
        }
      }
      .account {
        display: flex;
        align-items: center;
        text-decoration: none;
        transition: all 0.2s ease-in-out;
        margin-left: auto;

        @media screen and (min-width: 768px) {
          margin: unset;
        }

        &:hover,
        &:active,
        &.router-link-active {
          color: var(--lm-c-active);
          box-shadow: 0 3px 0 -1px var(--lm-c-active);
        }

        .account__link {
          text-decoration: none;
        }
      }
    }
  }
}
</style>
