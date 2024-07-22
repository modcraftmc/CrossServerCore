package fr.modcraftmc.crossservercore.api.networkdiscovery;

import java.util.Optional;

public interface ISyncServerProxy extends ISyncServer {
    public Optional<? extends ISyncServer> unproxy();
}
