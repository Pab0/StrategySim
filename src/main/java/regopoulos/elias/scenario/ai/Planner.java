package regopoulos.elias.scenario.ai;

import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;

import java.util.ArrayList;

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
	public static final String DEFAULT_AI = "SummerAI";
	//TODO: Add methods, implement Spring-, Summer-, AutumnAI- and Winter-AI
	Team lnkTeam;

	public Planner(Team lnkTeam)
	{
		this.lnkTeam = lnkTeam;
	}
	public abstract Action getNextAction(Agent agent);

	/**Creates and returns Planner implementation named `name`
	 *
	 * @param name The name of the class. Has to implement Planner.
	 * @return
	 */
	public static Planner createPlannerByName(String name, Team lnkTeam)
	{
		Planner planner = null;
		switch (name)
		{
			case "SummerAI":
				planner = new SummerAI(lnkTeam);
				break;
			case "SpringAI":
				planner = new SpringAI(lnkTeam);
				break;
				//TODO
		}
		return planner;
	}

	public static ArrayList<String> getPlannerTypes()
	{
		ArrayList<String> plannerTypes = new ArrayList<>();
		plannerTypes.add("SummerAI");
		plannerTypes.add("SpringAI");
		//TODO add the other two as well
		return plannerTypes;
	}

	abstract public String getPlannerName();
}
