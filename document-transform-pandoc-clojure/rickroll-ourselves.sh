#!/usr/bin/env bash

# We could rickroll from the org file:
#
# pandoc --standalone \
#     --from=org+smart \
#     --shift-heading-level-by=1 \
#     --toc \
#     -i index.org \
#     --filter rickroll.sh \
#     -o rickroll-ourselves.html

# But that's too easy, so let's use the HTML file instead.

pandoc --standalone \
    --toc \
    -i index.html \
    --filter rickroll.sh \
    -o rickroll-ourselves.html
