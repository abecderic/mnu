package com.abecderic.mnu.entity;

import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.util.DataSerializerFluid;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;

public class EntityCube extends EntityThrowable
{
    private static final DataParameter<Integer> ENERGY = EntityDataManager.createKey(EntityCube.class, DataSerializers.VARINT);
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityCube.class, DataSerializers.OPTIONAL_ITEM_STACK);
    private static final DataParameter<FluidStack> FLUID = EntityDataManager.createKey(EntityCube.class, DataSerializerFluid.OPTIONAL_FLUID_STACK);

    public EntityCube(World worldIn)
    {
        super(worldIn);
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
        IEnergyStorage storage = receiver.getCapability(CapabilityEnergy.ENERGY, EnumFacing.getFacingFromVector((float)motionX, (float)motionY, (float)motionZ).getOpposite());
        if (storage != null)
        {
            storage.receiveEnergy(getEnergy(), false);
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
            if (getItem() != ItemStack.EMPTY)
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
