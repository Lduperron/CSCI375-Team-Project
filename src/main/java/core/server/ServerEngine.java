package core.server;

import static core.shared.ConfigOptions.MAP_SIZE_X;
import static core.shared.ConfigOptions.MAP_SIZE_Y;
import static core.shared.ConfigOptions.TILE_SIZE;
import static core.shared.ConfigOptions.VIEW_DISTANCE_X;
import static core.shared.ConfigOptions.VIEW_DISTANCE_Y;

import core.shared.SoundManager;

import gameCode.obj.Obj;
import gameCode.obj.getObjUID;
import gameCode.obj.effect.projectile.Projectile;
import gameCode.obj.item.Item;
import gameCode.obj.item.weapon.Weapon;
import gameCode.obj.mob.Mob;
import gameCode.obj.mob.Direction;
import gameCode.obj.mob.humans.EnemySoldier;
import gameCode.obj.structure.Door;
import gameCode.obj.structure.Wall;
import gameCode.ai.EnemyAI;
import helpers.Hand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.primitives.MutableInteger;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.esotericsoftware.kryonet.Connection;

import core.client.ClientEngine;
import core.network.NetServer;
import core.shared.ConfigOptions;
import core.shared.Message;
import core.shared.Position;
import core.shared.UidPair;

public class ServerEngine extends Thread
{
	
	private NetServer network;
	boolean masterLoop = true;
	long masterLoopTime = System.currentTimeMillis();
	
	public TiledMap map;
	public ArrayList<ArrayList<ArrayList<Obj>>> ObjectArray = new ArrayList<ArrayList<ArrayList<Obj>>>();
	public HashMap<Integer, Obj> ObjectArrayByID = new HashMap<Integer, Obj>();
	public Lock standby = new ReentrantLock();
	public LinkedList<Obj> ProcessingObjects = new LinkedList<Obj>();
	
	Obj onlyPlayer;
	ArrayList<EnemyAI> enemies;
	public TweenManager tweenManager;
	
	public SoundManager soundManager;
	
	int playerHealth = 100;
	
	public ServerEngine() 
	{
		
		

		TmxMapLoader.Parameters p = new TmxMapLoader.Parameters();
		
		p.yUp = true;
		p.convertObjectToTileSpace = true;
		
		GLTexture.setEnforcePotImages(false);
		map = new TmxMapLoader().load("maps/Map.tmx" , p );
		
		standby.lock();
		generateMapObjects();
		standby.unlock();
		
		tweenManager = new TweenManager();
		soundManager = new SoundManager();
		
	}
	
	public static class ServerEngineReference
	{
		static ServerEngine Self;
		
		public static void setSelf(ServerEngine e)
		{
			
			Self = e;
			
		}
		
		public static ServerEngine getSelf()
		{
			
			return Self;
			
		}
		
	}
	
	public class Pair
	{
		
		int x;
		int y;
		
		Pair(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
	}
	
	// INITIAL ENEMY POSITIONS
	Pair[] enemyPositions = { new Pair(25, 10), new Pair(40, 29), new Pair(26, 26),
							  new Pair(8, 58), new Pair(2, 91), new Pair(40, 114), new Pair(40, 118), new Pair(40, 116),
							  new Pair(40, 119), new Pair(40, 117)};
	
	@Override
	public void run() 
	{

		// The network could not bind a port. Fatal error
		try {
			network = new NetServer(this);
			ServerEngineReference.setSelf(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		while(masterLoop) // Do AI, etc..
		{
			long masterLoopDelta = System.currentTimeMillis() - masterLoopTime;
			float DeltaInSeconds = (float) (masterLoopDelta / 1000.0);
	
			tweenManager.update(DeltaInSeconds);
			
			
			// Perform AI actions
			if (enemies != null)
			{
				for (EnemyAI e : enemies)
				{
					e.doAction();
				}
			}			

			for(int i = 0; i < ProcessingObjects.size(); i++)
			{

				ProcessingObjects.get(i).process();
				
			}
			
			
			masterLoopTime = System.currentTimeMillis();
			
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void test()
	{
		
		System.err.println("TESTTT");
		
	}
	
	
	
	
	

	public void generateMapObjects()
	{
		
		TiledMapTileLayer tiledDoorLayer = (TiledMapTileLayer)map.getLayers().get("Doors");
		TiledMapTileLayer tiledFloorLayer = (TiledMapTileLayer)map.getLayers().get("Floor");
		TiledMapTileLayer tiledWallLayer = (TiledMapTileLayer)map.getLayers().get("Walls");
		
		for(int row = 0; row <= MAP_SIZE_X; row++)
		{
			
			ObjectArray.add(new ArrayList<ArrayList<Obj>>());

			for(int column = 0; column <= MAP_SIZE_Y; column++)
			{
				ObjectArray.get(row).add(new ArrayList<Obj>());
				
				Cell c = tiledDoorLayer.getCell(row,column);
				
				if(c != null)
				{
					Door d = new Door(row, column);
					ObjectArray.get(row).get(column).add(d);
					ObjectArrayByID.put(d.UID, d);
					
					Cell FloorTile = tiledFloorLayer.getCell(row,column);
					c.setTile(new StaticTiledMapTile(FloorTile.getTile().getTextureRegion()));
					
					
				}
				
				c = tiledWallLayer.getCell(row,column);
				if(c != null)
				{
					Wall w = new Wall(row, column);
					ObjectArray.get(row).get(column).add(w);
					ObjectArrayByID.put(w.UID, w);
					
					Cell FloorTile = tiledFloorLayer.getCell(row,column);
					c.setTile(new StaticTiledMapTile(FloorTile.getTile().getTextureRegion()));
					
				}
				
				
			}
		}
		
		MapObjects DoorControls = map.getLayers().get("DoorControls").getObjects();
		
		
		for(int i = 0; i < DoorControls.getCount(); i++)
		{
			
			Object o = DoorControls.get(i);
			
			if(o.getClass() == RectangleMapObject.class)
			{
				
				RectangleMapObject area = (RectangleMapObject) o;
				
				int x = (int) area.getRectangle().x;
				int y = (int) area.getRectangle().y;
				
				MapProperties properties = area.getProperties();		

				if(properties.containsKey("locked"))
				{
					for(Obj door : ObjectArray.get(x).get(y))
					{
						if(Door.class.isAssignableFrom(door.getClass()))
						{
							Door d = (Door)ObjectArrayByID.get(door.UID);
							d.locked =Boolean.valueOf((String) properties.get("locked"));
						}
					}

				}
			}
			
		}		
		
		
		
		
		
		
		
		
		
	}
	
	
	public boolean isCellPassable(int x, int y, MutableInteger collidedObjectUID)
	{

		if( x < 0 || x > MAP_SIZE_X || y < 0 || y > MAP_SIZE_Y)
		{
			return false;
			
		}
		
		for(Obj obj : ObjectArray.get(x).get(y))
		{
			
			if(obj.dense)
			{
				collidedObjectUID.setValue(obj.UID);
				return false;
				
			}
			
		}

		return true;
	}
	
	public boolean isCellOpaque(int tileX, int tileY)
	{
		if( tileX < 0 || tileX > MAP_SIZE_X || tileY < 0 || tileY > MAP_SIZE_Y)
		{
			return true;
			
		}
		
		
		for(Obj obj : ObjectArray.get(tileX).get(tileY))
		{
			
			if(obj.opaque)
			{
				
				return true;
				
			}
			
		}

		return false;
	}

	public void spawnMob()
	{
		onlyPlayer = new Mob(2,3);

		
		
		Weapon aGun = new Weapon(-1, -1);

		
		Mob m = (Mob) onlyPlayer;
		m.setTextures(ConfigOptions.charUp, ConfigOptions.charRight, 
				      ConfigOptions.charDown, ConfigOptions.charLeft);
		
		m.setDirection(Direction.RIGHT);
		
		aGun.containerUID = m.UID;
		m.leftHand = aGun;
		
		
		addToWorld(aGun);
		addToWorld(onlyPlayer);
		
		// Create enemy objects/assign them to AI controllers
		
		enemies = new ArrayList<EnemyAI>();
		
		for (Pair p : enemyPositions)
		{
			Mob enemyMob = new Mob(p.x, p.y);

			enemyMob.setAIcontrolled(true);
			enemyMob.setTexture(ConfigOptions.enemyTexture);
			
			Weapon enemyGun = new Weapon(-1, -1);
			
			
			enemyGun.containerUID = enemyMob.UID;
			enemyMob.leftHand = enemyGun;
			
			/*
			 * Passes server engine into enemyAI so it can refer to objects.
			 * Was not aware of ServerEngineReference at this time. Should probably
			 * update this eventually.
			 * 
			 * 100 is starting HP for enemy, but should probably be changed to be
			 * derived from some constant, or if we make different enemy types or something.
			 */
			EnemyAI enemy = new EnemyAI(enemyMob, m, this, 100);
			
			enemies.add(enemy);
			
			addToWorld(enemyGun);
			addToWorld(enemy.getEnemyObject());		
		}
		
		
		network.sendAll(Message.YOUCONTROL, onlyPlayer.UID);
		network.sendAll(Message.CHANGEHEALTH, playerHealth);
		
	}
	
	public void addToWorld(Obj o)
	{
		
		if(o.tileXPosition > 0)
		{
			ObjectArray.get(o.tileXPosition).get(o.tileYPosition).add(o);
		}

		ObjectArrayByID.put(o.UID, o);
		
		notifyClientsOfNewObject(o);
	}

	private void notifyClientsOfNewObject(Obj o)
	{
		
		network.sendAll(Message.NEWOBJECT, o);

		
	}

	public void sendCompleteState()
	{
		
		standby.lock();
		for(int i = 1; i < ObjectArrayByID.size() ; i++)
		{
			Obj o = ObjectArrayByID.get(i);
			notifyClientsOfNewObject(o);
			
		}
		
		standby.unlock();
		
	}
	
	public void removeObject(int objectUID)
	{
		Obj o = ObjectArrayByID.get(objectUID);
		
		
		// If the object to be removed is an enemy, kill the AI as well
		if (Mob.class.isAssignableFrom(o.getClass()))
		{
			if (((Mob) o).isAIcontrolled())
			{
				// an enemy was hit!
				for (EnemyAI e : enemies)
				{
					if (e.getEnemyObject().UID == objectUID)
					{
						/*
						 * Hit enemy for 100 damage -- this should probably be dependent on weapon, so
						 * should change later.
						 */
						e.getHit(100);
						System.out.println("Enemy at "+e.getHealth()+"% HP");
						
						if (e.getHealth() <= 0)
						{
							enemies.remove(e);
							if(o.tileXPosition >= 0)
							{
								ObjectArray.get(o.tileXPosition).get(o.tileYPosition).remove(o);
								ObjectArrayByID.remove(objectUID);
								network.sendAll(Message.REMOVEOBJECT, objectUID);
							}
						}
						break; // break or else for each loop will die terribly
					}
				}
			}
			else
			{
				// player was hit!
				playerHealth -= 25;
				network.sendAll(Message.CHANGEHEALTH, playerHealth);
				
				if (playerHealth == 0)
				{
					if(o.tileXPosition >= 0)
					{
						System.out.println("Kill player");
						ObjectArray.get(o.tileXPosition).get(o.tileYPosition).remove(o);
						ObjectArrayByID.remove(objectUID);
						network.sendAll(Message.REMOVEOBJECT, objectUID);
					}
				}
			}
		}
		else {
			ProcessingObjects.remove(o);
			if(o.tileXPosition >= 0)
			{
				ObjectArray.get(o.tileXPosition).get(o.tileYPosition).remove(o);
				ObjectArrayByID.remove(objectUID);
			}
			
			network.sendAll(Message.REMOVEOBJECT, objectUID);
		}
	}
	
	
	public void objectRelocate(Position P)
	{
		Obj o = ObjectArrayByID.get(P.UID);
		
		ObjectArray.get(o.tileXPosition).get(o.tileYPosition).remove(o);
	
		o.tileXPosition = P.x;
		o.tileYPosition = P.y;
				
		ObjectArray.get(P.x).get(P.y).add(o);
		

		o.setX(P.x*TILE_SIZE);
		o.setY(P.y*TILE_SIZE);

		
		network.sendAll(Message.OBJMOVE, P);
	}

	MutableInteger collidedObjectUID = new MutableInteger(-1);
	public void requestMove(Position p)
	{
		long currentTime = System.currentTimeMillis();
		
		Obj movingObject = ObjectArrayByID.get(p.UID);
		
		if(movingObject == null)
		{
			
			return;
			
		}
		
		int nextTileX = (int) (movingObject.tileXPosition + p.x);
		int nextTileY = (int) (movingObject.tileYPosition  + p.y);		
		
		collidedObjectUID.setValue(-1);
		if(isCellPassable(nextTileX, nextTileY, collidedObjectUID))
		{
			if(currentTime < movingObject.lastMoveTime + movingObject.moveDelay)
			{
				return;
				
			}
			else
			{
				movingObject.lastMoveTime = currentTime;
				
				
			}
			
			p.x = nextTileX;
			p.y = nextTileY;
			objectRelocate(p);
		}
		
		else if(collidedObjectUID.intValue() >= 0)
		{
			
			if(currentTime < movingObject.lastActionTime + movingObject.actionDelay)
			{
				return;
				
			}
			else
			{
				movingObject.lastActionTime = currentTime;
				
			}
			
			//System.out.println("Collided with " + collidedObjectUID.intValue());
			Obj collidedObject = ObjectArrayByID.get(collidedObjectUID.intValue());

			collidedObject.collide(p.UID);
			
			UidPair u = new UidPair();
			
			u.first = collidedObject.UID;
			u.second = p.UID;

			network.sendAll(Message.COLLISION, u);
		}
		else{}
		
		
		
	}

	
	Position objectPosition = new Position();
	public void mouseEvent(int mouseEventTargetUID)
	{
		Obj movingObject = onlyPlayer;
		long currentTime = System.currentTimeMillis();
		
		if(currentTime < movingObject.lastActionTime + movingObject.actionDelay)
		{
			return;
			
		}
		else
		{
			movingObject.lastActionTime = currentTime;
			
		}
		
		Obj o = ObjectArrayByID.get(mouseEventTargetUID);
		
		// Do checks that we can actually click it, etc, etc...
		Item inHand = getActiveHandItem();
		// TODO:  Send what we actually clicked it with (empty hand, divine rapier, etc.)
		
		
		if(IsMapAdjacent(onlyPlayer, o))
		{
			o.onClick(inHand);

			network.sendAll(Message.MOUSEEVENTFROMSERVER, mouseEventTargetUID);
		}
		else
		{
			
			if(inHand != null)
			{
				objectPosition.UID = mouseEventTargetUID;
				objectPosition.x = o.tileXPosition;
				objectPosition.y = o.tileYPosition;
				inHand.rangedEvent(objectPosition);
			}
			
		}
		
		
		
		
	}


	public void mouseEvent(Position cell)
	{
		
		Obj movingObject = onlyPlayer;
		
		long currentTime = System.currentTimeMillis();
		if(currentTime < movingObject.lastActionTime + movingObject.actionDelay)
		{
			return;
			
		}
		else
		{
			movingObject.lastActionTime = currentTime;
			
		}
		
		
		

		Item currentlyHeldItem = getActiveHandItem();
		
		if(IsMapAdjacent(onlyPlayer, cell))
		{			
			if(currentlyHeldItem == null)
			{
				return; // you stare intently at the empty tile with your empty hand
			}
			else
			{
				//currentlyHeldItem.
			}
		
		}
		else
		{
			
			if(currentlyHeldItem == null)
			{
				return; // you stare intently at the empty tile with your empty hand
			}
			else
			{
				currentlyHeldItem.rangedEvent(cell);
			}
			
		}
	
	}
	
	private boolean IsMapAdjacent(Obj A, Obj B)
	{
		int AX = A.getTileXPosition();
		int AY = A.getTileYPosition();
		
		int BX = B.getTileXPosition();
		int BY = B.getTileYPosition();
		
		int distX = Math.abs(AX - BX);
		int distY = Math.abs(AY - BY);
		
		if(distX > 1 || distY > 1)
		{
			return false;
			
		}
		else
		{
			return true;
		}

	}

	private boolean IsMapAdjacent(Obj A, Position B)
	{
		int AX = A.getTileXPosition();
		int AY = A.getTileYPosition();
		
		int BX = B.x;
		int BY = B.y;
		
		int distX = Math.abs(AX - BX);
		int distY = Math.abs(AY - BY);
		
		if(distX > 1 || distY > 1)
		{
			return false;
			
		}
		else
		{
			return true;
		}

		

	}


	private Item getActiveHandItem()
	{
		if(Mob.class.isAssignableFrom(onlyPlayer.getClass()))
		{
			
			Mob m = (Mob) onlyPlayer;
			
			if(m.ActiveHand == Hand.LEFT)
			{
				
				return m.leftHand;
				
			}
			else if(m.ActiveHand == Hand.RIGHT)
			{
				
				return m.rightHand;
				
			}
			
			
		}
		
		return null;
	}


	public ArrayList<ArrayList<ArrayList<Obj>>> getObjects()
	{
		return ( this.ObjectArray );
	}
	
	public void changeCharDirection(Position p)
	{
		// switch character direction based on movement
		Mob m = (Mob) onlyPlayer;
		if (p.x == 0 && p.y == 1)
		{
			m.setDirection(Direction.UP);
		}
		else if (p.x == 0 && p.y == -1)
		{
			m.setDirection(Direction.DOWN);
		}
		else if (p.x == 1 && p.y == 0)
		{
			m.setDirection(Direction.RIGHT);
		}
		else if (p.x == -1 && p.y == 0)
		{
			m.setDirection(Direction.LEFT);
		}
	}


}
