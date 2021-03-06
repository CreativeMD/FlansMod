package com.flansmod.common;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class BlockItemHolder extends BlockContainer
{
	public ItemHolderType type;
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	public BlockItemHolder(ItemHolderType type) 
	{
		super(Material.ROCK);
		this.type = type;
		setCreativeTab(FlansMod.tabFlanParts);
		setHardness(2F);
		setResistance(4F);
	    setUnlocalizedName(type.shortName);
	    GameRegistry.registerBlock(this, type.shortName);
		setCreativeTab(FlansMod.tabFlanParts);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		type.block = this;
		type.item = Item.getItemFromBlock(this);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
	    return null;
	}
		
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
	   return false;
	}
		
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	@Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        EnumFacing enumfacing = EnumFacing.fromAngle((double)placer.rotationYaw);
        worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
    }
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        if (!facing.getAxis().isHorizontal())
        {
            facing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, facing.getOpposite());
    }
	
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    public int getMetaFromState(IBlockState state)
    {
        byte b0 = 0;
        int i = b0 | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
        return i;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }

    
	@Override
	public boolean canPlaceBlockAt(World par1World, BlockPos pos)
	{
	    return par1World.isSideSolid(pos.add(0, -1, 0), EnumFacing.UP);
	}
    
    /*@Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, BlockPos pos)
    {
    	setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }
    
    @Override
    public void setBlockBoundsForItemRender()
    {
        float var1 = 0.5F;
        float var2 = 0.015625F;
        float var3 = 0.5F;
        this.setBlockBounds(0.0F, 0.5F - var2, 0.0F, 1F, 0.5F + var2, 1F);
    }

	@Override
	public int getMobilityFlag()
	{
		return 1;
	}*/

	@Override
	public TileEntity createNewTileEntity(World var1, int i)
	{
		return new TileEntityItemHolder(type);
	}
		
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	if(world.isRemote)
    	{
    		FlansMod.playerHandler.getPlayerData(player, Side.CLIENT).shootTimeLeft = FlansMod.playerHandler.getPlayerData(player, Side.CLIENT).shootTimeRight = 10;
    		return true;
    	}

    	TileEntityItemHolder holder = (TileEntityItemHolder)world.getTileEntity(pos);
    	ItemStack item = player.getHeldItemMainhand();
    	
    	if(!holder.getStackInSlot(0).isEmpty())
    	{
    		holder.setInventorySlotContents(0, item);
    		player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
    	}
    	else
    	{
    		world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), holder.getStackInSlot(0)));
    		holder.setInventorySlotContents(0, null);
    		FlansMod.playerHandler.getPlayerData(player, Side.SERVER).shootTimeLeft = FlansMod.playerHandler.getPlayerData(player, Side.SERVER).shootTimeRight = 10;
    	}
    	
    	//world.markUpdate(pos);

        return true;
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }
}
