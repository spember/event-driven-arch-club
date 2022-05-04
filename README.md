# Event Driven Architecture Project

This repository contains a sample project that I and some teammates are using to practice event driven programming.

The intention of this project is to simulate a 'Chair company' as it shifts from a tightly coupled set of services to a Message Driven approach.
Each branch in this repo will be numerated and build on the previous. The 'task at hand' section below will change

## Requirements

Working with this project requires the following technologies:

* Java 11+ (install using [sdkman](https://sdkman.io/))
* Docker - a recentish version
* [Minikube](https://minikube.sigs.k8s.io/docs/start/) + [kubectl](https://kubernetes.io/docs/reference/kubectl/) - both are available via homebrow for Mac users.


## setup

1. `minikube start`
1. `minikube addons enable ingress`
1. `eval $(minikube -p minikube docker-env)`
1. `kubectl apply -f shared.yaml`
1. build the repos (in a separate terminal window, cd into the sub folders and `./gradlew build`)
1. build the images: `docker build -t eventclub/chair-admin admin/.` and `docker build -t eventclub/chairfront chairfront/.` _Note_: ensure you've run the eval command above, first. This scopes your terminal window to use Minikube's docker environment and not your laptop's. This also means that builds of the apps should be done in another terminal.
1. `kubectl apply -f jobs.yaml`
1. `kubectl apply -f chairfront/kubernetes.yaml`
1. `kubectl apply -f admin/kubernetes.yaml`
1. `minikube tunnel` (in a seprate terminal window)

You should now be able to use commands like `kubectl get svc` to see the public ips of the two java services (e.g. localhost:8001).

### Additional useful commands

* `minikube delete` - trash your environment and start over
* `kubectl get pods` - see all your running pods, a great way to spot check everything's working
* `kubectl logs <pod>` - ideal for spot checking that things are working
* `minikube dashboard` - or just spawn the UI to peek into the system 

---


## Current Situation: Intro

Welcome to the Chair Company! We sell chairs of all types. As of now we have two services: 

* `admin` or `chairs-admin`: The main API for administration of our platform. Intended to be used by administrators to create and report on our 'Chair' domain objects. It's main endpoint is `/chairs`, refer to the `event.club.admin.http.ChairController` for more info.
* `chairfront`: the Customer - oriented api. Will show which types of 'Chairs' we have, if any. It's main endpoint is `/catalog`, refer to `event.club.chairfront.http.CatalogController` for more info.

We do not and will never have a UI. API only / use Postman!

### Scenario:

Our admins POST data to /chairs on the admin service in order to create new Chair types. The admin service then notifies Chairfront of this new type, although chairfront does not receive inventory information (yet) so there may not be anything to sell.

The problem is that we have a tight coupling between the Admin service and Chairfront, and Chairfront is slow. Our admin users are starting to complain. What can we do?

### Task at hand:

We want to see if making this call Asynchronous provides better performance in the near term. (We need to crawl before we can run) Research mechanisms on how to execute asynchronous behaviors within Java. For example, [Completable Futures](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) seem promising, as do tools like [RxJava](https://github.com/ReactiveX/RxJava).

_Basic Challenge_: Update the Admin service so that it's call to Chairfront is now done asynchronously
_Advanced Challenge_: Write an Integration test to prove it.
