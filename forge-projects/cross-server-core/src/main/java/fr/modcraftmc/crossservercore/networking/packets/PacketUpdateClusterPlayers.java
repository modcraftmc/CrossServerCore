package fr.modcraftmc.crossservercore.networking.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class PacketUpdateClusterPlayers implements IPacket {
    private final List<String> players;

    public PacketUpdateClusterPlayers(List<String> players) {
        this.players = players;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(players.size());
        for(String player : players) {
            buffer.writeUtf(player);
        }
    }

    public static PacketUpdateClusterPlayers decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<String> players = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            players.add(buffer.readUtf());
        }
        return new PacketUpdateClusterPlayers(players);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        // handle on client side
    }

    public List<String> getPlayers() {
        return players;
    }
}
