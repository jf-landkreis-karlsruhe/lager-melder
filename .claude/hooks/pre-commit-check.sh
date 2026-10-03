#!/usr/bin/env bash
# PreToolUse (Bash): before a `git commit`, run the fast checks of the touched sub-projects.
# Exit code 2 blocks the commit and shows stderr to the agent. The full test suites are not run here (too slow);
# AGENTS.md still requires them and CI runs them.
set -uo pipefail

command=$(jq -r '.tool_input.command // empty')
case "$command" in
  *"git commit"*) ;;
  *) exit 0 ;;
esac

root=$(cd "$(dirname "$0")/../.." && pwd)
cd "$root"

# Staged, modified and new files: covers `git add -A && git commit` in one command.
changed=$( { git diff --name-only --cached --diff-filter=ACMR; git diff --name-only --diff-filter=ACMR; git ls-files --others --exclude-standard; } | sort -u)

failed=0
report() { echo "$1" >&2; failed=1; }

frontend_files=$(echo "$changed" | grep -E '^frontend/.*\.(ts|vue|js|cjs|mjs)$' | grep -vE '^frontend/(dist|node_modules)/' || true)
if [ -n "$frontend_files" ]; then
  bin="$root/frontend/node_modules/.bin"
  rel=$(echo "$frontend_files" | sed 's#^frontend/##')
  # shellcheck disable=SC2086
  (cd frontend && "$bin/eslint" --max-warnings=0 $rel) >&2 || report "Frontend: ESLint failed (npm run lint)."
  # shellcheck disable=SC2086
  (cd frontend && "$bin/prettier" --check --log-level=warn $rel) >&2 || report "Frontend: Prettier check failed (npm run format)."
  (cd frontend && "$bin/vue-tsc" --build --force) >&2 || report "Frontend: type check failed (npm run type-check)."
fi

if echo "$changed" | grep -qE '^backend/.*\.(kt|kts)$'; then
  (cd backend && ./gradlew -q --console=plain spotlessCheck detekt) >&2 \
    || report "Backend: spotlessCheck/detekt failed (./gradlew spotlessApply, see build/reports/detekt)."
fi

if [ "$failed" -ne 0 ]; then
  echo "Commit blocked by .claude/hooks/pre-commit-check.sh. Fix the findings above, then commit again." >&2
  exit 2
fi
exit 0
