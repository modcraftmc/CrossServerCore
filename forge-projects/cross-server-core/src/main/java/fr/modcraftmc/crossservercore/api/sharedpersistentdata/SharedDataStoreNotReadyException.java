package fr.modcraftmc.crossservercore.api.sharedpersistentdata;

public class SharedDataStoreNotReadyException extends Exception {
    public SharedDataStoreNotReadyException() {
        super("SharedDataStore is not ready");
    }
}
