# DO NOT EDIT DIRECTLY -- THIS MAKEFILE IS GENERATED
# SEE `make clean` TARGET


# Generate phony target for all pages - default target
.PHONY: everything
everything:  index.html aphorisms/index.html bitemporal-worldview/index.html capability-feature-assembly/index.html clojure-interactive/index.html discover-difference/index.html emacs/index.html feedback-interface-implementation/index.html fuse/index.html grow-knowlege-together/index.html heterarchies/index.html inspiration-howto/index.html intention-relation-action/index.html interaction-value-differential/index.html iterate-knowledge-archipelago/index.html journal/index.html knowledge-worker/index.html lost-in-specificity/index.html maksimal-opsjonalitet/index.html narrow-statements/index.html narrow-waist/index.html oggpo/index.html orthogonality-enables-optionality/index.html play.teod.eu-journal/index.html play.teod.eu-rolling-retrospective/index.html precise-general-novel/index.html product-for-developers/index.html purposeful-software-development/index.html rdf-intro/index.html sannhet-deskriptiv-preskriptiv/index.html shabakka/index.html software-architecture-as-language-construction/index.html software-architecture/index.html strong-relations/index.html unix-signals-intro/index.html why-dont-we-have-a-strategy/index.html wip/index.html


# Generate target for root index
index.html: index.clj aphorisms/play.edn bitemporal-worldview/play.edn capability-feature-assembly/play.edn clojure-interactive/play.edn discover-difference/play.edn emacs/play.edn feedback-interface-implementation/play.edn fuse/play.edn grow-knowlege-together/play.edn heterarchies/play.edn inspiration-howto/play.edn intention-relation-action/play.edn interaction-value-differential/play.edn iterate-knowledge-archipelago/play.edn journal/play.edn knowledge-worker/play.edn lost-in-specificity/play.edn maksimal-opsjonalitet/play.edn narrow-statements/play.edn narrow-waist/play.edn oggpo/play.edn orthogonality-enables-optionality/play.edn play.teod.eu-journal/play.edn play.teod.eu-rolling-retrospective/play.edn precise-general-novel/play.edn product-for-developers/play.edn purposeful-software-development/play.edn rdf-intro/play.edn sannhet-deskriptiv-preskriptiv/play.edn shabakka/play.edn software-architecture-as-language-construction/play.edn software-architecture/play.edn strong-relations/play.edn unix-signals-intro/play.edn why-dont-we-have-a-strategy/play.edn wip/play.edn
	./index.clj


# Generate target for each page
aphorisms/index.html: aphorisms/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i aphorisms/index.org -o aphorisms/index.html

bitemporal-worldview/index.html: bitemporal-worldview/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i bitemporal-worldview/index.org -o bitemporal-worldview/index.html

capability-feature-assembly/index.html: capability-feature-assembly/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i capability-feature-assembly/index.org -o capability-feature-assembly/index.html

clojure-interactive/index.html: clojure-interactive/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i clojure-interactive/index.org -o clojure-interactive/index.html

discover-difference/index.html: discover-difference/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i discover-difference/index.org -o discover-difference/index.html

emacs/index.html: emacs/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i emacs/index.org -o emacs/index.html

feedback-interface-implementation/index.html: feedback-interface-implementation/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i feedback-interface-implementation/index.org -o feedback-interface-implementation/index.html

fuse/index.html: fuse/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i fuse/index.org -o fuse/index.html

grow-knowlege-together/index.html: grow-knowlege-together/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i grow-knowlege-together/index.org -o grow-knowlege-together/index.html

heterarchies/index.html: heterarchies/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i heterarchies/index.org -o heterarchies/index.html

inspiration-howto/index.html: inspiration-howto/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i inspiration-howto/index.org -o inspiration-howto/index.html

intention-relation-action/index.html: intention-relation-action/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i intention-relation-action/index.org -o intention-relation-action/index.html

interaction-value-differential/index.html: interaction-value-differential/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i interaction-value-differential/index.org -o interaction-value-differential/index.html

iterate-knowledge-archipelago/index.html: iterate-knowledge-archipelago/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i iterate-knowledge-archipelago/index.org -o iterate-knowledge-archipelago/index.html

journal/index.html: journal/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i journal/index.org -o journal/index.html

knowledge-worker/index.html: knowledge-worker/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i knowledge-worker/index.org -o knowledge-worker/index.html

lost-in-specificity/index.html: lost-in-specificity/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i lost-in-specificity/index.org -o lost-in-specificity/index.html

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

play.teod.eu-journal/index.html: play.teod.eu-journal/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i play.teod.eu-journal/index.org -o play.teod.eu-journal/index.html

play.teod.eu-rolling-retrospective/index.html: play.teod.eu-rolling-retrospective/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i play.teod.eu-rolling-retrospective/index.org -o play.teod.eu-rolling-retrospective/index.html

precise-general-novel/index.html: precise-general-novel/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i precise-general-novel/index.org -o precise-general-novel/index.html

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

strong-relations/index.html: strong-relations/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i strong-relations/index.org -o strong-relations/index.html

unix-signals-intro/index.html: unix-signals-intro/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i unix-signals-intro/index.org -o unix-signals-intro/index.html

why-dont-we-have-a-strategy/index.html: why-dont-we-have-a-strategy/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i why-dont-we-have-a-strategy/index.org -o why-dont-we-have-a-strategy/index.html

wip/index.html: wip/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i wip/index.org -o wip/index.html


# One `make clean` and then `make` should always give you fresh state
.PHONY: clean
clean:
	rm -f index.html
	./makemakefile.clj > Makefile
