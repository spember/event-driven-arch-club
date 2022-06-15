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
1. build the library and service repos (in `terminal window 2`: cd into the sub folders and `./gradlew build`, start with `chair-messages-lib`). Alternatively, a bash script will do this for you: in the same location as this file, run `./build-repos.sh` -> you may need to `chmod +x` it first.
1. build the docker images: `docker build -t eventclub/chair-admin admin/.` and `docker build -t eventclub/chairfront chairfront/.` _Note_: ensure you've run the eval command above, first. This scopes your terminal window to use Minikube's docker environment and not your laptop's. This also means that builds of the apps should be done in another terminal (`terminal window 2`).
1. `kubectl apply -f chairfront/kubernetes.yaml`
1. `kubectl apply -f chairhouse/kubernetes.yaml`
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

## Current Situation 03 - Query Models & CQRS

Alright, things are starting to hum along here at the Chair Company. We've got some messages firing on Kafka, and we can asynchronously recalculate pricing on our Inventory. Inventory, in this case is the individual, actual 'instances' of Chairs sitting in our warehouse with their own serial numbers, lifecycles, etc.

Hmm... Inventory. Now that you mention it, we should probably start having Inventory reflected in Chairfront. Our customer's cannot buy anything, as Chairfront always reports 0 inventory for our Chairs. That's a problem.

### Task at hand:

This week we're going to update Chairfront such that it is a customer-facing, downstream recipient of messages from _both_ Admin and Chairhouse. This will effectively make it a Query Model, whose state is derived from the actions which occur in our "Source of Truth" systems.


_Basic Challenge_: 
* Update Chairhouse such that it can accept Commands to Receive new inventory or Ship existing inventory to customers, and then publish Messages about what's happening. 
* One may want to create a new Kafka Topic for inventory.
* Update Chairfront to take not of these messages and adjust the inventory accordingly.
* Chairfront, at this point, is just a big Query Model for data coming from Admin and Chairhouse. This will change next time!


_Advanced Challenge_

Chairfront is listening for messages from Chairfront, but due to the nature of the Kafka journal, it may be that we re-read the journal and end up with messages more than once. This is a problem, as the Inventory's `revision` is specific to the Inventory, not the Chair. Because Chairfront is treating inventory count as a static field on the Chair (and not tracking individual inventory objects like the warehouse), the Domain Driven Design term is that Inventory is a Value Object at best from Chairfront's perspective. This makes 'replays' tricky.

* ___Investigate and Implement a solution to deal with this!___ There are few techniques to alleviate this; for example: Chairfront could track updates by inventory item for a short while, or it could routinely (say, every hour) check the current inventory state with Chairhouse.





