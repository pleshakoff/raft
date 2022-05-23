# RAFT
## Raft Consensus Algorithm implementation for K-V storage 

1. [Description](#desc)
2. [Get started](#get-started)
3. [Examples](#examples)


<a name="desc"></a>
## Description.
[Russian version](https://github.com/pleshakoff/raft/blob/master/README_RUS.md).


My article in the Habr magazine (in russian) https://habr.com/ru/company/otus/blog/495356/

![alt text](https://github.com/pleshakoff/raft/blob/master/RAFT.png?raw=true"")

Distributed (Consistency + Partition tolerance) system for key-value data storage.

The RAFT consensus algorithm has been designed for data replication and data consistency maintaining.
The algorithm itself is not described in the current document, you can read about it in the specification https://raft.github.io/raft.pdf
My RAFT model is implemented on Java+Spring Boot.
 
The system consists of two modules for two types of nodes: client and server.
You can deploy an unlimited number of instances of the server and the client.
In the current configuration you can run 3 server-nodes (to simplify problems emulation) and 1 client-node. 

### Server

https://github.com/pleshakoff/raft/tree/master/server

There are three nodes with the IDs: 1,2,3.

You can use Swagger for work with API(described in [API](#api))

#### Description 

A server node has three states:
* Follower. Accepts read requests from the client. Takes a heartbeat from the leader.
* Candidate. Accepts read requests from the client. Sends vote requests to other nodes.
* Leader. Accepts read and write requests. Sends heartbeat requests to other nodes.
Sends data append requests to other nodes.

Each server node provides access to the transaction log storage.
The log storage contains sequentially recorded data changing operations.

Also, each server node has access to the database with the data records.
  
Each node has its own database and log.

The current implementation uses embedded in-memory solutions for both the log and the database.
(Concurrent Map and List)
If necessary, you can implement the interfaces to support other types of storage.

Log data (operations) is replicated by the leader to other nodes. After confirmation of the operation receiving by the majority of nodes,
the operation applies by the state machine, and the data is put into the database.
After that, the infornmation, that the operation applied by the leader, sent to other nodes, and they apply it on their databases.

The server node provides data exchange with other nodes. Two types of requests are supported.
* Vote. Used in a process of a voting round.   
* Append aka heartbeat (empty data). Used to replicate the log data to followers and prevent a new round of voting from starting.

There are two types of timers running on the server:
* Vote. Used to start a voting round. Restarts when a heartbeat is received from the server.
Timeout configured separately for each server. In the current configuration: 5 seconds, 7 seconds, 9 seconds
* Heartbeat. Used to send the append-request to followers. In the current configuration the timeout is 2 seconds.

If a node does not receive a heartbeat and the voting timer has expired, the node becomes a candidate and
initiates elections, increments the voting round number, and sends vote requests to other nodes.
If the node gains the most votes, then it becomes the leader and starts sending heartbeats.

It is possible to use API to stop nodes to emulate the node shutdown without containers shutdown.    
If the node is stopped, it doesn't send any requests, and it is not available ti other nodes and for the client.
But it's possible to make a request to the log and database, which is very convenient for studying the behavior of the cluster
in an emergency situation. There is also a backdoor for inserting data into the log, which is useful for emulating the situation
when the leader does not know that he is cut off from the cluster and continues to receive data. 
Read more in [examples](#examples)  
     
     
<a name="api"></a>            
#### API 

Node #1:  http://localhost:8081/api/v1/Swagger-ui.html

Node #2:  http://localhost:8082/api/v1/Swagger-ui.html

Node #3:  http://localhost:8083/api/v1/Swagger-ui.html
   
The following groups of methods are available in the API: 

* Context. Gets node metadata. Stop/Start node 
* Log. CRUD for the log.   
* Storage. Reads data from the database.
* Replication. Endpoint for the append/heartbeat requests
* Election. Endpoint for vote requests

#### Implementation


Packages:

* [node](https://github.com/pleshakoff/raft/tree/master/server/src/main/java/com/raft/server/node). 
Node metadata. Voting round, last applied operation index, other nodes data, etc. 
* [election](https://github.com/pleshakoff/raft/tree/master/server/src/main/java/com/raft/server/election). 
Voting timer. Service for sending and processing vote requests.  
* [replication](https://github.com/pleshakoff/raft/tree/master/server/src/main/java/com/raft/server/replication). 
Heartbeat timer. Service for sending and processing append requests.  
* [operations](https://github.com/pleshakoff/raft/tree/master/server/src/main/java/com/raft/server/operations). 
Interface for accessing the operation log. Log in memory implementation. Service for log operations.  
* [storage](https://github.com/pleshakoff/raft/tree/master/server/src/main/java/com/raft/server/storage). 
Interface for accessing the database. Database in memory implementation. Service for database operations.
* [context](https://github.com/pleshakoff/raft/tree/master/server/src/main/java/com/raft/server/context). 
Facade for easy access to node metadata. 

  
### Client

https://github.com/pleshakoff/raft/tree/master/client

In the current configuration, it starts as a single instance.
You can use Swagger (for more details, see the [API](#apiclient) section)

#### Description 

The client sends requests to the server.
It can collect metadata from the entire cluster and show available nodes and their states.

The client discovers the leader node and redirects the write request to it. 
The client can read data from any node.
In the current implementation, the client does not use a balancing algorithm to select an available node for a read request. 
The main goal is to give opportunity to test behavior of different nodes.    

In the current implementation, the client, does not wait for confirmation from most nodes,
as required by the specification. It just sends the request asynchronously.
      
<a name="apiclient"></a>            
#### API 

Client:  http://localhost:8080/api/v1/Swagger-ui.html
   
The following groups of methods are available in the API: 

* Context. Getting the metadata of the entire cluster. Stop/start nodes. Get leader id. 
* Log. View the log.
* Storage. CRUD for working with the database.  

#### Implementation 


Packages

* [exchange](https://github.com/pleshakoff/raft/tree/master/client/src/main/java/com/raft/client/exchange). 
Service for getting server node metadata   

API for redirecting requests to nodes.   

<a name="get-started"></a>
## Get started 

There is [docker-compose.yml](https://github.com/pleshakoff/raft/blob/master/docker-compose.yml) at the root of the repository. 
It must be launched, three server nodes and a client will be up.
` docker-compose up`

After starting the nodes will choose a leader, and in 5 seconds the cluster will be ready to work.

##### Swagger 

Node #1:  http://localhost:8081/api/v1/Swagger-ui.html

Node #2:  http://localhost:8082/api/v1/Swagger-ui.html

Node #3:  http://localhost:8083/api/v1/Swagger-ui.html

Client:  http://localhost:8080/api/v1/Swagger-ui.html

GET requests can be made directly from the browser.
For example, you can get the state of the nodes at the link: http://localhost:8080/api/v1/context

##### Vote timeout 

Node #1:  5 seconds 

Node #2:  7 seconds

Node #3:  9 seconds

You can config timeouts in the docker-compose.yml


##### Logs 

`docker-compose logs -f raft-server-1 `

`docker-compose logs -f raft-server-2`
 
`docker-compose logs -f raft-server-3` 

If you want see more detailed, just uncomment debug profile setting for "command" tag in the docker-compose.yml file

`--spring.profiles.active=debug`   

Example: 
`command: --raft.election-timeout=5 --raft.id=1 --server.port=8081 --spring.profiles.active=debug
`

<a name="examples"></a>
## Examples

Here you can find test scenarios for the cluster.  

All examples must be executed by the Swagger of the client node.
All the same can be done by calling the server nodes directly, but it is more convenient to do this by the client. 

When a node is disabled via the API, CRUD is not available. But the operation of viewing the log is not blocked,.

<a name="election"></a>
### Election 

Let's check how the cluster behaves when the leader is lost.

Get the leader ID http://localhost:8080/api/v1/context/leader

For example, it's equal to 1

Disconnect the leader from the cluster.
   
**POST** http://localhost:8080/api/v1/context/stop?peerId=1

Get the data of the entire cluster http://localhost:8080/api/v1/context

We see that the new leader was selected, and the old leader is still the leader, but it is disabled (active: false)

Disable next leader **POST** http://localhost:8080/api/v1/context/stop?peerId=2

Attempts to get the leader ID failed, because there is no quorum in the cluster http://localhost:8080/api/v1/context/leader

Restore the nodes, and look who won.

**POST** http://localhost:8080/api/v1/context/start?peerId=1

**POST** http://localhost:8080/api/v1/context/start?peerId=2

<a name="normal"></a>
### Regular replication 

Checking replication

Вставляем,удаляем,редактируем данные через ендопоинты группы storage
Insert, delete, edit data by using storage API 

For example 
**POST** "http://localhost:8080/api/v1/storage 

`{
  "key": 5,
  "val": "test data"
}
`
We read data from the database from different nodes.
For example for the second node: http://localhost:8080/api/v1/storage?peerId=2
 
### Missed nodes   

Add data as described in the paragraph [Regular replication](#normal)

Then stop the nodes as described in [election](#election) 
 
Add data, start/stop nodes.

Then check that all the nodes has been synchronized.

http://localhost:8080/api/v1/storage?peerId=1
     
http://localhost:8080/api/v1/storage?peerId=2
     
http://localhost:8080/api/v1/storage?peerId=3     


### Leaders conflict

We emulate the situation when the leader disconnected from the cluster and still considers himself the leader and continues to receive data.
At this time, the cluster has chosen a new leader, and also continues to receive data.

First you need to disable the leader and add data to his log.

Disable the current leader as described in [election](#election).
You cannot add data to a disabled node through a client node.
Therefore, we connect directly to the server node that we have disabled.
For example, if the leader is 1 then http://localhost:8081/api/v1/Swagger-ui.html (id = last digit of the port)

You can also use a special method that allows you to add data to the log of a disabled node.

**POST** http://localhost:8081/api/v1/log/sneaky
{
  "key": 1000,
  "val": "BAD DATA"
}   


Still call the server node directly

Make sure that the data is in the log http://localhost:8081/api/v1/log

Check that this data did not get from the log to the database for this node, because there is no quorum http://localhost:8081/api/v1/storage

Now we add data to the cluster through the available leader. This time, through the client node, as described in [regular replication](#normal)

Thus, we got two leaders, both with data, one has data replicated and confirmed by the majority,
the second one doesn't.

Start the disconnected leader and check its log and storage, there should be correct data from the cluster
 
http://localhost:8080/api/v1/log?peerId=1

http://localhost:8080/api/v1/storage?peerId=1


 




  
 
 
