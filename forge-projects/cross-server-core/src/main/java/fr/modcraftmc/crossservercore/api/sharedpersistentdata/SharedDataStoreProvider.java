package fr.modcraftmc.crossservercore.api.sharedpersistentdata;

public abstract class SharedDataStoreProvider {
    protected static SharedDataStoreProvider provider;
    public static ISharedDataStore get(String id){
        return provider.getSharedDataStore(id);
    }
    public abstract ISharedDataStore getSharedDataStore(String id);
}
