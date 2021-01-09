# simple-jesque

Simple project for getting started with jesque

# Start redis in local for mac users:
terminal> brew install redis
terminal> redis-server

# build project:
terminal> mvn clean package

# start producer
terminal> java -cp target/simple-jesque-1.0-SNAPSHOT.jar example.SimpleConsumer

# start consumer
terminal> java -cp target/simple-jesque-1.0-SNAPSHOT.jar example.SimpleProducer
