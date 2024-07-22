package fr.modcraftmc.crossservercore.api.message;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

@AutoRegister("send_message")
public class SendMessage extends BaseMessage {

    @AutoSerialize
    public Component message;
    @AutoSerialize
    public ISyncPlayer player;

    private SendMessage() {}

    public SendMessage(Component message, ISyncPlayer player) {
        this.message = message;
        this.player = player;
    }

    public void send(){
        player.getServer().sendMessage(this);
    }

    @Override
    public void handle() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerPlayer serverPlayer = server.getPlayerList().getPlayer(player.getUUID());
        if (serverPlayer != null) {
            serverPlayer.sendSystemMessage(message, false);
        }
    }
}
