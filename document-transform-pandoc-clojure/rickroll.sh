#!/usr/bin/env bash

jet --from json --keywordize | bb rickroll.clj | jet --to json --keywordize
