# CrossServerCore
Cross Server Core aka CSC

## What is Cross Server Core ?
Cross Server Core is a minecraft mod that enables cross server communication between minecraft servers.
It uses RabbitMQ for reliable direct data transfer and MongoDB for delayed data transfer and data storage.
It really start to be useful when you have these multiple minecraft servers connected on a proxy. 

## What is it used for ?

In my case I use it to enable player transition between different server instances.
I can with this mod transfer player inventories, states.
I can also create a home and tp system between servers.
I use it to synchronize other mods like FTBTeams, FTBQuests, Waystones

# Installation

## Mod Setup

### 1. Install RabbitMQ and MongoDB on your server.
For the setup of Cross Server Core, you need to have [RabbitMQ](https://www.rabbitmq.com/) and [MongoDB](https://www.mongodb.com/) installed on your server.

### 2. Get a Cross Server Core jar file.
You can find recent versions of Cross Server Core on the [maven](https://maven.modcraftmc.fr/#/releases/fr/modcraftmc/cross-server-core).

### 3. Put the jar file in your mods folder.
isn't it obvious ?

### 4. Edit the configuration file.
You can find the configuration file in the `config` folder of your server (it will only be created if you launched the server at least 1 time). The configuration file is named `cross-server-core-config.toml`.

Here is a configuration file explanation:
```toml
server_name = <name> # The name of your server (each server must have its own unique name) ex: "server1"

[rabbitmq]
host = <host> # The host of your RabbitMQ server ex: "localhost" || "127.0.0.1"
port = <port> # The port of your RabbitMQ server ex: 5672
username = <username> # The username of your RabbitMQ server ex: "admin"
password = <password> # The password of your RabbitMQ server ex: "12345678"
vhost = <vhost> # The vhost of your RabbitMQ server ex: "cross-server-core-vhost"

[mongodb]
host = <host> # The host of your MongoDB server ex: "localhost" || "127.0.0.1"
port = <port> # The port of your MongoDB server ex: 27017
username = <username> # The username of your MongoDB server ex: "admin"
password = <password> # The password of your MongoDB server ex: "12345678"
database = <database> # The database of your MongoDB server ex: "cross-server-core"
```

### 5. Launch your server.
You can now launch your server and add any other mods that are dependent on Cross Server Core.

## Proxy extension setup [Optional]
> (Require step 1 of the mod setup )

This project also contains a Velocity plugin that can be used to transfer players between servers. 
To use it, you need to install the plugin on your proxy server.

### 1. Get a Cross Server Core Proxy Extension jar file.

Well... currently I don't know how you can get one, I don't know how to manage the different version of this plugin so I don't know how to distribute it.
I will try to find a solution for this. You still can compile one by yourself but if you are really interested in this project, you can open a simple issue and I'll find a solution.

### 2. Put the jar file in your plugins folder.

You know what to do...

### 3. Edit the configuration file.

You can find the configuration file in the `plugins/crossservercoreproxy` folder of your server (it will only be created if you launched the proxy at least 1 time). The configuration file is named `config.toml`.

Here is a configuration file explanation:
```toml
[rabbitmq]
host = <host> # The host of your RabbitMQ server ex: "localhost" || "127.0.0.1"
port = <port> # The port of your RabbitMQ server ex: 5672
username = <username> # The username of your RabbitMQ server ex: "admin"
password = <password> # The password of your RabbitMQ server ex: "12345678"
vhost = <vhost> # The vhost of your RabbitMQ server ex: "cross-server-core-vhost"
```

# Having an issue ?

Feel free to open an issue on this repository, I will try to help you as much as I can.

You can also join the discord server [here](https://discord.gg/7Q5q3Q7) and contact us (It is primarily a minecraft server but just mention the name 'CrossServerCore' and I'll try to help you).

# API

### Adding Cross Server Core API to your project.

Into your `build.gradle` file, add the following lines:
```gradle
repositories {
    maven { 
        url = 'https://maven.modcraftmc.fr/releases'
    }
}

dependencies {
    implementation('fr.modcraftmc:cross-server-core:x.x.x:api') // replace with x.x.x the version of the Cross Server Core
}
```

### Creating a message.

Messages are the way to communicate between servers (and even on same server but forge events are already here for this). 
You can create your own message by extending the `BaseMessage` class.
It require almost zero boilerplate code (I couldn't find a way to remove the empty constructor, if you have an idea, please tell me) but I'am really proud of that.

```java
package com.example.project.message;

import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

import java.util.UUID;

@AutoRegister("transfer_data")
public class TransferData extends BaseMessage {

    @AutoSerialize
    public UUID anUUID;
    @AutoSerialize
    public String aString;
    @AutoSerialize
    public ISyncPlayer aPlayer;
    @AutoSerialize
    public ISyncServer aServer;

    private TransferPlayer() {} // this is very important

    public TransferPlayer(ISyncPlayer player, ISyncServer server) {
        this.anUUID = player.getUUID();
        this.aString = "hello";
        this.aPlayer = player;
        this.aServer = server;
    }

    @Override
    public void handle() {
        //Do something with these data
    }
}
```

Let's explain the code above:
- `@AutoRegister("transfer_data")`: This annotation is used to automatically register the message. The string parameter is the message name.
- `@AutoSerialize`: This annotation is used to automatically serialize the field. (Only some types are supported by default, you can add your own (see below). By default, the supported types are: `String`, `int`, `double`, `float`, `boolean`, `UUID`, `Component` (from minecraft), `ISyncPlayer`, `ISyncServer`, `ISyncPlayerProxy`, `ISyncServerProxy`, `JsonElement`, `List`, `Optional`).
- `private TransferPlayer() {}` is very important because the mod uses reflection to create the message and it needs a default constructor (it can be private).
- `public void handle()`: This method is called when the message is received. `@AutoSerialize` fields are automatically filled with the data received.

### Sending a message.

To send a message you have to use the CrossServerCoreAPI.

```java
public void functionThatSendAMessage(ISyncPlayer player, ISyncServer server) {
    TransferData message = new TransferData(player);
    CrossServerCoreAPI.sendCrossMessageToAllOtherServer(message); // Send the message to all other servers
}
``` 

This code will send the message to all other servers. 
you can also send to all servers with `CrossServerCoreAPI.sendCrossMessageToAllOtherServer(message)`. 

You can also send to a specific server with `CrossServerCoreAPI.sendCrossMessageToServer(message, serverName)` but you should use the method inside ISyncServer instead as it is cleaner.

```java
public void functionThatSendAMessage(ISyncPlayer player, ISyncServer server) {
    TransferData message = new TransferData(player);
    server.sendMessage(message); // Send the message to a specific server
}
``` 

### MongoDB collection access.

You can access ore create any collection in the MongoDB database.
After accessing the MongoDB collection you can use it as a normal MongoDB collection.

```java
package com.example.project.StoringSomething;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.sharedpersistentdata.ISharedDataStore;
import fr.modcraftmc.crossservercore.api.sharedpersistentdata.SharedDataStore;
import org.bson.Document;


public class StoringSomething {
    private ISharedDataStore myData = new SharedDataStore("myCollection");

    public void saveSomething(ISyncPlayer player) {
        String something = "Hello World";
        Document document = new Document("uuid", player.getUUID().toString());
        document.append("data", something);

        myData.accessOrThrow().insertOne(document);
    }

    public String loadSomething(ISyncPlayer player) {
        Document document = myData.accessOrThrow().find(Filters.eq("uuid", player.getUUID().toString())).first();
        return document.getString("data");
    }
}
```

to use MongoDB database you need to implement the java driver as following:
```gradle
dependencies {
    implementation (group: 'org.mongodb', name: 'mongodb-driver-sync', version: '[4.9.0]')
}
```

<img width="22%" src="https://media1.tenor.com/m/5C0BYM6rIQAAAAAd/as-shrimple-as-that-shrimp.gif" style="float: right; padding-left: 20px;">

### Transfer player between servers.

Transfering player is as shrimple as that.


```java
public void transferPlayer(ISyncPlayer player, ISyncServer server) {
    CrossServerCoreProxyExtensionAPI.transferPlayer(player, server);
}
```

### Adding custom serialization.

You can implement your own serialization for new types to be used in messages.
To do this, you need to create a class that extends the `FieldSerializer` class.

Here is the implementation of the `UUID` serialization:

```java
package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDSerializer extends FieldSerializer<UUID> {
    @Override
    public JsonElement serialize(UUID value) {
        return new JsonPrimitive(value.toString());
    }

    @Override
    public UUID deserialize(JsonElement json, Type typeOfT) {
        return UUID.fromString(json.getAsString());
    }

    @Override
    public Type getType() {
        return UUID.class;
    }
}
```

the field `typeOfT` contains the information on the type of the field being deserialized into.
It is specially useful when you have a generic type and you want to know the type of the generic parameters.
See the `ListSerializer` or `Optional` implementation for an example.

After creating the class, you need to register it.

```java[CrossServerCoreAPIImpl.java](forge-projects%2Fcross-server-core%2Fsrc%2Fmain%2Fjava%2Ffr%2Fmodcraftmc%2Fcrossservercore%2FCrossServerCoreAPIImpl.java)
public void onCrossServerCoreReady(CrossServerCoreReadyEvent event) {
        CrossServerCoreAPI.getMessageAutoPropertySerializer().registerFieldSerializer(new UUIDSerializer());
}
```

whith the function being registered inside Forge event bus. (`MinecraftForge.EVENT_BUS.addListener(this::onCrossServerCoreReadyEvent);`)

### Sync players

Sync players are used to represent players across servers.

#### ISyncPlayer

You can get an `Optional<ISyncPlayer>` from a UUID or a name with `CrossServerCoreAPI.getPlauer(uuid)` or `CrossServerCoreApi.getPlayer(name)`.
Optional will be empty if the player is not found.

You can also get all players with `CrossServerCoreAPI.getAllPlayersOnCluster()`.

Some events give you an ISyncPlayer.

ISyncPlayer contains the following methods:
- `UUID getUUID()`: Get the player UUID.
- `String getName()`: Get the player name.
- `ISyncServer getServer()`: Get the server the player is on.
- `ISyncPlayerProxy proxy()`: Get a proxy for the sync player.

Holding an ISyncPlayer in a variable for too long is not recommended, as if the player disconnects or changes server (wich mean the player will be disconnected for a short time), the ISyncPlayer will be invalidated and may throw an exception if the method `getServer()` is called.
For these cases, you can use the `ISyncPlayerProxy`

#### ISyncPlayerProxy

ISyncPlayerProxy is a proxy to the ISyncPlayer, it will never be invalidated.
However, getting the server from the proxy will throw an exception if the player is not connected.

You can get an ISyncPlayerProxy from an ISyncPlayer with the `proxy()` method. Or by using the following methods from CrossServerCoreAPI:

- `ISyncPlayerProxy getImmediatePlayer(UUID playerUUID, String playerName)`
- `ISyncPlayerProxy getImmediatePlayer(Player player)`

To check if the player is connected, you can use the `unproxy()` method that will return an `Optional<ISyncPlayer>`, then see if the optional is empty.

### Sync servers

Sync servers are used to represent servers across the cluster.

#### ISyncServer

You can get an `Optional<ISyncServer>` from a server name with `CrossServerCoreAPI.getServer(name)`.
Optional will be empty if the server is not found.

Some events give you an ISyncServer.

ISyncServer contains the following methods:
- `String getName()`: Get the server name.
- `sendMessage(BaseMessage message)`: Send a message to the server.
- `List<? extends ISyncPlayer> getPlayers()`: Get all players on the server.
- `ISyncServerProxy proxy()`: Get a proxy for the sync server.

As for ISyncPlayer, holding an ISyncServer in a variable for too long is not recommended, as if the server is stopped (less likely to happen), the ISyncServer will be invalidated and may throw an exception if the methods `getPlayers()` or `sendMessage(message)` are called.
For these cases, you can use the `ISyncServerProxy`

#### ISyncServerProxy

ISyncServerProxy is a proxy to the ISyncServer, it will never be invalidated.
However, getting the players or sending a message from the proxy will throw exceptions if the server is stopped.

You can get an ISyncServerProxy from an ISyncServer with the `proxy()` method. Or by using `CrossServerCoreAPI.getImmediateServer(name)`.

To check if the server is stopped, you can use the `unproxy()` method that will return an `Optional<ISyncServer>`, then see if the optional is empty.

### Events

Some events related to Cross Server Core are fired on the Forge event bus.

#### CrossServerCoreReadyEvent
This event is fired when Cross Server Core is ready to be used.
It does not contain any information, it just tells you that Cross Server Core is ready (but you may have guess it).

#### PlayerJoinClusterEvent
This event is fired across all servers when a player joins the cluster.

- `ISyncPlayer getPlayer()`: Get the player that joined the cluster.
- `isCurrentServer()`: Check if the event is fired on the server the player joined.

#### PlayerLeaveClusterEvent
This event is fired across all servers when a player leaves the cluster (You guess it, it is the opposite of join !).

- `ISyncPlayer getPlayer()`: Get the player that left the cluster. (The player will be invalidated son after this event)
- `isCurrentServer()`: Check if the event is fired on the server the player left.

#### TransferPlayerEvent
This event is fired when a player is transfered between servers.

- `ISyncPlayerProxy getPlayer()`: Get the player that is transfered. (A proxy is used to avoid invalidation during transfer)
- `ISyncServer getDestination()`: Get the server the player is transfered to.

### Commands

When creating commands, you may want to specify a SyncPlayer as a parameter.
I couldn't create a NetworkPlayerArgument as it is a server only mod.
(During v1 of this mod I created a client version of this mod just to have this argument working but the benefit was not worth having a client mod for this.)

So for this v2 I modified the NetworkPlayerArgument to be a static class that you can use in your own command.

In the command add a StringArgumentType.word() for the argument, add NetworkPlayerArgument::listSuggestions as a suggestion provider and use NetworkPlayerArgument.getNetworkPlayer(context, "argumentName") to get the player.

This is a part of code I created using Cross Server Core to teleport players between servers.

```java
protected void buildCommand() {
    COMMANDS.add(Commands.literal("tp")
        .then(Commands.argument("target", StringArgumentType.word())
            .suggests(NetworkPlayerArgument::listSuggestions)
            .executes(context -> tp(context.getSource(), NetworkPlayerArgument.getNetworkPlayer(context, "target")))
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests(NetworkPlayerArgument::listSuggestions)
                .executes(context -> tp(context.getSource(), NetworkPlayerArgument.getNetworkPlayer(context, "target"), NetworkPlayerArgument.getNetworkPlayer(context, "player"))))
        ));
}
```

## You made it to the end ! Congratulations ! 
(or you just scrolled down to see the end)

