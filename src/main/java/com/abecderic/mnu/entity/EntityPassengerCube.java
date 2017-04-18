package com.abecderic.mnu.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityPassengerCube extends Entity
{
    private boolean hasRider = false;

    public EntityPassengerCube(World world)
    {
        super(world);
        isImmuneToFire = true;
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
        if (!world.isRemote && hasRider && !isBeingRidden())
        {
            this.setDead();
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND)
        {
            player.startRiding(this);
            hasRider = true;
        }
        return false;
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