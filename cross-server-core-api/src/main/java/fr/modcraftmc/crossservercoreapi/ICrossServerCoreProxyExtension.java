package fr.modcraftmc.crossservercoreapi;

public interface ICrossServerCoreProxyExtension {
    public boolean isProxyExtensionEnabled();
    public void transferPlayer(String playerName, String serverName);
}
