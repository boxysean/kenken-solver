all: KenKenSolver.class

run:
	scala KenKenSolver

%.class: %.scala
	scalac $<
