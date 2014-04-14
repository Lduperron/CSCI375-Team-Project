package gameCode.ai;

import java.util.ArrayList;

import gameCode.obj.Obj;
import gameCode.obj.item.weapon.Weapon;
import gameCode.obj.mob.humans.EnemySoldier;
import gameCode.obj.mob.Mob;
import gameCode.obj.mob.Direction;

import core.server.ServerEngine;
import core.shared.Position;
import core.shared.ConfigOptions;

public class EnemyAI {

	private Obj enemyObject;
	private Mob playerObject;
	private boolean playerInRange;
	
	// determines how close the player must be for the AI to take action
	private int rangeDistance = 8;
	
	// time until next AI move
	private int timeTillAction = 7;
	
	private ServerEngine server;
	
	private int enemyHealth;
	
	private int tID;
	
	public EnemyAI(Mob s, Mob p, ServerEngine server, int startHP)
	{
		this.playerObject = p;
		this.playerInRange = false;
		this.server = server;
		this.enemyHealth = startHP;
		
		// Initialize texture data for the enemy mob object
		this.tID = (int) (Math.random() * ConfigOptions.enemyUp.length);
		
		s.setTextures(ConfigOptions.enemyUp[tID], ConfigOptions.enemyRight[tID],
					  ConfigOptions.enemyDown[tID], ConfigOptions.enemyLeft[tID]);
		
		s.setDirection(Direction.RIGHT);
		
		this.enemyObject = s;
		
		
	}
	
	public Obj getEnemyObject()
	{
		return (this.enemyObject);
	}
	
	/*
	 *  Will calculate and perform an AI action
	 *  ie. move enemy, shoot at player, etc.
	 */
	public void doAction()
	{
		if (timeTillAction == 0)
		{
			// Calculate if the player is in range of the AI object
			this.playerInRange = this.playerInSight(this.server.getObjects());
			
			if (playerInRange)
			{
				// Player is in range, do some AI action
				
				// Move in a random direction
				int random = (int) (Math.random() * 5);
				
				Position p = new Position();
				Direction d = null;
				Mob m = (Mob) this.enemyObject;
				p.UID = this.enemyObject.UID;
				if (this.enemyObject.tileYPosition == this.playerObject.tileYPosition)
				{
					// shoot at the player!
					System.out.println(enemyObject.UID+" shoots it up.");
					Position shootingAt = new Position();
					shootingAt.UID = -1;
					shootingAt.x = this.enemyObject.tileXPosition - this.distanceXToPlayer();
					shootingAt.y = this.enemyObject.tileYPosition;
					Mob eObj = (Mob) this.enemyObject;
					eObj.leftHand.rangedEvent(shootingAt);
					
					if (this.distanceXToPlayer() < 0)
					{
						d = Direction.RIGHT;
					}
					else
					{
						d = Direction.LEFT;
					}
				}
				else if (this.enemyObject.tileXPosition == this.playerObject.tileXPosition)
				{
					// shoot at the player!
					System.out.println(enemyObject.UID+" shoots it up.");
					Position shootingAt = new Position();
					shootingAt.UID = -1;
					shootingAt.x = this.enemyObject.tileXPosition;
					shootingAt.y = this.enemyObject.tileYPosition - this.distanceYToPlayer();
					Mob eObj = (Mob) this.enemyObject;
					eObj.leftHand.rangedEvent(shootingAt);
					if (this.distanceYToPlayer() < 0)
					{
						d = Direction.UP;
					}
					else
					{
						d = Direction.DOWN;
					}
				}
				else 
				{
					if (random == 0)
					{
						p.x = 0;
						p.y = 1;
						System.out.println(enemyObject.UID+" is moving up.");
						d= Direction.UP;
					}
					else if (random == 1)
					{
						p.x = 0;
						p.y = -1;	
						System.out.println(enemyObject.UID+" is moving down.");
						d = Direction.DOWN;
					}
					else if (random == 2)
					{
						p.x = 1;
						p.y = 0;
						System.out.println(enemyObject.UID+" is moving right.");
						d = Direction.RIGHT;
					}
					else if (random == 3)
					{
						p.x = -1;
						p.y = 0;	
						System.out.println(enemyObject.UID+" is moving left.");
						d = Direction.LEFT;
					}
					else if (random == 4)
					{
						p.x = 0;
						p.y = 0;	
						System.out.println(enemyObject.UID+" stays put.");
					}
					this.server.requestMove(p);
				}
				m.setDirection(d);
				this.server.updateEnemyDirection(m.UID, d);
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
		return ( (Math.abs(distanceXToPlayer()) < this.rangeDistance) && (Math.abs(distanceYToPlayer()) < this.rangeDistance) );
	}
	
	public int distanceXToPlayer()
	{
		return ( this.enemyObject.tileXPosition - this.playerObject.tileXPosition );
	}
	
	public int distanceYToPlayer()
	{
		return ( this.enemyObject.tileYPosition - this.playerObject.tileYPosition );
	}
	
	public void getHit(int damage)
	{
		this.enemyHealth -= damage;
	}
	
	public int getHealth()
	{
		return this.enemyHealth;
	}
}
