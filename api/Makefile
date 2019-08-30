.PHONY: run test install

SCALA_FILES = $(shell find src -name '*.scala')

run:
	sbt run

test:
	sbt test

install: build.sbt
	sbt reload
	sbt update

target/scala-2.13/solvemykenken.jar: $(SCALA_FILES)
	sbt assembly

deploy: target/scala-2.13/solvemykenken.jar
	serverless deploy
