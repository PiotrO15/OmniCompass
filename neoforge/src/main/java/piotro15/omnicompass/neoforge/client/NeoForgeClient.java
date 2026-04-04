package piotro15.omnicompass.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.client.ModItemProperties;
import piotro15.omnicompass.client.screens.CompassScreen;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.registry.ModItems;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.neoforge.NeoForgePlatform;

import java.util.Map;

@Mod(value = OmniCompass.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OmniCompass.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClient {
    public NeoForgeClient(ModContainer container) {
        OmniCompass.initClient();

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
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

    public static void handleCompassScreenPacket(CompassScreenPacket msg, IPayloadContext ctx) {
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
}
