## graphify

This project has a graphify knowledge graph at graphify-out/.

Rules:
- Before answering architecture or codebase questions, read graphify-out/GRAPH_REPORT.md for god nodes and community structure
- If graphify-out/wiki/index.md exists, navigate it instead of reading raw files
- For cross-module "how does X relate to Y" questions, prefer `graphify query "<question>"`, `graphify path "<A>" "<B>"`, or `graphify explain "<concept>"` over grep — these traverse the graph's EXTRACTED + INFERRED edges instead of scanning files
- After modifying code files in this session, run `graphify update .` to keep the graph current (AST-only, no API cost)

## safety-backup

Before modifying any files in this repository, always create a safety backup of the current working tree:

- Inspect the repository state with `git status --short` and `git diff --stat`
- If there are uncommitted changes (staged, unstaged, or untracked), stash them with:
  `git stash push -m "pre-opencode-backup: <timestamp>"`
  - **WARNING:** Do NOT use `--include-untracked` by default, as it stashes untracked files (like image folders, data directories) and removes them from the working tree. Only use `--include-untracked` if the user explicitly requests it or if the untracked files are safe to stash.
  - **ALWAYS** update the agent's todo (using `todowrite` tool) when working on image-related tasks, either during or after completing the work. Ensure image upload progress and folder status are documented in the task tracking.
- If a stash from an earlier backup in this session already exists, create a new one instead of overwriting
- Verify the working tree is clean before proceeding with any modifications
- **NEVER** delete or remove user files without explicit permission. Do not assume a file is "temporary" or "disposable" just because it was created during a test or session. Always ask the user before deleting any file they own.
- After completing the requested changes, summarize what was modified and remind the user how to revert:
  - `git stash list` to view backups
  - `git stash pop` to restore the previous state
  - `git checkout stash@{0} -- <file>` to restore a specific file

## git-commits

All commit messages must be written in English, following the convention defined in `SPEC.md` section 7.

- Format: `<type>(<optional scope>): <imperative description>`
- Allowed types: `feat`, `fix`, `test`, `refactor`, `chore`, `docs`, `style`, `perf`, `build`, `ci`
- Do not mix Spanish and English in commit messages.

Example:
```
feat(coupon): add CuponServicio with extended queries and tests
```

## skills-configuration

This project uses a hybrid skill setup: global skills for universal tooling and project-local skills for stack-specific tasks.

### Global Skills (in `~/.agents/skills/`)

Available across all projects:

- `find-skills` — Discover and install new skills
- `documentation-writer` — Technical documentation authoring
- `docker-expert` — Containerization and deployment
- `git-workflow` — Branching strategies and collaborative patterns
- `security-review` — Vulnerability auditing and OWASP checks
- `grill-me` — Stress-test plans and designs via questioning
- `napkin` — Visual whiteboard collaboration

### Project-Local Skills (in `.opencode/skills/`)

Available only when working in this repository. Registered via `.opencode/opencode.json` (`skills.paths`).

**Java / Spring Boot:**
- `java-spring-boot` — Spring Boot REST APIs, Security, Data, Actuator

**Angular Stack:**
- `angular-component`, `angular-developer`, `angular-di`, `angular-directives`, `angular-forms`, `angular-http`, `angular-routing`, `angular-signals`, `angular-testing`, `angular-tooling`

**HyperFrames Stack:**
- `hyperframes`, `hyperframes-cli`, `hyperframes-registry`, `remotion-to-hyperframes`, `website-to-hyperframes`

**Frontend & Animation:**
- `animejs`, `css-animations`, `frontend-design`, `gsap`, `lottie`, `tailwind`, `three`, `waapi`

### Compatibility

Global skills are symlinked to `~/.claude/skills/` for Claude Code and OpenClaw compatibility. Project-local skills are resolved by OpenCode via `.opencode/opencode.json`.
