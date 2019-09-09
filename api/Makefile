SCALA_FILES=$(shell find src -name '*.scala')
BIG_FAT_JAR="target/scala-2.13/solvemykenken-assembly-1.0.jar"

.PHONY: run
run:
	sbt run

.PHONY: test
test:
	sbt test

$(BIG_FAT_JAR): $(SCALA_FILES)
	sbt assembly

.PHONY: install-serverless
install-serverless:
	which serverless || npm install -g serverless

.PHONY: deploy
deploy: $(BIG_FAT_JAR) install-serverless
	serverless deploy

.PHONY: deploy-deployment-policy
deploy-deployment-policy: serverless.deployment-iam-policy.json
	aws iam create-policy-version \
	  --policy-arn arn:aws:iam::858316752957:policy/serverless-deployment \
		--policy-document file://./serverless.deployment-iam-policy.json \
		--set-as-default
