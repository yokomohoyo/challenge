## Challenge Component A1
### Origin
This program was created in response to Challenge A section from the "CyVerse SE Technical Challenge" document
### What
This program consumes random friends quotes from a public api and publishes the data to a nats queue.
It is a trivial java 15 application built by gradle. It can be run as a simple CLI tool and can be built as a
docker image.

To quickly start a publisher... [^1]
```commandline
./gradlew run
```


[^1]: This presumes you have a nats server listening on nats://localhost:4222