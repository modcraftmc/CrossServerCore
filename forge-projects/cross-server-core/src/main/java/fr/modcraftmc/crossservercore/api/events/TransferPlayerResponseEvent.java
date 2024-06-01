package fr.modcraftmc.crossservercore.api.events;

import fr.modcraftmc.crossservercore.message.TransferPlayerResponse;
import net.minecraftforge.eventbus.api.Event;

public class TransferPlayerResponseEvent extends Event {
    private final TransferPlayerResponse transferPlayerResponse;

    public TransferPlayerResponseEvent(TransferPlayerResponse transferPlayerResponse) {
        this.transferPlayerResponse = transferPlayerResponse;
    }

    public TransferPlayerResponse getTransferPlayerResponse() {
        return transferPlayerResponse;
    }
}
