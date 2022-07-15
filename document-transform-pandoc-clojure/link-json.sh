#!/usr/bin/env bash

pandoc link.md -t json | jq > link.json
