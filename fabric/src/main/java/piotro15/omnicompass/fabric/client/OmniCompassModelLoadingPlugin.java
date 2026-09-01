package piotro15.omnicompass.fabric.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.client.OmniCompassClient;

public class OmniCompassModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void initialize(Context context) {
        ModelModifier.AfterBakeItem modifier = (model, renderContext) -> {
            if (renderContext.itemId().equals(OmniCompass.id("compass"))) {
                return new OmniCompassClient.CompassWrapper(model);
            }
            return model;
        };

        context.modifyItemModelAfterBake().register(modifier);
    }
}
