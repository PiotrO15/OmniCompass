package piotro15.omnicompass.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class CommonConfig {
    public static final CommonConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    public final ModConfigSpec.IntValue horizontalResolution;
    public final ModConfigSpec.IntValue verticalResolution;
    public final ModConfigSpec.IntValue biomeSearchRange;

    public final ModConfigSpec.IntValue structureSearchRange;

    private CommonConfig(ModConfigSpec.Builder builder) {
        horizontalResolution = builder.defineInRange("horizontal_resolution", 32, 1, 512);
        verticalResolution = builder.defineInRange("vertical_resolution", 64, 1, 512);
        biomeSearchRange = builder.defineInRange("biome_search_range", 6400, 1, 128000);

        structureSearchRange = builder.defineInRange("structure_search_range", 512, 1, 2048);
    }

    static {
        Pair<CommonConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }
}
