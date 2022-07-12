.PHONY: everything
everything:  index.html aphorisms/index.html bitemporal-worldview/index.html clojure-interactive/index.html discover-difference/index.html emacs/index.html feedback-design-impl/index.html fuse/index.html grow-knowlege-together/index.html heterarchies/index.html inspiration-howto/index.html iterate-knowledge-archipelago/index.html journal/index.html knowledge-worker/index.html maksimal-opsjonalitet/index.html narrow-statements/index.html narrow-waist/index.html oggpo/index.html orthogonality-enables-optionality/index.html play.teod.eu/index.html product-for-developers/index.html purposeful-software-development/index.html rdf-intro/index.html sannhet-deskriptiv-preskriptiv/index.html shabakka/index.html software-architecture-as-language-construction/index.html software-architecture/index.html unix-signals-intro/index.html


index.html: index.clj aphorisms/play.edn bitemporal-worldview/play.edn clojure-interactive/play.edn discover-difference/play.edn emacs/play.edn feedback-design-impl/play.edn fuse/play.edn grow-knowlege-together/play.edn heterarchies/play.edn inspiration-howto/play.edn iterate-knowledge-archipelago/play.edn journal/play.edn knowledge-worker/play.edn maksimal-opsjonalitet/play.edn narrow-statements/play.edn narrow-waist/play.edn oggpo/play.edn orthogonality-enables-optionality/play.edn play.teod.eu/play.edn product-for-developers/play.edn purposeful-software-development/play.edn rdf-intro/play.edn sannhet-deskriptiv-preskriptiv/play.edn shabakka/play.edn software-architecture-as-language-construction/play.edn software-architecture/play.edn unix-signals-intro/play.edn
	./index.clj


aphorisms/index.html: aphorisms/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i aphorisms/index.org -o aphorisms/index.html

bitemporal-worldview/index.html: bitemporal-worldview/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i bitemporal-worldview/index.org -o bitemporal-worldview/index.html

clojure-interactive/index.html: clojure-interactive/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i clojure-interactive/index.org -o clojure-interactive/index.html

discover-difference/index.html: discover-difference/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i discover-difference/index.org -o discover-difference/index.html

emacs/index.html: emacs/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i emacs/index.org -o emacs/index.html

feedback-design-impl/index.html: feedback-design-impl/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i feedback-design-impl/index.org -o feedback-design-impl/index.html

fuse/index.html: fuse/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i fuse/index.org -o fuse/index.html

grow-knowlege-together/index.html: grow-knowlege-together/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i grow-knowlege-together/index.org -o grow-knowlege-together/index.html

heterarchies/index.html: heterarchies/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i heterarchies/index.org -o heterarchies/index.html

inspiration-howto/index.html: inspiration-howto/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i inspiration-howto/index.org -o inspiration-howto/index.html

iterate-knowledge-archipelago/index.html: iterate-knowledge-archipelago/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i iterate-knowledge-archipelago/index.org -o iterate-knowledge-archipelago/index.html

journal/index.html: journal/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i journal/index.org -o journal/index.html

knowledge-worker/index.html: knowledge-worker/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i knowledge-worker/index.org -o knowledge-worker/index.html

maksimal-opsjonalitet/index.html: maksimal-opsjonalitet/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i maksimal-opsjonalitet/index.org -o maksimal-opsjonalitet/index.html

narrow-statements/index.html: narrow-statements/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i narrow-statements/index.org -o narrow-statements/index.html

narrow-waist/index.html: narrow-waist/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i narrow-waist/index.org -o narrow-waist/index.html

oggpo/index.html: oggpo/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i oggpo/index.org -o oggpo/index.html

orthogonality-enables-optionality/index.html: orthogonality-enables-optionality/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i orthogonality-enables-optionality/index.org -o orthogonality-enables-optionality/index.html

play.teod.eu/index.html: play.teod.eu/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i play.teod.eu/index.org -o play.teod.eu/index.html

product-for-developers/index.html: product-for-developers/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i product-for-developers/index.org -o product-for-developers/index.html

purposeful-software-development/index.html: purposeful-software-development/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i purposeful-software-development/index.org -o purposeful-software-development/index.html

rdf-intro/index.html: rdf-intro/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i rdf-intro/index.org -o rdf-intro/index.html

sannhet-deskriptiv-preskriptiv/index.html: sannhet-deskriptiv-preskriptiv/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i sannhet-deskriptiv-preskriptiv/index.org -o sannhet-deskriptiv-preskriptiv/index.html

shabakka/index.html: shabakka/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i shabakka/index.org -o shabakka/index.html

software-architecture-as-language-construction/index.html: software-architecture-as-language-construction/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i software-architecture-as-language-construction/index.org -o software-architecture-as-language-construction/index.html

software-architecture/index.html: software-architecture/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i software-architecture/index.org -o software-architecture/index.html

unix-signals-intro/index.html: unix-signals-intro/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i unix-signals-intro/index.org -o unix-signals-intro/index.html


.PHONY: clean
clean:
	rm index.html
	./makemakefile.clj > Makefile
