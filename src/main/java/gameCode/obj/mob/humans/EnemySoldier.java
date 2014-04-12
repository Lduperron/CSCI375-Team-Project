package gameCode.obj.mob.humans;

import core.shared.ConfigOptions;

public class EnemySoldier extends Human
{
	
	public EnemySoldier(int x, int y)
	{
		super(x, y);

		this.AIcontrolled = true;
		this.texture = ConfigOptions.enemyTexture;
	}

}
