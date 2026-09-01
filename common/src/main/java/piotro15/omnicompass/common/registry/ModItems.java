package piotro15.omnicompass.common.registry;

import net.minecraft.world.item.Item;
import piotro15.omnicompass.common.items.CompassItem;
import piotro15.omnicompass.util.Platform;

import java.util.function.Supplier;

public class ModItems {
    public static Supplier<Item> COMPASS;

    public static void load() {
        COMPASS = Platform.getInstance().registerItem("compass", CompassItem::new, Item.Properties::new);
    }
}
