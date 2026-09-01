package piotro15.omnicompass;

import net.minecraft.resources.Identifier;
import piotro15.omnicompass.common.items.compass.CompassTargetConditionRegistry;
import piotro15.omnicompass.common.items.compass.CompassTargetRegistry;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;
import piotro15.omnicompass.common.registry.ModRegistries;

public final class OmniCompass {
    public static final String MOD_ID = "omnicompass";

    public static void init() {
        ModItems.load();
        ModDataComponents.load();
        CompassTargetConditionRegistry.registerConditions();
        CompassTargetRegistry.registerEntries();
        ModRegistries.init();
    }

    public static void initClient() {}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
