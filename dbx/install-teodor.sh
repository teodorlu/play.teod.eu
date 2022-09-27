#!/usr/bin/env sh

# This is /NOT/ a generic install script - it's for teodor (https://teod.eu)
#
# You're free to use it, or not. But it's going to give you some links that you
# probably don't care about.

ln -srf dbxx.clj ~/.local/bin/dbxx
ln -srf dbx.edn ~/.config/dbx/dbx.edn


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
