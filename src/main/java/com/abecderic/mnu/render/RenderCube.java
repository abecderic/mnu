package com.abecderic.mnu.render;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.entity.EntityCube;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class RenderCube extends Render<EntityCube>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MNU.MODID, "textures/entity/cube.png");

    private ModelBase model = new ModelBase() {};
    private ModelRenderer cube;

    public RenderCube(RenderManager renderManager)
    {
        super(renderManager);

        cube = new ModelRenderer(model, 0, 0);
        cube.setTextureSize(64, 64);
        cube.addBox(-8f, -8f, -8f, 16, 16, 16);
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
        Minecraft.getMinecraft().mcProfiler.startSection("mnu:cube");
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        Fluid fluid = FluidRegistry.LAVA;
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite sprite = map.getTextureExtry(fluid.getStill().toString());
        if (sprite == null) {
            sprite = map.getTextureExtry(TextureMap.LOCATION_MISSING_TEXTURE.toString());
            if (sprite == null) return;
        }
        renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1);
        GlStateManager.scale(0.25f, 0.25f, 0.25f);
        GlStateManager.translate(0, 0, 0);
        GlStateManager.disableLighting();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vbo = tessellator.getBuffer();
        vbo.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        int lightmap = 0xF000F0;
        /* +x */
        vbo.pos(1D, -1D, -1D).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(1D, 1D, -1D).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(1D, 1D, 1D).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(1D, -1D, 1D).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        /* -x */
        vbo.pos(-1D, -1D, 1D).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, 1D, 1D).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, 1D, -1D).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, -1D, -1D).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        /* +y */
        vbo.pos(1D, 1D, -1D).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, 1D, -1D).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, 1D, 1D).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(1D, 1D, 1D).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        /* -y */
        vbo.pos(1D, -1D, 1D).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, -1D, 1D).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, -1D, -1D).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(1D, -1D, -1D).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        /* +z */
        vbo.pos(1D, -1D, 1D).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(1D, 1D, 1D).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, 1D, 1D).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, -1D, 1D).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        /* -z */
        vbo.pos(-1D, -1D, -1D).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(-1D, 1D, -1D).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(1D, 1D, -1D).tex(sprite.getMinU(), sprite.getMinV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        vbo.pos(1D, -1D, -1D).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(lightmap, lightmap).color(1F, 1F, 1F, 1F).endVertex();
        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }
}
