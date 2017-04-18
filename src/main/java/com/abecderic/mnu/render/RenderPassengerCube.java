package com.abecderic.mnu.render;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.entity.EntityPassengerCube;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderPassengerCube extends Render<EntityPassengerCube>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MNU.MODID, "textures/entity/passenger_cube.png");

    private ModelBase model = new ModelBase() {};
    private ModelRenderer cube;

    public RenderPassengerCube(RenderManager renderManager)
    {
        super(renderManager);

        cube = new ModelRenderer(model, 0, 0);
        cube.setTextureSize(64, 64);
        cube.addBox(-8f, -8f, -8f, 16, 16, 16);
        cube.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPassengerCube entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityPassengerCube entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Minecraft.getMinecraft().mcProfiler.startSection("mnu:passenger_cube");
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderManager.renderEngine.bindTexture(TEXTURE);
        cube.render(0.046875f);
        GlStateManager.popMatrix();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }
}
