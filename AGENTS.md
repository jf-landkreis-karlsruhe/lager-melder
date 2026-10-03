# AGENTS.md — Lager-Melder

## Project Structure

Monorepo with four sub-projects:

| Directory        | Stack                              |
|------------------|------------------------------------|
| `frontend/`      | Vue 3 + TypeScript + Vite (primary)|
| `backend/`       | Spring Boot + Kotlin + Gradle       |
| `account-creator/` | Go CLI                           |
| `page-to-pdf/`   | Node.js + Puppeteer                |

Most active development happens in `frontend/`. All commands below assume you are inside `frontend/` unless stated otherwise.

---

## Rules for Agents

These rules are mandatory. Follow them for every change.

### Before every commit

Run the checks for every sub-project you touched. Only commit when they pass.

| Sub-project | Commands (run inside the directory) |
|---|---|
| `backend/` | `./gradlew spotlessApply` then `./gradlew check` (ktlint, detekt, all tests incl. architecture tests) |
| `frontend/` | `npm run format`, `npm run lint:check`, `npm run type-check`, `npm run test:unit:run`, and `npm run test:e2e:docker` when UI behaviour or layout changed |

- Never commit with failing or skipped checks. If a check fails for reasons unrelated to your change, stop and report it instead of working around it.
- Never bypass checks (`--no-verify`, `@Disabled`, deleting tests, raising thresholds, adding entries to the detekt baseline or to the allow-lists in `ArchitectureTest`).
- Backend integration tests need a running Docker daemon (Testcontainers). If Docker is unavailable, say so. Do not skip the tests silently.
- After changing a page or component, look at the rendered result in the browser (see [Viewing the app in a browser](#viewing-the-app-in-a-browser)) before committing.

### Tests

- New or changed behaviour needs tests in the same commit. Bug fixes start with a test that reproduces the bug.
- Every test function must carry `@Test` / `@ParameterizedTest`. Methods without them never run, and `ArchitectureTest` fails on them.
- Backend: use `@IntegrationTest` (never plain `@SpringBootTest`) for tests that need Spring or the database. Prefer plain unit tests for logic in services and helpers.
- Backend authorization: when an endpoint touches department-owned data, test both the allowed case and the forbidden case (a `USER` of another department gets 403).
- Frontend: logic in helpers and services gets Vitest unit tests; components get Vitest component tests (Vuetify is registered in `vitest.setup.ts`); user flows get Playwright tests in `frontend/e2e/` with the mocked API from `e2e/support/api.ts`.
- When you change an API contract, adapt the fixtures in `frontend/e2e/support/api.ts` and `backend/.../JsonContractTest.kt`, which sends JSON exactly as the frontend does.
- Visual regression screenshots (`frontend/e2e/__screenshots__/linux/`) are only updated with `npm run test:e2e:update-screenshots`, and only for intended layout changes. Look at the new screenshots before committing them.

### Architecture

- Read [`backend/ARCHITECTURE.md`](backend/ARCHITECTURE.md) before changing the backend, and update it when you change the structure, the security model or conventions.
- Layering: `rest.controller` → `core.service` → `core.persistence`. Controllers never use repositories. `core` never imports from `rest`. These rules are enforced by `ArchitectureTest`.
- Authorization happens in services via `AuthorityService`, not in controllers.
- Database changes: add a new Liquibase script `backend/src/main/resources/db/scripts/NNN_description.xml` and include it in `changelog-main.xml`. Never modify an existing changeset.
- New error keys must be added to both `backend/.../exception/ErrorConstants.kt` and `frontend/src/services/errorConstants.ts`.

### Commits

- Keep commits small and focused. Put mechanical changes (formatting, renames) in a separate commit from behaviour changes.
- Do not reformat code you did not otherwise touch, unless that is the purpose of the commit.

---

## Frontend Commands

```sh
cd frontend
npm install                       # install dependencies
npm run dev                       # dev server at localhost:9000, talks to the backend at 127.0.0.1:8080 directly
npm run dev:agent                 # dev server at localhost:9000, proxies /api to the backend (for the browser agent)
npm run build                     # type-check + vite build
npm run type-check                # vue-tsc --build --force (app, unit tests, e2e)
npm run lint                      # eslint --fix
npm run lint:check                # eslint without fixes, fails on warnings
npm run format                    # prettier --write src/ e2e/
npm run format:check              # prettier --check src/ e2e/
npm run test:unit                 # vitest, watch mode
npm run test:unit:run             # vitest, single run
npm run test:coverage             # vitest with coverage report (coverage/index.html)
npm run test:e2e                  # Playwright with a local browser (npx playwright install chromium)
npm run test:e2e:docker           # Playwright in the official Docker image (no local browser needed)
npm run test:e2e:update-screenshots  # recreate visual regression baselines (Docker, linux)
```

### Running Tests

```sh
# Single unit test file / single test by name
npx vitest run src/helper/__tests__/juleika.spec.ts
npx vitest run -t "test name here"

# Single E2E file
npx playwright test e2e/login.spec.ts
```

- Unit and component tests live in `__tests__/` next to the code (`*.spec.ts`, jsdom, time zone Europe/Berlin).
- E2E tests live in `frontend/e2e/` and run against the Vite dev server with a mocked backend. `mockApi(page, overrides)`
  answers known endpoints with fixtures and returns the list of unmocked calls. `loginAs(page, role)` fakes a login per role.
- Visual tests (`e2e/visual.spec.ts`) compare against Linux baselines, so run them in Docker (`test:e2e:docker`).

---

## Viewing the app in a browser

The Playwright MCP server (configured in `/.mcp.json`) gives the agent a real Chromium. It runs in Docker, so it reaches
the app via `host.docker.internal`. Use `browser_navigate`, `browser_snapshot` (accessibility tree) and
`browser_take_screenshot` to check pages after UI changes.

1. Start Postgres: `docker compose -f backend/docker-compose/docker-compose-postgres.yml up -d`
2. Start the backend: `cd backend && SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun` (port 8080)
3. Start the frontend in agent mode: `cd frontend && npm run dev:agent` (port 9000, proxies `/api` to the backend)
4. Open `http://host.docker.internal:9000/login` in the browser of the MCP server.

Dev users (only in the Liquibase `dev` context), password `lagermelder-dev` for all:

| User | Role |
|---|---|
| `feuerwehr@dev.lagermelder` | `USER` (department Ettlingen) |
| `lk-karlsruhe@dev.lagermelder` | `LK_KARLSRUHE` |
| `fachgebietsleiter@dev.lagermelder` | `SPECIALIZED_FIELD_DIRECTOR` |
| `admin@dev.lagermelder` | `ADMIN` |

Notes:
- On a fresh database, log in as `fachgebietsleiter@dev.lagermelder` once first. The settings are created on first read,
  which only this role may do. Until then, department users cannot add attendees.
- If a backend is already running from an older checkout, restart it so new Liquibase changesets (e.g. the dev users) are applied.
- Do not change data you did not create yourself in a shared dev database.

---

## Backend Commands

```sh
cd backend
./gradlew build                 # compile, lint, test, assemble
./gradlew check                 # spotlessCheck (ktlint) + detekt + test + JaCoCo report
./gradlew spotlessApply         # auto-format Kotlin sources and *.gradle.kts (ktlint)
./gradlew detekt                # static analysis, report in build/reports/detekt/
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun   # needs local Postgres (docker-compose/docker-compose-postgres.yml)

# Run a single test class
./gradlew test --tests "*MyTestClass"

# Run a single test method
./gradlew test --tests "*MyTestClass.myMethod"
```

- Architecture, layers, security model and test setup: see [`backend/ARCHITECTURE.md`](backend/ARCHITECTURE.md).
- Formatting: ktlint (`ktlint_official` style) via Spotless, configured in `backend/.editorconfig` (max line length 140, wildcard imports allowed).
- Static analysis: detekt with `backend/config/detekt/detekt.yml`. Existing findings are frozen in `baseline.xml`; new code must not add findings.
- Tests: JUnit 5, Mockito, AssertJ, MockMvc. Integration tests run against Postgres 18 via Testcontainers (Docker required). Coverage report: `build/reports/jacoco/test/html/index.html`.

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

Config lives in `frontend/eslint.config.js` (ESLint 9 flat config). It uses:
- `eslint-plugin-vue` `flat/recommended` (attribute order, hyphenated props/events in templates, `v-for` keys, no prop mutation)
- `@vue/eslint-config-typescript` recommended rules (no `any`, no unused variables)
- `@vue/eslint-config-prettier/skip-formatting` (formatting is Prettier's job)

Custom rule: `vue/no-undef-components` is set to `error`; `v-*` (Vuetify) and `router-*` components are whitelisted.
Only disable a rule for a single line, with a comment that explains why.

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
