package com.flansmod.common.teams;

import java.util.List;

import javax.annotation.Nullable;

import com.flansmod.common.FlansMod;
import com.flansmod.common.parts.EnumPartCategory;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// Does nothing. Just here for rendering purposes
public class ItemRewardBox extends Item 
{
	public RewardBox type;
	
	public ItemRewardBox(RewardBox box)
	{
		super();
		type = box;
		type.item = this;

		GameRegistry.registerItem(this, type.shortName, FlansMod.MODID);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add("Useless item. Never used outside of rank-based PVP");
	}
}
