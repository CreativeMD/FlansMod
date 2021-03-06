package com.flansmod.common.paintjob;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.flansmod.apocalypse.common.FlansModApocalypse;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.guns.ShootableType;
import com.flansmod.common.teams.TeamsManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.text.ITextComponent;

public class TileEntityPaintjobTable extends TileEntity implements IInventory, IUpdatePlayerListBox
{
	// Stack 0 is InfoType being painted. Stack 1 is paint cans
	private ItemStack inventoryStacks[] = new ItemStack[2];
	//private CustomPaintjob inProgressPaintjob;
	
	public TileEntityPaintjobTable()
	{
		
	}
	
	@Override
	public String getName() { return "PaintjobTable"; }

	@Override
	public boolean hasCustomName() { return false; }

	@Override
	public ITextComponent getDisplayName() { return null; }

	@Override
	public int getSizeInventory() { return 2; }

	@Override
	public ItemStack getStackInSlot(int index) 
	{ 
		return inventoryStacks[index]; 
	}

	@Override
	public ItemStack decrStackSize(int index, int count) 
	{ 
		if(getStackInSlot(index) != null) 
		{ 
			if(count >= getStackInSlot(index).getCount())
			{
				ItemStack returnStack = getStackInSlot(index);
				setInventorySlotContents(index, null);
				return returnStack;
			}
			else
			{
				ItemStack returnStack = getStackInSlot(index).splitStack(count);
				
				return returnStack;
			}
		} 
		return ItemStack.EMPTY; 
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) 
	{ 
		ItemStack returnStack = getStackInSlot(index);
		setInventorySlotContents(index, null);
		return returnStack; 
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{ 
		inventoryStacks[index] = stack;
	}

	@Override
	public int getInventoryStackLimit() { return 64; }

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return true; }

	@Override
	public void openInventory(EntityPlayer player) { }

	@Override
	public void closeInventory(EntityPlayer player) { }

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) { return true; }

	@Override
	public int getField(int id) { return 0; }

	@Override
	public void setField(int id, int value) { }

	@Override
	public int getFieldCount() { return 0; }

	@Override
	public void clear() 
	{ 
		for(int i = 0; i < getSizeInventory(); i++)
		{
			setInventorySlotContents(i, null);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		for(int i = 0; i < inventoryStacks.length; i++)
		{
			NBTTagCompound stackNBT = new NBTTagCompound();
			if(getStackInSlot(i) != null)
				getStackInSlot(i).writeToNBT(stackNBT);
			nbt.setTag("stack_" + i, stackNBT);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		for(int i = 0; i < inventoryStacks.length; i++)
		{
			setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack_" + i)));
		}
	}

	@Override
	public void update() 
	{
	}
	
	@Override
    public Packet getDescriptionPacket()
    {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(getPos(), getBlockMetadata(), nbt);
    }
	
	@Override
    public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.S35PacketUpdateTileEntity packet)
    {
		readFromNBT(packet.getNbtCompound());
    }

	public ItemStack getPaintableStack() 
	{
		return inventoryStacks[0];
	}

	public void setPaintableStack(ItemStack stack) 
	{
		inventoryStacks[0] = stack;
	}
	
	public ItemStack getPaintCans()
	{
		return inventoryStacks[1];
	}
}
