package com.abecderic.mnu.entity;

import com.abecderic.mnu.block.TileEntityPassengerCubeSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EntityPassengerCube extends Entity
{
    private boolean hasRider = false;
    private BlockPos origin;
    private List<BlockPos> list = new ArrayList<>();
    private List<Double> sList = new ArrayList<>();
    private double lastDistanceToNextSq = -1;

    public EntityPassengerCube(World world)
    {
        super(world);
        isImmuneToFire = true;
        list.add(new BlockPos(370, 56, -186));
        sList.add(1D);
        list.add(new BlockPos(370, 79, -186));
        sList.add(1D);
        list.add(new BlockPos(374, 70, -194));
        sList.add(1D);
        list.add(new BlockPos(358, 66, -180));
        sList.add(1D);
        list.add(new BlockPos(388, 59, -186));
        sList.add(1D);
    }

    public EntityPassengerCube(World worldIn, BlockPos origin)
    {
        this(worldIn);
        this.origin = origin;
    }

    @Override
    protected void entityInit()
    {
        setSize(0.75F, 0.4F);
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (!world.isRemote)
        {
            if (posY >= 1024)
            {
                this.setDead();
            }
            if (hasRider)
            {
                if (!isBeingRidden())
                {
                    this.setDead();
                }
            }
        }
        if (hasRider)
        {
            if (list.isEmpty())
            {
                this.setDead();
                return;
            }
            double dx = origin.getX() + 0.5 - posX;
            double dy = origin.getY() + 0.5 - posY;
            double dz = origin.getZ() + 0.5 - posZ;
            double distanceToLastSq = dx*dx + dy*dy - dz*dz;
            dx = list.get(0).getX() + 0.5 - posX;
            dy = list.get(0).getY() + 0.5 - posY;
            dz = list.get(0).getZ() + 0.5 - posZ;
            double distanceToNextSq = dx*dx + dy*dy - dz*dz;
            if (lastDistanceToNextSq < 0)
                lastDistanceToNextSq = distanceToNextSq;
            double dist = Math.sqrt(Math.min(distanceToLastSq, distanceToNextSq));
            double move = Math.max(Math.min(dist, sList.get(0)), 0.1);
            posX += motionX * move;
            posY += motionY * move;
            posZ += motionZ * move;
            if (distanceToNextSq < 0.001)
            {
                posX = list.get(0).getX() + 0.5;
                posY = list.get(0).getY() + 0.5;
                posZ = list.get(0).getZ() + 0.5;
                origin = list.get(0);
                list.remove(0);
                sList.remove(0);
                if (!list.isEmpty())
                {
                    Vec3d vec = new Vec3d(list.get(0).getX() + 0.5 - posX, list.get(0).getY() + 0.5 - posY, list.get(0).getZ() + 0.5 - posZ);
                    vec = vec.normalize();
                    motionX = vec.xCoord;
                    motionY = vec.yCoord;
                    motionZ = vec.zCoord;
                }
            }
            lastDistanceToNextSq = distanceToNextSq;
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND)
        {
            player.startRiding(this);
            if (origin != null)
            {
                TileEntity te = world.getTileEntity(origin);
                if (te != null && te instanceof TileEntityPassengerCubeSpawner)
                {
                    ((TileEntityPassengerCubeSpawner) te).spawnPassengerCube();
                }
            }
        }
        if (hand == EnumHand.MAIN_HAND)
        {
            hasRider = true;
            origin = new BlockPos(posX, posY, posZ);
            Vec3d vec = new Vec3d(list.get(0).getX() + 0.5 - posX, list.get(0).getY() + 0.5 - posY, list.get(0).getZ() + 0.5 - posZ);
            vec = vec.normalize();
            motionX = vec.xCoord;
            motionY = vec.yCoord;
            motionZ = vec.zCoord;
        }
        return true;
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound)
    {
        hasRider = compound.getBoolean("rider");
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound)
    {
        compound.setBoolean("rider", hasRider);
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.0d;
    }
}