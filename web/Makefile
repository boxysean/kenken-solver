CFN_STACK_NAME=kenken-gg-website

.PHONY: web-cloudformation-stack
web-cloudformation-stack: cfn-template.yml
	aws cloudformation deploy \
	  --stack-name $(CFN_STACK_NAME) \
		--template-file cfn-template.yml
	aws cloudformation wait stack-create-complete --stack-name $(CFN_STACK_NAME)
	aws cloudformation wait stack-update-complete --stack-name $(CFN_STACK_NAME)

.PHONY: web-deploy
web-deploy: web-cloudformation-stack
	npm run build
	aws s3 sync build/ s3://kenken.gg/

.PHONY: clean
web-clean-deploy:
	aws cloudformation delete-stack --stack-name $(CFN_STACK_NAME)
	aws s3 rb --force s3://kenken.gg

.PHONY: web-install
web-install: package.json
	npm install

.PHONY: web-test
web-test: install
	npm test
