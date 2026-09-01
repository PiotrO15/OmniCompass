package piotro15.omnicompass.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModRegistries;

public record CompassNeedleTintSource(boolean shadow) implements ItemTintSource {
    public static final MapCodec<CompassNeedleTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("shadow", false).forGetter(CompassNeedleTintSource::shadow)
            ).apply(instance, CompassNeedleTintSource::new)
    );

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        Identifier compassId = stack.get(ModDataComponents.COMPASS_TYPE.get());
        if (compassId == null) return -1;

        if (Minecraft.getInstance().getConnection() == null) return -1;

        Registry<CompassType> registry = Minecraft.getInstance().getConnection()
                .registryAccess().lookupOrThrow(ModRegistries.COMPASS_TYPE);

        CompassType blendType = registry.getValue(compassId);
        if (blendType == null)
            return -1;

        int color = shadow ? blendType.needleShadeColor() : blendType.needleColor();
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }

    @Override
    public @NonNull MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}