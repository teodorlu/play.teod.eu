#!/usr/bin/env bash

watchexec -f '*.org' -- 'printf "Rebuilding @ " && date && pandoc -s -i index.org -o index.html'
