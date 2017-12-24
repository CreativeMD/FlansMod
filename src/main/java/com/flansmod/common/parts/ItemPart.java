package com.flansmod.common.parts;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.flansmod.common.FlansMod;
import com.flansmod.common.types.IFlanItem;
import com.flansmod.common.types.InfoType;

public class ItemPart extends Item implements IFlanItem
{
	public PartType type;
	
	public ItemPart(PartType type1)
	{
		super();
		type = type1;
		setMaxStackSize(type.stackSize);
		if (type.category == EnumPartCategory.FUEL)
		{
			setMaxDamage(type.fuel);
			setHasSubtypes(true);
		}
		type.item = this;
		setUnlocalizedName("FlansMod:" + type.iconPath);
		setCreativeTab(FlansMod.tabFlanParts);
		GameRegistry.registerItem(this, type.shortName, FlansMod.MODID);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(type.category == EnumPartCategory.FUEL)
		{
			tooltip.add("Fuel Stored: " + (type.fuel - stack.getItemDamage()) + " / " + type.fuel);
		}
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