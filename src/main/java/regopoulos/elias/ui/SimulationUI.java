package regopoulos.elias.ui;

import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.MapViewTeam;

public interface SimulationUI
{
	public void start(String[] args);

	public void initOnSimLoad();

	public void log(String string);

	public MapViewTeam getSelectedTeam();

	public void setSelectedTeam(MapViewTeam team);

	public Agent getSelectedAgent();

	public void setSelectedAgent(Agent agent);
}
