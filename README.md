# Challenge
This is in response to the Cyverse SE Technical Challenge
## Approach

### Stack
This challenge response requires the installation and configuration of few
different elements. Namely:
1. minikube
2. nats.io
3. postgres
4. Java 15 [^1]
5. Python 3.7
6. Check out the project [here](https://github.com/yokomohoyo/challenge)

### Environment setup
Due to no provided container repository, we will be working locally. Ensure that
your shell is configured for local images.
```console
$ eval $(minikube docker-env)
```

You can verify your java version as follows:
```console
$ java -version
```

### Publisher at a High Level
The publisher app is a simple Java 15 application built by gradle. It is located
in the CyverseChallenge_A1 folder. It has a rudimentary http client to ping a
friends quote api and a nats client to push json data to the queue. The process
spawns a thread which polls the api every 5 seconds to fetch a set of 10 random
quotes.
```console
$ cd CyverseChallenge_A1
$ ./gradlew jibDockerBuild
```
This will build as image of the publisher portion of challenge a. [^1]

### Consumer at a High Level
The consumer is a simple python script with a nats and postgres client. It reads
messages from queue_a and stores the json in a postgres table. Let's package it
for k8s consumption by executing the following command
```console
$ cd CyverseChallenge_A2
$ docker build . -t cabro.org/subscriber
```

### Deployment
We need to set up nats and postgres to get the apps up and running. Let's start
with NATS. In this case I just used a handy dandy helm chart.
```console
$ helm repo add nats https://nats-io.github.io/k8s/helm/charts/
$ helm repo update
$ helm install my-nats nats/nats
$ helm install my-stan nats/stan --set stan.nats.url=nats://my-nats:4222
```

Now we can get postgres set up. In this case I just created a few small yaml
files.
```console
$ cd ./k8s/postgres
$ for k in `ls *.yml`; do kubectl apply -f $k; done
```

Now we should see everything is up.
```console
$ kubectl get pods
NAME                           READY   STATUS    RESTARTS      AGE
my-nats-0                      3/3     Running   0             97s
my-nats-box-59c679c68d-b7gk9   1/1     Running   0             74s
my-stan-0                      2/2     Running   2 (66s ago)   79s
postgres-86f6d4c959-6p9rp      1/1     Running   0             3s
```

Finally, we can proceed with dropping in the publisher and consumer.
```console
$ cd ./k8s
$ kubectl apply -f publisher.yml
$ kubectl apply -f subscriber.yml
```

This will create 2 jobs we should be able to see
```console
$ kubectl get jobs
NAME         COMPLETIONS   DURATION   AGE
publisher    0/1           9s         9s
subscriber   0/1           4s         4s
```

If all the pods are running then we should see some data in postgres. So let's
run some commands on the container to find out:
```console
$ kubectl exec -it postgres-86f6d4c959-6p9rp -- /bin/bash
$ su - postgres
$ psql
$ select * from queue_a;
id  |                                                                               body
------+------------------------------------------------------------------------------------------------------------------------------------------------------------------
  1 | {"quote":"This is brand-new information!","character":"Phoebe"}
  2 | {"quote":"Here come the meat sweats.","character":"Joey"}
  3 | {"quote":"But they don\u0027t know that we know they know we know!","character":"Phoebe"}
  4 | {"quote":"It\u0027s a moo point. It\u0027s like a cow\u0027s opinion; it doesn\u0027t matter. It\u0027s moo.","character":"Joey"}
  5 | {"quote":"Phoebe. That\u0027s P, as in Phoebe, H as in hoebe, O as in oebe, E as in ebe, B as in bebe, and E as in \u0027Ello there mate.","character":"Phoebe"}
  6 | {"quote":"Smelly cat, smelly cat, what are they feeding you? Smelly cat, smelly cat, it\u0027s not your fault.","character":"Phoebe"}
  7 | {"quote":"We were on a break!","character":"Ross"}
  8 | {"quote":"Guys can fake it? Unbelievable! The one thing that’s ours!","character":"Monica"}
  9 | {"quote":"I\u0027m not so good with the advice. Can I interest you in a sarcastic comment?","character":"Chandler"}
 10 | {"quote":"How you doin?","character":"Joey"}
 11 | {"quote":"Pivot! Pivot! Pivot! Pivot! Pivot!","character":"Ross"}
 12 | {"quote":"Guys can fake it? Unbelievable! The one thing that’s ours!","character":"Monica"}
 13 | {"quote":"It\u0027s a moo point. It\u0027s like a cow\u0027s opinion; it doesn\u0027t matter. It\u0027s moo.","character":"Joey"}
 14 | {"quote":"Just so you know, it\u0027s not that common, it doesn\u0027t happen to every guy, and it is a big deal!","character":"Rachel"}
 15 | {"quote":"Joey doesn\u0027t share food!","character":"Joey"}
 16 | {"quote":"We were on a break!","character":"Ross"}
 17 | {"quote":"Phoebe. That\u0027s P, as in Phoebe, H as in hoebe, O as in oebe, E as in ebe, B as in bebe, and E as in \u0027Ello there mate.","character":"Phoebe"}
 18 | {"quote":"I got off the plane.","character":"Rachel"}
 19 | {"quote":"I grew up in a house with Monica, okay. If you didn\u0027t eat fast, you didn\u0027t eat.","character":"Ross"}
 20 | {"quote":"This is brand-new information!","character":"Phoebe"}...
```



[^1]: If you need a java 15 installation, try [sdkman](https://sdkman.io/)
