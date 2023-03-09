#!/usr/bin/env sh

cat <<EOF | pandoc --from org --to json
visa writes in [[id:7d7ef8e9-9312-4cbe-9fc9-12ff7bda489b][visakanv's 50yr "plan" for global nerd network [wip]​]]:
EOF

# Run with bash bug-perhaps.sh | jq
