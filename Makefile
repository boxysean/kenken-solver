all: ReadFile.scala

run:
	scala ReadFile

%.class: %.scala
	scalac $<
