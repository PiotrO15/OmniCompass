package piotro15.omnicompass.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.client.ModItemProperties;
import piotro15.omnicompass.common.registry.ModItems;
import piotro15.omnicompass.neoforge.NeoForgePlatform;

import java.util.Map;

@Mod(value = OmniCompass.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OmniCompass.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClient {
    public NeoForgeClient() {
        OmniCompass.initClient();
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        ModItemProperties.init();
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        NeoForgePlatform.itemColors.forEach(
                (item, color) -> event.register(color, item.get())
        );
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (Map.Entry<ResourceLocation, Resource> entry : FileToIdConverter.json("models/compass").listMatchingResources(Minecraft.getInstance().getResourceManager()).entrySet()) {
            ResourceLocation compassLocation = ResourceLocation.parse(entry.getKey().toString().replace("models/compass", "compass").replace(".json", ""));
            event.register(ModelResourceLocation.standalone(compassLocation));
        }
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return CompassRenderer.INSTANCE;
            }
        }, ModItems.COMPASS.get());
    }
}
