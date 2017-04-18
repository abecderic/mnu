package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockMirror extends BlockNotifySlave
{
    protected BlockMirror()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.MIRROR);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
        setTickRandomly(true);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.MIRROR;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityMirror();
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        super.randomTick(worldIn, pos, state, random);
        if (!check(worldIn, pos))
        {
            notifyBlockRemoved(worldIn, pos);
        }
    }

    private boolean check(World world, BlockPos pos)
    {
        return world.canBlockSeeSky(pos.add(0, 1, 0));
    }

    @Override
    public void tryLink(World world, BlockPos pos, BlockPos master, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(master);
        if (te != null && te instanceof TileEntitySolarFusionController)
        {
            TileEntitySolarFusionController controller = (TileEntitySolarFusionController) te;
            int ylevel = controller.getMirrorsYLevel();
            if (ylevel >= 0)
            {
                if (pos.getY() != ylevel)
                {
                    player.sendMessage(new TextComponentTranslation("msg.mirror.ylevel", ylevel));
                    return;
                }
            }
            if (!check(world, pos))
            {
                player.sendMessage(new TextComponentTranslation("msg.mirror.seesky"));
                return;
            }
            if (pos.distanceSqToCenter(master.getX() + 0.5, master.getY() + 0.5, master.getZ() + 0.5) > 576)
            {
                player.sendMessage(new TextComponentTranslation("msg.mirror.toofar"));
                return;
            }
            TileEntity mirrorTE = world.getTileEntity(pos);
            if (mirrorTE != null && mirrorTE instanceof TileEntityNotifySlave)
            {
                ((TileEntityNotifySlave) mirrorTE).setMaster(master);
                controller.addBlock(pos);
                player.sendMessage(new TextComponentTranslation("msg.mirror.success"));
            }
        }
    }
}
