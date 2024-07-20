# CrossServerCore
Cross Server Core aka CSC

## What is Cross Server Core ?
Cross Server Core is a minecraft mod that enables cross server communication between minecraft servers. It uses RabbitMQ for reliable direct data transfer and MongoDB for delayed data transfer and data storage. It really start to be useful when you have these multiple minecraft servers connected on a proxy. 

## What is it used for ?

In my case I use it to enable player transition between different server instances. I can with this mod transfer player inventories, states. I can also create a home and tp system between servers. I use it to synchronize other mods like FTBTeams, FTBQuests, Waystones

## Setup

### 1. Install RabbitMQ and MongoDB on your server.
> For the setup of Cross Server Core, you need to have [RabbitMQ](https://www.rabbitmq.com/) and [MongoDB](https://www.mongodb.com/) installed on your server.

### 2. Get a Cross Server Core jar file.
> You can find recent versions of Cross Server Core on the [maven](https://maven.modcraftmc.fr/#/releases/fr/modcraftmc/cross-server-core).

### 3. Put the jar file in your mods folder.
> isn't it obvious ?

### 4. Edit the configuration file.
> You can find the configuration file in the `config` folder of your server (it will only be created if you launched the server at least 1 time). The configuration file is named `cross-server-core-config.toml`.
> 
> Here is a configuration file explanation:
> ```toml
> server_name = <name> # The name of your server (each server must have its own unique name) ex: "server1"
> 
> [rabbitmq]
> host = <host> # The host of your RabbitMQ server ex: "localhost" || "127.0.0.1"
> port = <port> # The port of your RabbitMQ server ex: 5672
> username = <username> # The username of your RabbitMQ server ex: "admin"
> password = <password> # The password of your RabbitMQ server ex: "12345678"
> vhost = <vhost> # The vhost of your RabbitMQ server ex: "cross-server-core-vhost"
> 
> [mongodb]
> host = <host> # The host of your MongoDB server ex: "localhost" || "127.0.0.1"
> port = <port> # The port of your MongoDB server ex: 27017
> username = <username> # The username of your MongoDB server ex: "admin"
> password = <password> # The password of your MongoDB server ex: "12345678"
> database = <database> # The database of your MongoDB server ex: "cross-server-core"
> ```

5. Launch your server.
> You can now launch your server and add any other mods that are dependent on Cross Server Core.
