#!/usr/bin/env sh

ln -s dbxx.clj /usr/local/bin/dbxx
ln -s dbx.edn ~/.config/dbx/dbx.edn


# Copy to .zshrc or .bashrc:
#
#   function dbx() {
#       dbxx "$@"
#   }
#
