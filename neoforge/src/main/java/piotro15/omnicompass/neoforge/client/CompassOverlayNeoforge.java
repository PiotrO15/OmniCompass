package piotro15.omnicompass.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.client.screens.CompassOverlay;

@EventBusSubscriber(modid = "omnicompass", value = Dist.CLIENT)
public class CompassOverlayNeoforge {
    @SubscribeEvent
    public static void onCompassOverlay(RegisterGuiLayersEvent event) {
        event.registerAboveAll(OmniCompass.id("compass_overlay"), new CompassOverlay());
    }
}
