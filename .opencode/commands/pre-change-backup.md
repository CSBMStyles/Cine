Create a safety snapshot of the current working tree before making any modifications, enabling easy rollback if needed.

Optional context for the snapshot: ``

Rules:

- Before touching ANY file, create a backup point:
  - `git status --short`
  - `git diff --stat`
  - `git stash push -m "pre-opencode-backup: <timestamp> - <context>" --include-untracked`
  - If the stash fails because there are no changes, note that no backup was needed.
- If there is a previous stash from an earlier backup in this session, do NOT overwrite it — create a new stash instead.
- After stashing, verify the working tree is clean:
  - `git status --short` should show nothing (or only untracked files you are about to create).
- Proceed with the user's requested changes ONLY after the backup is confirmed.
- After completing the changes, summarize what was modified and how to revert.

How to revert (tell the user):

1. View the backup stash:
   `git stash list`
2. Inspect what the backup contains:
   `git stash show -p stash@{0}`
3. To undo all my changes and restore the exact previous state:
   `git stash pop`  (restores files and removes the stash)
   OR
   `git stash apply` (restores files but keeps the stash for safety)
4. If you only want to revert specific files from the backup:
   `git checkout stash@{0} -- <file-path>`

Flow:

1. Inspect current repository state.
2. If there are uncommitted changes (staged, unstaged, or untracked), stash them with a descriptive message including a timestamp.
3. Confirm the working tree is clean.
4. Proceed with the user's requested modifications.
5. When finished, show a summary of changes and remind the user how to revert using the stash.
