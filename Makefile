index.html: index.clj feedback-loops-api-design-how-stuff-works/index.html orthogonality-enables-optionality/index.html website-so-what/index.html
	./index.clj

feedback-loops-api-design-how-stuff-works/index.html: feedback-loops-api-design-how-stuff-works/index.org
	pandoc -s -i feedback-loops-api-design-how-stuff-works/index.org -o feedback-loops-api-design-how-stuff-works/index.html

orthogonality-enables-optionality/index.html: orthogonality-enables-optionality/index.org
	pandoc -s -i orthogonality-enables-optionality/index.org -o orthogonality-enables-optionality/index.html

website-so-what/index.html: website-so-what/index.org
	pandoc -s -i website-so-what/index.org -o website-so-what/index.html

@PHONY: pages
pages:  feedback-loops-api-design-how-stuff-works/index.html orthogonality-enables-optionality/index.html website-so-what/index.html

