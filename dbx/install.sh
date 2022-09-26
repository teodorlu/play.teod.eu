#!/usr/bin/env sh

ln -srf ./dbxx.clj ~/.local/bin/dbxx

# To install the shell wrapper, this must be sourced too:
#
#   function dbx() {
#       dbxx "$@"
#   }
#
# I use a shell wrapper because I eventually want a
#
#   dbx cd
#
# command.
#
#   dbx cd github-clone teodorlu/hotload
#
# man, this is extensible.
#
# aaand the embark function should /return/ the cd argument I think
# or not.
#
# Hmm.
