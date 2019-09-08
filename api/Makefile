SCALA_FILES=$(shell find src -name '*.scala')

.PHONY: api-run
api-run:
	sbt run

.PHONY: api-test
api-test:
	sbt test

.PHONY: api-install
api-install: build.sbt
	sbt reload
	sbt update

target/scala-2.13/solvemykenken.jar: $(SCALA_FILES)
	sbt assembly

.PHONY: api-install-serverless
api-install-serverless:
	which serverless || npm install -g serverless

.PHONY: api-deploy
api-deploy: target/scala-2.13/solvemykenken.jar api-install-serverless
	serverless deploy
