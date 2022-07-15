echo '{"args": [1, 2]}' | \
    jet --from json --keywordize | \
    bb '(assoc *input* :sum (reduce + (:args *input*)))' | \
    jet --to json
