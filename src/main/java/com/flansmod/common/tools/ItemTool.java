package com.flansmod.common.tools;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.flansmod.client.debug.EntityDebugVector;
import com.flansmod.common.FlansMod;
import com.flansmod.common.PlayerData;
import com.flansmod.common.PlayerHandler;
import com.flansmod.common.driveables.DriveablePart;
import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.network.PacketFlak;
import com.flansmod.common.types.IFlanItem;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.vector.Vector3f;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTool extends ItemFood implements IFlanItem
{
	public ToolType type;

	public ItemTool(ToolType t)
	{
		super(t.foodness, false);
		maxStackSize = 1;
		type = t;
		type.item = this;
		setMaxDamage(type.toolLife);
		if(type.foodness == 0)
		{
			setCreativeTab(FlansMod.tabFlanParts);
			if(type.remote)
				setCreativeTab(FlansMod.tabFlanGuns);
			if(type.healDriveables)
				setCreativeTab(FlansMod.tabFlanDriveables);
		}
		GameRegistry.registerItem(this, type.shortName, FlansMod.MODID);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(type.description != null)
		{
			Collections.addAll(tooltip, type.description.split("_"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
    	return type.colour;
    }

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn)
	{
		if(type.foodness > 0 || handIn == EnumHand.OFF_HAND)
			super.onItemRightClick(world, player, handIn);
		
		ItemStack stack = player.getHeldItemMainhand();
		
		if(type.parachute)
		{
			
			
			//Create a parachute, spawn it and put the player in it
			if(!world.isRemote)
			{
				EntityParachute parachute = new EntityParachute(world, type, player);
				world.spawnEntity(parachute);
				player.startRiding(parachute);
			}
			
			//If not in creative and the tool should decay, damage it
			if(!player.capabilities.isCreativeMode && type.toolLife > 0)
				stack.setItemDamage(stack.getItemDamage() + 1);
			//If the tool is damagable and is destroyed upon being used up, then destroy it
			if(type.toolLife > 0 && type.destroyOnEmpty && stack.getItemDamage() == stack.getMaxDamage())
				stack.shrink(1);
			//Our work here is done. Let's be off
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		
		else if(type.remote)
		{
			PlayerData data = PlayerHandler.getPlayerData(player, world.isRemote ? Side.CLIENT : Side.SERVER);
			//If we have some remote explosives out there
			if(data.remoteExplosives.size() > 0)
			{
				//Detonate it
				data.remoteExplosives.get(0).detonate();
				//Remove it from the list to detonate
				if(data.remoteExplosives.get(0).detonated)
					data.remoteExplosives.remove(0);
				
				//If not in creative and the tool should decay, damage it
				if(!player.capabilities.isCreativeMode && type.toolLife > 0)
					stack.setItemDamage(stack.getItemDamage() + 1);
				//If the tool is damagable and is destroyed upon being used up, then destroy it
				if(type.toolLife > 0 && type.destroyOnEmpty && stack.getItemDamage() == stack.getMaxDamage())
					stack.shrink(1);
				//Our work here is done. Let's be off
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}
		else
		{
		
	    	//Raytracing
	        float cosYaw = MathHelper.cos(-player.rotationYaw * 0.01745329F);
	        float sinYaw = MathHelper.sin(-player.rotationYaw * 0.01745329F);
	        float cosPitch = -MathHelper.cos(player.rotationPitch * 0.01745329F);
	        float sinPitch = MathHelper.sin(player.rotationPitch * 0.01745329F);
	        double length = 5D;
	        Vec3d posVec = new Vec3d(player.posX, player.posY + 1.62D - player.getYOffset(), player.posZ);        
	        Vec3d lookVec = posVec.addVector(sinYaw * cosPitch * length, sinPitch * length, cosYaw * cosPitch * length);
	        
	        if(world.isRemote && FlansMod.DEBUG)
	        {
	        	world.spawnEntity(new EntityDebugVector(world, new Vector3f(posVec), new Vector3f(posVec.subtract(lookVec)), 100));
	        }
	        
	        if(type.healDriveables)
	        {
				//Iterate over all EntityDriveables
				for(int i = 0; i < world.loadedEntityList.size(); i++)
				{
					Object obj = world.loadedEntityList.get(i);
					if(obj instanceof EntityDriveable)
					{
						EntityDriveable driveable = (EntityDriveable)obj;
						//Raytrace
						DriveablePart part = driveable.raytraceParts(new Vector3f(posVec), Vector3f.sub(new Vector3f(lookVec), new Vector3f(posVec), null));
						//If we hit something that is healable
						if(part != null && part.maxHealth > 0)
						{
							//If its broken and the tool is inifinite or has durability left
							if(part.health < part.maxHealth && (type.toolLife == 0 || stack.getItemDamage() < stack.getMaxDamage()))
							{
								//Heal it
								part.health += type.healAmount;
								//If it is over full health, cap it
								if(part.health > part.maxHealth)
									part.health = part.maxHealth;
								//If not in creative and the tool should decay, damage it
								if(!player.capabilities.isCreativeMode && type.toolLife > 0)
									stack.setItemDamage(stack.getItemDamage() + 1);
								//If the tool is damagable and is destroyed upon being used up, then destroy it
								if(type.toolLife > 0 && type.destroyOnEmpty && stack.getItemDamage() == stack.getMaxDamage())
									stack.shrink(1);;
								//Our work here is done. Let's be off
								return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
							}
						}
					}
				}
			}
	
			if(!world.isRemote && type.healPlayers)
			{
				//By default, heal the player
				EntityLivingBase hitLiving = player;

				//Iterate over entities within range of the ray
				List<EntityLivingBase> list = world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(
						Math.min(posVec.x, lookVec.x), Math.min(posVec.y, lookVec.y), Math.min(posVec.z, lookVec.z), 
						Math.max(posVec.x, lookVec.x), Math.max(posVec.y, lookVec.y), Math.max(posVec.z, lookVec.z)));
				for (Object aList : list) {
					if (!(aList instanceof EntityLivingBase))
						continue;
					EntityLivingBase checkEntity = (EntityLivingBase) aList;
					//Don't check the player using it
					if (checkEntity == player)
						continue;
					//Do a more accurate ray trace on this entity
					RayTraceResult hit = checkEntity.getEntityBoundingBox().calculateIntercept(posVec, lookVec);
					//If it hit, heal it
					if (hit != null)
						hitLiving = checkEntity;
				}
				//Now heal whatever it was we just decided to heal
				if(hitLiving != null)
				{
					//If its finished, don't use it
					if(stack.getItemDamage() >= stack.getMaxDamage() && type.toolLife > 0)
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

					hitLiving.heal(type.healAmount);
					FlansMod.getPacketHandler().sendToAllAround(new PacketFlak(hitLiving.posX, hitLiving.posY, hitLiving.posZ, 5, "heart"), new NetworkRegistry.TargetPoint(hitLiving.dimension, hitLiving.posX, hitLiving.posY, hitLiving.posZ, 50F));

					//If not in creative and the tool should decay, damage it
					if(!player.capabilities.isCreativeMode && type.toolLife > 0)
						stack.setItemDamage(stack.getItemDamage() + 1);
					//If the tool is damagable and is destroyed upon being used up, then destroy it
					if(type.toolLife > 0 && type.destroyOnEmpty && stack.getItemDamage() >= stack.getMaxDamage())
						stack.shrink(1);
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}
	
	@Override
	public String toString()
	{
		return type == null ? getUnlocalizedName() : type.name;
	}

	@Override
	public InfoType getInfoType() 
	{
		return type;
	}
}
