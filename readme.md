
# Connect to Kafka
docker-compose exec kafka bash

# List Topics
kafka-topics --bootstrap-server localhost:9092 --list

# Check Topics
kafka-console-consumer --bootstrap-server localhost:9092 --topic start-watching-events --property print.timestamp=true --property print.key=true --property print.value=true --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic stop-watching-events --property print.timestamp=true --property print.key=true --property print.value=true --from-beginning

# Delete Topic
kafka-topics --bootstrap-server localhost:9092 --topic short-sessions --delete
