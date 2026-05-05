# Gestion Soutenance Logic Fix Plan

This file is the implementation backlog for fixing app logic across:

- Backend: `C:\Users\Omar Safi\Desktop\projet-soa\backend`
- Frontend: `C:\Users\Omar Safi\Desktop\GestionSoutenance-Front`

Constraint: do not change existing domain entities for this pass. Fix flow, authorization, filtering, UI behavior, controller/service logic, DTO/auth payloads, and tests around the current model.

## Target Product Logic

### Roles

| Role | Purpose | Should be allowed |
| --- | --- | --- |
| `ADMIN` | Academic administration / coordinator | Manage master data, plan soutenances, assign juries, manage rooms, validate/publish results, see all records |
| `ENSEIGNANT` | Jury member / evaluator | See only soutenances where they are president/rapporteur/examinateur, enter or update only their own jury note, view relevant student/soutenance info needed for evaluation |
| `ETUDIANT` | Student | See only their own profile, own soutenances, and their own published result |

### Main Workflow

1. Admin creates/maintains students, teachers, rooms.
2. Admin plans a soutenance with student, room, date/duration, and three distinct jury members.
3. System prevents room, teacher, and student schedule conflicts.
4. Teachers see assigned soutenances only.
5. Each assigned teacher enters only their own note for their assigned jury role.
6. When all three notes exist, result can be calculated.
7. Admin validates result.
8. Admin publishes result.
9. Student sees only published result for their own soutenance.

## Current Logic Problems Found

### Backend Problems

- `SecurityConfig` currently allows `ENSEIGNANT` to `GET /api/**`, which means teachers can list all students, teachers, rooms, soutenances, results, stats, etc.
- `OwnershipSecurity.canAccessEtudiant` currently lets every teacher access every student. That is too broad.
- `GET /api/soutenances` returns all soutenances for admins and teachers. Teachers should get only assigned soutenances.
- `GET /api/resultats` returns all results for admins and teachers. Teachers should not see all results by default.
- `GET /api/resultats/published` returns all published results and is allowed to students. Students should see only their own published result.
- `GET /api/resultats/stats` is effectively available to teachers through broad GET rules. It should be admin-only.
- `JuryController` does not have method-level authorization. It relies on fallback route rules, which is fragile and unclear.
- `JuryOrchestrator` can change or remove jury members without reusing all planning conflict checks.
- `JuryOrchestrator.deleteJury` clears notes but does not consider existing result rows. This can leave stale result data.
- `SoutenanceController` write actions are admin-only only because of fallback security rules, not explicit annotations.
- `SoutenanceController.updateStatut` has no explicit role/lifecycle rules.
- `NotationOrchestrator` auto-calculates a result when all notes are present. That is fine, but duplicate result calculation can throw if notes are edited after a result exists.
- There is no clean "current user context" endpoint or auth response data for linked `enseignantId` / `etudiantId`, forcing the frontend to guess.

### Frontend Problems

- Teachers can access teacher/student/room list pages and see CRUD buttons that should be admin-only.
- Teachers can see Add/Edit/Delete buttons on pages where routes or backend later reject the action.
- Teacher note page lets a teacher choose any evaluator and role manually. Teacher should not choose another evaluator; it should use the current linked teacher and valid assigned role.
- Student result page currently uses "all published results", which leaks other students' published results.
- Student has no proper "my space" flow for own soutenances/profile/result.
- Root route and catch-all redirect to `/etudiants`, which is wrong for students.
- `salles` routes are duplicated in `app-routing.module.ts`.
- There are duplicate teacher concepts/pages: `encadrants` and `enseignants`, both hitting the same backend aliases.
- Result page only shows IDs and does not support admin validation/publishing flow.
- List pages are mostly CRUD-oriented instead of role-oriented views.

## Implementation Tasks

### Task 01 - Define and Centralize Permission Matrix

Backend:
- Replace broad `GET /api/**` teacher access with explicit endpoint permissions.
- Add method-level annotations to every controller method that matters.
- Make `SecurityConfig` act as a coarse fallback only, not the source of business policy.

Frontend:
- Add a small route/nav permission helper for role checks.
- Use it consistently for route guards, nav links, page buttons, and action columns.

Acceptance:
- A teacher cannot reach admin CRUD endpoints even by direct URL/API call.
- A student cannot list all students, teachers, rooms, soutenances, or results.

### Task 02 - Add Current User Context to Auth

Backend:
- Add `enseignantId` and `etudiantId` to `AuthResponse`.
- Include linked IDs in login and refresh responses.
- Optionally add `GET /api/auth/me` returning username, role, linked IDs, and enabled status.

Frontend:
- Extend `AuthResponse` and `AuthSession` with `enseignantId` and `etudiantId`.
- Store linked IDs in local storage session.
- Add helper getters: `getCurrentEnseignantId()`, `getCurrentEtudiantId()`.

Acceptance:
- Frontend can route/filter by current linked teacher/student without guessing from username.

### Task 03 - Fix Student Data Access

Backend:
- Change `OwnershipSecurity.canAccessEtudiant` so teachers do not automatically access every student.
- Teachers may access a student only if the student has a soutenance where that teacher is in the jury.
- Students may access only their own `etudiantId`.
- Admin keeps full access.
- Add safe endpoints:
  - `GET /api/etudiants/me` for students.
  - `GET /api/etudiants/{id}` restricted by ownership.
  - `GET /api/etudiants` admin-only, or teacher-filtered if needed.

Frontend:
- Create/adjust student "my profile" view for `ETUDIANT`.
- Hide student CRUD from teachers.

Acceptance:
- `teacher.ayari` cannot list every student unless using an admin-only page.
- `student.omar` can see Omar only.

### Task 04 - Fix Teacher Data Access

Backend:
- Keep `GET /api/enseignants` admin-only or limited to lightweight lookup for planning if needed.
- Add `GET /api/enseignants/me` for teachers.
- If teachers need to see jury colleague names, expose them through soutenance/jury DTOs rather than full teacher CRUD.

Frontend:
- Remove `ENSEIGNANT` access from teacher CRUD pages.
- Add a simple teacher profile page if useful.
- Consolidate `encadrants` vs `enseignants` routes/pages or choose one name.

Acceptance:
- A teacher cannot open teacher add/edit/delete/list management screens.

### Task 05 - Fix Room/Salle Access

Backend:
- Make room creation, update, delete admin-only.
- Decide whether teacher can list rooms. Recommended: no separate room list for teachers; room info appears through assigned soutenances.

Frontend:
- Hide salle nav/page for teachers unless a read-only planning calendar is intentionally added.
- Hide Add/Edit/Delete buttons from non-admins.

Acceptance:
- Teachers cannot CRUD rooms from UI or API.

### Task 06 - Fix Soutenance Listing and Ownership

Backend:
- Change `GET /api/soutenances` behavior:
  - Admin: all soutenances.
  - Teacher: only assigned soutenances.
  - Student: not via this endpoint, or only own if explicitly supported.
- Add clear endpoints:
  - `GET /api/soutenances/me` returns assigned/own soutenances depending on role.
  - Keep `GET /api/soutenances/etudiant/{id}` with ownership.
- Add method-level admin-only checks for create/update/delete/status changes.

Frontend:
- Admin page: "Gestion des soutenances" with create/edit/delete/status actions.
- Teacher page: "Mes soutenances" read-only list, with action to enter notes.
- Student page: "Mes soutenances" read-only list.

Acceptance:
- Teacher sees only soutenances where their `enseignantId` is president, rapporteur, or examinateur.
- Student sees only their soutenance(s).

### Task 07 - Fix Planning and Jury Assignment Flow

Backend:
- Decide one source of truth:
  - Either planning creates soutenance with full jury immediately.
  - Or planning creates soutenance without jury and jury assignment happens later.
- Current entity supports full jury directly; recommended: admin plans with full jury in one flow.
- If `JuryController` stays:
  - Make it admin-only.
  - Reuse conflict validation when changing jury.
  - Prevent jury changes after notes/result exist unless explicitly resetting dependent data safely.

Frontend:
- Remove duplicate "Jury assignment" if soutenance form already handles jury.
- Or make jury page admin-only and clearly a reassignment tool.

Acceptance:
- Admin cannot accidentally create stale notes/results by changing jury after evaluation.

### Task 08 - Fix Teacher Note Entry

Backend:
- Teacher can create/update only the note corresponding to their own `enseignantId` and assigned jury role.
- Admin can view notes and optionally correct notes if that is desired, but this should be explicit.
- Prevent note edits after result is validated/published unless admin reopens result.
- Make duplicate note edits update the existing role note safely instead of throwing duplicate result errors.

Frontend:
- Teacher note page should:
  - Show only assigned soutenances.
  - Auto-select current teacher.
  - Auto-detect role(s) for that soutenance.
  - Hide evaluator selector for teachers.
  - Show only teacher's own note form.
- Admin note page can remain broader if required.

Acceptance:
- A teacher cannot submit a note for another teacher.
- A teacher cannot edit notes after publication.

### Task 09 - Fix Result Lifecycle

Backend:
- Formalize result states with existing fields:
  - calculated: result row exists.
  - validated: `valide=true`.
  - published: `publie=true`.
- Only admin can validate/publish.
- Teachers may view only results for assigned soutenances, preferably not before publication unless the business wants jury preview.
- Students may view only own published result.
- `GET /api/resultats/published` should not be available to students as a global list.
- Add `GET /api/resultats/me` for student result and maybe teacher assigned results.

Frontend:
- Admin result page:
  - list all calculated results.
  - validate button for unvalidated.
  - publish button only after validation.
  - show status badges.
- Teacher result page:
  - read-only assigned results if allowed.
- Student result page:
  - only own published result.

Acceptance:
- `student.omar` cannot see `student.lina` result through UI or direct API.
- Admin can validate and publish from the UI.

### Task 10 - Fix Route Defaults and Navigation

Frontend:
- Root redirect should be role-aware:
  - Admin: `/soutenances` or dashboard.
  - Teacher: `/mes-soutenances`.
  - Student: `/mes-resultats` or `/mes-soutenances`.
- Catch-all should not always redirect to `/etudiants`.
- Add an unauthorized page instead of redirecting every forbidden route to `/login`.
- Nav should show role-specific entries only.

Acceptance:
- Logged-in teacher hitting an admin route sees "access denied", not login.
- Student refresh on unknown route lands in a student-safe page.

### Task 11 - Add Role-Specific Dashboards

Frontend:
- Admin dashboard:
  - totals: students, teachers, rooms, planned soutenances, results awaiting validation/publication.
- Teacher dashboard:
  - assigned upcoming soutenances.
  - notes pending from current teacher.
- Student dashboard:
  - next soutenance.
  - result publication status.

Backend:
- Add dashboard summary endpoints if needed, or compose from filtered endpoints.

Acceptance:
- Each role lands on a useful first page after login.

### Task 12 - Clean CRUD UI Actions

Frontend:
- Every page with Add/Edit/Delete must hide or disable actions by role.
- Remove CRUD wording from teacher/student pages.
- Use read-only tables for non-admin roles.
- Add consistent empty/error/loading states.

Acceptance:
- Teacher never sees a button that leads to a 403.

### Task 13 - Align Frontend Services With Backend Endpoints

Frontend:
- Stop using old duplicate services where possible:
  - `src/app/services/encadrant.service.ts`
  - `src/app/core/services/encadrants.service.ts`
  - `src/app/features/enseignant/enseignant.service.ts`
- Pick one teacher service layer.
- Update services to call `/me` and filtered endpoints.

Acceptance:
- No duplicate teacher/encadrant API wrappers for the same concept unless one is deliberately legacy.

### Task 14 - Add Backend Tests for Security Rules

Backend:
- Extend `SecurityAccessControlTest` and ownership tests for:
  - teacher cannot CRUD students/teachers/rooms.
  - teacher sees assigned soutenances only.
  - student sees own data only.
  - admin can validate/publish results.
  - student cannot list all published results.

Acceptance:
- Permission matrix is test-protected.

### Task 15 - Add Frontend Guard/Visibility Tests

Frontend:
- Add focused unit tests for:
  - route permissions.
  - role-aware home redirect.
  - nav visibility.
  - logout clears session.

Acceptance:
- Build and tests catch accidental teacher/admin UI exposure.

## Suggested Implementation Order

1. Task 01 - Permission matrix.
2. Task 02 - Current user context.
3. Task 06 - Soutenance ownership and `/me`.
4. Task 08 - Teacher note entry.
5. Task 09 - Result lifecycle.
6. Task 03 - Student data access.
7. Task 04 - Teacher data access.
8. Task 05 - Room access.
9. Task 10 - Route defaults/navigation.
10. Task 12 - CRUD UI cleanup.
11. Task 07 - Jury/planning cleanup.
12. Task 11 - Dashboards.
13. Task 13 - Service consolidation.
14. Task 14 - Backend tests.
15. Task 15 - Frontend tests.

## Active TODO

- [x] Slice 1: expose linked `enseignantId` / `etudiantId` in auth responses and frontend session.
- [x] Slice 1: add backend current-user helper for controllers/services.
- [x] Slice 1: make `GET /api/soutenances` role-aware: admin gets all, teacher gets assigned only, student gets own only or uses `/me`.
- [x] Slice 1: tighten route security so teachers are not treated as CRUD admins.
- [x] Slice 1: update frontend nav/routes so teacher CRUD pages are not available to teachers.
- [x] Slice 1: update student results view to call own-result endpoint, not global published results.
- [x] Slice 1: verify backend tests and frontend build.
- [x] Slice 2: implement teacher note entry ownership so teachers can create/update only their own assigned role note.
- [x] Slice 2: make result lifecycle endpoints role-aware, including student-only own published result and admin-only validate/publish.
- [x] Slice 2: add `/me` endpoints for student/teacher profile flows.
- [x] Slice 2: add focused backend tests for note/result ownership rules.
- [x] Slice 2: clean frontend note/result pages around the new role-specific backend behavior.
- [x] Slice 3: add unauthorized page and role-safe default dashboard route.
- [x] Slice 3: add dashboard summaries for admin, teacher, and student landing flows.
- [x] Slice 3: prevent stale results when jury members change through planning/jury flows.
- [x] Slice 3: verify backend tests and frontend build.
- [x] Slice 4: consolidate duplicate teacher/encadrant and soutenance frontend service layers.
- [x] Slice 4: add frontend unit tests for guards, login redirect, logout, notes, and result actions.
- [x] Slice 4: improve admin result page with calculate/recalculate entry points and richer student/teacher names instead of IDs.

## Notes For Later Implementation

- Keep existing entity classes unchanged unless a future task explicitly approves data model changes.
- Prefer adding filtered endpoints and DTO/session fields over exposing broad list endpoints to all roles.
- Backend security must be authoritative. Frontend hiding is only UX.
- Avoid relying on route fallback rules for important authorization. Put explicit `@PreAuthorize` on controller methods.
- When a workflow changes dependent state, define what happens to downstream data. Example: changing jury after notes exist must either be blocked or reset notes/results in a controlled admin-only action.
