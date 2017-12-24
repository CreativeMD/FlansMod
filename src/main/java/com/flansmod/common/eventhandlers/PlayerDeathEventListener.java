package com.flansmod.common.eventhandlers;

import com.flansmod.common.FlansMod;
import com.flansmod.common.PlayerHandler;
import com.flansmod.common.guns.EntityBullet;
import com.flansmod.common.guns.EntityGrenade;
import com.flansmod.common.network.PacketKillMessage;
import com.flansmod.common.teams.Team;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerDeathEventListener {

	public PlayerDeathEventListener() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	@SubscribeEvent
	public void PlayerDied(LivingDeathEvent DamageEvent) {
		DamageSource source = DamageEvent.getSource();
		if ((source.getDamageType().equalsIgnoreCase("explosion") && ((source.getImmediateSource() instanceof EntityGrenade) || (source.getImmediateSource() instanceof EntityBullet))) && DamageEvent.getEntity() instanceof EntityPlayer) {
			boolean isGrenade;
			if (source.getImmediateSource() instanceof EntityGrenade) {
				isGrenade = true;
				EntityGrenade Grenade = (EntityGrenade) source.getImmediateSource();
			} else {
				isGrenade = false;
				EntityBullet Grenade = (EntityBullet) source.getImmediateSource();
			}
			EntityPlayer killer = null;
			EntityPlayer killed = (EntityPlayer) DamageEvent.getEntityLiving();
			Team killerTeam = null;
			Team killedTeam = null;
			if (isGrenade) {
				killer = (EntityPlayer) ((EntityGrenade) source.getImmediateSource()).thrower;
			} else {
				killer = (EntityPlayer) ((EntityBullet) source.getImmediateSource()).owner;
			}
			killerTeam = PlayerHandler.getPlayerData(killer).team;
			killedTeam = PlayerHandler.getPlayerData(killed).team;
			if (DamageEvent.getEntityLiving() instanceof EntityPlayer && !isGrenade) {
				FlansMod.getPacketHandler().sendToDimension(new PacketKillMessage(false, ((EntityBullet) source.getImmediateSource()).type, (killedTeam == null ? "f" : killedTeam.textColour) + ((EntityPlayer) DamageEvent.getEntity()).getDisplayName().getFormattedText(), (killerTeam == null ? "f" : killedTeam.textColour) + ((EntityPlayer) source.getImmediateSource()).getDisplayName().getFormattedText()), DamageEvent.getEntityLiving().dimension);
			}
			if (DamageEvent.getEntityLiving() instanceof EntityPlayer && isGrenade) {
				FlansMod.getPacketHandler().sendToDimension(new PacketKillMessage(false, ((EntityGrenade) source.getImmediateSource()).type, (killedTeam == null ? "f" : killedTeam.textColour) + ((EntityPlayer) DamageEvent.getEntity()).getDisplayName().getFormattedText(), (killerTeam == null ? "f" : killedTeam.textColour) + ((EntityPlayer) source.getImmediateSource()).getDisplayName().getFormattedText()), DamageEvent.getEntityLiving().dimension);
			}
		}
	}
}
