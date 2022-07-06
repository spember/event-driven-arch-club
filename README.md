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

## Current Situation 04 - Saga Pattern

Great progress! Our warehousing app can now handle receiving and reserving new inventory. Our customer-facing app, Chairfront, now listens for messages coming from ChairHouse in order to update it's inventory counts.

Now, though, we need to start taking Orders. Our engineers have warned us that we need to spend some time and make the Order Processing operate asynchronously via messages, in order to handle any anticipated scale. This basically means that we can't put all of the operations inside of Chairfront. So what do we do!?

### 

Let's implement a Saga. A Saga is - basically - constructing a sequential pipeline of steps between microservices, where the messages emitted signal "It's OK to proceed with the next step", or "there was an error with this Saga". Participants in the saga listen just for messages for the step they belong to, or errors. Other messages may be seen but are discarded / 'accepted and ignored'. All Participants know how to roll back their piece of the Saga, if appropriate, on an error. Saga Messages tend to be either _named_ using their stage (e.g. `ProcessOrderStage2`) or have the stage encoded as a header.

We have several steps we need to accomplish in our Order Processing Saga:

1. Receive payload from Customer, perform minor validation (i.e. ensure fields are not empty)
1. Reserve an inventory item from the Warehouse
1. Process Payment* 
1. Send Confirmation Notification*
1. Ship the reserved inventory (in reality this might occur after some time, but our warehouse folks are very fast)

> * note that, as a toy app, some of these steps should be simulated


Normally we might create an Order Service to track the Saga. For the sake of time, we'll overload Admin service's responsibilities to handle this, for now.

_Basic and Advanced Challenge_

A big one, this time. This practice will involve the following steps, in no particular order:

* Create Endpoint in Chairfront to receive an 'Order' payload from the Customer. (Chairfront, after all, is the Customer-facing portion). This stage should also generate an ID for the Order (in reality we might have some 'ticketing' or Order ID generation endpoint to call)
* Add Saga messages in the shared library
* Add Order entity to Admin
* Publish Messages according to the stages, listed above
* Create consumers to operate on each stage
* Add consumers in Admin which update the progress of the Order on each message (Observability!)

