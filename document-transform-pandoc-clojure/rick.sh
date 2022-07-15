#!/usr/bin/env bash

pandoc --standalone -i index.html --filter rickroll.sh -o rick.html
