package fr.modcraftmc.crossservercore.api.dataintegrity;

public interface ISecurityWatcher {
    public void addIssue(SecurityIssue issue);

    public void removeIssue(SecurityIssue issue);

    public void registerOnSecureEvent(Runnable runnable);

    public void unregisterOnSecureEvent(Runnable runnable);

    public void registerOnInsecureEvent(Runnable runnable);

    public void unregisterOnInsecureEvent(Runnable runnable);
}
