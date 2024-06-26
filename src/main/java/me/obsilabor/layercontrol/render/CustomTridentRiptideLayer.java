package me.obsilabor.layercontrol.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class CustomTridentRiptideLayer<T extends LivingEntity> extends RenderLayer<T, PlayerModel<T>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/trident_riptide.png");
    private final ModelPart box;

    public CustomTridentRiptideLayer(RenderLayerParent<T, PlayerModel<T>> renderLayerParent, EntityModelSet entityModelSet) {
        super(renderLayerParent);
        ModelPart modelPart = entityModelSet.bakeLayer(ModelLayers.PLAYER_SPIN_ATTACK);
        this.box = modelPart.getChild("box");
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        for(int m = 0; m < 3; ++m) {
            poseStack.pushPose();
            float n = j * (float)(-(45 + m * 5));
            poseStack.mulPose(Axis.YP.rotationDegrees(n));
            float o = 0.75F * (float)m;
            poseStack.scale(o, o, o);
            poseStack.translate(0.0, -0.2F + 0.6F * (float)m, 0.0);
            this.box.render(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}
