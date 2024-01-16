# vier-workshop

# building the projects  
to build a project either use IDE built in tools  
or navigate to an exercise subfolder and execute "mvn clean install"  
and execute the .jar file in the generated target folder with "java -jar *.jar"

# docker compose
A docker-compose file exists in the root of this project. Execute  
```sh
docker compose up  
```
to spin it up. To utilize your local Kafka use a different properties setup for the clients.
```java
properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
properties.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, "true");
```