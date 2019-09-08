.PHONY: run test install serverless

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

serverless:
	which serverless || npm install -g serverless

deploy: target/scala-2.13/solvemykenken.jar serverless
	serverless deploy
