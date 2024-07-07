package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import net.minecraftforge.common.MinecraftForge;

@AutoRegister("transfer_player_event")
public class TransferPlayerEvent extends BaseMessage {

    @AutoSerialize
    private ISyncPlayer player;
    @AutoSerialize
    private ISyncServer serverDestination;

    public TransferPlayerEvent(ISyncPlayer player, ISyncServer serverDestination) {
        this.player = player;
        this.serverDestination = serverDestination;
    }

    public TransferPlayerEvent(fr.modcraftmc.crossservercore.api.events.TransferPlayerEvent event) {
        this(event.getPlayer(), event.getServerDestination());
    }

    @Override
    public void handle() {
        MinecraftForge.EVENT_BUS.post(new fr.modcraftmc.crossservercore.api.events.TransferPlayerEvent(player, serverDestination));
    }
}
