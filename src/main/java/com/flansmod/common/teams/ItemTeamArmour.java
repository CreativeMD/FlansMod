package com.flansmod.common.teams;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.flansmod.common.FlansMod;
import com.flansmod.common.types.IFlanItem;
import com.flansmod.common.types.InfoType;
import com.google.common.collect.Multimap;

public class ItemTeamArmour extends ItemArmor implements ISpecialArmor, IFlanItem
{
	public ArmourType type;
	protected static final UUID[] uuid = new UUID[] { UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID() };
	
	public ItemTeamArmour(ArmourType t)
	{
		super(ItemArmor.ArmorMaterial.LEATHER, 0, t.type);
		type = t;
		type.item = this;
		setCreativeTab(FlansMod.tabFlanTeams);
		GameRegistry.registerItem(this, type.shortName, FlansMod.MODID);
	}
	
	public ItemTeamArmour(ItemArmor.ArmorMaterial armorMaterial, int renderIndex, EntityEquipmentSlot armourType) 
	{
		super(armorMaterial, renderIndex, armourType);
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) 
	{
		return new ArmorProperties(1, type.defence, Integer.MAX_VALUE);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) 
	{
		return (int)(type.defence * 20);
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) 
	{
		//Do nothing to the armour. It should not break as that would leave the player's team ambiguous
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String typeString)
	{
		return "flansmod:armor/" + type.armourTextureName + "_" + (type.type == EntityEquipmentSlot.LEGS ? "2" : "1") + ".png";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> lines, ITooltipFlag flagIn)
	{
		if(type.description != null)
		{
			Collections.addAll(lines, type.description.split("_"));
		}
		if(Math.abs(type.jumpModifier - 1F) > 0.01F)
			lines.add("\u00a73+" + (int)((type.jumpModifier - 1F) * 100F) + "% Jump Height");
		if(type.smokeProtection)
			lines.add("\u00a72+Smoke Protection");
		if(type.nightVision)
			lines.add("\u00a72+Night Vision");
		if(type.negateFallDamage)
			lines.add("\u00a72+Negates Fall Damage");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
	{
		return type.colour;
	}
    
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
       	Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
       	map.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(uuid[type.type.getIndex()], "KnockbackResist", type.knockbackModifier, 0));
       	map.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(uuid[type.type.getIndex()], "MovementSpeed", type.moveSpeedModifier - 1F, 2));
       	return map;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default)
    {
        return type.model;
    }
    
	@Override
	public InfoType getInfoType() 
	{
		return type;
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
	{
		if(type.nightVision && FlansMod.ticker % 25 == 0)
			player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 250));
		if(type.jumpModifier > 1.01F && FlansMod.ticker % 25 == 0)
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 250, (int)((type.jumpModifier - 1F) * 2F), true, false));
		if(type.negateFallDamage)
			player.fallDistance = 0F;
	}
}
