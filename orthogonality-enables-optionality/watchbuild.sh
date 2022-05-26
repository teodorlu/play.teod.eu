#!/usr/bin/env bash

watchexec -f '*.html' -- pandoc -s -i index.org -o index.html
