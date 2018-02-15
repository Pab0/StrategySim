package regopoulos.elias.scenario.ai;

/**Gets thrown if agent is standing next to UKNOWN tile.
 * This should never happen, since all neighbouring tiles
 * are added to the agent's team's visibility map.
 */
public class BadVisibilityException extends Throwable
{

}
