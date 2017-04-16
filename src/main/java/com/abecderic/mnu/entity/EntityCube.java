package com.abecderic.mnu.entity;

import com.abecderic.mnu.block.TileEntityCubeSender;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

public class EntityCube extends EntityThrowable
{
    private static final DataParameter<Integer> ENERGY = EntityDataManager.createKey(EntityCube.class, DataSerializers.VARINT);

    public EntityCube(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.getDataManager().register(ENERGY, 0);
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
                    receiveCube((TileEntityCubeSender) te);
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

    private void receiveCube(TileEntityCubeSender receiver)
    {
        receiver.getCapability(CapabilityEnergy.ENERGY, EnumFacing.getFacingFromVector((float)motionX, (float)motionY, (float)motionZ).getOpposite()).receiveEnergy(getEnergy(), false);
    }

    private void splat()
    {
        for (int i = 0; i < 8; i++)
        {
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + rand.nextDouble() - 0.5, this.posY, this.posZ + rand.nextDouble() - 0.5, 0, 0, 0);
        }

        // TODO remove
        /* debug */
        if (!world.isRemote)
        {
            System.out.println("Cube splat: " + this + ", Energy: " + getEnergy());
        }
    }

    public int getEnergy()
    {
        return getDataManager().get(ENERGY);
    }

    public void setEnergy(int energy)
    {
        getDataManager().set(ENERGY, energy);
    }
}
