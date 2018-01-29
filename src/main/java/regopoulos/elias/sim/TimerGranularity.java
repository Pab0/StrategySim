package regopoulos.elias.sim;

/* Determines how often the game should be re-rendered.
 * Everything is processed regardless of granularity,
 * but the user may want to inspect the visual outcome
 * on a different granularity throughout the simulation.
 */
public enum TimerGranularity
{
	AGENT		(),
	TEAM		(),
	ROUND		(),
	SIMULATION	();
}
