package piotro15.omnicompass.common.items.compass.conditions;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public interface CompassTargetCondition {
    Identifier id();

    boolean isMet(ServerPlayer player);
}
