# simple-jesque

Simple project for getting started with jesque

### Start redis in local for mac users:
- `brew install redis`
- `redis-server`

### build project:
- `mvn clean package`

### start producer
- `java -cp target/simple-jesque-1.0-SNAPSHOT.jar example.SimpleConsumer`

### start consumer
- `java -cp target/simple-jesque-1.0-SNAPSHOT.jar example.SimpleProducer`
