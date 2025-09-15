package piotro15.omnicompass.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.util.Platform;

public class ModRegistries {
    public static final ResourceKey<Registry<CompassType>> COMPASS_TYPE =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(OmniCompass.MOD_ID, "compass_type"));

    public static void init() {
        Platform.getInstance().registerDataRegistry(COMPASS_TYPE, CompassType.CODEC);
    }
}
