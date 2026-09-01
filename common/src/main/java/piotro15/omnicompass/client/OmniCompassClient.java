package piotro15.omnicompass.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModRegistries;

public class OmniCompassClient {
    public static class CompassWrapper implements ItemModel {
        private final ItemModel originalModel;

        public CompassWrapper(ItemModel originalModel) {
            this.originalModel = originalModel;
        }

        @Override
        public void update(@NonNull ItemStackRenderState renderState, ItemStack stack, @NonNull ItemModelResolver resolver, @NonNull ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
            Identifier compassId = stack.has(ModDataComponents.COMPASS_TYPE.get())
                    ? stack.get(ModDataComponents.COMPASS_TYPE.get())
                    : null;

            if (level != null && compassId != null) {
                var compassType = level.registryAccess().lookupOrThrow(ModRegistries.COMPASS_TYPE).get(compassId);

                if (compassType.isPresent()) {
                    if (!compassType.get().value().model().equals(Identifier.withDefaultNamespace("compass"))) {
                        ItemModel overrideModel = Minecraft.getInstance().getModelManager().getItemModel(compassType.get().value().model());

                        overrideModel.update(renderState, stack, resolver, displayContext, level, owner, seed);
                        return;
                    }
                }
            }

            this.originalModel.update(renderState, stack, resolver, displayContext, level, owner, seed);
        }
    }
}
