package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayerProxy;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import net.minecraftforge.common.MinecraftForge;

@AutoRegister("transfer_player_event")
public class TransferPlayerEventMessage extends BaseMessage {

    @AutoSerialize
    private ISyncPlayerProxy player;
    @AutoSerialize
    private ISyncServer destination;

    public TransferPlayerEventMessage(ISyncPlayerProxy player, ISyncServer destination) {
        this.player = player;
        this.destination = destination;
    }

    public TransferPlayerEventMessage(fr.modcraftmc.crossservercore.api.events.TransferPlayerEvent event) {
        this(event.getPlayer(), event.getDestination());
    }

    @Override
    public void handle() {
        MinecraftForge.EVENT_BUS.post(new fr.modcraftmc.crossservercore.api.events.TransferPlayerEvent(player, destination));
    }
}
