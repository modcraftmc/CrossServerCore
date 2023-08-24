package fr.modcraftmc.crossservercore.mongodb;

import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;
import fr.modcraftmc.crossservercore.CrossServerCore;

public class MongodbServerMonitorListener implements ServerMonitorListener {
    private boolean alive;

    private final Runnable onHeartbeatFailed;
    private final Runnable onHeartbeatSucceeded;

    public MongodbServerMonitorListener(Runnable onHeartbeatFailed, Runnable onHeartbeatSucceeded) {
        this.alive = true;
        this.onHeartbeatFailed = onHeartbeatFailed;
        this.onHeartbeatSucceeded =onHeartbeatSucceeded;
    }

    @Override
    public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {
        successHeartbeat();
    }

    @Override
    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
        CrossServerCore.LOGGER.error(String.format("Error on mongodb connection : %s", event.getThrowable().getMessage()));
        failHeartbeat();
    }

    private void successHeartbeat(){
        if(!alive){
            CrossServerCore.LOGGER.warn("Mongodb server is back online");
            onHeartbeatSucceeded.run();
            alive = true;
        }
    }

    private void failHeartbeat(){
        if(alive){
            onHeartbeatFailed.run();
            alive = false;
        }
    }
}