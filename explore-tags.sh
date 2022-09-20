#!/usr/bin/env sh

./play.clj relations :from files :to lines2 | fzf
