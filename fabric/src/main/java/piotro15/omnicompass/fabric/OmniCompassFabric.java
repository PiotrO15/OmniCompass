package piotro15.omnicompass.fabric;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import piotro15.omnicompass.OmniCompass;
import net.fabricmc.api.ModInitializer;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.items.compass.targets.SingleTarget;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.network.CompassSelectEntryPacket;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.util.Platform;

public final class OmniCompassFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Platform.setup(new FabricPlatform());

        OmniCompass.init();

        PayloadTypeRegistry.playC2S().register(CompassSelectEntryPacket.TYPE, CompassSelectEntryPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(CompassScreenPacket.TYPE, CompassScreenPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CompassSelectEntryPacket.TYPE, (msg, ctx) -> {
            ServerPlayer player = ctx.player();

            Registry<CompassType> registry = player.level().registryAccess().registryOrThrow(ModRegistries.COMPASS_TYPE);
            CompassType compassType = registry.get(msg.compassType());

            if (compassType == null) {
                return;
            }

            SingleTarget singleTarget = compassType.getTargets((ServerLevel) player.level()).stream()
                    .filter(entry ->
                            entry.entryId().equals(msg.targetId()) &&
                                    entry.targetType().equals(msg.targetType())
                    ).findFirst().orElseThrow();

            player.displayClientMessage(Component.translatable("omnicompass.compass.scanning"), true);

            singleTarget.find(player, msg.compassType(), msg.targetType(), msg.targetId());
        });
    }
}
