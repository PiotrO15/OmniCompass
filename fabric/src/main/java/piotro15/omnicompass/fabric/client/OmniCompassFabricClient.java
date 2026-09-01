package piotro15.omnicompass.fabric.client;

import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.client.CompassNeedleTintSource;
import piotro15.omnicompass.client.OmniCompassAngle;
import piotro15.omnicompass.client.screens.CompassOverlay;
import piotro15.omnicompass.client.screens.CompassScreen;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;
import piotro15.omnicompass.common.registry.ModRegistries;

public final class OmniCompassFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OmniCompass.initClient();

        ConfigScreenFactoryRegistry.INSTANCE.register(OmniCompass.MOD_ID, ConfigurationScreen::new);

        CompassOverlay compassOverlay = new CompassOverlay();
        HudElementRegistry.addFirst(OmniCompass.id("compass_overlay"), compassOverlay::render);

        ItemTintSources.ID_MAPPER.put(OmniCompass.id("needle"), CompassNeedleTintSource.MAP_CODEC);

        RangeSelectItemModelProperties.ID_MAPPER.put(OmniCompass.id("target_position"), OmniCompassAngle.MAP_CODEC);

        ModelLoadingPlugin.register(new OmniCompassModelLoadingPlugin());

        ClientPlayNetworking.registerGlobalReceiver(CompassScreenPacket.TYPE, (msg, ctx) -> {
            ClientLevel level = Minecraft.getInstance().level;

            if (level == null) {
                return;
            }

            Registry<CompassType> registry = level.registryAccess().lookupOrThrow(ModRegistries.COMPASS_TYPE);
            CompassType compassType = registry.getValue(msg.compassType());

            if (compassType == null || compassType.entries().isEmpty()) {
                return;
            }

            Minecraft.getInstance().setScreen(new CompassScreen(msg.compassType(), msg.targets()));
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            if (Minecraft.getInstance().level != null) {
                Registry<CompassType> blendTypeRegistry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(ModRegistries.COMPASS_TYPE);

                for (ResourceKey<CompassType> blendKey : blendTypeRegistry.registryKeySet()) {
                    ItemStack stack = new ItemStack(ModItems.COMPASS.get());
                    stack.set(ModDataComponents.COMPASS_TYPE.get(), blendKey.identifier());
                    entries.accept(stack);
                }
            }
        });
    }
}
