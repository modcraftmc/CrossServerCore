package fr.modcraftmc.crossservercore.api;

public interface ICrossServerCoreProxyExtension {
    public boolean isProxyExtensionEnabled();
    public void transferPlayer(String playerName, String serverName);
}
