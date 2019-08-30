.PHONY: test deploy

buildweb:
	cd web && npm run build

test:
	serverless test
