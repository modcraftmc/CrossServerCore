package fr.modcraftmc.crossservercore.api.networkdiscovery;

import java.util.Optional;

public interface ISyncPlayerProxy extends ISyncPlayer {
    public Optional<? extends ISyncPlayer> unproxy();
}
