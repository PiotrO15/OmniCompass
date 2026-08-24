package piotro15.omnicompass.common.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record CompassTargetType(ResourceLocation type, ResourceLocation id, boolean isTag) {
    public static final Codec<CompassTargetType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(CompassTargetType::type),
            ResourceLocation.CODEC.fieldOf("id").forGetter(CompassTargetType::id),
            Codec.BOOL.fieldOf("tag").forGetter(CompassTargetType::isTag)
    ).apply(instance, CompassTargetType::new));

    public static final StreamCodec<ByteBuf, CompassTargetType> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, CompassTargetType::type,
            ResourceLocation.STREAM_CODEC, CompassTargetType::id,
            ByteBufCodecs.BOOL, CompassTargetType::isTag,
            CompassTargetType::new
    );

    public MutableComponent getTargetType() {
        return Component.translatable("omnicompass.target." + type.toLanguageKey());
    }

    public MutableComponent getTargetName() {
        Language language = Language.getInstance();

        String prefix = isTag ? "tag." : "";
        String key = switch (type.toString()) {
            case "omnicompass:structure" -> prefix + "structure." + id.toLanguageKey();
            case "omnicompass:biome" -> prefix + "biome." + id.toLanguageKey();
            default -> "omnicompass.target.unknown";
        };

        if (language.has(key)) {
            return Component.translatable(key);
        } else {
            String[] words = id.getPath().split("[_/]");
            for (int i = 0; i < words.length; i++) {
                if (!words[i].isEmpty()) {
                    words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
                }
            }
            return Component.literal(String.join(" ", words));
        }
    }
}
