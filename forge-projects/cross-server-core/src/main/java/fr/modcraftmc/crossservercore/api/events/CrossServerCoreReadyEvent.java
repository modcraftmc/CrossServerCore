package fr.modcraftmc.crossservercore.api.events;

import fr.modcraftmc.crossservercore.api.CrossServerCoreAPI;
import net.minecraftforge.eventbus.api.Event;

public class CrossServerCoreReadyEvent extends Event {

    private CrossServerCoreAPI instance;

    public CrossServerCoreReadyEvent(CrossServerCoreAPI instance) {
        this.instance = instance;
    }

    public CrossServerCoreAPI getInstance() {
        return instance;
    }
}
