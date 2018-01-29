package regopoulos.elias.sim;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.ui.gui.Camera;
import regopoulos.elias.ui.gui.Renderer;
import regopoulos.elias.ui.gui.SimWindow;

import static regopoulos.elias.sim.TimerGranularity.AGENT;

/* Acts as the game loop
 */
public class SimLoop extends AnimationTimer
{
	private static final double MAX_SIM_TICK_TIME = 10000.0;	//in ms
	private static final double MIN_SIM_TICK_TIME = 0.1;			//in ms
	private static final double DEFAULT_SIM_TICK_TIME = MIN_SIM_TICK_TIME;	//in ms

	private TimerGranularity granularity;
	private boolean isPaused;	//Concerns simulation. Rendering speed remains independent
	private double simulationTickTime = DEFAULT_SIM_TICK_TIME;	//time to wait, in ms, between different each simulation's tick. Rendering speed remains independent.
	private double lastSimTick;	//in ms
	private double msPassed;

	private Simulation lnkSim;
	private Agent curAgent;
	private Team curTeam;
	private Camera camera;
	private Renderer lnkRenderer;

	private int roundsCount;
	public StringProperty roundIndicator;
	public StringProperty simTickTimeIndicator;

	public SimLoop()
	{
		this.granularity = AGENT;
		this.isPaused = false;
		this.lnkSim = Simulation.sim;
		this.curTeam = lnkSim.getScenario().getTeams()[0];
		this.curAgent = curTeam.getAgents()[0];
		SimWindow sw = (SimWindow)lnkSim.getSimUI();
		this.camera = sw.getCamera();
		this.lnkRenderer = sw.getRenderer();
		this.roundIndicator = new SimpleStringProperty("Round indicator");
		this.simTickTimeIndicator = new SimpleStringProperty("Sim tick indicator");
	}

	/* Each SimLoop tick (called from JavaFX itself, defaults to 60 fps)
	 * the scene ought to be rendered. Depending on the granularity
	 * we wish for, one agent, one team, all teams, or the whole simulation
	 * are calculated in between.
	 */
	@Override
	public void handle(long now)
	{
		if (!isPaused && !waitForNextStep())
		{
			switch (granularity)
			{
				case AGENT:
					nextAgent();
					break;
				case TEAM:
					nextTeam();
					break;
				case ROUND:
					nextRound();
					break;
				case SIMULATION:
					nextSimulation();
					break;
			}
		}
		//rendering continues regardless of pause
		if (lnkRenderer!=null)	//don't render if CLUI is used
		{
			camera.update();
			lnkRenderer.render();
		}
	}

	private void nextAgent()
	{
		this.curAgent = curTeam.getNextAgent(curAgent);
		if (curAgent==null)
		{
			nextTeam();
		}
		this.roundIndicator.set("Round " + roundsCount + ", " + curTeam + ", " + curAgent);
		curAgent.update();
	}

	private void nextTeam()
	{
		this.curTeam = lnkSim.getScenario().getNextTeam(this.curTeam);
		if (curTeam==null)
		{
			nextRound();
		}
		this.curAgent = null;	//indicating that the next team has started
		switch (granularity)
		{
			case AGENT: //don't want agent granularity to morph into team granularity
				nextAgent();
				break;
			default:
				while (curTeam.hasNextAgent(curAgent))
				{
					nextAgent();
				}
				break;
		}
	}

	private void nextRound()
	{
		roundsCount++;
		this.curTeam = lnkSim.getScenario().getNextTeam(this.curTeam);
		if (lnkSim.getScenario().getWinner()!=null)
		{
			nextSimulation();
		}
		this.curTeam = null;	//indicating that the next round has started
		switch (granularity)
		{
			case AGENT:	//don't want agent granularity to morph into round granularity
			case TEAM:	//don't want team granularity to morph into round granularity
				nextTeam();
				break;
			default:
				while (lnkSim.getScenario().hasNextTeam(this.curTeam))
				{
					nextTeam();
				}
				break;
		}
	}

	private void nextSimulation()
	{
		//TODO
	}


	public void setGranularity(TimerGranularity granularity)
	{
		this.granularity = granularity;
	}

	public void flipPaused()
	{
		this.isPaused = !this.isPaused;
	}

	public boolean isPaused()
	{
		return isPaused;
	}

	/* Returns whether the next step should be calculated,
		 * depending on simulationTickTime.
		 */
	private boolean waitForNextStep()
	{
		msPassed += System.currentTimeMillis() - lastSimTick;
		lastSimTick = System.currentTimeMillis();
		boolean waitForStep = true;
		if  (msPassed>simulationTickTime)
		{
			System.out.println("Ticks/sec: " + 1000/msPassed);
			msPassed %= simulationTickTime;	//% rather than -, in order to skip multiple accumulated frames
			waitForStep = false;
		}
		return waitForStep;
	}

	/* Tick time halves on "Faster" button click.*/
	public void faster()
	{
		setSimulationTickTime(simulationTickTime/2);
	}

	/* Tick time doubles on "Slower" button click.*/
	public void slower()
	{
		setSimulationTickTime(simulationTickTime*2);
	}

	private void setSimulationTickTime(double simTickTime)
	{
		this.simulationTickTime = simTickTime;
		//check sim tick bounds
		simulationTickTime = Math.min(simulationTickTime, SimLoop.MAX_SIM_TICK_TIME);
		simulationTickTime = Math.max(simulationTickTime, SimLoop.MIN_SIM_TICK_TIME);
		this.simTickTimeIndicator.set("Sim tick: " + String.format("%.2f",this.simulationTickTime) + "ms");
	}
}
