#!/usr/bin/env bash

cat link.json | jet --from json --keywordize > link.edn
