index.html: index.clj aphorisms/index.html emacs/index.html feedback-loops-api-design-how-it-works/index.html hourglass-architecture/index.html journal/index.html knowledge-worker/index.html maksimal-opsjonalitet/index.html opt-in-hierarchies/index.html orthogonality-enables-optionality/index.html product-for-developers/index.html sannhet-deskriptiv-preskriptiv/index.html unix-signals-crash-course/index.html
	./index.clj

aphorisms/index.html: aphorisms/index.org
	pandoc -s --toc --from=org+smart -i aphorisms/index.org -o aphorisms/index.html

emacs/index.html: emacs/index.org
	pandoc -s --toc --from=org+smart -i emacs/index.org -o emacs/index.html

feedback-loops-api-design-how-it-works/index.html: feedback-loops-api-design-how-it-works/index.org
	pandoc -s --toc --from=org+smart -i feedback-loops-api-design-how-it-works/index.org -o feedback-loops-api-design-how-it-works/index.html

hourglass-architecture/index.html: hourglass-architecture/index.org
	pandoc -s --toc --from=org+smart -i hourglass-architecture/index.org -o hourglass-architecture/index.html

journal/index.html: journal/index.org
	pandoc -s --toc --from=org+smart -i journal/index.org -o journal/index.html

knowledge-worker/index.html: knowledge-worker/index.org
	pandoc -s --toc --from=org+smart -i knowledge-worker/index.org -o knowledge-worker/index.html

maksimal-opsjonalitet/index.html: maksimal-opsjonalitet/index.org
	pandoc -s --toc --from=org+smart -i maksimal-opsjonalitet/index.org -o maksimal-opsjonalitet/index.html

opt-in-hierarchies/index.html: opt-in-hierarchies/index.org
	pandoc -s --toc --from=org+smart -i opt-in-hierarchies/index.org -o opt-in-hierarchies/index.html

orthogonality-enables-optionality/index.html: orthogonality-enables-optionality/index.org
	pandoc -s --toc --from=org+smart -i orthogonality-enables-optionality/index.org -o orthogonality-enables-optionality/index.html

product-for-developers/index.html: product-for-developers/index.org
	pandoc -s --toc --from=org+smart -i product-for-developers/index.org -o product-for-developers/index.html

sannhet-deskriptiv-preskriptiv/index.html: sannhet-deskriptiv-preskriptiv/index.org
	pandoc -s --toc --from=org+smart -i sannhet-deskriptiv-preskriptiv/index.org -o sannhet-deskriptiv-preskriptiv/index.html

unix-signals-crash-course/index.html: unix-signals-crash-course/index.org
	pandoc -s --toc --from=org+smart -i unix-signals-crash-course/index.org -o unix-signals-crash-course/index.html

@PHONY: pages
pages:  aphorisms/index.html emacs/index.html feedback-loops-api-design-how-it-works/index.html hourglass-architecture/index.html journal/index.html knowledge-worker/index.html maksimal-opsjonalitet/index.html opt-in-hierarchies/index.html orthogonality-enables-optionality/index.html product-for-developers/index.html sannhet-deskriptiv-preskriptiv/index.html unix-signals-crash-course/index.html

