index.html: index.clj feedback-loops-api-design-how-it-works/index.html hourglass-architecture/index.html opt-in-hierarchy/index.html options-to-the-max/index.html orthogonality-enables-optionality/index.html pandoc-codeblocks/index.html truth-descriptive-prescriptive/index.html unix-signals-crash-course/index.html website-so-what/index.html
	./index.clj

feedback-loops-api-design-how-it-works/index.html: feedback-loops-api-design-how-it-works/index.org
	pandoc -s --toc --from=org+smart -i feedback-loops-api-design-how-it-works/index.org -o feedback-loops-api-design-how-it-works/index.html

hourglass-architecture/index.html: hourglass-architecture/index.org
	pandoc -s --toc --from=org+smart -i hourglass-architecture/index.org -o hourglass-architecture/index.html

opt-in-hierarchy/index.html: opt-in-hierarchy/index.org
	pandoc -s --toc --from=org+smart -i opt-in-hierarchy/index.org -o opt-in-hierarchy/index.html

options-to-the-max/index.html: options-to-the-max/index.org
	pandoc -s --toc --from=org+smart -i options-to-the-max/index.org -o options-to-the-max/index.html

orthogonality-enables-optionality/index.html: orthogonality-enables-optionality/index.org
	pandoc -s --toc --from=org+smart -i orthogonality-enables-optionality/index.org -o orthogonality-enables-optionality/index.html

pandoc-codeblocks/index.html: pandoc-codeblocks/index.org
	pandoc -s --toc --from=org+smart -i pandoc-codeblocks/index.org -o pandoc-codeblocks/index.html

truth-descriptive-prescriptive/index.html: truth-descriptive-prescriptive/index.org
	pandoc -s --toc --from=org+smart -i truth-descriptive-prescriptive/index.org -o truth-descriptive-prescriptive/index.html

unix-signals-crash-course/index.html: unix-signals-crash-course/index.org
	pandoc -s --toc --from=org+smart -i unix-signals-crash-course/index.org -o unix-signals-crash-course/index.html

website-so-what/index.html: website-so-what/index.org
	pandoc -s --toc --from=org+smart -i website-so-what/index.org -o website-so-what/index.html

@PHONY: pages
pages:  feedback-loops-api-design-how-it-works/index.html hourglass-architecture/index.html opt-in-hierarchy/index.html options-to-the-max/index.html orthogonality-enables-optionality/index.html pandoc-codeblocks/index.html truth-descriptive-prescriptive/index.html unix-signals-crash-course/index.html website-so-what/index.html

