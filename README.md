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


## Current Situation: 02 - Making More Robust Consumers And Scaling Workloads

Great news! Work is coming along in our Decoupling Journey. We know have both Chairfront and Chairhouse listening for Create and Update messages regarding Chair types which are asynchronously being written to Kafka topics by the Admin service. Yes, that means there is now an 'update' controller endpoint in Admin that one can use to manipulate an existing Chair.

Additionally, our engineers have noticed that they created a good deal of repeatable code. They've extracted some of this into a new, shared library called `chair-messages-lib`. _Note_: new users will need to get in the habit of "publishing to maven local" (`./gradlew pTML`) to make use of it. This library is meant to force a common approach to communication (e.g. set headers in Kafka messages) AND contain common message classes. We're excited to see where this goes!


### Scenario:

Even though we've just entered into the world of Kafka Messages, Publishers, and Consumers... our team is getting nervous. Things seem fine for now with our two messages, but concerns are being raised. Is it true that Messages can be re-delivered? What is this 'Idempotent' word we've heard about? An engineer just asked me if they could change the name of a Message; is that a good idea?

As an added concern, our warehouse just received a large shipment of instances of Chairs. This would normally be good news but 1) we forgot to add pricing details on each record and 2) our Chairfront app doesn't know about this inventory! This process would take a while if we did it in one big push, can we somehow make use of Messages here?

### Task at hand:


_Basic Challenge_: Let's make our Message Consumption more robust. We're very worried about processing messages more than once, and are concerned about potential Message versioning.

1. Devise an approach for Chairhouse and Chairfront such that they would ignore / skip Messages for Chair types that they've already seen before. There exists base code to start down the path of using `version`, but other approaches are valid. 
1. Create a design for 'aliasing' Messages. Currently, the shared library is relying on the `className`  of the Message. This is straightforward, but also very brittle. What happens if someone renames a Message or moves it within packages? A better solution is to create one or more aliases for individual shared Message classes. There are multiple ways to accomplish this, including a hard-coded lookup table or declarative discovery on application startup (java annotations are good for this, can be discovered during a classpath scan). Note: this will almost certainly involve updating the shared library such that the other apps can make use of your aliasing.
1. Bonus Points: explore expanding your Message Consumer classes such that they map the shared Message class into some internally understand class unique to the Service.

_Advanced Challenge_: Uh Oh! We've discovered that we have over a hundred chairs in our inventory currently and they're all priced at $0. Why do we have individual prices set for each inventory item? Shhhhh just ignore that for now. Anyway, we'll need to go through the process of updating all of the prices for the Inventory items for a given Chair. We unfortunately cannot just use a single SQL update right now, we're forced to call into our (simulated) Pricing Service by utilizing the `event.club.warehouse.repositories.ExternalPricingRepository`, which will take a second or two to function. It also doesn't support batch so we have to go one at a time. 

While we work with that team to fix how pricing works, we unfortunately need to do the following: 

1. Create an entry point into ChairHouse which will 'kick off' the work. We need to discover all of the Inventory `Serials` for a given chair type (we can use `event.club.warehouse.services.InventoryManagementService#loadAllSerialsForChair`).
1. Using these serials, publish messages - either singularly or in batches - to a Topic consumed just by ChairHouse. In effect, chairhouse is publishing to itself.
1. Update the Consumer to read these Messages, load the Inventory items by their `serial` value, and update the price using the `event.club.warehouse.services.InventoryManagementService#recalculatePrice` method. This will persist the inventory as well.

This effectively breaks up a long-running task (recalculating pricing) into small, stateless chunks which can picked up and worked on by any instance of ChairHouse. _Bonus Points_: scale up ChairHouse before running.






