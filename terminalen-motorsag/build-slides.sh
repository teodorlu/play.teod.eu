#!/usr/bin/env bash

pandoc -t s5 -s --self-contained index.org -o slides.html
