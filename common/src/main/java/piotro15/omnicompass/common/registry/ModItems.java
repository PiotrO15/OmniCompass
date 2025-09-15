package piotro15.omnicompass.common.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.common.items.CompassItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(OmniCompass.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> COMPASS = ITEMS.register("compass", CompassItem::new);
}
