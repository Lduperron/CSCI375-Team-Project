package core.server;

import static core.shared.ConfigOptions.MAP_SIZE_X;
import static core.shared.ConfigOptions.MAP_SIZE_Y;
import gameCode.obj.Obj;
import gameCode.obj.structure.Door;
import gameCode.obj.structure.Wall;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.esotericsoftware.kryonet.Connection;

import core.network.NetServer;

public class ServerEngine extends Thread
{
	
	private NetServer network;
	boolean masterLoop = true;
	public TiledMap map;
	ArrayList<ArrayList<ArrayList<Obj>>> ObjectArray = new ArrayList<ArrayList<ArrayList<Obj>>>();
	
	
	
	public ServerEngine() 
	{
		
		

		TmxMapLoader.Parameters p = new TmxMapLoader.Parameters();
		
		p.yUp = true;
		p.convertObjectToTileSpace = true;
		
		Texture.setEnforcePotImages(false);
		map = new TmxMapLoader().load("maps/Map.tmx" , p );
		
		generateMapObjects();
		
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
					ObjectArray.get(row).get(column).add(new Door());
				}
				
				c = tiledWallLayer.getCell(row,column);
				if(c != null)
				{
					ObjectArray.get(row).get(column).add(new Wall());
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
							Door d = (Door)ObjectArray.get(x).get(y).get(0);
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


}
