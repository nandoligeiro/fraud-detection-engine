.PHONY: up down logs load-test spike-test openapi

up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

load-test:
	k6 run k6/load-test.js

spike-test:
	k6 run k6/spike-test.js

openapi:
	@echo "OpenAPI contract: openapi/fraud-api.yaml"
