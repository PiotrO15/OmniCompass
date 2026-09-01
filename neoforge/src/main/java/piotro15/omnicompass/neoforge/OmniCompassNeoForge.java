package piotro15.omnicompass.neoforge;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import piotro15.omnicompass.OmniCompass;
import net.neoforged.fml.common.Mod;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.items.compass.targets.SingleTarget;
import piotro15.omnicompass.common.network.CompassSelectEntryPacket;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.config.ClientConfig;
import piotro15.omnicompass.config.CommonConfig;
import piotro15.omnicompass.neoforge.client.NeoForgeClient;
import piotro15.omnicompass.util.Platform;

import java.util.function.Supplier;

@Mod(OmniCompass.MOD_ID)
@EventBusSubscriber(modid = OmniCompass.MOD_ID)
public final class OmniCompassNeoForge {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, OmniCompass.MOD_ID);

    public static final Supplier<MapCodec<ConfigResourceCondition>> CONFIG_RESOURCE_CONDITION =
            CONDITION_CODECS.register("config", () -> ConfigResourceCondition.CODEC);

    public OmniCompassNeoForge(IEventBus modEventBus, ModContainer container) {
        Platform.setup(new NeoForgePlatform());

        CONDITION_CODECS.register(modEventBus);

        NeoForgePlatform.ITEMS.register(modEventBus);
        NeoForgePlatform.DATA_COMPONENTS.register(modEventBus);

        OmniCompass.init();

        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    @SubscribeEvent
    private static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                CompassScreenPacket.TYPE,
                CompassScreenPacket.CODEC,
                FMLEnvironment.getDist().isClient()
                        ? NeoForgeClient::handleCompassScreenPacket
                        : (msg, ctx) -> {}
        );

        registrar.playToServer(
                CompassSelectEntryPacket.TYPE,
                CompassSelectEntryPacket.CODEC,
                (msg, ctx) -> {
                    ctx.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) ctx.player();

                        Registry<CompassType> registry = player.level().registryAccess().lookupOrThrow(ModRegistries.COMPASS_TYPE);
                        CompassType compassType = registry.getValue(msg.compassType());

                        if (compassType == null) {
                            return;
                        }

                        SingleTarget singleTarget = compassType.getTargets((ServerLevel) ctx.player().level()).stream()
                                .filter(entry ->
                                        entry.entryId().equals(msg.targetId()) &&
                                        entry.targetType().equals(msg.targetType())
                                ).findFirst().orElseThrow();

                        player.sendOverlayMessage(Component.translatable("omnicompass.compass.scanning"));

                        singleTarget.find(player, msg.compassType(), msg.targetType(), msg.targetId());
                    });
                }
        );

    }

    @SubscribeEvent
    private static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        for (NeoForgePlatform.DataRegistryRegisterable<?> registerable : NeoForgePlatform.dataRegistryRegisterables) {
            registerable.register(event);
        }
    }

    @SubscribeEvent
    private static void registerCompassesInCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) return;

        event.getParameters().holders().lookupOrThrow(ModRegistries.COMPASS_TYPE).listElementIds().forEach(compassTypeKey -> {
            ItemStack stack = new ItemStack(ModItems.COMPASS.get());
            stack.set(ModDataComponents.COMPASS_TYPE.get(), compassTypeKey.identifier());
            event.accept(stack);
        });
    }

    @SubscribeEvent
    private static void onReload(AddServerReloadListenersEvent event) {
        CompassType.invalidateCache();
    }
}
