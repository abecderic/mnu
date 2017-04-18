package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockReactorController extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    protected BlockReactorController()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.REACTOR_CONTROLLER);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.REACTOR_CONTROLLER;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (playerIn.isSneaking()) return false;
        if (!worldIn.isRemote)
        {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityReactorController)
            {
                TileEntityReactorController reactor = (TileEntityReactorController) te;
                if (!reactor.isComplete())
                {
                    EnumFacing direction = state.getValue(FACING);
                    BlockPos center = pos.offset(state.getValue(FACING), 4);
                    String[] layer0 = {"  ss ss  ", " sss sss ", "sssssssss", "sss_ _sss", "  s s s  ", "sss_ _sss", "sssssssss", " sss sss ", "  ss ss  "};
                    if (!checkYLevel(worldIn, center, 0, layer0, playerIn))
                    {
                        return true;
                    }
                    String[] layer1 = {"  sssss  ", " sssssss ", "sssssssss", "sss___sss", "sss___sss", "sss___sss", "sssssssss", " sssssss ", "  sssss  "};
                    if (!checkYLevel(worldIn, center, 1, layer1, playerIn))
                    {
                        return true;
                    }
                    if (!checkYLevel(worldIn, center, -1, layer1, playerIn))
                    {
                        return true;
                    }
                    String[] layer2 = {"         ", "   sss   ", "  sssss  ", " sssssss ", " sss_sss ", " sssssss ", "  sssss  ", "   sss   ", "         "};
                    if (!checkYLevel(worldIn, center, 2, layer2, playerIn))
                    {
                        return true;
                    }
                    if (!checkYLevel(worldIn, center, -2, layer2, playerIn))
                    {
                        return true;
                    }
                    String[] layer3 = {"         ", "         ", "   sss   ", "  sssss  ", "  ss_ss  ", "  sssss  ", "   sss   ", "         ", "         "};
                    if (!checkYLevel(worldIn, center, 3, layer3, playerIn))
                    {
                        return true;
                    }
                    if (!checkYLevel(worldIn, center, -3, layer3, playerIn))
                    {
                        return true;
                    }
                    String[] layer4 = {"         ", "         ", "         ", "   ttt   ", "   t_t   ", "   ttt   ", "         ", "         ", "         "};
                    for (int i = 0; i < 8; i++)
                    {
                        if (!checkYLevel(worldIn, center, 4+i, layer4, playerIn))
                        {
                            return true;
                        }
                        if (!checkYLevel(worldIn, center, -4-i, layer4, playerIn))
                        {
                            return true;
                        }
                    }
                    String[] layer5 = {"         ", "         ", "         ", "   sss   ", "   s_s   ", "   sss   ", "         ", "         ", "         "};
                    if (!checkYLevel(worldIn, center, 12, layer5, playerIn))
                    {
                        return true;
                    }
                    if (!checkYLevel(worldIn, center, -12, layer5, playerIn))
                    {
                        return true;
                    }
                    String[] layer6 = {"         ", "         ", "         ", "   sss   ", "   sss   ", "   sss   ", "         ", "         ", "         "};
                    if (!checkYLevel(worldIn, center, 13, layer6, playerIn))
                    {
                        return true;
                    }
                    if (!checkYLevel(worldIn, center, -13, layer6, playerIn))
                    {
                        return true;
                    }
                    if (!checkCenterSpecial(worldIn, center, direction, playerIn))
                    {
                        return true;
                    }
                    reactor.setComplete(true);
                }
            }
        }
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facingIn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        EnumFacing facing;
        if (placer == null)
        {
            facing = EnumFacing.NORTH;
        }
        else
        {
            facing = placer.getHorizontalFacing();
        }
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.getHorizontal(meta);
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        EnumFacing facing = state.getValue(FACING);
        return facing.getHorizontalIndex();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityReactorController();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    private boolean checkYLevel(World worldIn, BlockPos center, int y, String[] pattern, EntityPlayer playerIn)
    {
        for (int x = -4; x <= 4; x++)
        {
            for (int z = -4; z <= 4; z++)
            {
                char c = pattern[x+4].charAt(z+4);
                BlockPos p = center.add(x, y, z);
                IBlockState block = worldIn.getBlockState(p);
                switch (c)
                {
                    case 's':
                        if (block.getBlock() != MNUBlocks.reactorCasing || block.getValue(BlockReactorCasing.TRANSPARENT))
                        {
                            playerIn.sendMessage(new TextComponentTranslation("msg.reactor.incomplete", p.getX(), p.getY(), p.getZ()));
                            return false;
                        }
                        break;
                    case 't':
                        if (block.getBlock() != MNUBlocks.reactorCasing || !block.getValue(BlockReactorCasing.TRANSPARENT))
                        {
                            playerIn.sendMessage(new TextComponentTranslation("msg.reactor.incomplete4", p.getX(), p.getY(), p.getZ()));
                            return false;
                        }
                        break;
                    case '_':
                        if (!block.getBlock().isAir(block, worldIn, p))
                        {
                            playerIn.sendMessage(new TextComponentTranslation("msg.reactor.incomplete2", p.getX(), p.getY(), p.getZ()));
                            return false;
                        }
                        break;
                }
                if (c == 's' || c == 't')
                {
                    TileEntity checkTE = worldIn.getTileEntity(p);
                    if (checkTE != null && checkTE instanceof TileEntityNotifySlave)
                    {
                        ((TileEntityNotifySlave) checkTE).setMaster(p);
                    }
                    else
                    {
                        playerIn.sendMessage(new TextComponentTranslation("msg.reactor.incomplete", p.getX(), p.getY(), p.getZ()));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkCenterSpecial(World worldIn, BlockPos center, EnumFacing facing, EntityPlayer playerIn)
    {
        for (int i = 0; i < 4; i++)
        {
            BlockPos p1 = center.offset(EnumFacing.getHorizontal(i), 3);
            BlockPos p2 = center.offset(EnumFacing.getHorizontal(i), 4);
            BlockPos p3 = center.offset(EnumFacing.getHorizontal(i), 1);
            IBlockState b1 = worldIn.getBlockState(p1);
            IBlockState b2 = worldIn.getBlockState(p2);
            IBlockState b3 = worldIn.getBlockState(p3);
            if (i == facing.getOpposite().getHorizontalIndex())
            {
                if (b1.getBlock() != MNUBlocks.reactorCasing || b1.getValue(BlockReactorCasing.TRANSPARENT))
                {
                    playerIn.sendMessage(new TextComponentTranslation("msg.reactor.incomplete", p1.getX(), p1.getY(), p1.getZ()));
                    return false;
                }
                if (b3.getBlock() != MNUBlocks.reactorCasing || b1.getValue(BlockReactorCasing.TRANSPARENT))
                {
                    playerIn.sendMessage(new TextComponentTranslation("msg.reactor.incomplete", p3.getX(), p3.getY(), p3.getZ()));
                    return false;
                }
            }
            else
            {
                if (b1.getBlock() != MNUBlocks.cubeSender)
                {
                    playerIn.sendMessage(new TextComponentTranslation("msg.reactor.incomplete3", p1.getX(), p1.getY(), p1.getZ()));
                    return false;
                }
                if (!b2.getBlock().isAir(b2, worldIn, p2))
                {
                    playerIn.sendMessage(new TextComponentTranslation("msg.reactor.incomplete2", p2.getX(), p2.getY(), p2.getZ()));
                    return false;
                }
                if (!b3.getBlock().isAir(b3, worldIn, p3))
                {
                    playerIn.sendMessage(new TextComponentTranslation("msg.reactor.incomplete2", p3.getX(), p3.getY(), p3.getZ()));
                    return false;
                }
            }
        }
        return true;
    }
}
