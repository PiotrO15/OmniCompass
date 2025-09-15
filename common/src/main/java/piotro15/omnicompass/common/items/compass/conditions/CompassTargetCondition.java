package piotro15.omnicompass.common.items.compass.conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface CompassTargetCondition {
    ResourceLocation id();

    boolean isMet(ServerPlayer player);
}
