package com.flansmod.common.teams;

import com.flansmod.common.FlansMod;
import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.server.FMLServerHandler;

public class CommandTeams extends CommandBase 
{
	
	public static TeamsManager teamsManager = TeamsManager.getInstance();

	@Override
	public String getName() 
	{
		return "teams";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(teamsManager == null)
		{
			sender.sendMessage(new TextComponentString("Teams mod is broken. You will need to look at the server side logs to see what's wrong"));
			return;
		}
		if(args == null || args.length == 0 || args[0].equals("help") || args[0].equals("?"))
		{
			if(args.length == 2)
				sendHelpInformation(sender, Integer.parseInt(args[1]));
			else sendHelpInformation(sender, 1);
			return;
		}
		//On / off
		if(args[0].equals("off"))
		{
			teamsManager.currentRound = null;
			teamsManager.enabled = false;
			TeamsManager.messageAll("Flan's Teams Mod disabled");
			return;
		}
		if(args[0].equals("on"))
		{
			teamsManager.enabled = true;
			TeamsManager.messageAll("Flan's Teams Mod enabled");
			return;
		}
		if(!teamsManager.enabled)
		{
			sender.sendMessage(new TextComponentString("Teams mod is disabled. Try /teams on"));
			return;
		}
		if(args[0].equals("survival"))
		{
			teamsManager.explosions = true;
			teamsManager.driveablesBreakBlocks = true;
			teamsManager.bombsEnabled = true;
			teamsManager.bulletsEnabled = true;
			teamsManager.forceAdventureMode = false;
			teamsManager.overrideHunger = false;
			teamsManager.canBreakGuns = true;
			teamsManager.canBreakGlass = true;
			teamsManager.armourDrops = true;
			teamsManager.weaponDrops = 1;
			teamsManager.vehiclesNeedFuel = true;
			teamsManager.mgLife = teamsManager.planeLife = teamsManager.vehicleLife = teamsManager.aaLife = teamsManager.mechaLove = 0;
			teamsManager.messageAll("Flan's Mod switching to survival presets");
			return;
		}
		if(args[0].equals("arena"))
		{
			teamsManager.explosions = false;
			teamsManager.driveablesBreakBlocks = false;
			teamsManager.bombsEnabled = true;
			teamsManager.bulletsEnabled = true;
			teamsManager.forceAdventureMode = true;
			teamsManager.overrideHunger = true;
			teamsManager.canBreakGuns = true;
			teamsManager.canBreakGlass = false;
			teamsManager.armourDrops = false;
			teamsManager.weaponDrops = 2;
			teamsManager.vehiclesNeedFuel = false;
			teamsManager.mgLife = teamsManager.planeLife = teamsManager.vehicleLife = teamsManager.aaLife = teamsManager.mechaLove = 120;
			TeamsManager.messageAll("Flan's Mod switching to arena mode presets");
			return;
		}
		if(args[0].equals("motd"))
		{
			teamsManager.motd = "";
			for(int i = 0; i < args.length - 1; i++)
			{
				teamsManager.motd += args[i + 1];
				if(i != args.length - 2)
				{
					teamsManager.motd += " ";
				}
			}
			sender.sendMessage(new TextComponentString("Server message of the day is now:"));
			sender.sendMessage(new TextComponentString(teamsManager.motd));
			return;
		}
		if(args[0].equals("listGametypes"))
		{
			sender.sendMessage(new TextComponentString("\u00a72Showing all avaliable gametypes"));
			sender.sendMessage(new TextComponentString("\u00a72To pick a gametype, use \"/teams setGametype <gametype>\" with the name in brackets"));
			for(Gametype gametype : Gametype.gametypes.values())
			{
				sender.sendMessage(new TextComponentString("\u00a7f" + gametype.name + " (" + gametype.shortName + ")"));
			}
			return;
		}
		/*
		No longer used
		if(args[0].equals("setGametype"))
		{
			if(args.length != 2)
			{
				sender.addChatMessage(new TextComponentString("\u00a74To set the gametype, use \"/teams setGametype <gametype>\" with a valid gametype."));
				return;
			}
			if(args[1].toLowerCase().equals("none"))
			{
				if(teamsManager.currentGametype != null)
					teamsManager.currentGametype.stopGametype();
				teamsManager.currentGametype = null;
				for(PlayerData data : PlayerHandler.serverSideData.values())
				{
					if(data != null)
						data.team = null;
				}
				return;
			}
			Gametype gametype = Gametype.getGametype(args[1]);
			if(gametype == null)
			{
				sender.addChatMessage(new TextComponentString("\u00a74Invalid gametype. To see gametypes available type \"/teams listGametypes\""));
				return;
			}
			if(teamsManager.currentGametype != null)
			{
				teamsManager.currentGametype.stopGametype();
			}
			teamsManager.currentGametype = gametype;

			TeamsManager.messageAll("\u00a72" + sender.getCommandSenderName() + "\u00a7f changed the gametype to \u00a72" + gametype.name);
			if(teamsManager.teams != null && gametype.numTeamsRequired == teamsManager.teams.length)
			{
				TeamsManager.messageAll("\u00a7fTeams will remain the same unless altered by an op.");
			}
			else
			{
				teamsManager.teams = new Team[gametype.numTeamsRequired];
				TeamsManager.messageAll("\u00a7fTeams must be reassigned for this gametype. Please wait for an op to do so.");
			}
			gametype.initGametype();
			return;
		}*/
		if(args[0].equals("listMaps"))
		{
			if(teamsManager.maps == null)
			{
				sender.sendMessage(new TextComponentString("The map list is null"));
				return;
			}
			sender.sendMessage(new TextComponentString("\u00a72Listing maps"));
			for(TeamsMap map : teamsManager.maps.values())
			{
				sender.sendMessage(new TextComponentString((teamsManager.currentRound != null && map == teamsManager.currentRound.map ? "\u00a74" : "") + map.name + " (" + map.shortName + ")"));
			}
			return;
		}
		if(args[0].equals("addMap"))
		{
			if(args.length < 3)
			{
				sender.sendMessage(new TextComponentString("You need to specify a map name"));
				return;
			}
			String shortName = args[1];
			String name = args[2];
			for(int i = 3; i < args.length; i++)
			{
				name += " " + args[i];
			}
			teamsManager.maps.put(shortName, new TeamsMap(sender.getEntityWorld(), shortName, name));
			sender.sendMessage(new TextComponentString("Added new map : " + name + " (" + shortName + ")"));
			return;
		}
		if(args[0].equals("removeMap"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("You need to specify a map's short name"));
				return;
			}
			if(teamsManager.maps.containsKey(args[1]))
			{
				teamsManager.maps.remove(args[1]);
				sender.sendMessage(new TextComponentString("Removed map " + args[1]));
			}
			else
			{
				sender.sendMessage(new TextComponentString("Map (" + args[1] + ") not found"));
			}
			
			return;
		}
		if(args[0].equals("setRound"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("You need to specify the round index (see /teams listRounds)"));
				return;
			}
			TeamsRound round = teamsManager.rounds.get(Integer.parseInt(args[1]));
			if(round != null)
			{
				teamsManager.nextRound = round;
				TeamsManager.messageAll("\u00a72Next round will be " + round.gametype.shortName + " in " + round.map.name);
			}
			return;
		}
		/*
		if(args[0].equals("listTeams"))
		{
			if(teamsManager.currentGametype == null || teamsManager.teams == null)
			{
				sender.addChatMessage(new TextComponentString("\u00a74The gametype is not yet set. Set it by \"/teams setGametype <gametype>\""));
				return;
			}
			sender.addChatMessage(new TextComponentString("\u00a72Showing currently in use teams"));
			for(int i = 0; i < teamsManager.teams.length; i++)
			{
				Team team = teamsManager.teams[i];
				if(team == null)
					sender.addChatMessage(new TextComponentString("\u00a7f" + i + " : No team"));
				else
					sender.addChatMessage(new TextComponentString("\u00a7" + team.textColour + i + " : " + team.name + " (" + team.shortName + ")"));
			}
			return;
		}
		*/
		if(args[0].equals("listTeams") || args[0].equals("listAllTeams"))
		{
			if(Team.teams.size() == 0)
			{
				sender.sendMessage(new TextComponentString("\u00a74No teams available. You need a content pack that has some teams with it"));
				return;
			}
			sender.sendMessage(new TextComponentString("\u00a72Showing all avaliable teams"));
			sender.sendMessage(new TextComponentString("\u00a72To pick these teams, use /teams setTeams <team1> <team2> with the names in brackets"));
			for(Team team : Team.teams)
			{
				sender.sendMessage(new TextComponentString("\u00a7" + team.textColour + team.name + " (" + team.shortName + ")"));
			}
			return;
		}
		/*
		 * No longer used
		if(args[0].equals("setTeams"))
		{
			if(teamsManager.currentGametype == null || teamsManager.teams == null)
			{
				sender.addChatMessage(new TextComponentString("\u00a74No gametype selected. Please select the gametype with the setGametype command"));
				return;
			}
			if(args.length - 1 != teamsManager.teams.length)
			{
				sender.addChatMessage(new TextComponentString("\u00a74Wrong number of teams given. This gametype requires " + teamsManager.teams.length + " teams to work"));
				return;
			}
			Team[] teams = new Team[teamsManager.teams.length];
			String teamList = "";
			for(int i = 0; i < args.length - 1; i++)
			{
				Team team = Team.getTeam(args[i + 1]);
				if(team == null)
				{
					sender.addChatMessage(new TextComponentString("\u00a74" + args[i + 1] + " is not a valid team"));
					return;
				}
				for(int j = 0; j < i; j++)
				{
					if(team == teams[j])
					{
						sender.addChatMessage(new TextComponentString("\u00a74You may not add " + args[i + 1] + " twice"));
						return;
					}
				}
				teams[i] = team;
				teamList += (i == 0 ? "" : (i == args.length - 2 ? " and " : ", ")) + "\u00a7" + team.textColour + team.name + "\u00a7f";
			}
			teamsManager.teams = teams;
			teamsManager.currentGametype.teamsSet();
			TeamsManager.messageAll("\u00a72" + sender.getCommandSenderName() + "\u00a7f changed the teams to be " + teamList);
			return;
		}
		*/
		if(args[0].equals("getSticks") || args[0].equals("getOpSticks") || args[0].equals("getOpKit"))
		{
			EntityPlayerMP player = getPlayer(sender.getName());
			if(player != null)
			{
				player.inventory.addItemStackToInventory(new ItemStack(FlansMod.opStick, 1, 0));
				player.inventory.addItemStackToInventory(new ItemStack(FlansMod.opStick, 1, 1));
				player.inventory.addItemStackToInventory(new ItemStack(FlansMod.opStick, 1, 2));
				player.inventory.addItemStackToInventory(new ItemStack(FlansMod.opStick, 1, 3));
				sender.sendMessage(new TextComponentString("\u00a72Enjoy your op sticks."));
				sender.sendMessage(new TextComponentString("\u00a77The Stick of Connecting connects objects (spawners, banners etc) to bases (flagpoles etc)"));
				sender.sendMessage(new TextComponentString("\u00a77The Stick of Ownership sets the team that currently owns a base"));
				sender.sendMessage(new TextComponentString("\u00a77The Stick of Mapping sets the map that a base is currently associated with"));
				sender.sendMessage(new TextComponentString("\u00a77The Stick of Destruction deletes bases and team objects"));
			}
			return;
		}
		if(args[0].toLowerCase().equals("autobalance"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.autoBalance = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Autobalance is now " + (TeamsManager.autoBalance ? "enabled" : "disabled")));
			return;
		}
		if(args[0].equals("useRotation"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.voting = !Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Voting is now " + (TeamsManager.voting ? "enabled" : "disabled")));
			return;
		}
		if(args[0].equals("voting"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.voting = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Voting is now " + (TeamsManager.voting ? "enabled" : "disabled")));
			return;
		}
		if(args[0].equals("listRounds") || args[0].equals("listRotation"))
		{
			sender.sendMessage(new TextComponentString("\u00a72Current Round List"));
			for(int i = 0; i < TeamsManager.getInstance().rounds.size(); i++)
			{
				TeamsRound entry = TeamsManager.getInstance().rounds.get(i);
				if(entry.map == null)
				{
					sender.sendMessage(new TextComponentString("Round had null map"));
					return;
				}
				if(entry.gametype == null)
				{
					sender.sendMessage(new TextComponentString("Round had null gametype"));
					return;
				}
				String s = i + ". " + entry.map.shortName + ", " + entry.gametype.shortName;
				if(entry == TeamsManager.getInstance().currentRound)
				{
					s = "\u00a74" + s;
				}
				for(int j = 0; j < entry.teams.length; j++)
				{
					s += ", " + entry.teams[j].shortName;
				}
				s += ", " + entry.timeLimit;
				s += ", " + entry.scoreLimit;
				s += ", Pop : " + (int)(entry.popularity * 100F) + "%";
				sender.sendMessage(new TextComponentString(s));
			}
			return;
		}
		if(args[0].equals("removeRound") || args[0].equals("removeMapFromRotation") || args[0].equals("removeFromRotation") || args[0].equals("removeRotation"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <ID>"));	
				return;
			}
			int map = Integer.parseInt(args[1]);
			sender.sendMessage(new TextComponentString("Removed map " + map + " (" + TeamsManager.getInstance().rounds.get(map).map.shortName + ") from rotation"));
			TeamsManager.getInstance().rounds.remove(map);
			return;
		}
		if(args[0].equals("addMapToRotation") || args[0].equals("addToRotation") || args[0].equals("addRotation") || args[0].equals("addRound"))
		{
			if(args.length < 7)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <Map> <Gametype> <Team1> <Team2> ... <TimeLimit> <ScoreLimit>"));	
				return;
			}
			TeamsMap map = TeamsManager.getInstance().maps.get(args[1]);
			if(map == null)
			{
				sender.sendMessage(new TextComponentString("Could not find map : " + args[1]));	
				return;
			}
			Gametype gametype = Gametype.getGametype(args[2]);
			if(gametype == null)
			{
				sender.sendMessage(new TextComponentString("Could not find gametype : " + args[2]));	
				return;
			}
			if(args.length != 5 + gametype.numTeamsRequired)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <Map> <Gametype> <Team1> <Team2> ... <ScoreLimit> <TimeLimit>"));	
				return;
			}
			Team[] teams = new Team[gametype.numTeamsRequired];
			for(int i = 0; i < teams.length; i++)
			{
				teams[i] = Team.getTeam(args[3 + i]);
			}
			sender.sendMessage(new TextComponentString("Added map (" + map.shortName + ") to rotation"));
			TeamsManager.getInstance().rounds.add(new TeamsRound(map, gametype, teams, Integer.parseInt(args[3 + gametype.numTeamsRequired]), Integer.parseInt(args[4 + gametype.numTeamsRequired])));
			return;
		}
		if(args[0].equals("start") || args[0].equals("begin"))
		{
			teamsManager.start();
			sender.sendMessage(new TextComponentString("Started teams map rotation"));
			return;
		}
		if(args[0].equals("nextMap") || args[0].equals("next") || args[0].equals("nextRound"))
		{
			teamsManager.roundTimeLeft = 1;
			return;
		}
		/*
		 * Ignore
		if(args[0].equals("goToMap"))
		{
			if(args.length != 2)
			{
				sender.addChatMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <ID>"));	
				return;
			}
			int prevRotation = Integer.parseInt(args[1]) - 1;
			if(prevRotation == -1)
				prevRotation = teamsManager.rotation.size() - 1;
			teamsManager.currentRotationEntry = prevRotation;
			teamsManager.switchToNextGametype();
			return;
		}
		*/
		if(args[0].equals("forceAdventure") || args[0].equals("forceAdventureMode"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.forceAdventureMode = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Adventure mode will " + (TeamsManager.forceAdventureMode ? "now" : "no longer") + " be forced"));
			return;
		}
		if(args[0].equals("overrideHunger") || args[0].equals("noHunger"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.overrideHunger = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Players will " + (TeamsManager.overrideHunger ? "no longer" : "now") + " get hungry during rounds"));
			return;
		}
		if(args[0].equals("explosions"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.explosions = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Expolsions are now " + (TeamsManager.explosions ? "enabled" : "disabled")));
			return;
		}
		if(args[0].equals("bombs") || args[0].equals("allowBombs"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.bombsEnabled = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Bombs are now " + (TeamsManager.bombsEnabled ? "enabled" : "disabled")));
			return;
		}
		if(args[0].equals("bullets") || args[0].equals("bulletsEnabled"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.bulletsEnabled = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Bullets are now " + (TeamsManager.bulletsEnabled ? "enabled" : "disabled")));
			return;
		}
		if(args[0].equals("canBreakGuns"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.canBreakGuns = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("AAGuns and MGs can " + (TeamsManager.canBreakGuns ? "now" : "no longer") + " be broken"));
			return;
		}
		if(args[0].equals("canBreakGlass"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.canBreakGlass = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Glass and glowstone can " + (TeamsManager.canBreakGlass ? "now" : "no longer") + " be broken"));
			return;
		}
		if(args[0].equals("armourDrops") || args[0].equals("armorDrops"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.armourDrops = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Armour will " + (TeamsManager.armourDrops ? "now" : "no longer") + " be dropped"));
			return;
		}
		if(args[0].equals("weaponDrops"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <on/off/smart>"));	
				return;
			}
			if(args[1].toLowerCase().equals("on"))
			{
				TeamsManager.weaponDrops = 1;
				sender.sendMessage(new TextComponentString("Weapons will be dropped normally"));
			}
			else if(args[1].toLowerCase().equals("off"))
			{
				TeamsManager.weaponDrops = 0;
				sender.sendMessage(new TextComponentString("Weapons will be not be dropped"));
			}
			else if(args[1].toLowerCase().equals("smart"))
			{
				TeamsManager.weaponDrops = 2;
				sender.sendMessage(new TextComponentString("Smart drops enabled"));
			}
			return;
		}
		if(args[0].equals("fuelNeeded"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.vehiclesNeedFuel = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Vehicles will " + (TeamsManager.vehiclesNeedFuel ? "now" : "no longer") + " require fuel"));
			return;
		}
		if(args[0].equals("mgLife"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <time>"));	
				return;
			}
			TeamsManager.mgLife = Integer.parseInt(args[1]);
			if(TeamsManager.mgLife > 0)
				sender.sendMessage(new TextComponentString("MGs will despawn after " + TeamsManager.mgLife + " seconds"));
			else sender.sendMessage(new TextComponentString("MGs will not despawn"));
			return;
		}
		if(args[0].equals("planeLife"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <time>"));	
				return;
			}
			TeamsManager.planeLife = Integer.parseInt(args[1]);
			if(TeamsManager.planeLife > 0)
				sender.sendMessage(new TextComponentString("Planes will despawn after " + TeamsManager.planeLife + " seconds"));
			else sender.sendMessage(new TextComponentString("Planes will not despawn"));
			return;
		}
		if(args[0].equals("vehicleLife"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <time>"));	
				return;
			}
			TeamsManager.vehicleLife = Integer.parseInt(args[1]);
			if(TeamsManager.vehicleLife > 0)
				sender.sendMessage(new TextComponentString("Vehicles will despawn after " + TeamsManager.vehicleLife + " seconds"));
			else sender.sendMessage(new TextComponentString("Vehicles will not despawn"));
			return;
		}
		if(args[0].equals("mechaLife"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <time>"));	
				return;
			}
			TeamsManager.mechaLove = Integer.parseInt(args[1]);
			if(TeamsManager.mechaLove > 0)
				sender.sendMessage(new TextComponentString("Mechas will despawn after " + TeamsManager.mechaLove + " seconds"));
			else sender.sendMessage(new TextComponentString("Mechas will not despawn"));
			return;
		}
		if(args[0].equals("aaLife"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <time>"));	
				return;
			}
			TeamsManager.aaLife = Integer.parseInt(args[1]);
			if(TeamsManager.aaLife > 0)
				sender.sendMessage(new TextComponentString("AA Guns will despawn after " + TeamsManager.aaLife + " seconds"));
			else sender.sendMessage(new TextComponentString("AA Guns will not despawn"));
			return;
		}
		if(args[0].equals("vehiclesBreakBlocks"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <true/false>"));	
				return;
			}
			TeamsManager.driveablesBreakBlocks = Boolean.parseBoolean(args[1]);
			sender.sendMessage(new TextComponentString("Vehicles will " + (TeamsManager.driveablesBreakBlocks ? "now" : "no longer") + " break blocks"));
			return;
		}
		if(args[0].equals("scoreDisplayTime"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <time>"));	
				return;
			}
			TeamsManager.scoreDisplayTime = Integer.parseInt(args[1]) * 20;
			sender.sendMessage(new TextComponentString("Score summary menu will appear for " + TeamsManager.scoreDisplayTime / 20 + " seconds"));
			return;
		}
		if(args[0].equals("rankUpdateTime"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <time>"));	
				return;
			}
			TeamsManager.rankUpdateTime = Integer.parseInt(args[1]) * 20;
			sender.sendMessage(new TextComponentString("Rank update menu will appear for " + TeamsManager.rankUpdateTime / 20 + " seconds"));
			return;
		}
		if(args[0].equals("votingTime"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <time>"));	
				return;
			}
			TeamsManager.votingTime = Integer.parseInt(args[1]) * 20;
			sender.sendMessage(new TextComponentString("Voting menu will appear for " + TeamsManager.votingTime / 20 + " seconds"));
			return;
		}
		if(args[0].toLowerCase().equals("autobalancetime"))
		{
			if(args.length != 2)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams " + args[0] + " <time>"));	
				return;
			}
			TeamsManager.autoBalanceInterval = Integer.parseInt(args[1]) * 20;
			sender.sendMessage(new TextComponentString("Autobalance will now occur every " + TeamsManager.autoBalanceInterval / 20 + " seconds"));
			return;
		}
		if(args[0].equals("setVariable"))
		{
			if(TeamsManager.getInstance().currentRound == null)
			{
				sender.sendMessage(new TextComponentString("There is no gametype to set variables for"));		
				return;
			}
			if(args.length != 3)
			{
				sender.sendMessage(new TextComponentString("Incorrect Usage : Should be /teams setVariable <variable> <value>"));	
				return;
			}
			if(TeamsManager.getInstance().currentRound.gametype.setVariable(args[1], args[2]))
				sender.sendMessage(new TextComponentString("Set variable " + args[1] + " in gametype " + TeamsManager.getInstance().currentRound.gametype.shortName + " to " + args[2]));
			else sender.sendMessage(new TextComponentString("Variable " + args[1] + " did not exist in gametype " + TeamsManager.getInstance().currentRound.gametype.shortName));
			return;
		}
		if(args[0].toLowerCase().equals("setloadoutpool"))
		{
			LoadoutPool pool = LoadoutPool.GetPool(args[1]);
			if(pool != null)
			{
				TeamsManagerRanked.GetInstance().currentPool = pool;
				sender.sendMessage(new TextComponentString("Loadout pool set to " + args[1]));	
			}
			else
			{
				sender.sendMessage(new TextComponentString("No such loadout pool"));	
			} 
				
			return;
		}
		if(args[0].toLowerCase().equals("go"))
		{
			TeamsManagerRanked.GetInstance().currentPool = LoadoutPool.GetPool("modernLoadout");
			teamsManager.start();
			return;
		}
		if(args[0].toLowerCase().equals("xp"))
		{
			sender.sendMessage(new TextComponentString("Awarded " + Integer.parseInt(args[1]) + " XP"));	
			TeamsManagerRanked.AwardXP((EntityPlayerMP)sender, Integer.parseInt(args[1]));
			return;
		}
		if(args[0].toLowerCase().equals("resetrank"))
		{
			sender.sendMessage(new TextComponentString("Reset your rank"));	
			TeamsManagerRanked.ResetRank((EntityPlayerMP)sender);
			return;
		}
		if(args[0].toLowerCase().equals("giverewardbox"))
		{
			String name = args[1];
			RewardBox box = RewardBox.GetRewardBox(args[2]);
			if(box == null)
			{
				sender.sendMessage(new TextComponentString("Invalid box"));	
				return;
			}
			
			GameProfile profile = sender.getServer().getPlayerProfileCache().getGameProfileForUsername(name);
			if(profile != null)
			{
				RewardBoxInstance instance = RewardBoxInstance.CreateCheatReward(box, name);
				PlayerRankData data = TeamsManagerRanked.GetRankData(profile.getId());
				if(data != null)
				{
					data.AddRewardBoxInstance(instance);
				}
			}
			return;
		}
		if(args[0].toLowerCase().equals("xpmultiplier"))
		{
			float target = Float.parseFloat(args[1]);
			if(target < 0.5f || target > 2.0f)
			{
				sender.sendMessage(new TextComponentString("Not going to allow that for now. Keep it within 0.5 to 2.0"));
			}
			else
			{
				sender.sendMessage(new TextComponentString("XP multiplier is now " + target));
				TeamsManagerRanked.GetInstance().XPMultiplier = target;
			}
			return;
		}
		
		sender.sendMessage(new TextComponentString(args[0] + " is not a valid teams command. Try /teams help"));
	}
	
	public void sendHelpInformation(ICommandSender sender, int page)
	{
		if(page > 3 || page < 1)
		{
			TextComponentString text = new TextComponentString("Invalid help page, should be in the range (1-3)");
			text.getStyle().setColor(TextFormatting.RED);
			sender.sendMessage(text);
			return;
		}
		
		sender.sendMessage(new TextComponentString("\u00a72Listing teams commands \u00a7f[Page " + page + " of 3]"));
		switch(page)
		{
		case 1 : 
		{
			sender.sendMessage(new TextComponentString("/teams help [page]"));
			sender.sendMessage(new TextComponentString("/teams off"));
			sender.sendMessage(new TextComponentString("/teams arena"));
			sender.sendMessage(new TextComponentString("/teams survival"));
			sender.sendMessage(new TextComponentString("/teams getSticks"));
			sender.sendMessage(new TextComponentString("/teams listGametypes"));
			//sender.addChatMessage(new TextComponentString("/teams setGametype <name>"));
			//sender.addChatMessage(new TextComponentString("/teams listAllTeams"));
			sender.sendMessage(new TextComponentString("/teams listTeams"));
			//sender.addChatMessage(new TextComponentString("/teams setTeams <teamName1> <teamName2>"));
			sender.sendMessage(new TextComponentString("/teams addMap <shortName> <longName>"));
			sender.sendMessage(new TextComponentString("/teams listMaps"));
			sender.sendMessage(new TextComponentString("/teams removeMap <shortName>"));
			break;
		}
		case 2 :
		{

			//sender.addChatMessage(new TextComponentString("/teams setMap <shortName>"));
			sender.sendMessage(new TextComponentString("/teams useRotation <true / false>"));
			sender.sendMessage(new TextComponentString("/teams voting <true / false>"));
			sender.sendMessage(new TextComponentString("/teams addRound <map> <gametype> <team1> <team2> <TimeLimit> <ScoreLimit>"));
			sender.sendMessage(new TextComponentString("/teams listRounds"));
			sender.sendMessage(new TextComponentString("/teams removeRound <ID>"));
			sender.sendMessage(new TextComponentString("/teams nextMap"));			
			//sender.addChatMessage(new TextComponentString("/teams goToMap <ID>"));		
			sender.sendMessage(new TextComponentString("/teams votingTime <time>"));
			sender.sendMessage(new TextComponentString("/teams scoreDisplayTime <time>"));
			break;
		}
		case 3 :
		{
			sender.sendMessage(new TextComponentString("/teams setVariable <variable> <value>"));
			sender.sendMessage(new TextComponentString("/teams forceAdventure <true / false>"));
			sender.sendMessage(new TextComponentString("/teams overrideHunger <true / false>"));
			sender.sendMessage(new TextComponentString("/teams explosions <true / false>"));
			sender.sendMessage(new TextComponentString("/teams canBreakGuns <true / false>"));
			sender.sendMessage(new TextComponentString("/teams canBreakGlass <true / false>"));
			sender.sendMessage(new TextComponentString("/teams armourDrops <true / false>"));
			sender.sendMessage(new TextComponentString("/teams weaponDrops <off / on / smart>"));
			sender.sendMessage(new TextComponentString("/teams fuelNeeded <true / false>"));
			sender.sendMessage(new TextComponentString("/teams mgLife <time>"));
			sender.sendMessage(new TextComponentString("/teams planeLife <time>"));
			sender.sendMessage(new TextComponentString("/teams vehicleLife <time>"));
			sender.sendMessage(new TextComponentString("/teams aaLife <time>"));

			sender.sendMessage(new TextComponentString("/teams vehiclesBreakBlocks <true / false>"));		
			break;
		}
		}
	}

	public EntityPlayerMP getPlayer(String name)
	{
		return FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUsername(name);
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "Try \"/teams help\"";
	}
}
