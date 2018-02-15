package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;

/**Responsible for selecting Actions and setting goals,
 * which are then fed to the pathfinder.
 * Each team's agents share the same planner.
 *
 * The four AIs are the permutation of:
 * {Neural Net, Custom Rules} and {Distinct roles, Free roles},
 * and are named after the year's seasons.
 *
 * 					Neural Net:	|Custom Rules:
 * Free roles:		|WinterAI	|SummerAI
 * Distinct roles:	|AutumnAI	|SpringAI
 */
public abstract class Planner
{
	//TODO: Add methods, implement Spring-, Summer-, AutumnAI- and Winter-AI
	//TODO: Add option in OptionsPage to choose AI for team (or just make it default for team number X).
	Team lnkTeam;

	public Planner(Team lnkTeam)
	{
		this.lnkTeam = lnkTeam;
	}
	public abstract Action getNextAction(Agent agent);
}
