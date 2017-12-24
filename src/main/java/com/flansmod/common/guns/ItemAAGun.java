package com.flansmod.common.guns;

import java.util.ArrayList;

import com.flansmod.common.FlansMod;
import com.flansmod.common.types.IFlanItem;
import com.flansmod.common.types.InfoType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAAGun extends Item implements IFlanItem
{
    public static final ArrayList<String> names = new ArrayList<String>();
	public AAGunType type;

	public ItemAAGun(AAGunType type1)
	{
		maxStackSize = 1;
		type = type1;
		type.item = this;
		setCreativeTab(FlansMod.tabFlanGuns);
		GameRegistry.registerItem(this, type.shortName, FlansMod.MODID);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn)
	{
		if(handIn == EnumHand.OFF_HAND)
			return super.onItemRightClick(world, player, handIn);
		ItemStack itemstack = player.getHeldItemMainhand();
    	//Raytracing
        float cosYaw = MathHelper.cos(-player.rotationYaw * 0.01745329F - 3.141593F);
        float sinYaw = MathHelper.sin(-player.rotationYaw * 0.01745329F - 3.141593F);
        float cosPitch = -MathHelper.cos(-player.rotationPitch * 0.01745329F);
        float sinPitch = MathHelper.sin(-player.rotationPitch * 0.01745329F);
        double length = 5D;
        Vec3d posVec = new Vec3d(player.posX, player.posY + 1.62D - player.getYOffset(), player.posZ);        
        Vec3d lookVec = posVec.addVector(sinYaw * cosPitch * length, sinPitch * length, cosYaw * cosPitch * length);
        RayTraceResult movingobjectposition = world.rayTraceBlocks(posVec, lookVec, true);
        
        //Result check
		if (movingobjectposition == null)
		{
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
		}
		if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			int i = movingobjectposition.getBlockPos().getX();
			int j = movingobjectposition.getBlockPos().getY();
			int k = movingobjectposition.getBlockPos().getZ();
			if (!world.isRemote && world.isSideSolid(movingobjectposition.getBlockPos(), EnumFacing.UP))
			{
				world.spawnEntity(new EntityAAGun(world, type, (double) i + 0.5F, (double) j + 1F, (double) k + 0.5F, player));
			}
			if (!player.capabilities.isCreativeMode)
			{
				
				itemstack.shrink(1);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
	}
	
	public Entity spawnAAGun(World world, double x, double y, double z, ItemStack stack)
	{
		Entity entity = new EntityAAGun(world, type, x, y, z, null);
		if(!world.isRemote)
		{
			world.spawnEntity(entity);
        }
    	return entity;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
    	return type.colour;
    }

	
	@Override
	public InfoType getInfoType() 
	{
		return type;
	}
}
