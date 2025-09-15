package piotro15.omnicompass.common.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import piotro15.omnicompass.OmniCompass;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> REGISTRAR = DeferredRegister.create(OmniCompass.MOD_ID, Registries.DATA_COMPONENT_TYPE);
    public static final Supplier<DataComponentType<ResourceLocation>> COMPASS_TYPE = REGISTRAR.register(
            "compass_type",
            () -> DataComponentType.<ResourceLocation>builder()
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
                    .cacheEncoding()
                    .build()
    );

    public static void load() {}
}
