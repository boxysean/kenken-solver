SCALA_FILES=$(shell find src -name '*.scala')

.PHONY: run
run:
	sbt run

.PHONY: test
test:
	sbt test

target/scala-2.13/solvemykenken.jar: $(SCALA_FILES)
	sbt assembly

.PHONY: install-serverless
install-serverless:
	which serverless || npm install -g serverless

.PHONY: deploy
deploy: target/scala-2.13/solvemykenken.jar api-install-serverless
	serverless deploy
