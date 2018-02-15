package regopoulos.elias.scenario;

/**Gets thrown if too many agents are chosen for a scenario.
 * This happens if there are not enough traversable tiles
 * in a continuous region around any of each team's dropoffSites.
 *
 * Also gets thrown, if there's no eligible tile close enough to
 * the agent's position.
 */
public class NotEnoughTilesFoundException extends Exception
{

}
