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
  `git stash push -m "pre-opencode-backup: <timestamp>" --include-untracked`
- If a stash from an earlier backup in this session already exists, create a new one instead of overwriting
- Verify the working tree is clean before proceeding with any modifications
- After completing the requested changes, summarize what was modified and remind the user how to revert:
  - `git stash list` to view backups
  - `git stash pop` to restore the previous state
  - `git checkout stash@{0} -- <file>` to restore a specific file
