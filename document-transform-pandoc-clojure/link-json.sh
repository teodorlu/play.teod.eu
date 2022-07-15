#!/usr/bin/env bash

# pandoc link.md -t json | jq > link.json # pretty
pandoc link.md -o link.json # compact
