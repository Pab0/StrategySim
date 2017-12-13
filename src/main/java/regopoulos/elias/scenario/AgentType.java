package regopoulos.elias.scenario;

public enum AgentType
{
	VILLAGER	(3,1,0,1),
	HUNTER		(4,2,0,2),
	GUARD		(6,2,1,0.6),
	KNIGHT		(8,3,2,2.5);

	int maxHP;
	int attack;
	int defense;
	double speed;

	AgentType(int maxHP, int attack, int defense, double speed)
	{
		this.maxHP = maxHP;
		this.attack = attack;
		this.defense = defense;
		this.speed = speed;
	}
}
