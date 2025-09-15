package piotro15.omnicompass;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import piotro15.omnicompass.common.items.compass.CompassTargetConditionRegistry;
import piotro15.omnicompass.common.items.compass.CompassTargetRegistry;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.util.Platform;

public final class OmniCompass {
    public static final String MOD_ID = "omnicompass";

    public static void init() {
        ModItems.ITEMS.register();
        ModDataComponents.REGISTRAR.register();
        CompassTargetConditionRegistry.registerConditions();
        CompassTargetRegistry.registerEntries();
        ModRegistries.init();
    }

    public static void initClient() {
        Platform.getInstance().registerItemTint((stack, tintIndex) -> {
            ResourceLocation id = stack.get(ModDataComponents.COMPASS_TYPE.get());

            if (id == null) {
                return -1;
            }

            if (Minecraft.getInstance().getConnection() == null)
                return -1;

            Registry<CompassType> registry = Minecraft.getInstance().getConnection().registryAccess().registryOrThrow(ModRegistries.COMPASS_TYPE);

            CompassType compassType = registry.get(id);

            if (compassType == null)
                return -1;

            if (tintIndex == 1) {
                int color = compassType.needleShadeColor();
                return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
            }
            if (tintIndex == 2) {
                int color = compassType.needleColor();
                return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
            }
            return -1;
        }, ModItems.COMPASS);
    }
}
