# Event Driven Architecture Project

This repository contains a sample project that I and some teammates are using to practice event driven programming.

The intention of this project is to simulate a 'Chair company' as it shifts from a tightly coupled set of services to a Message Driven approach.
Each branch in this repository will be numerated and build on the previous. The 'task at hand' section below will change depending on the branch you're currently looking at.

## Requirements

Working with this project requires the following technologies:

* Java 11+ (install using [sdkman](https://sdkman.io/))
* Docker - a recentish version
* [Minikube](https://minikube.sigs.k8s.io/docs/start/) + [kubectl](https://kubernetes.io/docs/reference/kubectl/) - both are available via homebrow for Mac users.


## setup

> We do not and will never have a UI. API only / use Postman!

Note: most of these steps should be done in `terminal window 1`, with the exception of building the repos (`terminal window 2`) and running the minikube tunnel (`terminal window 3`).

1. `minikube start`
1. `minikube addons enable ingress`
1. `eval $(minikube -p minikube docker-env)`
1. `kubectl apply -f 00-shared.yaml`
1. `kubectl apply -f 01-zookeeper.yaml`. Wait until it's up and running by checking `kubectl --namespace=kafka get pods`
1. `kubectl apply -f 02-kafka.yaml`
1. `kubectl apply -f 03-jobs.yaml`
1. build the repos (in `terminal window 2`: cd into the sub folders and `./gradlew build`)
1. build the docker images: `docker build -t eventclub/chair-admin admin/.` and `docker build -t eventclub/chairfront chairfront/.` _Note_: ensure you've run the eval command above, first. This scopes your terminal window to use Minikube's docker environment and not your laptop's. This also means that builds of the apps should be done in another terminal (`terminal window 2`).
1. `kubectl apply -f chairfront/kubernetes.yaml`
1. `kubectl apply -f admin/kubernetes.yaml`
1. `minikube tunnel` (in `terminal window 3`)

You should now be able to use commands like `kubectl get svc` to see the public ips of the two java services (e.g. localhost:8001).

### Additional useful commands

* `minikube delete` - trash your environment and start over
* `kubectl get pods` - see all your running pods, a great way to spot check everything's working
* `kubectl logs <pod>` - ideal for spot checking that things are working
* `minikube dashboard` - or just spawn the UI to peek into the system 
* `kubectl rollout restart deployment <deployment name>` - used after you update an app and rebuild the docker image, restart all pods


### Basic workflow:

1. make changes in your java app
2. test using `./gradlew clean test` in `terminal window 2`
3. package via `./gradlew build` in `terminal window 2` - jars should appear in `./build/libs`
4. build the docker images
5. rolling restart the deployment


### Local Resources

If the minikube environment is operating sluggishly, you may need to give it more resources. Increase the available memory and cpu that you provide to Docker Desktop, and then:

```
$ minikube stop
$ minikube delete
$ minikube config set memory < a few gigs, as MB... e.g. 8192>
$ minikube config set cpus < maybe more than 1?>
$ minikube start
```


---


## Current Situation: 01 - Kafka Time

We here at the Chair Company are trying to expand our service-to-service communication. Recently we made an initiative to push how our Admin app communicates to our Chairfront service, moving it from Synchronous HTTP request to an async http request. Take a look at Admin's `ChairManagementService`; we created a `ChairPersistTask` (which implements `Runnable`), and submit it a ThreadPool for asynchronous processing. Our also started experimenting with the Observer Pattern, which we've heard may be useful in the future. We also added a test which proves our async behavior via a registered `InternalNotificationSubscriber<T>` (look at `event.club.admin.ChairControllerIntegrationTests` to see it in action).


That's not all! Our engineers have been busy! They've launched a skeleton of a new service we'll need in the future; a warehouse management service we've been sitting on creating that we're calling 'ChairHouse'. 

Therefore, we now have _three_ services: 

* `admin` or `chairs-admin`: The main API for administration of our platform. Intended to be used by administrators to create and report on our 'Chair' domain objects. It's main endpoint is `/chairs`, refer to the `event.club.admin.http.ChairController` for more info.
* `chairfront`: the Customer - oriented api. Will show which types of 'Chairs' we have, if any. It's main endpoint is `/catalog`, refer to `event.club.chairfront.http.CatalogController` for more info.
* `chairhouse`: The Warehouse tracking app. It's extremely bare bones as of now, but the plan is to track the Chair domain in order to know what types of Chairs our warehouse will stock... along with the Inventory, which represents _actual, specific_ chairs in our warehouse (or, instances of chairs). It doesn't do much yet, but we hope to build it out soon!


### Scenario:

Of course, things are getting more complicated. Our enigmatic engineers are getting worried about having to update not 1 but _2_ services when a Chair is created in the Admin app. Their concern is well grounded... having to keep track on which servers need to get notified about which changes is hard.

During a recent daily sit-down meeting (stand-ups are frowned upon here at CC) one of our enterprising engineers proposed using a message broker  - specifically [Apache Kafka](https://kafka.apache.org/) - as a means of "decoupling" (?) our applications. Excitingly, they've already created something called a 'Publisher' and a 'Consumer'  within our Admin and ChairHouse applications, a YAML to run it in our Kubernetes cluster, and a Test to prove we can interact with the Broker.


The problem here is that we want to increase our Kafka presence, eliminate the cross-service HTTP calls, and ensure that our "downstream" services (ChairHouse and don't forget Chairfront) are consuming data published by the Admin application. But how?

### Task at hand:

We want to see if making this call Asynchronous provides better performance in the near term. (We need to crawl before we can run) Research mechanisms on how to execute asynchronous behaviors within Java. For example, [Completable Futures](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) seem promising, as do tools like [RxJava](https://github.com/ReactiveX/RxJava).

The challenge is ramping up. There's several high-level things we want to address:


* Someone thoughtfully added Kafka-related code to Admin and ChairHouse (Take a look at `MessageConsumerService` and `MessageProducerService` in both). However, this has not been integrated into Chairfront. 
* Chairfront's `kubernetes.yaml` will also need to be updated to point at the broker
* Admin will need to publish messages when it's updated, and then Chairfront and Chairhouse will need to listen.


_Basic Challenge_: Research on how to consume messages from Kafka streams, as well as how to publish them. The Consumers and Publishers are currently set to send and receive Strings. Is this the best way to do this? Update the services to accomplish the high level needs above, and fully sever the HTTP request that Admin makes to Chairfront. In other words, when a Chair is created in Admin it should also be reflected in Chairfront and Chairhouse... all by the magic of Kafka.

_Advanced Challenge_: Explore what Tests start to look like when you just publish Messages (i.e. Admin and Chairhouse now have a Kafka TestContainer). Convert the data coming up off the Kafka stream by our Consumer into some internal class before processing. 


> Note: the Kafka related operations are making use of quite a lot of Spring Reactor specific code, which should make things a bit friendlier, but also hides some of the underlying Kafka primitives.


