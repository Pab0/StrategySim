package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.*;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Contains the state of each agent of Winter/AutumnAI planner.
 *
 * All values are normalized to [0,1]
 */

public class State
{
	private static int MAX_PATH_COST;	//Max path cost for given map, set as 2*Max(Width,Length)
	private static int MAX_HP;		//Maximum Health of any agent. HP/Attack/Defense are compared against it.

	private int index;	//keeps track of input array index
	private double[] state;

	private Agent lnkAgent;

	public State(Agent lnkAgent)
	{
		this.lnkAgent = lnkAgent;
		this.state = new double[State.getNInputCount()];
	}

	/** Calculates any immutable data needed later on during the simulation */
	public static void initCalculations()
	{
		Map map = Simulation.sim.getScenario().getMap();
		State.MAX_PATH_COST = Math.max(map.getHeight(), map.getWidth());

		State.MAX_HP = Arrays.stream(AgentType.values()).
				mapToInt(AgentType::getMaxHP).max().getAsInt();
	}

	double[] getState()
	{
		return this.state;
	}

	public void updateState()
	{
		updateTeam();
		updateAgent();
		updateActions();
	}

	private void updateTeam()
	{
		this.index = 0;
		Team lnkTeam = lnkAgent.getTeam();

		//Needed resources
		for (TerrainType type : TerrainType.values())
		{
			if (type.isResource())
			{
				Resource resource = lnkTeam.getResource(type);
				if (resource==null || resource.getGoal()==0)	//no such resource needed -> its goal is already reached
				{
					state[index++] = 1;
				}
				else
				{
					state[index++] = lnkTeam.getResource(type).getCurrent()/(double)lnkTeam.getResource(type).getGoal();
				}
			}
		}
	}

	private void updateAgent()
	{
		//Resource carrying
		TerrainType carrying = lnkAgent.getResouceCarrying();
		for (TerrainType type : TerrainType.values())
		{
			if (type.isResource())
			{
				state[index++] = (type.equals(carrying))?1:0;
			}
		}

		//HP
		state[index++] = lnkAgent.getHP()/(double)State.MAX_HP;
		//Attack
		state[index++] = lnkAgent.getType().getAttack()/(double)State.MAX_HP;
		//Defense
		state[index++] = lnkAgent.getType().getDefense()/(double)State.MAX_HP;
	}

	private void updateActions()
	{
		HashMap<ActionType, ArrayList<Action>> actions = getActionHashMap();
		for (ActionType actionType : ActionType.values())
		{
			for (Action action : actions.get(actionType))
			{
				addAction(action);

				//Filling out the rest of this actionTypes' input nodes with null actions
				int amountToConsider = actionType.getAmountToConsider();
				int amountConsidered = actions.get(actionType).size();
				for (int i=amountConsidered; i<amountToConsider; i++)
				{
					addEmptyActionArray(actionType);
				}
			}
		}
	}

	private void addAction(Action action)
	{
		double isAvailable = 1;
		double pathCost = action.getPathCost()/(double)State.MAX_PATH_COST;
		Agent enemyAgent = Simulation.sim.getScenario().getAgentAtPos(action.getPoI());

		switch (action.getType())
		{
			case EXPLORE:
				state[index++] = isAvailable;
				state[index++] = pathCost;
				break;
			case GATHER_WOOD:
			case GATHER_STONE:
			case GATHER_GOLD:
				state[index++] = isAvailable;
				state[index++] = pathCost;
				break;
			case ATTACK:
				state[index++] = isAvailable;
				state[index++] = pathCost;
				state[index++] = enemyAgent.getHP()/(double)State.MAX_HP;
				state[index++] = enemyAgent.getType().getAttack()/(double)State.MAX_HP;
				state[index++] = enemyAgent.getType().getDefense()/(double)State.MAX_HP;
				break;
			case DROP_OFF:
				state[index++] = isAvailable;
				state[index++] = pathCost;
				break;
		}

	}

	/** Adds to state an array corresponding to a null action of type actionType */
	private void addEmptyActionArray(ActionType actionType)
	{
		double[] array = null;
		switch (actionType)
		{
			case EXPLORE:
				array = new double[] {0,0};
				break;
			case GATHER_WOOD:
			case GATHER_STONE:
			case GATHER_GOLD:
				array = new double[] {0,0};
				break;
			case ATTACK:
				array = new double[] {0,0,0,0,0};
				break;
			case DROP_OFF:
				array = new double[] {0,0};
				break;
		}
		for (double emptyInputNode : array)
		{
			state[index++] = emptyInputNode;
		}
	}

	/** Returns lnkAgent's possible Actions, hashed by their ActionType */
	private HashMap<ActionType, ArrayList<Action>> getActionHashMap()
	{
		HashMap<ActionType, ArrayList<Action>> actions = new HashMap<>();
		for (ActionType actionType : ActionType.values())
		{
			actions.put(actionType, new ArrayList<Action>());
		}
		for (Action possibleAction : lnkAgent.getPossibleActions())
		{
			actions.get(possibleAction.getType()).add(possibleAction);
		}
		return actions;
	}

	/** @return The number of nodes in the neural network's input layer. */
	static int getNInputCount()
	{
		long resourceNum = Arrays.stream(TerrainType.values()).
				filter(TerrainType::isResource).count();
		int nInputCount = 0;

		//Team-specific:
		nInputCount += resourceNum;	 //1 input/resource (resources still needed)

		//Agent-specific:
		nInputCount += 3; //1 input each for HP, Attack, Defense
		nInputCount += resourceNum;	//1 input/possibly carrying resource

		//PoI-specific:
		int attacks				= ActionType.ATTACK.getAmountToConsider();

		int allActions = Arrays.stream(ActionType.values()).
				mapToInt(ActionType::getAmountToConsider).sum();

		//is available (and/or agent is eligible)
		nInputCount += 1*(allActions);
		//weighted path length
		nInputCount += 1*(allActions);
		//1 input each for HP, Attack, Defense
		nInputCount += 3*(attacks);

		return nInputCount;
	}

	/** @return The number of nodes in the neural network's output layer. */
	static int getNOutputCount()
	{
		int nOutputCount = Arrays.stream(ActionType.values()).
				mapToInt(ActionType::getAmountToConsider).sum();
		nOutputCount += 1;	//Do nothing; no Zugzwang here either.
		return nOutputCount;
	}

	@Override
	public String toString()
	{
		String str = "[";
		String digit;
		for (double d : this.state)
		{
			str += (d==0 || d==1)?d:String.format("%.4f",d);
			str += ", ";
		}
		str = str.substring(0, str.length() - 2);
		str += "]";
		return str;
	}

}
