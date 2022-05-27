#!/usr/bin/env bash

watchexec -f '*.org' -- 'printf "reloading @ " && date && pandoc -s -i index.org -o index.html'
