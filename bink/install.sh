#!/usr/bin/env sh

# A tiny install script for bink
#
# Usage:
#
#   1. Clone https://github.com/teodorlu/play.teod.eu
#   2. Run `bink/install.sh`

# Find setup folder in project
DIR=$(cd "$(dirname "$0")" && echo "$(git rev-parse --show-toplevel)/bink")

mkdir -p ~/.local/bin
ln -sf "${DIR}/bink.clj" ~/.local/bin/bink
