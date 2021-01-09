# simple-jesque

Simple jeque project for getting started with jeque

# Start redis in local for mac users:
brew install redis
redis-server

# install maven if not already installed:
brew install maven

# build project:
mvn clean package

# start producer
java -cp target/simple-jesque-1.0-SNAPSHOT.jar example.SimpleConsumer

# start consumer
java -cp target/simple-jesque-1.0-SNAPSHOT.jar example.SimpleProducer
