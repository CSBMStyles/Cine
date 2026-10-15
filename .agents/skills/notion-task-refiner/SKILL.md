---
name: notion-task-refiner
description: Refines and synchronizes UniCine engineering tasks in Notion using repository evidence, Graphify, code analysis, and external documentation. Use when auditing, creating, splitting, estimating, or updating tasks in the Tareas Proyecto Cine database.
---

# UniCine Notion Task Refiner

Use this skill for backlog refinement in the UniCine repository. The goal is not to create more rows; it is to make the next work item understandable, estimable, testable, and safe to execute.

## Operating Modes

Choose one mode explicitly:

- `audit`: read-only inventory of Notion, Git, repository evidence, Graphify, and documentation.
- `research`: investigate unknown framework, API, provider, or deployment behavior.
- `refine`: propose task edits, dependency changes, splits, labels, and estimates without mutating Notion.
- `apply`: apply an approved refinement in small Notion batches and verify each batch.
- `sync`: reconcile task status, branch, commit, test evidence, roadmap, and repository state.

Default to `audit` when the user has not explicitly approved a mutation. A user request to improve a table is not permission to delete pages, alter completed work, or change the database schema broadly.

## Mandatory Context

Before reasoning about architecture or cross-module dependencies:

1. Read `AGENTS.md`, `SPEC.md`, and `graphify-out/GRAPH_REPORT.md`.
2. Use `graphify query`, `graphify path`, or `graphify explain` for cross-module relationships when the graph can answer the question.
3. Inspect the current Git branch, status, recent commits, and relevant source files.
4. Fetch the Notion database first and use its returned data-source schema. Never guess property names, especially the unnamed phase property.
5. Query the task database before fetching individual task pages. Fetch the pages that are being refined and at least one completed page as a format reference.

The repository and Notion database are separate sources of truth. Report conflicts instead of silently choosing one.

## Evidence and Research

Rank evidence in this order:

1. Current source code, tests, build files, and configuration on the active branch.
2. Current Git history and verified command output.
3. Current Notion task content and documented decisions.
4. Official library or provider documentation.
5. Reputable engineering guidance and search results.
6. Graphify inferred edges and unsourced assumptions.

Use research tools deliberately:

- Use Context7 for library, framework, SDK, API, CLI, or cloud-service behavior. Resolve the library ID first, then query one focused topic per call.
- Use Exa first for broad discovery, current engineering guidance, or locating likely sources.
- Use Firecrawl to scrape known URLs, verify source details, or extract longer documentation after discovery.
- Record the source URL and the decision it supports in the task page when external behavior affects scope.

Do not use external guidance to override repository evidence without calling out the conflict.

## Expert Review

Use the `council` skill for judgment-heavy decisions:

- dependency order and phase gates;
- security, financial, external-service, or deployment work;
- whether a task should be split or kept whole;
- conflicting repository, roadmap, and Notion evidence;
- high-risk acceptance criteria.

Council members are read-only by default. Ask for independent reports covering evidence, current behavior, risks, cost, testability, recommendation, confidence, and unknowns. Synthesize the reports; do not treat a vote as proof.

Use `clean-code` when refining code-facing work. Require task language that reflects meaningful names, single responsibilities, small units, explicit error handling, no leaked entities or secrets, and tests that are fast, independent, repeatable, self-validating, and timely.

## Refinement Loop

Run this loop until the candidate task passes all gates:

1. **Inventory**: collect task ID, title, status, phase, component, type, priority, skills, estimate, branch, commit, description, and page content.
2. **Baseline**: identify the current implementation state as missing, partial, implemented-but-unverified, or complete. Cite paths and symbols.
3. **Outcome**: rewrite the task around one user, business, operational, or risk-reduction outcome. Technical enablers must name the consumer or capability they unblock.
4. **Scope**: separate included work, explicit exclusions, assumptions, and unknowns.
5. **Contract**: define API path/version, request and response shape, status codes, validation, authorization, pagination, external calls, persistence, and compatibility where relevant.
6. **Dependencies**: list hard blockers by task ID and artifact. Distinguish soft sequencing from true blockers.
7. **Graph validation**: build a dependency DAG and reject cycles. Never use a vague dependency such as “Phase 5”; name the task and the artifact it produces.
8. **Slice**: split oversized work by user journey, path, rule, data, interface, or time-boxed spike. Do not split normal features into backend, frontend, database, and test rows that cannot deliver independently.
9. **Estimate**: include implementation, tests, documentation, and integration. Prefer executable slices of 2-8 hours; flag anything above one day or with more than three independent outcomes.
10. **Acceptance**: write observable Given/When/Then-style criteria or equivalent verifiable checkboxes, including important failure and security paths.
11. **Verification**: name exact tests and commands. Include unit, repository, REST, security, external-adapter, frontend, or Docker verification only when relevant.
12. **Readiness**: mark missing evidence as `No lista` or `Bloqueado`; do not hide uncertainty inside an estimate.
13. **Apply**: after approval, update only the intended pages in small batches.
14. **Reconcile**: query the database again, fetch changed pages, compare properties and content, and report remaining conflicts.

## Split Rules

Split a task when any of these are true:

- it contains multiple user-visible capabilities;
- it spans multiple bounded contexts;
- it has more than three independent acceptance outcomes;
- it requires more than one day of work;
- its estimate is uncertain because of an unknown external contract;
- it has a dependency cycle;
- it promises atomicity, idempotency, security, or production readiness without a supporting design slice.

Use a spike only when the output is knowledge, a measured result, a prototype, or a recommendation with a time limit. Do not disguise unestimated implementation as a spike.

For the UniCine roadmap, treat these as likely decomposition candidates:

- REST controllers: split by bounded resource capability, not by controller class count alone.
- Authentication: separate identity contract, login/registration, JWT, role/ownership enforcement, and integration tests to avoid cycles.
- Purchases: separate server-side totals, transaction boundary, seat concurrency, idempotency, controller contract, and verification.
- Factus: separate discovery, invoice model, adapter, asynchronous or synchronous issuance, reconciliation, and document access.
- ImageKit: separate gateway abstraction, upload contract, authorization, compensation, and external-service tests.
- Angular: separate scaffold, API contract generation, HTTP services, auth state, guards, user journeys, and tests.
- Docker: separate database, backend, frontend, compose profiles, health checks, migrations, secrets, and smoke tests.

## Required Task Page Structure

For open tasks, use this Notion-flavored Markdown structure unless the page has a project-specific reason to differ:

```markdown
## Descripción
<one concise outcome>
## Estado actual
- Baseline: missing | partial | implemented-but-unverified
- Evidence: `<path>:<line or symbol>`
## Propuesta técnica
- <minimal design and affected boundaries>
## Dependencias
- Blocking: `<task-id>` produces <artifact>
- Non-blocking: <parallel work>
- Assumptions: <unknown requiring confirmation>
## Subtareas
- [ ] `<task-id>` <independently verifiable slice>
## Criterios de aceptación
- [ ] Given <context>, when <action>, then <observable result>.
## Fuera de alcance
- <explicit non-goal>
## Verificación
- <test type and exact command>
## Riesgos y rollback
- <risk, mitigation, and reversible fallback>
```

Use `Subtareas` only when child rows are tracked in the database. Otherwise use a checklist and state that it is internal execution detail. Do not use `<page>` for existing task references; use plain text or `<mention-page>` so pages are not moved or deleted accidentally.

## Definition of Ready

An open task is ready only when it has:

- one bounded outcome;
- a current baseline with repository evidence;
- explicit scope and non-goals;
- named blocking dependencies and no cycles;
- a contract or artifact boundary when applicable;
- acceptance criteria including key error and security paths;
- an estimate that includes verification;
- a verification command or concrete test plan;
- correct phase, component, type, priority, skill, and branch metadata.

If one of these is unknown, mark the task as not ready and create a focused discovery task instead of inventing details.

## Definition of Done Evidence

Do not mark a task complete from its title or page text alone. Require the relevant evidence:

- source paths and symbols changed;
- tests and exact command output;
- migration or configuration evidence when applicable;
- external integration test or stub evidence;
- commit or PR URL when the user provides it;
- documentation updated when the task changes a public contract.

Completed pages are preserved. Only correct non-invasive metadata or append evidence when explicitly requested.

## Notion Safety

- Fetch the database schema before every mutation session.
- Use exact property names and valid option values from the schema response.
- Never delete rows, child pages, or properties without explicit confirmation.
- Never overwrite a completed page's content during a broad refinement.
- Prefer `update_content` for small edits and `replace_content` only when intentionally replacing the full page body.
- Apply changes in batches small enough to verify.
- After creating child rows, query their titles and URLs and update parent references only with safe mentions.
- Never invent dates, estimates, commits, PRs, credentials, or external API behavior.
- Keep database properties concise; put the detailed contract and verification plan in page content.

## Project-Specific Checks

Before refining UniCine tasks, inspect:

- `AGENTS.md` for safety, Graphify, and commit policy;
- `SPEC.md` for naming, exceptions, validation, tests, and commit conventions;
- `readme.md` for documented architecture, while treating build files as authoritative for versions;
- service interfaces and DTOs before writing controller tasks;
- `GlobalExceptionHandler`, `ApiError`, and `Respuesta<T>` before writing response-contract tasks;
- `application.properties` and ignored secrets without copying secret values into Notion;
- existing tests and Gradle commands before promising verification.

Known project cautions:

- Do not expose JPA entities or password fields from REST responses.
- Do not trust client-supplied purchase totals, state, timestamps, or ownership identifiers.
- Treat transaction, seat-concurrency, and idempotency claims as separate design requirements.
- Do not add a controller for an entity that has no real aggregate/service boundary.
- Do not make Factus, SSE/WebSocket, Angular, or Docker work a hidden blocker for the core REST path.
- Treat Graphify inferred edges as hypotheses that require source confirmation.

## Final Report

End every refinement with:

- changes applied and pages affected;
- completed pages preserved;
- new or split task IDs;
- dependency and readiness changes;
- research sources used;
- verification query or fetch result;
- unresolved blockers and recommended next task;
- reminder that Git commits and push remain manual for this project.
