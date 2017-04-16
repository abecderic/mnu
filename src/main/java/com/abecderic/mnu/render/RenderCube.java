package com.abecderic.mnu.render;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.entity.EntityCube;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class RenderCube extends Render<EntityCube>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MNU.MODID, "textures/entity/cube.png");
    private static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation(MNU.MODID, "textures/entity/cube_overlay.png");

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

        if (!entity.getItem().isEmpty())
        {
            doRenderItem(entity, entity.getItem());
        }
        else if (entity.getFluid() != null)
        {
            doRenderFluid(entity.getFluid().getFluid());
        }
        else
        {
            renderManager.renderEngine.bindTexture(TEXTURE);
            cube.render(0.03125f);
        }

        renderManager.renderEngine.bindTexture(TEXTURE_OVERLAY);
        cube.render(0.0325f);

        GlStateManager.popMatrix();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    private void doRenderItem(EntityCube entity, ItemStack item)
    {
        GlStateManager.pushMatrix();
        if (item.getItem() instanceof ItemBlock)
        {
            GlStateManager.scale(0.75f, 0.75f, 0.75f);
        }
        else
        {
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
        }
        double angle = 0.0D;
        if (entity.motionZ < 0)
        {
            angle = 90.0D;
        }
        else if (entity.motionZ > 0)
        {
            angle = 270.0D;
        }
        else if (entity.motionX < 0)
        {
            angle = 180.0D;
        }
        GL11.glRotated(angle, 0.0D, 1.0D, 0.0D);
        Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
    }

    private void doRenderFluid(Fluid fluid)
    {
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
        GlStateManager.disableLighting();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

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

        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
