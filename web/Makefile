CFN_STACK_NAME=kenken-gg-website

.PHONY: deploy-cloudformation-stack
deploy-cloudformation-stack: cfn-template.yml
	aws cloudformation deploy \
	  --stack-name $(CFN_STACK_NAME) \
		--template-file cfn-template.yml
	aws cloudformation wait stack-create-complete --stack-name $(CFN_STACK_NAME)
	aws cloudformation wait stack-update-complete --stack-name $(CFN_STACK_NAME)

.PHONY: deploy-web
deploy-web:
	aws s3 sync build/ s3://kenken.gg/

.PHONY: build
build:
	npm run build

.PHONY: deploy-cloudformation-stack deploy-web
deploy: build deploy-cloudformation-stack deploy-web

.PHONY: undeploy
undeploy:
	aws cloudformation delete-stack --stack-name $(CFN_STACK_NAME)
	aws s3 rb --force s3://kenken.gg

.PHONY: install
install: package.json
	npm install

.PHONY: test
test: install
	npm test
