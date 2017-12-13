package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;

public class Agent
{
	private AgentType type;
	private TerrainType resouceCarrying;
	private boolean carriesResource;
	private int HP;
	private final double speed;
	private final int attack, defense;
	private double stepsToMake;		//accumulating steps according to speed

	public Dimension2D pos;	//agent's current position

	Agent(AgentType type)
	{
		this.type = type;
		this.HP = this.type.maxHP;
		this.speed = this.type.speed;
		this.attack = this.type.attack;
		this.defense = this.type.defense;
		this.carriesResource = false;
	}

	public void dropOffResource()
	{
		//TODO check if elligible, maube do check elsewhere?
		this.carriesResource = false;
		this.resouceCarrying = null;
	}

	public void gatherResource(TerrainType type)
	{
		//TODO check if resource, etc.
		this.carriesResource = true;
		this.resouceCarrying = type;
	}
}
