package com.abecderic.mnu.render;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.entity.EntityCube;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderCube extends Render<EntityCube>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MNU.MODID, "textures/entity/cube.png");

    private ModelBase model = new ModelBase() {};
    private ModelRenderer cube;

    public RenderCube(RenderManager renderManager)
    {
        super(renderManager);

        System.out.println("render created");
        cube = new ModelRenderer(model, 0, 0);
        cube.setTextureSize(32, 32);
        cube.addBox(-4F, -4F, -4F, 8, 8, 8);
        cube.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityCube entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityCube entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        renderManager.renderEngine.bindTexture(TEXTURE);
        cube.render(0.0625f);

        GlStateManager.popMatrix();
    }
}
