package com.abecderic.mnu.entity;

import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.util.DataSerializerFluid;
import com.abecderic.mnu.util.EnergyStorageInternal;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class EntityCube extends EntityThrowable
{
    private static final DataParameter<Integer> ENERGY = EntityDataManager.createKey(EntityCube.class, DataSerializers.VARINT);
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityCube.class, DataSerializers.OPTIONAL_ITEM_STACK);
    private static final DataParameter<FluidStack> FLUID = EntityDataManager.createKey(EntityCube.class, DataSerializerFluid.OPTIONAL_FLUID_STACK);

    private BlockPos origin;

    public EntityCube(World worldIn)
    {
        super(worldIn);
    }

    public EntityCube(World worldIn, BlockPos origin)
    {
        super(worldIn);
        this.origin = origin;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.getDataManager().register(ENERGY, 0);
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
        this.getDataManager().register(FLUID, null);
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
            if (posY >= world.getHeight() || ticksExisted >= 200 || posY < 0)
            {
                this.kill();
                pingback(null);
            }
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
                    pingback((TileEntityCubeSender) te);
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
        if (!world.isRemote)
        {
            /*StringBuilder sb = new StringBuilder();
            sb.append("Cube received: ").append(this).append(", Energy:").append(getEnergy());
            sb.append(", Item: ").append(getItem()).append(", Fluid: ");
            if (getFluid() == null)
            {
                sb.append("null");
            }
            else
            {
                sb.append(getFluid().amount).append("x").append(getFluid().getLocalizedName());
            }
            System.out.println(sb.toString());*/

            IEnergyStorage storage = receiver.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null && storage instanceof EnergyStorageInternal)
            {
                ((EnergyStorageInternal)storage).addEnergy(getEnergy());
            }
            IFluidHandler fluidHandler = receiver.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (fluidHandler != null)
            {
                fluidHandler.fill(getFluid(), true);
            }
            IItemHandler itemHandler = receiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            ItemStack stack = getItem();
            if (itemHandler != null)
            {
                for (int i = 0; i < itemHandler.getSlots(); i++)
                {
                    stack = itemHandler.insertItem(i, stack, false);
                    if (stack.isEmpty())
                    {
                        break;
                    }
                }
            }
        }
    }

    private void splat()
    {
        if (world.isRemote)
        {
            int particleAmount = 0;
            switch (Minecraft.getMinecraft().gameSettings.particleSetting)
            {
                case 0: particleAmount = 8; break;
                case 1: particleAmount = 1; break;
            }
            EnumParticleTypes type = EnumParticleTypes.BLOCK_CRACK;
            int particleId = 0;
            if (!getItem().isEmpty())
            {
                if (getItem().getItem() instanceof ItemBlock)
                {
                    particleId = Block.getIdFromBlock(((ItemBlock) getItem().getItem()).block);
                }
                else
                {
                    particleId = ItemBlock.getIdFromItem(getItem().getItem());
                    type = EnumParticleTypes.ITEM_CRACK;
                }
            }
            else if (getFluid() != null)
            {
                particleId = Block.getIdFromBlock(getFluid().getFluid().getBlock());
            }
            for (int i = 0; i < particleAmount; i++)
            {
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + rand.nextDouble() - 0.5, this.posY, this.posZ + rand.nextDouble() - 0.5, 0, 0, 0);
                if (particleId > 0)
                {
                    world.spawnParticle(type, this.posX + rand.nextDouble() - 0.5, this.posY, this.posZ + rand.nextDouble() - 0.5, 0, 0, 0, particleId);
                }
            }
        }

        if (!world.isRemote)
        {
            pingback(null);
        }

        // TODO remove
        /* debug */
        if (!world.isRemote)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Cube splat: ").append(this).append(", Energy:").append(getEnergy());
            sb.append(", Item: ").append(getItem()).append(", Fluid: ");
            if (getFluid() == null)
            {
                sb.append("null");
            }
            else
            {
                sb.append(getFluid().amount).append("x").append(getFluid().getLocalizedName());
            }
            System.out.println(sb.toString());
        }
    }

    private void pingback(TileEntityCubeSender receiver)
    {
        if (origin != null)
        {
            TileEntity te = world.getTileEntity(origin);
            if (te != null && te instanceof TileEntityCubeSender)
            {
                if (receiver != null)
                {
                    ((TileEntityCubeSender)te).cubePingback(receiver);
                }
                else
                {
                    ((TileEntityCubeSender)te).invalidateReciever();
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey("orig_x") && compound.hasKey("orig_y") && compound.hasKey("orig_z"))
        {
            origin = new BlockPos(compound.getInteger("orig_x"), compound.getInteger("orig_y"), compound.getInteger("orig_z"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        if (origin != null)
        {
            compound.setInteger("orig_x", origin.getX());
            compound.setInteger("orig_y", origin.getY());
            compound.setInteger("orig_z", origin.getZ());
        }
        return compound;
    }

    public int getEnergy()
    {
        return getDataManager().get(ENERGY);
    }

    public void setEnergy(int energy)
    {
        getDataManager().set(ENERGY, energy);
    }

    public ItemStack getItem()
    {
        return getDataManager().get(ITEM);
    }

    public void setItem(ItemStack item)
    {
        getDataManager().set(ITEM, item);
    }

    public FluidStack getFluid()
    {
        return getDataManager().get(FLUID);
    }

    public void setFluid(FluidStack fluid)
    {
        getDataManager().set(FLUID, fluid);
    }
}
