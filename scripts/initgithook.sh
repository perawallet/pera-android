#!/bin/bash
GIT_DIR=$(git rev-parse --git-dir)
GIT_ROOT=$(git rev-parse --show-toplevel)

echo "Installing git hooks..."
echo "GIT_DIR: $GIT_DIR"
echo "GIT_ROOT: $GIT_ROOT"

mkdir -p "${GIT_DIR}/hooks/"

cp "${GIT_ROOT}/scripts/hook-linter" "${GIT_DIR}/hooks/pre-push" && chmod +x "${GIT_DIR}/hooks/pre-push"

echo "Git hooks installed!"
