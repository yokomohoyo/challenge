## Challenge Component A2
### Origin
This program was created in response to Challenge A section from the "CyVerse SE Technical Challenge" document
### What
This program consumes random friends quotes from a public api and publishes the data to a nats queue.
It is a trivial python script.

To quickly run it via cli... [^1]
```commandline
python Subscriber.py
```


[^1]: This presumes you have a python runtime and postgres on localhost:5432
