#!/usr/bin/env sh

# A tiny install script for bink
#
# Usage:
#
#   1. Clone https://github.com/teodorlu/play.teod.eu
#   2. Run `bink/install.sh`

# Or you can just download `bink.clj` yourself. Just make it executable and put in on PATH.
# There are no dependencies other than what Babashka already provides.

# Find the absolute path to this directory
DIR=$(cd "$(dirname "$0")" && echo "$(git rev-parse --show-toplevel)/bink")

mkdir -p ~/.local/bin
ln -sf "${DIR}/bink.clj" ~/.local/bin/bink
ln -sf "${DIR}/bink-alacritty.sh" ~/.local/bin/bink-alacritty
