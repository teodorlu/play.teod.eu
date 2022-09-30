#!/usr/bin/env sh

sudo ln -sf "$PWD/dbxx.clj" /usr/local/bin/dbxx

mkdir -p ~/.config/dbx/
cp -f dbx.edn ~/.config/dbx/dbx.edn

# ls -lah ~/.config/dbx


# Copy to .zshrc or .bashrc:
#
#   function dbx() {
#       dbxx "$@"
#   }
#
