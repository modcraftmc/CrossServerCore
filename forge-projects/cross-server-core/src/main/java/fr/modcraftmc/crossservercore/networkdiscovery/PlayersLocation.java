package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;

import java.util.*;
import java.util.function.BiConsumer;

public class PlayersLocation {
    private final Map<String, SyncServer> playersLocation;

    public PlayersLocation() {
        this.playersLocation = new HashMap<>();
    }

    public Map<String, SyncServer> getPlayerServerMap() {
        return playersLocation;
    }

    public List<BiConsumer<String, SyncServer>> playerJoinedEvent = new ArrayList<>();
    public List<BiConsumer<String, SyncServer>> playerLeavedEvent = new ArrayList<>();

    public void setPlayerLocation(String player, SyncServer location) {
        if(playersLocation.containsKey(player)) {
            playersLocation.get(player).removePlayer(player);
            this.playersLocation.replace(player, location);
            location.addPlayer(player);
        }
        else {
            this.playersLocation.put(player, location);
            location.addPlayer(player);
            CrossServerCore.updatePlayersListToClients();
        }

        playerJoinedEvent.forEach(event -> event.accept(player, location));
    }

    public void removePlayer(String player) {
        if(playersLocation.containsKey(player)){
            SyncServer server = playersLocation.get(player);
            server.removePlayer(player);
            this.playersLocation.remove(player);
            playerLeavedEvent.forEach(event -> event.accept(player, server));
            CrossServerCore.updatePlayersListToClients();
        }
    }

    public Optional<SyncServer> getPlayerLocation(String player) {
        if(playersLocation.containsKey(player))
            return Optional.of(playersLocation.get(player));
        else
            return Optional.empty();
    }

    public List<String> getAllPlayers() {
        return this.playersLocation.keySet().stream().toList();
    }

    public void clear() {
        this.playersLocation.clear();
    }
}