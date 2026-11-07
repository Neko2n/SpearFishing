package dev.nekotune.spearfishing.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.nekotune.spearfishing.shared.definition.entity.projectile.ThrownSpear;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Renders a thrown spear as its item model
 */
public class ThrownSpearRenderer<T extends ThrownSpear> extends EntityRenderer<T> {

    private final ItemRenderer itemRenderer;

    public ThrownSpearRenderer(final Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(final T entity, final float entityYaw, final float partialTicks, final PoseStack poseStack,
            final MultiBufferSource buffer, final int packedLight) {
        poseStack.pushPose();
        poseStack.scale(2.0f, 2.0f, 2.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) + 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-1.0f * Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) - 45.0f - 90.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.translate(-0.15f, -0.25f, 0.0f);
        itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
