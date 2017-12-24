package com.flansmod.common.driveables.mechas;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.DriveableData;
import com.flansmod.common.driveables.EnumDriveablePart;
import com.flansmod.common.parts.PartType;
import com.flansmod.common.types.EnumType;
import com.flansmod.common.types.IPaintableItem;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.types.PaintableType;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMecha extends Item implements IPaintableItem
{
	public MechaType type;

	public ItemMecha(MechaType type1)
	{
		maxStackSize = 1;
		type = type1;
		type.item = this;
		setCreativeTab(FlansMod.tabFlanMechas);
		GameRegistry.registerItem(this, type.shortName, FlansMod.MODID);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(type.description != null)
		{
			Collections.addAll(tooltip, type.description.split("_"));
		}
		NBTTagCompound tags = getTagCompound(stack, worldIn);
		String engineName = tags.getString("Engine");
		PartType part = PartType.getPart(engineName);
		if(part != null)
			tooltip.add(part.name);
	}
	
	@Override
	/** Make sure client and server side NBTtags update */
	public boolean getShareTag()
	{
		return true;
	}
	
	private NBTTagCompound getTagCompound(ItemStack stack, World world)
	{
		if(stack.getTagCompound() == null)
		{
			if(stack.getTagCompound() == null)
			{
				NBTTagCompound tags = new NBTTagCompound();
				stack.setTagCompound(tags);
				tags.setString("Type", type.shortName);
				tags.setString("Engine", PartType.defaultEngines.get(EnumType.mecha).shortName);
			}
		}
		return stack.getTagCompound();
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
        if(movingobjectposition == null)
        {
        	return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
        if(movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK)
        {
        	BlockPos pos = movingobjectposition.getBlockPos();
            if(!world.isRemote)
            {
				world.spawnEntity(new EntityMecha(world, (double)pos.getX() + 0.5F, (double)pos.getY() + 1.5F + type.yOffset, (double)pos.getZ() + 0.5F, player, type, getData(itemstack, world), getTagCompound(itemstack, world)));
            }
			if(!player.capabilities.isCreativeMode)
			{	
				itemstack.shrink(1);
			}
		}
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}
	
	public DriveableData getData(ItemStack itemstack, World world)
	{
		return new DriveableData(getTagCompound(itemstack, world), itemstack.getItemDamage());
	}
   
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
	{
		return type.colour;
	}
	
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
    	ItemStack mechaStack = new ItemStack(this, 1, 0);
    	NBTTagCompound tags = new NBTTagCompound();
    	tags.setString("Type", type.shortName);
    	if(PartType.defaultEngines.containsKey(EnumType.mecha))
    		tags.setString("Engine", PartType.defaultEngines.get(EnumType.mecha).shortName);
    	for(EnumDriveablePart part : EnumDriveablePart.values())
    	{
    		tags.setInteger(part.getShortName() + "_Health", type.health.get(part) == null ? 0 : type.health.get(part).health);
    		tags.setBoolean(part.getShortName() + "_Fire", false);
    	}
    	mechaStack.setTagCompound(tags);
    	items.add(mechaStack);
    }
    
	@Override
	public InfoType getInfoType() 
	{
		return type;
	}

	@Override
	public PaintableType GetPaintableType()
	{
		return type;
	}
}
