package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.*;
import regopoulos.elias.sim.Simulation;

import java.util.Arrays;

/**
 * Contains the stateVector of each agent of Winter/AutumnAI planner.
 *
 * Inputs are (in order):
 * Team: 	Needed Resources (for each resource)
 * Agent: 	Carrying resource (for each resource)
 * 			Current Hit Points
 * 			Attack
 * 			Defense
 * Action:	Whether action is available
 * 			PathLength to PoI
 * 			HP of enemy (at PoI)
 * 			Attack of enemy (at PoI)
 * 			Defense of enemy (at PoI)
 *
 * All values are normalized to [0,1]
 *
 * Actions are (in order, same as in the Enum ActionType, and times their amountToConsider):
 * 		EXPLORE
 * 		GATHER_WOOD
 * 		GATHER_STONE
 * 		GATHER_GOLD
 * 		ATTACK
 * 		DROP_OFF
 * 		Nothing (null, no Action chosen)
 *
 */

public class State
{
	private static int MAX_PATH_COST;	//Max path cost for given map, set as 2*Max(Width,Length)
	private static int MAX_HP;		//Maximum Health of any agent. HP/Attack/Defense are compared against it.

	private int inputIndex;		//keeps track of input array inputIndex
	private int outputIndex;	//keeps track of output array actions
	private double[] stateVector;
	private Action[] actions;
	private static ActionType[] actionTypes;	//the "guide" of how many actions of a specific type to use.
	private Agent lnkAgent;

	public State(Agent lnkAgent)
	{
		this.lnkAgent = lnkAgent;
		this.stateVector = new double[State.getNInputCount()];
	}

	/** Calculates any immutable data needed later on during the simulation */
	public static void initCalculations()
	{
		Map map = Simulation.sim.getScenario().getMap();
		State.MAX_PATH_COST = Math.max(map.getHeight(), map.getWidth());

		State.MAX_HP = Arrays.stream(AgentType.values()).
				mapToInt(AgentType::getMaxHP).max().getAsInt();

		//Init actionTypes
		State.actionTypes = new ActionType[State.getNOutputCount()];
		int actionTypeIndex = 0;
		for (ActionType actionType : ActionType.values())
		{
			for (int i=0; i<actionType.getAmountToConsider(); i++)
			{
				State.actionTypes[actionTypeIndex++] = actionType;
			}
		}
	}

	public double[] getStateVector()
	{
		return this.stateVector;
	}

	public Action[] getActions()
	{
		return actions;
	}

	public void updateState()
	{
		updateTeam();
		updateAgent();
		updateActions();
	}

	private void updateTeam()
	{
		this.inputIndex = 0;
		Team lnkTeam = lnkAgent.getTeam();

		//Needed resources
		for (TerrainType type : TerrainType.values())
		{
			if (type.isResource())
			{
				Resource resource = lnkTeam.getResource(type);
				if (resource==null || resource.getGoal()==0)	//no such resource needed -> its goal is already reached
				{
					stateVector[inputIndex++] = 1;
				}
				else
				{
					stateVector[inputIndex++] = lnkTeam.getResource(type).getCurrent()/(double)lnkTeam.getResource(type).getGoal();
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
				stateVector[inputIndex++] = (type.equals(carrying))?1:0;
			}
		}

		//HP
		stateVector[inputIndex++] = lnkAgent.getHP()/(double)State.MAX_HP;
		//Attack
		stateVector[inputIndex++] = lnkAgent.getType().getAttack()/(double)State.MAX_HP;
		//Defense
		stateVector[inputIndex++] = lnkAgent.getType().getDefense()/(double)State.MAX_HP;
	}

	/** Sets the action vector,
	 * and updates the state vector with the actions' inputs.
	 */
	private void updateActions()
	{
		//Set action (output) vector
		this.outputIndex = 0;
		this.actions = getActionVector();

		//Set remaining state (input) vector
		for (int i=0; i<this.actions.length; i++)
		{
			if (this.actions[i]!=null)
			{
				addActionToState(this.actions[i]);
			}
			else	//Filling out the rest of this actionTypes' input nodes with null actions
			{
				addEmptyActionToState(State.actionTypes[i]);
			}
		}
	}

	/** Add Action to state vector */
	private void addActionToState(Action action)
	{
		double isAvailable = 1;
		double pathCost = action.getPathCost()/(double)State.MAX_PATH_COST;
		Agent enemyAgent = Simulation.sim.getScenario().getAgentAtPos(action.getPoI());
		if (enemyAgent==null)	//agent has just died
		{
			enemyAgent = action.getEnemyAgent();	//get last attacked agent
		}

		switch (action.getType())
		{
			case EXPLORE:
				stateVector[inputIndex++] = isAvailable;
				stateVector[inputIndex++] = pathCost;
				break;
			case GATHER_WOOD:
			case GATHER_STONE:
			case GATHER_GOLD:
				stateVector[inputIndex++] = isAvailable;
				stateVector[inputIndex++] = pathCost;
				break;
			case ATTACK:
				stateVector[inputIndex++] = isAvailable;
				stateVector[inputIndex++] = pathCost;
				stateVector[inputIndex++] = enemyAgent.getHP()/(double)State.MAX_HP;
				stateVector[inputIndex++] = enemyAgent.getType().getAttack()/(double)State.MAX_HP;
				stateVector[inputIndex++] = enemyAgent.getType().getDefense()/(double)State.MAX_HP;
				break;
			case DROP_OFF:
				stateVector[inputIndex++] = isAvailable;
				stateVector[inputIndex++] = pathCost;
				break;
		}

	}

	/** Adds to stateVector an array corresponding to a null action of type actionType */
	private void addEmptyActionToState(ActionType actionType)
	{
		double[] array = null;
		switch (actionType)
		{
			case EXPLORE:
				array = new double[] {0,0};
				break;
			case GATHER_WOOD:
				array = new double[] {0,0};
				break;
			case GATHER_STONE:
				array = new double[] {0,0};
				break;
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
			stateVector[inputIndex++] = emptyInputNode;
		}
	}

	/** Returns lnkAgent's possible Actions as an array,
	 *  padded by null objects to fit the respective amountToConsider */
	private Action[] getActionVector()
	{
		Action[] actions = new Action[State.getNOutputCount()];
		for (ActionType actionType : ActionType.values())
		{
			int amountToConsider = actionType.getAmountToConsider();
			int amountConsidered = 0;
			Action foo[] = lnkAgent.getPossibleActions().stream().
					filter(action -> action.getType().equals(actionType)).
					toArray(Action[]::new);
			for (Action action : foo)
			{
				actions[outputIndex++] = action;
				amountConsidered++;
			}
			//Filling out the rest of this actionTypes' input nodes with null actions
			for (int i=amountConsidered; i<amountToConsider; i++)
			{
				actions[outputIndex++] = null;
			}
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
		return nOutputCount;
	}

	@Override
	public String toString()
	{
		String str = "[";
		String digit;
		for (double d : this.stateVector)
		{
			str += (d==0 || d==1)?d:String.format("%.4f",d);
			str += ", ";
		}
		str = str.substring(0, str.length() - 2);
		str += "]";
		return str;
	}

}
