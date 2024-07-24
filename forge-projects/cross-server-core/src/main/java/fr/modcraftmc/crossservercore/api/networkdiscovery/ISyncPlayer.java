package fr.modcraftmc.crossservercore.api.networkdiscovery;

import java.util.UUID;

public interface ISyncPlayer {
    public UUID getUUID();
    public String getName();
    public ISyncServer getServer();
    public ISyncPlayerProxy proxy();
}
