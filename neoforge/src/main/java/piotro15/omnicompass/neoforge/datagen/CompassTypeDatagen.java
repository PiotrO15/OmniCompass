package piotro15.omnicompass.neoforge.datagen;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.items.compass.targets.AllOfTarget;
import piotro15.omnicompass.common.registry.ModRegistries;

import java.util.List;

public class CompassTypeDatagen {
    public static void registerCompassTypes(BootstrapContext<CompassType> bootstrapContext) {
        bootstrapContext.register(
                ResourceKey.create(
                        ModRegistries.COMPASS_TYPE,
                        ResourceLocation.fromNamespaceAndPath("minecraft", "biome")
                ),
                new CompassType.CompassTypeBuilder().entries(List.of(
                        new AllOfTarget(ResourceLocation.fromNamespaceAndPath("minecraft", "worldgen/biome"))
                )).needleColor(0xc29d62).needleShadeColor(0x967441)
                        .build()
        );

        bootstrapContext.register(
                ResourceKey.create(
                        ModRegistries.COMPASS_TYPE,
                        ResourceLocation.fromNamespaceAndPath("minecraft", "structure")
                ),
                new CompassType.CompassTypeBuilder().entries(List.of(
                        new AllOfTarget(ResourceLocation.fromNamespaceAndPath("minecraft", "worldgen/structure"))
                )).needleColor(0xaaaaaa).needleShadeColor(0x7f3e2c)
                        .build()
        );
    }
}
