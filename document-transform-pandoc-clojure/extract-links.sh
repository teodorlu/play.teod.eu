#!/usr/bin/env bash

pandoc index.org --to json \
    | jet --from json --keywordize \
    | bb extract_links.clj \
    | bb '(clojure.pprint/pprint *input*)'
