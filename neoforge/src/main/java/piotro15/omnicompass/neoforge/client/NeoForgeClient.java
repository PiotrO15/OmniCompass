package piotro15.omnicompass.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.client.CompassNeedleTintSource;
import piotro15.omnicompass.client.OmniCompassAngle;
import piotro15.omnicompass.client.OmniCompassClient;
import piotro15.omnicompass.client.screens.CompassOverlay;
import piotro15.omnicompass.client.screens.CompassScreen;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.registry.ModRegistries;

@Mod(value = OmniCompass.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OmniCompass.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClient {
    public NeoForgeClient(ModContainer container) {
        OmniCompass.initClient();

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(OmniCompass.id("needle"), CompassNeedleTintSource.MAP_CODEC);
    }

    @SubscribeEvent
    public static void modifyBakingResults(ModelEvent.ModifyBakingResult event) {
        event.getBakingResult().itemStackModels().computeIfPresent(
                Identifier.fromNamespaceAndPath(OmniCompass.MOD_ID, "compass"),
                (_, model) -> new OmniCompassClient.CompassWrapper(model)
        );
    }

    @SubscribeEvent
    public static void onCompassOverlay(RegisterGuiLayersEvent event) {
        CompassOverlay compassOverlay = new CompassOverlay();
        event.registerAboveAll(OmniCompass.id("compass_overlay"), compassOverlay::render);
    }

    public static void handleCompassScreenPacket(CompassScreenPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
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
    }

    @SubscribeEvent
    public static void registerItemProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(OmniCompass.id("target_position"), OmniCompassAngle.MAP_CODEC);
    }
}
