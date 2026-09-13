# AGENTS.md — Lager-Melder

## Project Structure

Monorepo with four sub-projects:

| Directory        | Stack                              |
|------------------|------------------------------------|
| `frontend/`      | Vue 3 + TypeScript + Vite (primary)|
| `backend/`       | Spring Boot + Kotlin + Maven       |
| `account-creator/` | Go CLI                           |
| `page-to-pdf/`   | Node.js + Puppeteer                |

Most active development happens in `frontend/`. All commands below assume you are inside `frontend/` unless stated otherwise.

---

## Frontend Commands

```sh
cd frontend
npm install          # install dependencies
npm run dev          # dev server at localhost:9000 (sets VITE_BACKEND_URL)
npm run build        # type-check + vite build
npm run type-check   # vue-tsc --build --force
npm run lint         # eslint --fix on all .vue/.ts files
npm run format       # prettier --write src/
npm run test:unit    # run all tests (vitest, watch mode)
```

### Running Tests

```sh
# All tests (watch mode)
npm run test:unit

# All tests (single run, CI mode)
npx vitest run

# Single test file
npx vitest run src/components/__tests__/LmFooter.spec.ts

# Single test by name
npx vitest run -t "test name here"

# Single file, single run, with coverage
npx vitest run --coverage src/components/__tests__/LmFooter.spec.ts
```

Test files live in `__tests__/` subdirectories next to the code they test and use the `*.spec.ts` naming pattern. The test environment is `jsdom` (configured in `vitest.config.ts`).

---

## Backend Commands

```sh
cd backend
./mvnw install
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run

# Run a single test class
./mvnw test -Dtest=MyTestClass

# Run a single test method
./mvnw test -Dtest=MyTestClass#myMethod
```

---

## Code Style — Prettier

Config lives in `frontend/.prettierrc.json`:

- No semicolons (`"semi": false`)
- Single quotes (`"singleQuote": true`)
- 2-space indentation (`"tabWidth": 2`)
- Print width 120 (`"printWidth": 120`)
- No trailing commas (`"trailingComma": "none"`)

Always run `npm run format` after bulk edits.

---

## Code Style — ESLint

Config lives in `frontend/.eslintrc.cjs`. Extends:
- `plugin:vue/vue3-essential`
- `eslint:recommended`
- `@vue/eslint-config-typescript`
- `@vue/eslint-config-prettier/skip-formatting`

Custom rule: `vue/no-undef-components` is set to `error`; `v-*` (Vuetify) and `router-*` components are whitelisted.

---

## Imports

Ordering convention (no enforcer, but follow this pattern):

```ts
// 1. Vue core
import { ref, computed, onMounted, type Ref } from 'vue'
import { useRouter } from 'vue-router'

// 2. Third-party libraries
import { useToast } from 'vue-toastification'

// 3. Internal services/helpers via @/ alias
import { getAttendees, type Attendee } from '@/services/attendee'
import { filterByDepartment } from '@/helper/filterHelper'

// 4. Relative imports (components, local files)
import LmHeader from './LmHeader.vue'
```

Use the **inline `type` import** syntax, not `import type`:

```ts
// Correct
import { getData, type ApiResponse } from '@/helper/fetch'

// Avoid
import type { ApiResponse } from '@/helper/fetch'
```

Use the `@/` alias for imports from outside the current directory; use relative paths for files in the same directory.

---

## Naming Conventions

| Category              | Convention              | Examples                              |
|-----------------------|-------------------------|---------------------------------------|
| `.ts` files           | `camelCase`             | `attendee.ts`, `filterHelper.ts`      |
| Vue components        | `PascalCase` + `Lm` prefix | `LmHeader.vue`, `LmFooter.vue`     |
| View components       | `PascalCase` + `View` suffix | `LoginView.vue`, `DepartmentDetailView.vue` |
| Functions             | `camelCase`             | `getAttendees`, `saveNewAttendee`     |
| Event handlers        | `handle` prefix         | `handleFormSave`, `handleUpdateAttendee` |
| Module-level constants| `SCREAMING_SNAKE_CASE`  | `BASE_URL`, `CODE_LENGTH`             |
| Interfaces            | `PascalCase`, no `I` prefix | `Attendee`, `Department`, `JWT`   |
| Enums                 | `PascalCase`            | `AttendeeRole`, `Food`, `Roles`       |
| Enum values           | `SCREAMING_SNAKE_CASE`  | `YOUTH`, `YOUTH_LEADER`, `VEGETARIAN` |
| CSS classes           | `kebab-case`            | `nav-bar__list`, `hero-image-container` |

---

## TypeScript Patterns

- Use **`interface`** (not `type` aliases) for data shapes.
- Use **`enum`** for string constants; co-locate enums with the interface they relate to.
- Extend interfaces when specialising: `interface YouthLeader extends Attendee { ... }`
- Use generics on fetch helpers: `getData<Attendees>(...)`, `ref<string>('')`
- Use `defineProps<T>()` and `defineEmits<T>()` with inline TypeScript generics — no `PropType` / `withDefaults` style.
- Prefer **optional chaining** (`?.`) over defensive null checks.
- Use `as const` on plain object literals used as lookup maps.

```ts
// Service function pattern
export const getAttendees = () => getData<Attendees>('attendees', withAuthenticationHeader())

// Co-located enum + interface
export enum AttendeeRole { YOUTH = 'YOUTH', YOUTH_LEADER = 'YOUTH_LEADER' }
export interface Attendee extends NewAttendee { id: string; role: AttendeeRole }
```

---

## Vue Component Patterns

- All components use `<script setup lang="ts">` — **no Options API**.
- Prefer **`ref<T>()`** over `reactive()` for component state.
- Fetch data in `onMounted`; pre-load config/settings in `onBeforeMount`.
- Use **`computed`** for all derived or filtered lists — never filter inline in the template.
- Keep state updates immutable using spread:

```ts
attendees.value = { ...attendees.value, [type]: [...attendees.value[type], newAtt] }
```

- Pass data down via **props**, surface changes via **emits** — no global store (no Pinia/Vuex).
- Style with `<style lang="scss">`. Use `:deep(.selector)` to style child component internals.

---

## Error Handling

`fetchData` (in `helper/fetch.ts`) throws the raw `Response` object on non-ok status. Callers receive a `Response`, not a pre-parsed object.

Preferred pattern in components:

```ts
createEvent({ name: eventName.value })
  .then((result) => { /* handle success */ })
  .catch(async (err) => {
    await showErrorToast(toast, err, 'Fallback error message')
  })
```

To extract a message from the error manually:

```ts
const errorMessage = await getErrorMessage(err)  // from services/errorConstants.ts
if (errorMessage) toast.error(errorMessage)
```

Only use empty `catch {}` to deliberately swallow a known-safe error (e.g. a JSON parse attempt on a non-JSON body).

---

## Services Pattern

Services in `src/services/` are **plain exported async functions** — no classes, no dependency injection.

```ts
// src/services/attendee.ts
export const getAttendees = () => getData<Attendees>('attendees', withAuthenticationHeader())
export const createAttendee = (attendee: NewAttendee) =>
  postData<Attendee>('attendees', attendee, withAuthenticationHeader())
```

Keep interfaces, enums, and service functions in the same file when they belong together.
