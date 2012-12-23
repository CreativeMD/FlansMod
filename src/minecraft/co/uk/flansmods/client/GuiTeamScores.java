package co.uk.flansmods.client;

import java.io.DataInputStream;

import co.uk.flansmods.common.FlansMod;
import co.uk.flansmods.common.teams.Team;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.gui.GuiScreen;

public class GuiTeamScores extends GuiScreen 
{
	//Store the client side teams data statically in the Gui. Seems untidy, but its the only place its going to be used...
	public static String gametype;
	public static int numTeams;
	public static TeamData[] teamData;

	private static class TeamData
	{
		public Team team;
		public int score;
		public int numPlayers;
		public PlayerData[] playerData;
		
		private static class PlayerData
		{
			public String username;
			public int score;
			public int kills;
			public int deaths;
		}
	}
	
	//Move the packet interpretation here for simplicity
	public static void interpret(DataInputStream stream)
	{
		try
		{
			gametype = stream.readUTF();
			numTeams = stream.readInt();
			if(numTeams == 0)
				return;
			teamData = new TeamData[numTeams];
			for(int i = 0; i < numTeams; i++)
			{
				String teamName = stream.readUTF();
				teamData[i].team = Team.getTeam(teamName);
				teamData[i].score = stream.readInt();
				teamData[i].numPlayers = stream.readInt();
				for(int j = 0; j < teamData[i].numPlayers; j++)
				{
					teamData[i].playerData[j].username = stream.readUTF();
					teamData[i].playerData[j].score = stream.readInt();
					teamData[i].playerData[j].kills = stream.readInt();
					teamData[i].playerData[j].deaths = stream.readInt();
				}
			}
		}
        catch(Exception e)
        {
        	FlansMod.log("Error reading team info packet");
        	e.printStackTrace();
        }
	}
}