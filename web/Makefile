.PHONY: cloudformation-stack deploy install test
CFN_STACK_NAME = kenken-gg-website

cloudformation-stack: cfn-template.yml
	aws cloudformation deploy \
	  --stack-name $(CFN_STACK_NAME) \
		--template-file cfn-template.yml
	aws cloudformation wait stack-create-complete --stack-name $(CFN_STACK_NAME)
	aws cloudformation wait stack-update-complete --stack-name $(CFN_STACK_NAME)

deploy: cloudformation-stack
	npm run build
	aws s3 sync build/ s3://kenken.gg/

clean:
	aws cloudformation delete-stack --stack-name $(CFN_STACK_NAME)
	aws s3 rb --force s3://kenken.gg

install: package.json
	npm install

test: install
	npm test
