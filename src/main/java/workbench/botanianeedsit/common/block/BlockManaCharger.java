/**
 * This class was created by Workbench
 * File created at [Jan 10, 2019, 15:24 UTC + 7]
 */
package workbench.botanianeedsit.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaPool;
import workbench.botanianeedsit.Mod;
import workbench.botanianeedsit.ModBlocks;
import workbench.botanianeedsit.common.tile.TileManaCharger;

import javax.annotation.Nullable;

public class BlockManaCharger extends BlockBase {
    public static final AxisAlignedBB AABB = new AxisAlignedBB((1f/8) + (1f/32), (1f/16) + (1f/32), (1f/8) + (1f/32), 1 - (1f/8) - (1f/32), (1f/16) + (1f/32) + (1f/8), 1 - (1f/8) - (1f/32));

    public BlockManaCharger(String name, boolean registerCreativeTab) {
        super(name, registerCreativeTab, Material.ROCK);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;

        if (!checkAndDestroy(worldIn, pos, state))
            return true;

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileManaCharger) {
            return ((TileManaCharger) tileEntity).handleClick(playerIn, hand);
        } else return true;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        checkAndDestroy((World) world, pos, world.getBlockState(pos));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileManaCharger)
                ((TileManaCharger) te).onBlockBreak();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return canStay(worldIn, pos);
    }

    protected boolean checkAndDestroy(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote) return true;
        if (canStay(world, pos)) return true;
        else {
            Mod.logger.info("break block");
            world.setBlockToAir(pos);
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModBlocks.blockManaCharger, 1));
            return false;
        }
    }

    public boolean canStay(World world, BlockPos pos) {
        return world.getTileEntity(pos.down()) instanceof IManaPool;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileManaCharger();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
}