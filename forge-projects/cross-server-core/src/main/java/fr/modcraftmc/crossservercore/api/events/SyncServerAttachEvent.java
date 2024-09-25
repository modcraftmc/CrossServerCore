package fr.modcraftmc.crossservercore.api.events;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import net.minecraftforge.eventbus.api.Event;

public class SyncServerAttachEvent extends Event {
    private ISyncServer syncServer;
    private AttachType attachType;
    public enum AttachType {
        EXISTING,
        NEW
    }

    public SyncServerAttachEvent(ISyncServer syncServer, AttachType attachType) {
        this.syncServer = syncServer;
        this.attachType = attachType;
    }

    public ISyncServer getSyncServer() {
        return syncServer;
    }

    public AttachType getAttachType() {
        return attachType;
    }
}
