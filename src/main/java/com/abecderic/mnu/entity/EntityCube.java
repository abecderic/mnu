package com.abecderic.mnu.entity;

import com.abecderic.mnu.block.TileEntityCubeSender;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityCube extends EntityThrowable
{
    public EntityCube(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void onUpdate()
    {
        double prevMotionX = motionX;
        double prevMotionY = motionY;
        double prevMotionZ = motionZ;
        super.onUpdate();
        /* reset motion since cubes are not affected by gravity */
        motionX = prevMotionX;
        motionY = prevMotionY;
        motionZ = prevMotionZ;

        if (!world.isRemote)
        {
            //System.out.println(posX + ", " + posY + ", " + posZ + " (" + motionX + ", " + motionY + ", " + motionZ + ")");
        }
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            TileEntity te = world.getTileEntity(result.getBlockPos());
            if (te != null && te instanceof TileEntityCubeSender)
            {
                if (!world.isRemote)
                {
                    ((TileEntityCubeSender) te).receiveCube(this);
                    this.world.setEntityState(this, (byte)3);
                    this.setDead();
                }
            }
            else
            {
                splat();
            }
        }
        else
        {
            splat();
        }
        this.kill();
    }

    private void splat()
    {
        for (int i = 0; i < 8; i++)
        {
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + rand.nextDouble() - 0.5, this.posY, this.posZ + rand.nextDouble() - 0.5, 0, 0, 0);
        }
    }
}
