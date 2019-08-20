.PHONY: run test deploy testapi

run:
	sbt run

test:
	sbt test

target/scala-2.13/solvemykenken.jar:
	sbt assembly

deploy: target/scala-2.13/solvemykenken.jar
	serverless deploy

test-serverless:
	serverless invoke local -f solve
