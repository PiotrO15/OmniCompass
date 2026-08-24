package piotro15.omnicompass.common.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
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
        String prefix = isTag ? "tag." : "";
        return switch (type.toString()) {
            case "omnicompass:structure" -> Component.translatable(prefix + "structure." + id.toLanguageKey());
            case "omnicompass:biome" -> Component.translatable(prefix + "biome." + id.toLanguageKey());
            default -> Component.translatable("omnicompass.target.unknown");
        };
    }
}
