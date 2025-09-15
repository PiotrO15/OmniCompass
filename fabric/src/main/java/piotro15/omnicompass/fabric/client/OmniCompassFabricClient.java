package piotro15.omnicompass.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.client.ModItemProperties;
import piotro15.omnicompass.client.screens.CompassScreen;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;
import piotro15.omnicompass.common.registry.ModRegistries;

import java.util.Map;

public final class OmniCompassFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OmniCompass.initClient();
        ModItemProperties.init();

        ClientPlayNetworking.registerGlobalReceiver(CompassScreenPacket.TYPE, (msg, ctx) -> {
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

        ModelLoadingPlugin.register(plugin -> {
            for (Map.Entry<ResourceLocation, Resource> entry : FileToIdConverter.json("models/compass").listMatchingResources(Minecraft.getInstance().getResourceManager()).entrySet()) {
                ResourceLocation blendType = ResourceLocation.parse(entry.getKey().toString().replace("models/compass", "compass").replace(".json", ""));
                plugin.addModels(blendType);
            }
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            if (Minecraft.getInstance().level != null) {
                Registry<CompassType> blendTypeRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(ModRegistries.COMPASS_TYPE);

                for (ResourceKey<CompassType> blendKey : blendTypeRegistry.registryKeySet()) {
                    ItemStack stack = new ItemStack(ModItems.COMPASS.get());
                    stack.set(ModDataComponents.COMPASS_TYPE.get(), blendKey.location());
                    entries.accept(stack);
                }
            }
        });
    }
}
