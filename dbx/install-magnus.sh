#!/usr/bin/env sh

ln -s dbxx.clj /usr/local/bin/dbxx

mkdir -p ~/.config/dbx/
ln -s dbx.edn ~/.config/dbx/dbx.edn


# Copy to .zshrc or .bashrc:
#
#   function dbx() {
#       dbxx "$@"
#   }
#
