package regopoulos.elias.sim;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import regopoulos.elias.scenario.Agent;
import regopoulos.elias.scenario.Team;
import regopoulos.elias.scenario.ai.BadVisibilityException;
import regopoulos.elias.ui.gui.Camera;
import regopoulos.elias.ui.gui.Renderer;
import regopoulos.elias.ui.gui.SimWindow;

import java.io.InputStream;
import java.util.Properties;

import static regopoulos.elias.sim.TimerGranularity.AGENT;

/**Acts as the game loop
 */
public class SimLoop extends AnimationTimer
{
	private static double MAX_SIM_TICK_TIME;	//in ms
	private static double MIN_SIM_TICK_TIME;			//in ms
	private static int MAX_ROUNDS_COUNT;	//maximum rounds to loop through until scenario is reset

	private TimerGranularity granularity;
	private boolean isPaused;	//Concerns simulation. Rendering speed remains independent
	private boolean isFinished;
	private double simulationTickTime;	//time to wait, in ms, between different each simulation's tick. Rendering speed remains independent.
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
		loadProperties();
		this.granularity = AGENT;
		this.isPaused = false;
		this.lnkSim = Simulation.sim;
		this.curTeam = null;	//will be set on loop
		this.curAgent = null;	//will be set on loop
		SimWindow sw = (SimWindow)lnkSim.getSimUI();
		this.camera = sw.getCamera();
		this.lnkRenderer = sw.getRenderer();
		this.roundIndicator = new SimpleStringProperty("Round indicator");
		this.simTickTimeIndicator = new SimpleStringProperty("Sim tick indicator");
	}

	private void loadProperties()
	{
		Properties prop = new Properties();
		try (InputStream fis = SimLoop.class.getClassLoader().getResourceAsStream("SimulationLoop.properties"))
		{
			prop.loadFromXML(fis);
			SimLoop.MAX_SIM_TICK_TIME = Double.parseDouble(prop.getProperty("MaxSimTickTime"));
			SimLoop.MIN_SIM_TICK_TIME = Double.parseDouble(prop.getProperty("MinSimTickTime"));
			SimLoop.MAX_ROUNDS_COUNT  = Integer.parseInt(prop.getProperty("MaxRoundsAllowed"));
			this.simulationTickTime   = MIN_SIM_TICK_TIME;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public SimLoop restartedLoop()
	{
		this.roundsCount = 0;
		this.isFinished = false;
		SimWindow sw = (SimWindow)lnkSim.getSimUI();
		this.camera = sw.getCamera();
		return this;
	}

	/**Each SimLoop tick (called from JavaFX itself, defaults to 60 fps)
	 * the scene ought to be rendered. Depending on the granularity
	 * we wish for, one agent, one team, all teams, or the whole simulation
	 * are calculated in between.
	 */
	@Override
	public void handle(long now)
	{
		if (!isPaused && !waitForNextStep() && !isFinished)
		{
			switch (granularity)
			{
				case AGENT:
					doAgent();
					break;
				case TEAM:
					doTeam();
					break;
				case ROUND:
					doRound();
					break;
			}
		}
		//rendering continues regardless of pause
		if (lnkRenderer!=null)	//don't render if CLUI is used
		{
			camera.update();
			lnkRenderer.render();
			((SimWindow)Simulation.sim.getSimUI()).updateChildren();
		}
	}

	private void doAgent()
	{
		switchAgent();
		if (isFinished)	//don't calculate to end of team/round, if scenario is finished
		{
			return;
		}
		this.roundIndicator.set("Round " + roundsCount + ", " + curTeam + ", " + curAgent);
		try
		{
			curAgent.update();
		}
		catch (BadVisibilityException e)
		{
			e.printStackTrace();
		}
	}

	private void doTeam()
	{
		do
		{
			doAgent();
		}while (curTeam.hasNextAgent(curAgent));
	}

	private void doRound()
	{
		for (Team team : Simulation.sim.getScenario().getTeams())
		{
			doTeam();
		}
	}

	private void endScenario()
	{
		Simulation.sim.log("Scenario run No." + lnkSim.getScenario().getRunCount() + " has ended.");
		Team winner = lnkSim.getScenario().getWinner();
		winner.win();
		lnkSim.getScenario().finish();

		if (lnkSim.hasFinished())
		{
			lnkSim.finish();
		}
		else
		{
			lnkSim.getScenario().restart();
		}
	}

	private void switchAgent()
	{
		if (curTeam==null)
		{
			switchTeam();
		}
		this.curAgent = curTeam.getNextAgent(curAgent);
		while (curAgent==null)	//while rather than if, to account for case where multiple teams don't have any agents left
		{
			switchTeam();
			this.curAgent = curTeam.getNextAgent(curAgent);
		}
	}

	private void switchTeam()
	{
		this.curTeam = lnkSim.getScenario().getNextTeam(curTeam);
		if (this.curTeam==null)
		{
			switchRound();
			this.curTeam = lnkSim.getScenario().getNextTeam(curTeam);
		}
		if (!isFinished)
		{
			Simulation.sim.log("Switching to " + this.curTeam);
		}
	}

	private void switchRound()
	{
		this.roundsCount++;
		if (lnkSim.getScenario().hasWinner())
		{
			endScenario();
		}
		if (getRoundsCount()>SimLoop.MAX_ROUNDS_COUNT)
		{
			Simulation.sim.log("Reached " + MAX_ROUNDS_COUNT + " rounds, resetting.");
			lnkSim.getScenario().restart();
		}
		if (!isFinished)
		{
			Simulation.sim.log("\nEntering round No." + roundsCount);
		}
	}

	public int getRoundsCount()
	{
		return this.roundsCount;
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

	/**Returns whether the next step should be calculated,
		 * depending on simulationTickTime.
		 */
	private boolean waitForNextStep()
	{
		msPassed += System.currentTimeMillis() - lastSimTick;
		lastSimTick = System.currentTimeMillis();
		boolean waitForStep = true;
		if  (msPassed>simulationTickTime)
		{
			msPassed %= simulationTickTime;	//% rather than -, in order to skip multiple accumulated frames
			waitForStep = false;
		}
		return waitForStep;
	}

	/**Tick time halves on "Faster" button click.*/
	public void faster()
	{
		setSimulationTickTime(simulationTickTime/2);
	}

	/**Tick time doubles on "Slower" button click.*/
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

	void finish()
	{
		this.isFinished = true;
	}
}
