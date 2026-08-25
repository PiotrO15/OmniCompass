package piotro15.omnicompass.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfig {
    public static final ClientConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    public final ModConfigSpec.BooleanValue showPosition;

    public final ModConfigSpec.ConfigValue<OverlayStyle> overlayStyle;
    public final ModConfigSpec.ConfigValue<VerticalAlignment> verticalAlignment;
    public final ModConfigSpec.ConfigValue<HorizontalAlignment> horizontalAlignment;

    public ClientConfig(ModConfigSpec.Builder builder) {
        showPosition = builder.define("show_position", true);

        overlayStyle = builder.defineEnum("overlay_style", OverlayStyle.COLUMN);
        verticalAlignment = builder.defineEnum("vertical_alignment", VerticalAlignment.TOP);
        horizontalAlignment = builder.defineEnum("horizontal_alignment", HorizontalAlignment.LEFT);
    }

    static {
        Pair<ClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public enum OverlayStyle {
        ACTION_BAR,
        BAR,
        COLUMN,
        NONE
    }

    public enum VerticalAlignment {
        TOP,
        CENTER,
        BOTTOM
    }

    public enum HorizontalAlignment {
        LEFT,
        RIGHT
    }
}
