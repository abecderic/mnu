package com.abecderic.mnu.block;

import com.abecderic.mnu.entity.EntityCube;
import com.abecderic.mnu.fluid.MNUFluids;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityReactorController extends TileEntity implements ITickable, INotifyMaster
{
    private static final int RUNTIME = 20000;
    private static final int ENERGY_PER_TICK = 500000;

    private int tickPart;
    private boolean isComplete;
    private int runPart = 0;
    private TileEntityCubeSender fluidProvider1;
    private TileEntityCubeSender fluidProvider2;
    private TileEntityCubeSender energyReceiver;

    public TileEntityReactorController()
    {
        super();
        tickPart = (int)(Math.random() * 20D);
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (!isComplete) return;
            if (runPart > 0)
            {
                if (energyReceiver != null)
                {
                    energyReceiver.addEnergyInternal(ENERGY_PER_TICK);
                    energyReceiver.doSendCube();
                }
                runPart--;
                if (world.getTotalWorldTime() % 20 == tickPart)
                {
                    EnumFacing facing = world.getBlockState(pos).getValue(BlockReactorController.FACING);
                    BlockPos center = pos.offset(facing, 4);
                    EntityCube cube = new EntityCube(world, null);
                    BlockPos spawn = center.offset(EnumFacing.UP, 12);
                    cube.setPosition(spawn.getX() + 0.5, spawn.getY() + 0.5, spawn.getZ() + 0.5);
                    cube.setMotion(0, -1, 0);
                    world.spawnEntity(cube);
                    cube = new EntityCube(world, null);
                    spawn = center.offset(EnumFacing.DOWN, 12);
                    cube.setPosition(spawn.getX() + 0.5, spawn.getY() + 0.5, spawn.getZ() + 0.5);
                    cube.setMotion(0, 1, 0);
                    world.spawnEntity(cube);
                }
            }
            else if (world.getTotalWorldTime() % 20 == tickPart)
            {
                if (fluidProvider1 != null && fluidProvider2 != null)
                {
                    IFluidHandler fluidHandler1 = fluidProvider1.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    IFluidHandler fluidHandler2 = fluidProvider2.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    if (fluidHandler1 != null && fluidHandler2 != null)
                    {
                        FluidStack dm1 = fluidHandler1.drain(new FluidStack(MNUFluids.fluidDarkMatter, 1000), false);
                        FluidStack mnu1 = fluidHandler1.drain(new FluidStack(MNUFluids.fluidMNU, 1000), false);
                        FluidStack dm2 = fluidHandler2.drain(new FluidStack(MNUFluids.fluidDarkMatter, 1000), false);
                        FluidStack mnu2 = fluidHandler2.drain(new FluidStack(MNUFluids.fluidMNU, 1000), false);
                        if (dm1 != null && dm1.amount == 1000)
                        {
                            if (mnu1 != null && mnu1.amount == 1000)
                            {
                                fluidHandler1.drain(new FluidStack(MNUFluids.fluidDarkMatter, 1000), true);
                                fluidHandler1.drain(new FluidStack(MNUFluids.fluidMNU, 1000), true);
                                runPart = RUNTIME;
                            }
                            else if (mnu2 != null && mnu2.amount == 1000)
                            {
                                fluidHandler1.drain(new FluidStack(MNUFluids.fluidDarkMatter, 1000), true);
                                fluidHandler2.drain(new FluidStack(MNUFluids.fluidMNU, 1000), true);
                                runPart = RUNTIME;
                            }
                        }
                        else if (dm2 != null && dm2.amount == 1000)
                        {
                            if (mnu1 != null && mnu1.amount == 1000)
                            {
                                fluidHandler2.drain(new FluidStack(MNUFluids.fluidDarkMatter, 1000), true);
                                fluidHandler1.drain(new FluidStack(MNUFluids.fluidMNU, 1000), true);
                                runPart = RUNTIME;
                            }
                            else if (mnu2 != null && mnu2.amount == 1000)
                            {
                                fluidHandler2.drain(new FluidStack(MNUFluids.fluidDarkMatter, 1000), true);
                                fluidHandler2.drain(new FluidStack(MNUFluids.fluidMNU, 1000), true);
                                runPart = RUNTIME;
                            }
                        }
                    }
                }
                else
                {
                    readCubeSenders();
                }
            }
            markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        isComplete = compound.getBoolean("complete");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setBoolean("complete", isComplete);
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
    }

    public boolean isComplete()
    {
        return isComplete;
    }

    public void setComplete(boolean complete)
    {
        isComplete = complete;
    }

    private void readCubeSenders()
    {
        EnumFacing facing = world.getBlockState(pos).getValue(BlockReactorController.FACING);
        BlockPos center = pos.offset(facing, 4);
        energyReceiver = (TileEntityCubeSender) world.getTileEntity(center.offset(facing, 3));
        fluidProvider1 = (TileEntityCubeSender) world.getTileEntity(center.offset(facing.rotateYCCW(), 3));
        fluidProvider2 = (TileEntityCubeSender) world.getTileEntity(center.offset(facing.rotateY(), 3));
    }

    @Override
    public void addBlock(BlockPos pos, EntityPlayer player)
    {
        /* NO-OP */
    }

    @Override
    public void removeBlock(BlockPos pos, EntityPlayer player)
    {
        setComplete(false);
    }
}
