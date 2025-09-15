package piotro15.omnicompass.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import piotro15.omnicompass.OmniCompass;
import net.neoforged.fml.common.Mod;
import piotro15.omnicompass.client.screens.CompassScreen;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.items.compass.targets.SingleTarget;
import piotro15.omnicompass.common.network.CompassSelectEntryPacket;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.util.Platform;

@Mod(OmniCompass.MOD_ID)
public final class OmniCompassNeoForge {
    public OmniCompassNeoForge(IEventBus modEventBus) {
        Platform.setup(new NeoForgePlatform());

        OmniCompass.init();

        modEventBus.addListener(this::registerDatapackRegistries);
        modEventBus.addListener(this::registerPayloadHandlers);
        modEventBus.addListener(this::registerCompassesInCreativeTab);
    }

    @SubscribeEvent
    private void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(
                CompassScreenPacket.TYPE,
                CompassScreenPacket.CODEC,
                (msg, ctx) -> {
                    ctx.enqueueWork(() -> {
                        ClientLevel level = Minecraft.getInstance().level;

                        if (level == null) {
                            return;
                        }

                        Registry<CompassType> registry = level.registryAccess().registryOrThrow(ModRegistries.COMPASS_TYPE);
                        CompassType compassType = registry.get(msg.compassType());

                        if (compassType == null || compassType.entries().isEmpty()) {
                            return;
                        }

                        Minecraft.getInstance().setScreen(new CompassScreen(msg.compassType(), msg.targets()));
                    });
                }
        );

        event.registrar("1").playToServer(
                CompassSelectEntryPacket.TYPE,
                CompassSelectEntryPacket.CODEC,
                (msg, ctx) -> {
                    ctx.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) ctx.player();

                        Registry<CompassType> registry = player.level().registryAccess().registryOrThrow(ModRegistries.COMPASS_TYPE);
                        CompassType compassType = registry.get(msg.compassType());

                        if (compassType == null) {
                            return;
                        }

                        SingleTarget singleTarget = compassType.getTargets((ServerLevel) ctx.player().level()).stream()
                                .filter(entry ->
                                        entry.entryId().equals(msg.targetId()) &&
                                        entry.targetType().equals(msg.targetType())
                                ).findFirst().orElseThrow();

                        player.displayClientMessage(Component.translatable("omnicompass.compass.scanning"), true);

                        singleTarget.find(player, msg.compassType(), msg.targetType(), msg.targetId());
                    });
                }
        );

    }

    @SubscribeEvent
    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        for (NeoForgePlatform.DataRegistryRegisterable<?> registerable : NeoForgePlatform.dataRegistryRegisterables) {
            registerable.register(event);
        }
    }

    @SubscribeEvent
    public void registerCompassesInCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) return;

        if (Minecraft.getInstance().level != null) {
            Registry<CompassType> blendTypeRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(ModRegistries.COMPASS_TYPE);

            for (ResourceKey<CompassType> blendKey : blendTypeRegistry.registryKeySet()) {
                ItemStack stack = new ItemStack(ModItems.COMPASS.get());
                stack.set(ModDataComponents.COMPASS_TYPE.get(), blendKey.location());
                event.accept(stack);
            }
        }
    }
}
