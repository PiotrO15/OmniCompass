package piotro15.omnicompass.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class OmniCompassAngle implements RangeSelectItemModelProperty {
    public static final MapCodec<OmniCompassAngle> MAP_CODEC;
    private final OmniCompassAngleState state;

    public OmniCompassAngle(OmniCompassAngleState state) {
        this.state = state;
    }

    public float get(@NonNull ItemStack itemStack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        return this.state.get(itemStack, level, owner, seed);
    }

    public @NonNull MapCodec<OmniCompassAngle> type() {
        return MAP_CODEC;
    }

    static {
        MAP_CODEC = OmniCompassAngleState.MAP_CODEC.xmap(OmniCompassAngle::new, (c) -> c.state);
    }
}
