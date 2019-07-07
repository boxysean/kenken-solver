.PHONY: run

all: classes/main/*.class

run:
	scala -classpath classes main.KenKenSolver

classes:
	mkdir -p classes

classes/%.class: src/%.scala classes
	scalac -d classes $<
