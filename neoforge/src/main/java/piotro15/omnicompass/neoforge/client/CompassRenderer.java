package piotro15.omnicompass.neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.common.registry.ModDataComponents;

public class CompassRenderer extends BlockEntityWithoutLevelRenderer {
    public static final CompassRenderer INSTANCE = new CompassRenderer();
    private static final ResourceLocation DEFAULT_COMPASS_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "item/compass");

    public CompassRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack itemStack, @NotNull ItemDisplayContext itemDisplayContext, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        if (itemStack.isEmpty()) {
            return;
        }

        ResourceLocation compassType = itemStack.has(ModDataComponents.COMPASS_TYPE.get())
                ? itemStack.get(ModDataComponents.COMPASS_TYPE.get())
                : null;

        if (compassType == null) {
            super.renderByItem(itemStack, itemDisplayContext, poseStack, multiBufferSource, packedLight, packedOverlay);
            return;
        }

        ResourceLocation baseModelLocation = ResourceLocation.fromNamespaceAndPath(compassType.getNamespace(), "compass/" + compassType.getPath());
        ResourceLocation needleModelLocation = ResourceLocation.fromNamespaceAndPath(compassType.getNamespace(), "compass/" + compassType.getPath() + "_needle");

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(baseModelLocation));
        BakedModel needleModel = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(needleModelLocation));
        if (!needleModel.equals(Minecraft.getInstance().getModelManager().getMissingModel())) {
            needleModel = needleModel.getOverrides().resolve(needleModel, itemStack, Minecraft.getInstance().level, Minecraft.getInstance().player, packedLight);
        }
        baseModel = baseModel.getOverrides().resolve(baseModel, itemStack, Minecraft.getInstance().level, Minecraft.getInstance().player, packedLight);


        if (baseModel != null && !baseModel.equals(Minecraft.getInstance().getModelManager().getMissingModel())) {
            poseStack.popPose();
            poseStack.pushPose();

            baseModel.applyTransform(itemDisplayContext, poseStack, isLeftHand(itemDisplayContext));

            poseStack.translate(-0.5F, -0.5F, -0.5F);

            RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, true);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
            renderer.renderModelLists(baseModel, itemStack, packedLight, packedOverlay, poseStack, vertexConsumer);

            if (needleModel != null && !needleModel.equals(Minecraft.getInstance().getModelManager().getMissingModel())) {
                renderer.renderModelLists(needleModel, itemStack, packedLight, packedOverlay, poseStack, vertexConsumer);
            }
        }
    }

    private static boolean isLeftHand(ItemDisplayContext itemDisplayContext) {
        return itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }
}
