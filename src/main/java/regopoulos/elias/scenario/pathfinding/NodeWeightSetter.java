package regopoulos.elias.scenario.pathfinding;

import javafx.geometry.Dimension2D;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.AgentType;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.sim.Simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**Calculates and holds weights of pathfinding nodes.
 * Weights depend on amount and type of enemies nearby.
 */
public class NodeWeightSetter
{
	private HashMap<Dimension2D, Integer> nodeRisks;	//0 if no enemies nearby //TODO: Let nodeRisks fan out
	private List<Agent> knownEnemies;
	private HashMap<AgentType,Integer> risk;	//amount of risk (=damage received) from each enemy agent type

	public NodeWeightSetter()
	{
		this.nodeRisks = new HashMap<>();
		this.knownEnemies = new ArrayList<>();
		this.risk = new HashMap<>();
	}

	/**Gets called before each agent's pathfinding.
	 *
	 * @param curAgent
	 */
	public void update(Agent curAgent)
	{
		calcRisks(curAgent);
		setKnownEnemies(curAgent.getTeam());
		calcWeights(curAgent.getTeam());
		curAgent.setNodeRisks(this.nodeRisks);
	}

	private void calcRisks(Agent curAgent)
	{
		this.risk = new HashMap<>();
		for (AgentType type : AgentType.values())
		{
			this.risk.put(type, Math.max(0, type.getAttack()-curAgent.getType().getDefense()));
		}
	}

	private void setKnownEnemies(Team curTeam)
	{
		this.knownEnemies = Simulation.sim.getScenario().getPositionsWithAgents().values().stream().
				filter(agent -> agent.isAlive()).					//Living agents
				filter(agent -> curTeam.canSee(agent.pos)).			//that are visible
				filter(agent -> !agent.getTeam().equals(curTeam)).	//and enemies
				collect(Collectors.toList());
	}

	private void calcWeights(Team curTeam)
	{
		this.nodeRisks.clear();
		ArrayList<Dimension2D> dims;
		for (Agent enemy : knownEnemies)
		{
			dims = TileChecker.getNeighbours(enemy.pos).stream().
					filter(dim -> TileChecker.locationIsTraversable(dim, curTeam, false)).
					filter(dim -> TileChecker.locationIsNotOccupied(dim)).
					collect(Collectors.toCollection(ArrayList::new));

			for (Dimension2D dim : dims)
			{
				this.nodeRisks.put(dim, getNodeWeight(dim) + risk.get(enemy.getType()));
			}
		}

	}

	public int getNodeWeight(Dimension2D dim)
	{
		return (nodeRisks.get(dim)==null)?0: nodeRisks.get(dim);
	}

	//convenience method
	public int getNodeWeight(int y, int x)
	{
		return getNodeWeight(new Dimension2D(x,y));
	}

	public HashMap<Dimension2D, Integer> getNodeRisks()
	{
		return nodeRisks;
	}
}
