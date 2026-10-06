#!/usr/bin/env bash
# PostToolUse (Edit|Write): format the changed file with the project's formatter.
# Kotlin is not formatted per file (Spotless is too slow for that); pre-commit-check.sh runs spotlessCheck instead.
set -euo pipefail

file=$(jq -r '.tool_input.file_path // .tool_response.filePath // empty')
[ -n "$file" ] && [ -f "$file" ] || exit 0

root=$(cd "$(dirname "$0")/../.." && pwd)
case "$file" in
  "$root"/frontend/*.ts | "$root"/frontend/*.vue | "$root"/frontend/*.js | "$root"/frontend/*.json | "$root"/frontend/*.scss | "$root"/frontend/*.css)
    prettier="$root/frontend/node_modules/.bin/prettier"
    [ -x "$prettier" ] || exit 0
    (cd "$root/frontend" && "$prettier" --write --log-level=warn --ignore-unknown "$file")
    ;;
esac
