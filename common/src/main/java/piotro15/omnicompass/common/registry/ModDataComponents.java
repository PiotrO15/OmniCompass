package piotro15.omnicompass.common.registry;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.util.Platform;

import java.util.function.Supplier;

public class ModDataComponents {
    public static Supplier<DataComponentType<Identifier>> COMPASS_TYPE;
    public static Supplier<DataComponentType<CompassTargetType>> TARGET_TYPE;
    public static Supplier<DataComponentType<GlobalPos>> TARGET_POSITION;

    public static void load() {
        COMPASS_TYPE = Platform.getInstance().registerDataComponentType(
                OmniCompass.id("compass_type"),
                () -> DataComponentType.<Identifier>builder()
                        .persistent(Identifier.CODEC)
                        .networkSynchronized(Identifier.STREAM_CODEC)
                        .cacheEncoding()
                        .build()
        );

        TARGET_TYPE = Platform.getInstance().registerDataComponentType(
                OmniCompass.id("target_type"),
                () -> DataComponentType.<CompassTargetType>builder()
                        .persistent(CompassTargetType.CODEC)
                        .networkSynchronized(CompassTargetType.STREAM_CODEC)
                        .build()
        );

        TARGET_POSITION = Platform.getInstance().registerDataComponentType(
                OmniCompass.id("target_position"),
                () -> DataComponentType.<GlobalPos>builder()
                        .persistent(GlobalPos.CODEC)
                        .networkSynchronized(GlobalPos.STREAM_CODEC)
                        .build()
        );
    }
}
