package fr.modcraftmc.crossservercore.sharedpersistentdata;

import fr.modcraftmc.crossservercore.api.sharedpersistentdata.ISharedDataStore;
import fr.modcraftmc.crossservercore.api.sharedpersistentdata.SharedDataStoreProvider;

import java.util.function.Function;

public class SharedDataStoreProviderImpl extends SharedDataStoreProvider {
    private static Function<String, ISharedDataStore> provider;

    public SharedDataStoreProviderImpl(Function<String, ISharedDataStore> provider){
        SharedDataStoreProviderImpl.provider = provider;
        SharedDataStoreProvider.provider = this;
    }

    public ISharedDataStore getSharedDataStore(String id){
        return provider.apply(id);
    }
}
