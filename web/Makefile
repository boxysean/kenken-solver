CFN_STACK_NAME=kenken-gg-web
WEBSITE_URL=kenken.gg
CFN_PARAMETERS_FILE=kenken-gg-cfn-params.json

.PHONY: deploy-cloudformation-stack
deploy-cloudformation-stack: cfn-template.yml $(CFN_PARAMETERS_FILE)
	./deploy-cloudformation-stack.sh $(CFN_STACK_NAME) $(CFN_PARAMETERS_FILE)

.PHONY: deploy-web
deploy-web: build
	aws s3 sync build/ s3://$(WEBSITE_URL)/  # todo: make this get the bucket from CFN

.PHONY: build
build:
	npm run build

.PHONY: deploy-cloudformation-stack deploy-web
deploy: build deploy-cloudformation-stack deploy-web

.PHONY: undeploy
undeploy:
	aws cloudformation delete-stack --stack-name $(CFN_STACK_NAME)

.PHONY: install
install: package.json
	npm install

.PHONY: develop
develop:
	npm run start

.PHONY: test
test: install
	npm test
