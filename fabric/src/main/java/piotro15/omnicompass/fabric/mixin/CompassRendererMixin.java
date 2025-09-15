package piotro15.omnicompass.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;

@Mixin(ItemRenderer.class)
public abstract class CompassRendererMixin {
    @Shadow protected abstract void renderModelLists(BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer);

    @Inject(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void omnicompass$customRender(
            ItemStack itemStack,
            ItemDisplayContext itemDisplayContext,
            boolean bl,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            int j,
            BakedModel bakedModel,
            CallbackInfo ci
    ) {
        if (!itemStack.isEmpty() && itemStack.getItem() == ModItems.COMPASS.get()) {
            ResourceLocation compassType = itemStack.has(ModDataComponents.COMPASS_TYPE.get())
                    ? itemStack.get(ModDataComponents.COMPASS_TYPE.get())
                    : null;

            if (compassType != null) {
                ResourceLocation baseModelLocation = ResourceLocation.fromNamespaceAndPath(compassType.getNamespace(), "compass/" + compassType.getPath());
                ResourceLocation needleModelLocation = ResourceLocation.fromNamespaceAndPath(compassType.getNamespace(), "compass/" + compassType.getPath() + "_needle");

                BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(baseModelLocation);
                BakedModel needleModel = Minecraft.getInstance().getModelManager().getModel(needleModelLocation);
                if (needleModel != null && !needleModel.equals(Minecraft.getInstance().getModelManager().getMissingModel())) {
                    needleModel = needleModel.getOverrides().resolve(needleModel, itemStack, Minecraft.getInstance().level, Minecraft.getInstance().player, i);
                }

                if (baseModel != null && !baseModel.equals(Minecraft.getInstance().getModelManager().getMissingModel())) {
                    poseStack.pushPose();

                    if (needleModel != null && !needleModel.equals(Minecraft.getInstance().getModelManager().getMissingModel())) {
                        needleModel.getTransforms().getTransform(itemDisplayContext).apply(bl, poseStack);
                    } else {
                        baseModel.getTransforms().getTransform(itemDisplayContext).apply(bl, poseStack);
                    }

                    poseStack.translate(-0.5F, -0.5F, -0.5F);

                    RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, true);
                    VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
                    this.renderModelLists(baseModel, itemStack, i, j, poseStack, vertexConsumer);

                    if (needleModel != null && !needleModel.equals(Minecraft.getInstance().getModelManager().getMissingModel())) {
                        poseStack.pushPose();

                        this.renderModelLists(needleModel, itemStack, i, j, poseStack, vertexConsumer);
                        poseStack.popPose();
                    }

                    poseStack.popPose();

                    ci.cancel();
                }
            }
        }
    }
}