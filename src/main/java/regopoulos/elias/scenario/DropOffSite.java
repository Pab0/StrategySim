package regopoulos.elias.scenario;

import javafx.geometry.Dimension2D;
import regopoulos.elias.sim.Simulation;

public class DropOffSite
{
	private final Team lnkTeam;
	private final Dimension2D position;

	DropOffSite(TerrainType type, Dimension2D position)
	{
		this(Simulation.sim.getScenario().getTeamByID(type), position);
	}

	DropOffSite(Team lnkTeam, Dimension2D position)
	{
		this.lnkTeam = lnkTeam;
		this.position = position;
	}

	Dimension2D getPosition()
	{
		return position;
	}

	public Team getLnkTeam()
	{
		return lnkTeam;
	}
}
