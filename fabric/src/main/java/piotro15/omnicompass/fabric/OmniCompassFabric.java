package piotro15.omnicompass.fabric;

import com.mojang.serialization.MapCodec;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.config.ModConfig;
import piotro15.omnicompass.OmniCompass;
import net.fabricmc.api.ModInitializer;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.items.compass.targets.SingleTarget;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.network.CompassSelectEntryPacket;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.config.ClientConfig;
import piotro15.omnicompass.config.CommonConfig;
import piotro15.omnicompass.fabric.common.ConfigResourceCondition;
import piotro15.omnicompass.util.Platform;

public final class OmniCompassFabric implements ModInitializer {
    public static ResourceConditionType<ConfigResourceCondition> CONFIG_RESOURCE_CONDITION;

    @Override
    public void onInitialize() {
        Platform.setup(new FabricPlatform());

        OmniCompass.init();

        CONFIG_RESOURCE_CONDITION = createResourceConditionType("config", ConfigResourceCondition.CODEC);
        ResourceConditions.register(CONFIG_RESOURCE_CONDITION);

        NeoForgeConfigRegistry.INSTANCE.register(OmniCompass.MOD_ID, ModConfig.Type.CLIENT, ClientConfig.SPEC);
        NeoForgeConfigRegistry.INSTANCE.register(OmniCompass.MOD_ID, ModConfig.Type.COMMON, CommonConfig.SPEC);

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

    private static <T extends ResourceCondition> ResourceConditionType<T> createResourceConditionType(String name, MapCodec<T> codec) {
        return ResourceConditionType.create(OmniCompass.id(name), codec);
    }
}
