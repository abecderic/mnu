package com.abecderic.mnu.entity;

import com.abecderic.mnu.block.TileEntityPassengerCubeSpawner;
import com.abecderic.mnu.util.DataSerializerList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EntityPassengerCube extends Entity
{
    private static final DataParameter<List<BlockPos>> LIST = EntityDataManager.createKey(EntityPassengerCube.class, DataSerializerList.POS_LIST);

    private boolean hasRider = false;
    private BlockPos origin;
    private List<BlockPos> list = new ArrayList<>();
    private double lastDistanceToNextSq = -1;
    private int index = 0;

    public EntityPassengerCube(World world)
    {
        super(world);
        isImmuneToFire = true;
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
        this.getDataManager().register(LIST, new ArrayList<>());
        list = getList();
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
            if (index >= list.size())
            {
                this.setDead();
                return;
            }
            double dx = origin.getX() + 0.5 - posX;
            double dy = origin.getY() + 0.5 - posY;
            double dz = origin.getZ() + 0.5 - posZ;
            double distanceToLastSq = dx*dx + dy*dy - dz*dz;
            dx = list.get(index).getX() + 0.5 - posX;
            dy = list.get(index).getY() + 0.5 - posY;
            dz = list.get(index).getZ() + 0.5 - posZ;
            double distanceToNextSq = dx*dx + dy*dy - dz*dz;
            if (lastDistanceToNextSq < 0)
                lastDistanceToNextSq = distanceToNextSq;
            double dist = Math.sqrt(Math.min(distanceToLastSq, distanceToNextSq));
            double move = Math.max(Math.min(dist, 1), 0.1);
            posX += motionX * move;
            posY += motionY * move;
            posZ += motionZ * move;
            if (distanceToNextSq < 0.001)
            {
                posX = list.get(index).getX() + 0.5;
                posY = list.get(index).getY() + 0.5;
                posZ = list.get(index).getZ() + 0.5;
                origin = list.get(index);
                index++;
                if (index < list.size())
                {
                    Vec3d vec = new Vec3d(list.get(index).getX() + 0.5 - posX, list.get(index).getY() + 0.5 - posY, list.get(index).getZ() + 0.5 - posZ);
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
            list = getList();
            hasRider = true;
            if (list.isEmpty())
            {
                if (!world.isRemote)
                {
                    player.sendMessage(new TextComponentTranslation("msg.passenger_cube.notconfigured"));
                }
                this.setDead();
                return true;
            }
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
        index = compound.getInteger("index");
        if (index <= 0)
        {
            origin = new BlockPos(posX, posY, posZ);
        }
        else
        {
            origin = list.get(index-1);
        }
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound)
    {
        compound.setBoolean("rider", hasRider);
        compound.setInteger("index", index);
    }

    public void setMotion(double x, double y, double z)
    {
        super.motionX = x;
        super.motionY = y;
        super.motionZ = z;
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.0d;
    }

    public List<BlockPos> getList()
    {
        return getDataManager().get(LIST);
    }

    public void setList(List<BlockPos> list)
    {
        getDataManager().set(LIST, list);
    }
}