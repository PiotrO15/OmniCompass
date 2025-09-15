package piotro15.omnicompass.client;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.LodestoneTracker;
import piotro15.omnicompass.common.registry.ModItems;

public class ModItemProperties {
    static {
        ItemPropertiesRegistry.register(ModItems.COMPASS.get(), ResourceLocation.withDefaultNamespace("angle"), new CompassItemPropertyFunction((clientLevel, itemStack, entity) -> {
            LodestoneTracker lodestoneTracker = itemStack.get(DataComponents.LODESTONE_TRACKER);
            return lodestoneTracker != null ? lodestoneTracker.target().orElse(null) : null;
        }));
    }

    public static void init() {}
}
