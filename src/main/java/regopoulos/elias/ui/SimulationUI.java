package regopoulos.elias.ui;

import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.MapViewTeam;
import regopoulos.elias.scenario.Team;

public interface SimulationUI
{
	public void start(String[] args);

	public void initOnSimLoad();

	public MapViewTeam getSelectedTeam();

	public void setSelectedTeam(MapViewTeam team);

	public Agent getSelectedAgent();

	public void setSelectedAgent(Agent agent);

	//TODO: update()
}
