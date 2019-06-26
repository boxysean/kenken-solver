all: ReadFile.class

run:
	scala ReadFile

%.class: %.scala
	scalac $<
