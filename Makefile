.PHONY: up down logs load-test spike-test openapi kafka-topics run-mvp

up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

kafka-topics:
	bash scripts/create-kafka-topics.sh

run-mvp:
	FRAUD_KAFKA_ENABLED=true \
	FRAUD_KAFKA_ALERT_PUBLISHER_ENABLED=true \
	FRAUD_KAFKA_ALERT_CONSUMER_ENABLED=true \
	FRAUD_RULES_DEFAULT_ENABLED=true \
	KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
	mvn spring-boot:run

load-test:
	k6 run k6/load-test.js

spike-test:
	k6 run k6/spike-test.js

openapi:
	@echo "OpenAPI contract: openapi/fraud-api.yaml"
