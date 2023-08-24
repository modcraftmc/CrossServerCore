package fr.modcraftmc.crossservercore.client;

import com.mojang.logging.LogUtils;
import fr.modcraftmc.crossservercore.client.arguments.NetworkPlayerArgument;
import fr.modcraftmc.crossservercore.client.networking.Network;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.List;

@Mod("crossservercoreclient")
public class CrossServerCoreClient {
    public static final String MOD_ID = "crossservercoreclient";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static List<String> playersOnCluster;
    public static final Network network = new Network();
    public static DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, MOD_ID);

    static {
        CrossServerCoreClient.ARGUMENT_TYPES.register("network_player", () -> ArgumentTypeInfos.registerByClass(NetworkPlayerArgument.class, SingletonArgumentInfo.contextFree(NetworkPlayerArgument::new)));
    }

    public CrossServerCoreClient() {
        CrossServerCoreClient.LOGGER.info("CrossServerCoreClient is here !");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ARGUMENT_TYPES.register(modEventBus);
        network.Init();
    }
}
