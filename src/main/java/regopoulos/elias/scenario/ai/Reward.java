package regopoulos.elias.scenario.ai;

import com.sun.org.apache.regexp.internal.RE;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Resource;
import regopoulos.elias.scenario.TerrainType;

import java.io.InputStream;
import java.util.Properties;


/** Static class, calculating the reward of each action */
public class Reward
{
	private static double WIN_REWARD;
	private static double DROP_OFF_REWARD;	//only the resources still needed
	private static double GATHER_REWARD;	//only the resources still needed
	private static double ATTACK_REWARD;	//reward per damage point
	private static double KILL_REWARD;
	private static double DIE_REWARD;

	public static void loadProperties()
	{
		Properties prop = new Properties();
		try (InputStream fis = Reward.class.getClassLoader().getResourceAsStream("Reward.properties"))
		{
			prop.loadFromXML(fis);
			Reward.WIN_REWARD = Double.parseDouble(prop.getProperty("WinReward"));
			Reward.DROP_OFF_REWARD = Double.parseDouble(prop.getProperty("DropOffReward"));
			Reward.GATHER_REWARD = Double.parseDouble(prop.getProperty("GatherReward"));
			Reward.ATTACK_REWARD = Double.parseDouble(prop.getProperty("AttackReward"));
			Reward.KILL_REWARD = Double.parseDouble(prop.getProperty("KillReward"));
			Reward.DIE_REWARD = Double.parseDouble(prop.getProperty("DieReward"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	static double getReward(Agent agent, Action action, boolean didAction)
	{
		double reward = 0;
		if (didAction)
		{
			switch (action.getType())
			{
				case DROP_OFF:
					reward = Reward.getDropOffReward(agent, action);
					break;
				case GATHER_WOOD:
				case GATHER_STONE:
				case GATHER_GOLD:
					reward = Reward.getGatherReward(agent, action);
					break;
				case ATTACK:
					reward = Reward.getAttackReward(agent, action);
			}
		}
		if (!agent.isAlive())
		{
			reward = Reward.getDieReward();
		}
		return reward;
	}

	private static double getDropOffReward(Agent agent, Action action)
	{
		double dropOffReward = 0;
		TerrainType resourceDroppedOff = action.getResourceDroppedOff();
		Resource teamResource = agent.getTeam().getResource(resourceDroppedOff);
		if (teamResource.getCurrent()<=teamResource.getGoal())
		{
			dropOffReward = Reward.DROP_OFF_REWARD;
		}
		if (agent.getTeam().hasWon())
		{
			dropOffReward += Reward.WIN_REWARD;
		}
		return dropOffReward;
	}

	private static double getGatherReward(Agent agent, Action action)
	{
		double gatherReward = 0;
		TerrainType terrainGathered = action.getType().getTerrainType();
		Resource teamResource = agent.getTeam().getResource(terrainGathered);
		if (teamResource.getCurrent()<=teamResource.getGoal())
		{
			gatherReward = Reward.GATHER_REWARD;
		}
		return gatherReward;
	}

	private static double getAttackReward(Agent agent, Action action)
	{
		double attackReward = 0;
		Agent enemyAgent = action.getEnemyAgent();
		double damage = Math.max(0, agent.getType().getAttack() - enemyAgent.getType().getDefense());
		attackReward += damage*Reward.ATTACK_REWARD;
		if (!enemyAgent.isAlive())
		{
			attackReward += Reward.KILL_REWARD;
		}
		return attackReward;
	}

	private static double getDieReward()
	{
		double dieReward = Reward.DIE_REWARD;
		return dieReward;
	}

}
