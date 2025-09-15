package piotro15.omnicompass.common.items.compass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import piotro15.omnicompass.common.items.compass.targets.CompassTarget;
import piotro15.omnicompass.common.items.compass.targets.MultiTarget;
import piotro15.omnicompass.common.items.compass.targets.SingleTarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public record CompassType(
        List<CompassTarget> entries,
        int needleColor,
        int needleShadeColor
) {
    private static final Map<CompassType, List<SingleTarget>> CACHED_TARGETS = new HashMap<>();

    public List<SingleTarget> getTargets(ServerLevel level) {
        if (!CACHED_TARGETS.containsKey(this)) {
            CACHED_TARGETS.put(this, entries.stream().flatMap(entry -> {
                if (entry instanceof SingleTarget singleTarget) {
                    return Stream.of(singleTarget);
                } else if (entry instanceof MultiTarget multiTarget) {
                    return multiTarget.processTargets(level).stream();
                } else {
                    return Stream.of();
                }
            }).toList());
        }
        return CACHED_TARGETS.get(this);
    }

    public static final Codec<CompassType> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    CompassTargetRegistry.CODEC.listOf().fieldOf("entries").forGetter(CompassType::entries),
                    Codec.INT.optionalFieldOf("needle_color", 0xFFFFFF).forGetter(CompassType::needleColor),
                    Codec.INT.optionalFieldOf("needle_shade_color", 0xFFFFFF).forGetter(CompassType::needleShadeColor)
            ).apply(instance, CompassType::new)
    );

    public static class CompassTypeBuilder {
        private List<CompassTarget> entries;
        private int needleColor = 0xFFFFFF;
        private int needleShadeColor = 0xFFFFFF;

        public CompassTypeBuilder entries(List<CompassTarget> entries) {
            this.entries = entries;
            return this;
        }

        public CompassTypeBuilder needleColor(int needleColor) {
            this.needleColor = needleColor;
            return this;
        }

        public CompassTypeBuilder needleShadeColor(int needleShadeColor) {
            this.needleShadeColor = needleShadeColor;
            return this;
        }

        public CompassType build() {
            return new CompassType(entries, needleColor, needleShadeColor);
        }
    }
}
