package gameCode.ai;

import java.util.ArrayList;

import gameCode.obj.Obj;
import gameCode.obj.mob.humans.EnemySoldier;
import gameCode.obj.mob.Mob;

import core.server.ServerEngine;
import core.shared.Position;

public class EnemyAI {

	private EnemySoldier enemyObject;
	private Mob playerObject;
	private boolean playerInRange;
	
	// determines how close the player must be for the AI to take action
	private int rangeDistance  = 8;
	
	public EnemyAI(EnemySoldier s, Mob p)
	{
		this.enemyObject = s;
		this.playerObject = p;
		this.playerInRange = false;
	}
	
	public EnemySoldier getEnemyObject()
	{
		return (this.enemyObject);
	}
	
	/*
	 *  Will calculate and perform an AI action
	 *  ie. move enemy, shoot at player, etc.
	 */
	public void doAction(ServerEngine server)
	{
		// Calculate if the player is in range of the AI object
		this.playerInRange = this.playerInSight(server.getObjects());
		
		if (playerInRange)
		{
			// Player is in range, do some AI action
			
			// Move in a random direction
			int random = (int) (Math.random() * 4);
			
			Position p = new Position();
			p.UID = this.enemyObject.UID;
			
			if (random == 0)
			{
				p.x = 0;
				p.y = 1;
			}
			else if (random == 1)
			{
				p.x = 0;
				p.y = -1;	
			}
			else if (random == 2)
			{
				p.x = 1;
				p.y = 0;
			}
			else if (random == 3)
			{
				p.x = -1;
				p.y = 0;	
			}
			server.requestMove(p);
		}
	}
	
	/*
	 *  Right now, enemy will always be able to see the player, and will thus always
	 *  be moving. If there is time, it may use an A* search to determine if the player can be seen
	 */
	public boolean playerInSight(ArrayList<ArrayList<ArrayList<Obj>>> world)
	{
		return true;
	}
	
	public int distanceXToPlayer()
	{
		return ( Math.abs( this.enemyObject.tileXPosition - this.playerObject.tileXPosition ) );
	}
	
	public int distanceYToPlayer()
	{
		return ( Math.abs( this.enemyObject.tileYPosition - this.playerObject.tileYPosition ) );
	}
}
