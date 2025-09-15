package piotro15.omnicompass.neoforge.client;

import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class ModClientItemExtensions implements IClientItemExtensions {
    private final CompassRenderer CompassBEWLR = CompassRenderer.INSTANCE;

    @Override
    public @NotNull CompassRenderer getCustomRenderer() {
        return CompassBEWLR;
    }
}
