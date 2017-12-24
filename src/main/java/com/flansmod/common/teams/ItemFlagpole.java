package com.flansmod.common.teams;

import com.flansmod.common.FlansMod;
import com.flansmod.common.types.IFlanItem;
import com.flansmod.common.types.InfoType;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemFlagpole extends Item implements IFlanItem
{
	public ItemFlagpole() 
	{
		setCreativeTab(FlansMod.tabFlanTeams);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn)
	{
		if(handIn == EnumHand.OFF_HAND)
			return super.onItemRightClick(world, player, handIn);
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d = player.prevPosX + (player.posX - player.prevPosX) * f;
        double d1 = (player.prevPosY + (player.posY - player.prevPosY) * f + 1.6200000000000001D) - player.getYOffset();
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
        Vec3d vec3d = new Vec3d(d, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.141593F);
        float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.141593F);
        float f5 = -MathHelper.cos(-f1 * 0.01745329F);
        float f6 = MathHelper.sin(-f1 * 0.01745329F);
        float f7 = f4 * f5;
        float f8 = f6;
        float f9 = f3 * f5;
        double d3 = 5D;
        Vec3d vec3d1 = vec3d.addVector(f7 * d3, f8 * d3, f9 * d3);
        RayTraceResult movingobjectposition = world.rayTraceBlocks(vec3d, vec3d1, true);
        if(movingobjectposition == null)
        {
        	return super.onItemRightClick(world, player, handIn);
        }
        if(movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = movingobjectposition.getBlockPos();
            if(!world.isRemote)
            {
				if(world.getBlockState(pos).getBlock() == Blocks.SNOW)
				{
					pos.add(0, -1, 0);
				}
				if(isSolid(world, pos))
				{
					world.spawnEntity(new EntityFlagpole(world, pos));
				}		            
			}
		}
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
	}
	
	private boolean isSolid(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		return state.isFullBlock() && state.isOpaqueCube();
	}

	@Override
	public InfoType getInfoType() 
	{
		return null;
	}
}
