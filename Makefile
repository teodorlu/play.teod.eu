.PHONY: everything
everything:  aphorisms/index.html bitemporal-worldview/index.html emacs/index.html feedback-design-impl/index.html grow-knowlege-together/index.html heterarchies/index.html iterate-kunnskapshage/index.html journal/index.html knowledge-worker/index.html maksimal-opsjonalitet/index.html narrow-waist/index.html orthogonality-enables-optionality/index.html product-for-developers/index.html sannhet-deskriptiv-preskriptiv/index.html unix-signals-intro/index.html

index.html: index.clj aphorisms/play.edn bitemporal-worldview/play.edn emacs/play.edn feedback-design-impl/play.edn grow-knowlege-together/play.edn heterarchies/play.edn iterate-kunnskapshage/play.edn journal/play.edn knowledge-worker/play.edn maksimal-opsjonalitet/play.edn narrow-waist/play.edn orthogonality-enables-optionality/play.edn product-for-developers/play.edn sannhet-deskriptiv-preskriptiv/play.edn unix-signals-intro/play.edn
	./index.clj

aphorisms/index.html: aphorisms/index.org
	pandoc -s --toc --from=org+smart -i aphorisms/index.org -o aphorisms/index.html

bitemporal-worldview/index.html: bitemporal-worldview/index.org
	pandoc -s --toc --from=org+smart -i bitemporal-worldview/index.org -o bitemporal-worldview/index.html

emacs/index.html: emacs/index.org
	pandoc -s --toc --from=org+smart -i emacs/index.org -o emacs/index.html

feedback-design-impl/index.html: feedback-design-impl/index.org
	pandoc -s --toc --from=org+smart -i feedback-design-impl/index.org -o feedback-design-impl/index.html

grow-knowlege-together/index.html: grow-knowlege-together/index.org
	pandoc -s --toc --from=org+smart -i grow-knowlege-together/index.org -o grow-knowlege-together/index.html

heterarchies/index.html: heterarchies/index.org
	pandoc -s --toc --from=org+smart -i heterarchies/index.org -o heterarchies/index.html

iterate-kunnskapshage/index.html: iterate-kunnskapshage/index.org
	pandoc -s --toc --from=org+smart -i iterate-kunnskapshage/index.org -o iterate-kunnskapshage/index.html

journal/index.html: journal/index.org
	pandoc -s --toc --from=org+smart -i journal/index.org -o journal/index.html

knowledge-worker/index.html: knowledge-worker/index.org
	pandoc -s --toc --from=org+smart -i knowledge-worker/index.org -o knowledge-worker/index.html

maksimal-opsjonalitet/index.html: maksimal-opsjonalitet/index.org
	pandoc -s --toc --from=org+smart -i maksimal-opsjonalitet/index.org -o maksimal-opsjonalitet/index.html

narrow-waist/index.html: narrow-waist/index.org
	pandoc -s --toc --from=org+smart -i narrow-waist/index.org -o narrow-waist/index.html

orthogonality-enables-optionality/index.html: orthogonality-enables-optionality/index.org
	pandoc -s --toc --from=org+smart -i orthogonality-enables-optionality/index.org -o orthogonality-enables-optionality/index.html

product-for-developers/index.html: product-for-developers/index.org
	pandoc -s --toc --from=org+smart -i product-for-developers/index.org -o product-for-developers/index.html

sannhet-deskriptiv-preskriptiv/index.html: sannhet-deskriptiv-preskriptiv/index.org
	pandoc -s --toc --from=org+smart -i sannhet-deskriptiv-preskriptiv/index.org -o sannhet-deskriptiv-preskriptiv/index.html

unix-signals-intro/index.html: unix-signals-intro/index.org
	pandoc -s --toc --from=org+smart -i unix-signals-intro/index.org -o unix-signals-intro/index.html
