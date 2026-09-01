package piotro15.omnicompass.fabric;

import com.mojang.serialization.MapCodec;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
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

import java.util.function.Function;

public final class OmniCompassFabric implements ModInitializer {
    public static ResourceConditionType<ConfigResourceCondition> CONFIG_RESOURCE_CONDITION;

    @Override
    public void onInitialize() {
        Platform.setup(new FabricPlatform());

        OmniCompass.init();

        CONFIG_RESOURCE_CONDITION = createResourceConditionType("config", ConfigResourceCondition.CODEC);
        ResourceConditions.register(CONFIG_RESOURCE_CONDITION);

        ConfigRegistry.INSTANCE.register(OmniCompass.MOD_ID, ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ConfigRegistry.INSTANCE.register(OmniCompass.MOD_ID, ModConfig.Type.COMMON, CommonConfig.SPEC);

        PayloadTypeRegistry.serverboundPlay().register(CompassSelectEntryPacket.TYPE, CompassSelectEntryPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(CompassScreenPacket.TYPE, CompassScreenPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CompassSelectEntryPacket.TYPE, (msg, ctx) -> {
            ServerPlayer player = ctx.player();

            Registry<CompassType> registry = player.level().registryAccess().lookupOrThrow(ModRegistries.COMPASS_TYPE);
            CompassType compassType = registry.getValue(msg.compassType());

            if (compassType == null) {
                return;
            }

            SingleTarget singleTarget = compassType.getTargets(player.level()).stream()
                    .filter(entry ->
                            entry.entryId().equals(msg.targetId()) &&
                                    entry.targetType().equals(msg.targetType())
                    ).findFirst().orElseThrow();

            player.sendOverlayMessage(Component.translatable("omnicompass.compass.scanning"));

            singleTarget.find(player, msg.compassType(), msg.targetType(), msg.targetId());
        });

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return Identifier.fromNamespaceAndPath(OmniCompass.MOD_ID, "compass_cache_invalidator");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager manager) {
                        CompassType.invalidateCache();
                    }
                }
        );
    }

    private static <T extends ResourceCondition> ResourceConditionType<T> createResourceConditionType(String name, MapCodec<T> codec) {
        return ResourceConditionType.create(OmniCompass.id(name), codec);
    }

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, OmniCompass.id(name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }
}
