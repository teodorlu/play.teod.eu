#!/usr/bin/env bash

pandoc -t s5 -s --variable=s5-url: index.org -o slides.html
