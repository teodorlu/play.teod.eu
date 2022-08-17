# DO NOT EDIT DIRECTLY -- THIS MAKEFILE IS GENERATED
# SEE `make clean` TARGET


# Generate target for root index
index.html: index.clj a-perfect-day/index.html aphorisms/index.html attention-design/index.html bb-install/index.html bimodal-strategies/index.html bitemporal-worldview/index.html burnout-meaning-deep-work/index.html c-journal/index.html capability-feature-assembly/index.html clojure-interactive/index.html curious/index.html deliverable/index.html discover-difference/index.html document-transform-pandoc-clojure/index.html documentation/index.html emacs/index.html feedback-interface-implementation/index.html fuse/index.html git-commit-messages/index.html grow-knowlege-together/index.html heterarchies/index.html how-to-build-ideas/index.html inspiration-howto/index.html intention-relation-action/index.html interaction-value-differential/index.html interface-perception-design/index.html iterate-knowledge-archipelago/index.html journal/index.html knowledge-worker/index.html kosekoding/index.html learn-design/index.html list-of-problems/index.html literature-notes/index.html lost-in-specificity/index.html makefile-edn/index.html maksimal-opsjonalitet/index.html many-small-functions-bad/index.html narrow-statements/index.html narrow-waist/index.html nix/index.html oggpo/index.html okrs-are-scale-invariant/index.html orthogonality-enables-optionality/index.html play.teod.eu-retrospective/index.html practice-deliberately-with-journals/index.html precise-general-novel/index.html product-for-developers/index.html purposeful-software-development/index.html rdf-intro/index.html sannhet-deskriptiv-preskriptiv/index.html shabakka/index.html software-architecture/index.html software-architecture-as-language-construction/index.html solarpunk-designs/index.html strong-relations/index.html terminalen-motorsag/index.html thesis-antithesis-synthesis/index.html twitter-howto/index.html unix-signals-intro/index.html watchexec-make-dev/index.html why-dont-we-have-a-strategy/index.html wip/index.html xml-on-the-web/index.html a-perfect-day/play.edn aphorisms/play.edn attention-design/play.edn bb-install/play.edn bimodal-strategies/play.edn bitemporal-worldview/play.edn burnout-meaning-deep-work/play.edn c-journal/play.edn capability-feature-assembly/play.edn clojure-interactive/play.edn curious/play.edn deliverable/play.edn discover-difference/play.edn document-transform-pandoc-clojure/play.edn documentation/play.edn emacs/play.edn feedback-interface-implementation/play.edn fuse/play.edn git-commit-messages/play.edn grow-knowlege-together/play.edn heterarchies/play.edn how-to-build-ideas/play.edn inspiration-howto/play.edn intention-relation-action/play.edn interaction-value-differential/play.edn interface-perception-design/play.edn iterate-knowledge-archipelago/play.edn journal/play.edn knowledge-worker/play.edn kosekoding/play.edn learn-design/play.edn list-of-problems/play.edn literature-notes/play.edn lost-in-specificity/play.edn makefile-edn/play.edn maksimal-opsjonalitet/play.edn many-small-functions-bad/play.edn narrow-statements/play.edn narrow-waist/play.edn nix/play.edn oggpo/play.edn okrs-are-scale-invariant/play.edn orthogonality-enables-optionality/play.edn play.teod.eu-retrospective/play.edn practice-deliberately-with-journals/play.edn precise-general-novel/play.edn product-for-developers/play.edn purposeful-software-development/play.edn rdf-intro/play.edn sannhet-deskriptiv-preskriptiv/play.edn shabakka/play.edn software-architecture/play.edn software-architecture-as-language-construction/play.edn solarpunk-designs/play.edn strong-relations/play.edn terminalen-motorsag/play.edn thesis-antithesis-synthesis/play.edn twitter-howto/play.edn unix-signals-intro/play.edn watchexec-make-dev/play.edn why-dont-we-have-a-strategy/play.edn wip/play.edn xml-on-the-web/play.edn
	./index.clj


# Generate target for each page
a-perfect-day/index.html: a-perfect-day/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i a-perfect-day/index.org -o a-perfect-day/index.html

aphorisms/index.html: aphorisms/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i aphorisms/index.org -o aphorisms/index.html

attention-design/index.html: attention-design/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i attention-design/index.org -o attention-design/index.html

bb-install/index.html: bb-install/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i bb-install/index.org -o bb-install/index.html

bimodal-strategies/index.html: bimodal-strategies/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i bimodal-strategies/index.org -o bimodal-strategies/index.html

bitemporal-worldview/index.html: bitemporal-worldview/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i bitemporal-worldview/index.org -o bitemporal-worldview/index.html

burnout-meaning-deep-work/index.html: burnout-meaning-deep-work/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i burnout-meaning-deep-work/index.org -o burnout-meaning-deep-work/index.html

c-journal/index.html: c-journal/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i c-journal/index.org -o c-journal/index.html

capability-feature-assembly/index.html: capability-feature-assembly/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i capability-feature-assembly/index.org -o capability-feature-assembly/index.html

clojure-interactive/index.html: clojure-interactive/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i clojure-interactive/index.org -o clojure-interactive/index.html

curious/index.html: curious/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i curious/index.org -o curious/index.html

deliverable/index.html: deliverable/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i deliverable/index.org -o deliverable/index.html

discover-difference/index.html: discover-difference/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i discover-difference/index.org -o discover-difference/index.html

document-transform-pandoc-clojure/index.html: document-transform-pandoc-clojure/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i document-transform-pandoc-clojure/index.org -o document-transform-pandoc-clojure/index.html

documentation/index.html: documentation/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i documentation/index.org -o documentation/index.html

emacs/index.html: emacs/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i emacs/index.org -o emacs/index.html

feedback-interface-implementation/index.html: feedback-interface-implementation/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i feedback-interface-implementation/index.org -o feedback-interface-implementation/index.html

fuse/index.html: fuse/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i fuse/index.org -o fuse/index.html

git-commit-messages/index.html: git-commit-messages/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i git-commit-messages/index.org -o git-commit-messages/index.html

grow-knowlege-together/index.html: grow-knowlege-together/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i grow-knowlege-together/index.org -o grow-knowlege-together/index.html

heterarchies/index.html: heterarchies/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i heterarchies/index.org -o heterarchies/index.html

how-to-build-ideas/index.html: how-to-build-ideas/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i how-to-build-ideas/index.org -o how-to-build-ideas/index.html

inspiration-howto/index.html: inspiration-howto/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i inspiration-howto/index.org -o inspiration-howto/index.html

intention-relation-action/index.html: intention-relation-action/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i intention-relation-action/index.org -o intention-relation-action/index.html

interaction-value-differential/index.html: interaction-value-differential/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i interaction-value-differential/index.org -o interaction-value-differential/index.html

interface-perception-design/index.html: interface-perception-design/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i interface-perception-design/index.org -o interface-perception-design/index.html

iterate-knowledge-archipelago/index.html: iterate-knowledge-archipelago/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i iterate-knowledge-archipelago/index.org -o iterate-knowledge-archipelago/index.html

journal/index.html: journal/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i journal/index.org -o journal/index.html

knowledge-worker/index.html: knowledge-worker/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i knowledge-worker/index.org -o knowledge-worker/index.html

kosekoding/index.html: kosekoding/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i kosekoding/index.org -o kosekoding/index.html

learn-design/index.html: learn-design/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i learn-design/index.org -o learn-design/index.html

list-of-problems/index.html: list-of-problems/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i list-of-problems/index.org -o list-of-problems/index.html

literature-notes/index.html: literature-notes/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i literature-notes/index.org -o literature-notes/index.html

lost-in-specificity/index.html: lost-in-specificity/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i lost-in-specificity/index.org -o lost-in-specificity/index.html

makefile-edn/index.html: makefile-edn/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i makefile-edn/index.org -o makefile-edn/index.html

maksimal-opsjonalitet/index.html: maksimal-opsjonalitet/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i maksimal-opsjonalitet/index.org -o maksimal-opsjonalitet/index.html

many-small-functions-bad/index.html: many-small-functions-bad/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i many-small-functions-bad/index.org -o many-small-functions-bad/index.html

narrow-statements/index.html: narrow-statements/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i narrow-statements/index.org -o narrow-statements/index.html

narrow-waist/index.html: narrow-waist/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i narrow-waist/index.org -o narrow-waist/index.html

nix/index.html: nix/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i nix/index.org -o nix/index.html

oggpo/index.html: oggpo/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i oggpo/index.org -o oggpo/index.html

okrs-are-scale-invariant/index.html: okrs-are-scale-invariant/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i okrs-are-scale-invariant/index.org -o okrs-are-scale-invariant/index.html

orthogonality-enables-optionality/index.html: orthogonality-enables-optionality/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i orthogonality-enables-optionality/index.org -o orthogonality-enables-optionality/index.html

play.teod.eu-retrospective/index.html: play.teod.eu-retrospective/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i play.teod.eu-retrospective/index.org -o play.teod.eu-retrospective/index.html

practice-deliberately-with-journals/index.html: practice-deliberately-with-journals/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i practice-deliberately-with-journals/index.org -o practice-deliberately-with-journals/index.html

precise-general-novel/index.html: precise-general-novel/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i precise-general-novel/index.org -o precise-general-novel/index.html

product-for-developers/index.html: product-for-developers/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i product-for-developers/index.org -o product-for-developers/index.html

purposeful-software-development/index.html: purposeful-software-development/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i purposeful-software-development/index.org -o purposeful-software-development/index.html

rdf-intro/index.html: rdf-intro/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i rdf-intro/index.org -o rdf-intro/index.html

sannhet-deskriptiv-preskriptiv/index.html: sannhet-deskriptiv-preskriptiv/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i sannhet-deskriptiv-preskriptiv/index.org -o sannhet-deskriptiv-preskriptiv/index.html

shabakka/index.html: shabakka/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i shabakka/index.org -o shabakka/index.html

software-architecture/index.html: software-architecture/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i software-architecture/index.org -o software-architecture/index.html

software-architecture-as-language-construction/index.html: software-architecture-as-language-construction/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i software-architecture-as-language-construction/index.org -o software-architecture-as-language-construction/index.html

solarpunk-designs/index.html: solarpunk-designs/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i solarpunk-designs/index.org -o solarpunk-designs/index.html

strong-relations/index.html: strong-relations/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i strong-relations/index.org -o strong-relations/index.html

terminalen-motorsag/index.html: terminalen-motorsag/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i terminalen-motorsag/index.org -o terminalen-motorsag/index.html

thesis-antithesis-synthesis/index.html: thesis-antithesis-synthesis/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i thesis-antithesis-synthesis/index.org -o thesis-antithesis-synthesis/index.html

twitter-howto/index.html: twitter-howto/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i twitter-howto/index.org -o twitter-howto/index.html

unix-signals-intro/index.html: unix-signals-intro/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i unix-signals-intro/index.org -o unix-signals-intro/index.html

watchexec-make-dev/index.html: watchexec-make-dev/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i watchexec-make-dev/index.org -o watchexec-make-dev/index.html

why-dont-we-have-a-strategy/index.html: why-dont-we-have-a-strategy/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i why-dont-we-have-a-strategy/index.org -o why-dont-we-have-a-strategy/index.html

wip/index.html: wip/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i wip/index.org -o wip/index.html

xml-on-the-web/index.html: xml-on-the-web/index.org
	pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i xml-on-the-web/index.org -o xml-on-the-web/index.html


.PHONY: makefile
makefile:
	./play.clj makefile


# Rengenerate the index
.PHONY: clean
clean:
	rm -f index.html


# Regenerate everything
.PHONY: ultraclean
ultraclean: clean
	rm -f index.html a-perfect-day/index.html aphorisms/index.html attention-design/index.html bb-install/index.html bimodal-strategies/index.html bitemporal-worldview/index.html burnout-meaning-deep-work/index.html c-journal/index.html capability-feature-assembly/index.html clojure-interactive/index.html curious/index.html deliverable/index.html discover-difference/index.html document-transform-pandoc-clojure/index.html documentation/index.html emacs/index.html feedback-interface-implementation/index.html fuse/index.html git-commit-messages/index.html grow-knowlege-together/index.html heterarchies/index.html how-to-build-ideas/index.html inspiration-howto/index.html intention-relation-action/index.html interaction-value-differential/index.html interface-perception-design/index.html iterate-knowledge-archipelago/index.html journal/index.html knowledge-worker/index.html kosekoding/index.html learn-design/index.html list-of-problems/index.html literature-notes/index.html lost-in-specificity/index.html makefile-edn/index.html maksimal-opsjonalitet/index.html many-small-functions-bad/index.html narrow-statements/index.html narrow-waist/index.html nix/index.html oggpo/index.html okrs-are-scale-invariant/index.html orthogonality-enables-optionality/index.html play.teod.eu-retrospective/index.html practice-deliberately-with-journals/index.html precise-general-novel/index.html product-for-developers/index.html purposeful-software-development/index.html rdf-intro/index.html sannhet-deskriptiv-preskriptiv/index.html shabakka/index.html software-architecture/index.html software-architecture-as-language-construction/index.html solarpunk-designs/index.html strong-relations/index.html terminalen-motorsag/index.html thesis-antithesis-synthesis/index.html twitter-howto/index.html unix-signals-intro/index.html watchexec-make-dev/index.html why-dont-we-have-a-strategy/index.html wip/index.html xml-on-the-web/index.html
