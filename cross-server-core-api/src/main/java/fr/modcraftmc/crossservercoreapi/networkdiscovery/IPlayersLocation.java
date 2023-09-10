package fr.modcraftmc.crossservercoreapi.networkdiscovery;

import java.util.Map;
import java.util.function.BiConsumer;

public interface IPlayersLocation {
    public void registerOnPlayerJoinedClusterEvent(BiConsumer<String, ISyncServer> event);

    public void registerOnPlayerLeavedClusterEvent(BiConsumer<String, ISyncServer> event);

    public Map<String, ? extends ISyncServer> getPlayerServerMap();
}
