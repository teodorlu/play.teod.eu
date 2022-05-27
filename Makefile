index.html: index.clj feedback-loops-api-design-how-it-works/index.html hourglass-architecture/index.html options-to-the-max/index.html orthogonality-enables-optionality/index.html pandoc-codeblocks/index.html truth-descriptive-prescriptive/index.html website-so-what/index.html
	./index.clj

feedback-loops-api-design-how-it-works/index.html: feedback-loops-api-design-how-it-works/index.org
	pandoc -s -i feedback-loops-api-design-how-it-works/index.org -o feedback-loops-api-design-how-it-works/index.html

hourglass-architecture/index.html: hourglass-architecture/index.org
	pandoc -s -i hourglass-architecture/index.org -o hourglass-architecture/index.html

options-to-the-max/index.html: options-to-the-max/index.org
	pandoc -s -i options-to-the-max/index.org -o options-to-the-max/index.html

orthogonality-enables-optionality/index.html: orthogonality-enables-optionality/index.org
	pandoc -s -i orthogonality-enables-optionality/index.org -o orthogonality-enables-optionality/index.html

pandoc-codeblocks/index.html: pandoc-codeblocks/index.org
	pandoc -s -i pandoc-codeblocks/index.org -o pandoc-codeblocks/index.html

truth-descriptive-prescriptive/index.html: truth-descriptive-prescriptive/index.org
	pandoc -s -i truth-descriptive-prescriptive/index.org -o truth-descriptive-prescriptive/index.html

website-so-what/index.html: website-so-what/index.org
	pandoc -s -i website-so-what/index.org -o website-so-what/index.html

@PHONY: pages
pages:  feedback-loops-api-design-how-it-works/index.html hourglass-architecture/index.html options-to-the-max/index.html orthogonality-enables-optionality/index.html pandoc-codeblocks/index.html truth-descriptive-prescriptive/index.html website-so-what/index.html

