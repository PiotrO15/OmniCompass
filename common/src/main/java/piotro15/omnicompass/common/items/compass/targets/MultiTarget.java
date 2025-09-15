package piotro15.omnicompass.common.items.compass.targets;

import net.minecraft.server.level.ServerLevel;

import java.util.List;

public interface MultiTarget extends CompassTarget {
    List<SingleTarget> processTargets(ServerLevel level);
}
