## Toy Trade Capture System
A trade capture system is responsible for booking (recording) trades executed in the front office. It captures trades
from various sources, such as electronic trading platforms and manual entry etc, and records details of every trade,
including the instrument traded, price, and quantity. Trade capture forms the backbone of an investment bank, supporting
downstream activities, such as risk management, validation and confirmation, clearing and settlement, accounting and 
more.

This project presents a simplified version of a trade capture system in a microservices architecture, exploring various 
technologies such as Docker and Kubernetes.


### Prerequisite
The system is deployed as Docker containers, with container orchestration by Kubernetes optionally.
- Containerization: Docker Desktop.
- Container orchestration: Docker Desktop, `minikube` and `kubectl`.


### Quick Start
1. Clone the repository.
2. For the `CommonLibrary` directory, run `mvn install` to install the library for other microservices. It contains some
domain models used by several microservices.
3. For other microservices (`BookingService`, `Gateway`, `TradeGenerator`, `ViewService`), run `mvn package` to obtain 
JAR files for building docker images. Skip the test stage if tests failed.

#### Docker Containerization only
1. To deploy the application as containers without Kubernetes, run the following command in the root directory to start 
the application:
```
docker compose up
```
The API gateway is exposed in `localhost:8080` for external access to the trade capture system.
2. After a while, you may query the positions of a given trader book by HTTP GET request to 
`http://localhost:8080/position-view-service/getPositions?bookIds=<trader_books>`. For example,
`http://localhost:8080/position-view-service/getPositions?bookIds=book1,book2`.
3. To stop all containers:
```
docker compose down
```

#### Docker Containerization with Kubernetes Orchestration
Except for `activemq`, local images are used for containerization, as specified in Kubernetes configuration files. 
This is because the author does not want to upload the Docker images to Docker Hub.
- Refer to [here](https://betterstack.com/community/questions/how-to-use-local-docker-images-with-minikube/) for more.

1. Build the Docker images for each microservices (`mysql`, `BookingService`, `Gateway`, `TradeGenerator`, 
`ViewService`). This can be achieved either by (1) following the steps in the above section 
**Docker Containerization only**, or (2) running several `docker build ...`s:
- In `docker/mysql/`, run `docker build -t trade_capture-mysql .`.
- In `BookingService/`, run `docker build -t trade_capture-booking-service .`.
- In `Gateway/`, run `docker build -t trade_capture-gateway .`.
- In `TradeGenerator/`, run `docker build -t trade_capture-trade-generator .`.
- In `ViewService/`, run `docker build -t trade_capture-view-service .`.

2. Start the `minikube` cluster:
```
minikube start --cpus 4 --memory 4608
```
3. Upload the images to the cluster:
```
minikube image load <image_name>
```
4. In the root directory, deploy the application:
```
kubectl apply -f k8s/
```
5. To monitor the cluster:
```
kubectl get all
---
or
---
minikube dashboard
```

6. Due to the limitation of `minikube` with a `docker` driver, `NodePort` services cannot be accessed directly via 
`localhost`. Instead, a minikube tunnel has to be established by `minikube service <service-name> --url`. To access the 
API gateway:
```
minikube service gateway --url
```
- Refer to [here](https://stackoverflow.com/questions/66607112/minikube-on-wsl2-windows-10-minikube-ip-not-reachable) 
for more information.

7. Suppose `gateway` is exposed in `http://127.0.0.1:37581`. To query the positions, place a HTTP GET request to 
`http://127.0.0.1:37581/position-view-service/getPositions?bookIds=book1,book2`.
8. Stop the application and delete all containers: 
```
kubectl delete all --all
```
9. Stop the cluster:
```
minikube stop
```


### System Architecture
![System architecture](/resources/Architecture.png "System architecture")
**TODO: Improve and upload database schema**


### A Closer Look at Docker
#### Dockerfile and Docker Compose
A `Dockerfile` is responsible for building a docker image. It is like a recipe instructing about the steps of building
an image. There is one `Dockerfile` per microservice, except for services that directly use image available on Docker
Hub.

A `docker compose` file is a tool for defining and running multi-container applications. It simplifies the management of
resources, such as volumes and networks, used by containers. It also allows defining the order of starting containers.
For one, a backend Spring Boot service may depend on a MySQL database container, meaning that the backend service starts
after the database container starting to run and accept connections. In this situation, `depends_on` and `healthcheck`
comes in handy to define such a condition. The `healthcheck` test can be configured to execute certain commands once in
a while, and the backend service container can be configured to start only if the database container is healthy.

#### Networking
There are two types of networking that is worth attention: (1) Communication from host to docker containers and
(2) communication between containers.

##### Communication from Host to Container
In essence, we want to expose an application running within a container, such that it is accessible by requests from the
host (the machine running Docker with containers deployed, like our PC). This can be achieved by [publishing container 
ports](https://docs.docker.com/guides/docker-concepts/running-containers/publishing-ports/), which sets up a forwarding 
rule on the host machine (from host to container) but breaks networking isolation. This can be applied to expose the API
gateway endpoints of the system to the outside traffic.

Alternatively, using a [host network driver in a docker network](https://docs.docker.com/network/drivers/host/) also 
sounds feasible.

On CLI, when initializing a container, ports can be published by:
```
docker run -p <host_port>:<container_port> --name <container_name> <image_name>
```

For one, `docker run -p 9999:8080 --name my_gateway trade_capture-gateway` creates a new container named `my_gateway` 
from the image `trade_capture-gateway` and enables forwarding requests to host machine at port `9999` to `my_gateway`'s 
port `8080`. This means requests to `localhost:9999` on the host is forwarded to `localhost:8080` in `my_gateway`.

Or, in a `docker compose` file, port publishing can be configured by the `ports` section under each service.

Note that in a `Dockerfile`, the `EXPOSE` instruction is only used to indicate the packaged application will use the 
specified port. It does not publish the port by default. So one has to publish the ports manually as above.

##### Communication between Containers
Communication between containers cannot be achieved directly by the method in the previous section 
**Communication from Host to Container**, because the `localhost` in each container is different from the `localhost` on 
the host, meaning that traffic will not be forward to other containers by default. Also, publishing container ports to 
the host only for internal communication between containers does not sound a good practice and may lead to security 
threats.

Instead, communication between containers may rely on [docker network](https://docs.docker.com/network/). By default, 
docker containers run in a `default, bridge` network. In this project, the author defines a custom `bridge` network to 
isolate from the `default` network namespace.

When a container is attached to the `default, bridge` network, it inherits the DNS settings in the `/etc/resolv.conf` 
file on the host. When a container is attached to a custom network, the container's `/etc/resolv.conf` file will contain
an entry of Docker's embedded DNS server. The embedded DNS server in Docker answers name resolution queries from Docker 
containers. It creates a DNS record for each container using the container name as hostname and the IP address of it as 
value.

As a result, this enables containers in the same network resolve container name into the corresponding IP 
address. In other words, containers in the same network can communicate with each other by container names, and the 
destination port number remains the same as the port bind with the application by the destination container. Check out 
[here](https://dev.to/pemcconnell/docker-networking-network-namespaces-docker-and-dns-19f1) to understand more.


### A Closer Look at Kubernetes
#### Pod, ReplicaSet, Deployment, and Service
A `Pod` is the smallest and simplest unit in the Kubernetes object model. It represents a single instance of a running 
process in your cluster. A pod can contain one or more containers, and it manages the resources and networking for those
containers. Pods are ephemeral resources and should not be expected to be reliable and durable.

A `ReplicaSet` is a higher-level abstraction that ensures a specified number of Pod replicas are running at any given 
time. It provides a way to scale Pods up or down, and it automatically replaces Pods that fail or are deleted.

A `Deployment` is a higher-level resource that manages the deployment and scaling of ReplicaSets. It provides declarative 
updates for Pods and ReplicaSets, allowing you to describe the desired state of your application. Deployments handle 
rolling updates, rollbacks, and other life cycle management features for your application.

A `Service` is an abstraction that defines a logical set of Pods and a policy by which to access them. It provides a 
stable network endpoint for a group of pods, which can be accessed by traffic either inside or outside the cluster.
Services can also perform load balancing, service discovery, and traffic routing for the Pods they target.
- `ClusterIP` is a type of Service that enables communication from pods in the cluster.
- `NodePort` is a type of Service that enables communication initiated outside the cluster (but in the same network as the
cluster). Note that `NodePort` builds on top of `ClusterIP`, which means that it can also capture internal traffics.

#### Networking
In Kubernetes, a `Service` is a method for exposing a network application that is running as one or more `Pods` in the 
cluster.

##### Communication between Pods
Setting up the `ClusterIP` service makes the underlying pods accessible from within the cluster. Kubernetes will expose 
the service on a cluster-internal IP, and traffics can be forwarded to the pods by calling the service name directly 
(name resolution).

##### Communication from the Host of the Cluster (Outside the Cluster)
A `NodePort` service can be configured to enable communication with pods outside the cluster. The Kubernetes control 
plane allocates a port, and each node proxies that port into the specified service. meaning that the nodes will redirect
traffics going to that node to the underlying pods of the specified service. For example, if Kubernetes exposes port 
`30300` as a `NodePort` service, then any traffic sending to `localhost:30300` from the machines running the nodes 
will reach the underlying pods. Or, traffic directing `<node_ip>:30300` from machines that are on the same network as 
the cluster can reach the underlying pods as well.

However, the situation is a bit more complex when using `minikube` as the cluster. By default, `minikube` uses `docker` 
as the driver, and the behavior of `minikube start` is to create a Docker container that runs the cluster internally. 
This means requests to `localhost:<node_port>` sending from the host of Docker cannot reach to the cluster, because the 
host and the cluster (or the minikube container) are not running in the same network. To deal with this, one may 
consider using `SSH` to create a tunnel between the two. Or, one may run `minikube service <service_name> --url` to get 
a designated URL accessing the service as a workaround.

Alternatively, one may also consider using a `LoadBalancer` service or an `Ingress` controller in production.

##### Different Types of Ports Involved
There are several fields in the `deployment` and `service` configuration files related to ports.
1. In `deployment`, under `spec > template > spec > containers > ports`, there are fields for `containerPort`, which are 
purely informational.
2. In `ClusterIP service`, under `spec > ports`, there are `port` and `targetPort`. `port` is the port exposed within 
the container or pod, where the underlying application is bind with. `targetPort` is the port exposed by this service, 
such that internal traffics directing to `<service_name>:<target_port>` will be redirected to the pod's `port`, the 
application.
3. In `NodePort service`, under `spec > ports`, there are `port`, `targetPort`, and `nodePort`. For the first two, 
ditto. For `nodePort`, it is the port exposed on every node, such that traffic directing to `<node_ip>:<node_port>` will
be redirected to the underlying pods.

#### Compute Resource Allocation
On the Docker image level, we set `-XX:MaxRAMPercentage=70.0` such that the JVM's heap memory can acquire at most 70% of
the total system memory available to the pod. On top of that, the resource limits for CPU cores and memory are set for 
each pod at `deployment`. Refer 
[here](https://medium.com/@sharprazor.app/memory-settings-for-java-process-running-in-kubernetes-pod-1e608a5d2a64) 
to understand more.

#### Using Local Images
By default, `deployment` objects look up for images from Docker Hub, but it is also possible to configure it to look up 
for images in the local repository. Follow 
[this guide](https://betterstack.com/community/questions/how-to-use-local-docker-images-with-minikube/) to understand 
more. It requires building the image on the host, uploading the image to the cluster, and configuring the deployment 
with `imagePullPolicy: Never`.