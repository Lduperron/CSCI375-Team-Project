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
	
	// time until next AI move
	private int timeTillAction = 7;
	
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
		if (timeTillAction == 0)
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
					System.out.println(enemyObject.UID+" is moving up.");
				}
				else if (random == 1)
				{
					p.x = 0;
					p.y = -1;	
					System.out.println(enemyObject.UID+" is moving down.");
				}
				else if (random == 2)
				{
					p.x = 1;
					p.y = 0;
					System.out.println(enemyObject.UID+" is moving right.");
				}
				else if (random == 3)
				{
					p.x = -1;
					p.y = 0;	
					System.out.println(enemyObject.UID+" is moving left.");
				}
				server.requestMove(p);
			}
			
			this.timeTillAction = 7;
		}
		else
		{
			this.timeTillAction--;
		}
	}
	
	/*
	 *  The enemy AI will perform actions only if the player is in sight (slightly out of character's FOV).
	 */
	public boolean playerInSight(ArrayList<ArrayList<ArrayList<Obj>>> world)
	{
		return ( (distanceXToPlayer() < this.rangeDistance) && (distanceYToPlayer() < this.rangeDistance) );
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
