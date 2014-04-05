package core.server;

import static core.shared.ConfigOptions.MAP_SIZE_X;
import static core.shared.ConfigOptions.MAP_SIZE_Y;
import static core.shared.ConfigOptions.TILE_SIZE;
import static core.shared.ConfigOptions.VIEW_DISTANCE_X;
import static core.shared.ConfigOptions.VIEW_DISTANCE_Y;
import gameCode.obj.Obj;
import gameCode.obj.mob.Mob;
import gameCode.obj.structure.Door;
import gameCode.obj.structure.Wall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

import core.network.NetServer;
import core.shared.ConfigOptions;
import core.shared.Message;
import core.shared.Position;

public class ServerEngine extends Thread
{
	
	private NetServer network;
	boolean masterLoop = true;
	public TiledMap map;
	ArrayList<ArrayList<ArrayList<Obj>>> ObjectArray = new ArrayList<ArrayList<ArrayList<Obj>>>();
	HashMap<Integer, Obj> ObjectArrayByID = new HashMap<>();
	public Lock standby = new ReentrantLock();
	
	Mob onlyPlayer;
	long lastcommandTime = System.currentTimeMillis();
	
	
	public ServerEngine() 
	{
		
		

		TmxMapLoader.Parameters p = new TmxMapLoader.Parameters();
		
		p.yUp = true;
		p.convertObjectToTileSpace = true;
		
		Texture.setEnforcePotImages(false);
		map = new TmxMapLoader().load("maps/Map.tmx" , p );
		
		standby.lock();
		generateMapObjects();
		standby.unlock();
		
	}
	
	@Override
	public void run() 
	{

		// The network could not bind a port. Fatal error
		try {
			network = new NetServer(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		while(masterLoop) // Do AI, etc..
		{
			
			
			
			
			
			try
			{
				Thread.sleep(5000);
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
	
	
	public boolean isCellPassable(int x, int y)
	{

		for(Obj obj : ObjectArray.get(x).get(y))
		{
			
			if(obj.dense)
			{
				
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
		onlyPlayer = new Mob(6, 6);
		addToWorld(onlyPlayer);
		
		network.sendAll(Message.YOUCONTROL, onlyPlayer.UID);
		
		Position p = new Position(onlyPlayer.UID, 5, 5);
		network.sendAll(Message.OBJMOVE, p);
		
	}
	
	public void addToWorld(Obj o)
	{
		ObjectArray.get((int) o.getX()).get((int) o.getY()).add(o);
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
	
	
	public void objectRelocate(Position P)
	{
		Obj o = ObjectArrayByID.get(P.UID);
		
		ObjectArray.get((int) o.getX() / TILE_SIZE).get((int) o.getY() / TILE_SIZE).remove(o);
		
		
		o.setX(P.x*TILE_SIZE);
		o.setY(P.y*TILE_SIZE);
		
		ObjectArray.get((int) o.getX() / TILE_SIZE).get((int) o.getY() / TILE_SIZE).add(o);
	}

	public void requestMove(Position p)
	{
		long currentTime = System.currentTimeMillis();
		if(currentTime < lastcommandTime + ConfigOptions.moveDelay)
		{
			return;
			
		}
		else
		{
			lastcommandTime = currentTime;
			
		}
		
		int nextTileX = (int) (onlyPlayer.getX()/TILE_SIZE + p.x);
		int nextTileY = (int) (onlyPlayer.getY()/TILE_SIZE + p.y);
		
		if(isCellPassable(nextTileX, nextTileY))
		{
		
			p.UID = onlyPlayer.UID;
			p.x = nextTileX;
			p.y = nextTileY;
			objectRelocate(p);
			
			
			network.sendAll(Message.OBJMOVE, p);
			
		}
		
		else
		{}
		
		
		
	}


}
