package core.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import gameCode.obj.Obj;
import gameCode.obj.structure.Door;
import gameCode.obj.structure.Wall;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import core.network.NetClient;
import core.server.ServerEngine;


import static core.shared.ConfigOptions.MAP_SIZE_X;
import static core.shared.ConfigOptions.MAP_SIZE_Y;
import static core.shared.ConfigOptions.TILE_SIZE;
import static core.shared.ConfigOptions.VIEW_DISTANCE_X;
import static core.shared.ConfigOptions.VIEW_DISTANCE_Y;

public class ClientEngine extends Game 
{
	
	// Server used for hosting games
	ServerEngine server;

	// Used to connect to the server
	static NetClient network;
	
	
	static public OrthographicCamera camera;
	public OrthogonalTiledMapRenderer mapRenderer;
	public TiledMap map;

	ArrayList<ArrayList<ArrayList<Obj>>> ObjectArray = new ArrayList<ArrayList<ArrayList<Obj>>>();
	HashMap<Integer, Obj> ObjectArrayByID = new HashMap<>();
	
	int cameraTileX = 2;
	int cameraTileY = 3;
	
	
	Stage uiStage;
	Stage worldStage;
	
	
	
	boolean[][] OccluedTiles;
	
	//Obj[][][] ObjectArray = new Obj[MAP_SIZE_X][MAP_SIZE_Y][MAX_OBJECTS_PER_TILE];

	// Handles first-chance keyboard presses
	UIControlHandler uiControlHandler = new UIControlHandler(this);
	
	
	InputMultiplexer multiplexer = new InputMultiplexer();

	Rectangle cullingArea;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		TmxMapLoader.Parameters p = new TmxMapLoader.Parameters();
		
		p.yUp = true;
		p.convertObjectToTileSpace = true;
		
		Texture.setEnforcePotImages(false);
		map = new TmxMapLoader().load("maps/Map.tmx" , p );
		

		mapRenderer = new OrthogonalTiledMapRenderer(map);
		camera = new OrthographicCamera();
		

		
		
		
		
		
		
		
	    multiplexer.addProcessor(uiControlHandler);
	    Gdx.input.setInputProcessor(multiplexer);
		
		
		camera.position.x=cameraTileX*TILE_SIZE+16;
		camera.position.y=cameraTileY*TILE_SIZE+16;
		
		uiStage = new Stage();
		
		worldStage = new Stage();



		cullingArea= new Rectangle(
				(cameraTileX-VIEW_DISTANCE_X/2)*TILE_SIZE,
				(cameraTileY-VIEW_DISTANCE_Y/2)*TILE_SIZE,
				VIEW_DISTANCE_X*TILE_SIZE ,
				VIEW_DISTANCE_Y*TILE_SIZE
				);
		worldStage.getRoot().setCullingArea(cullingArea);

		
		generateMapObjects();
		
		OccluedTiles = new boolean[VIEW_DISTANCE_X][VIEW_DISTANCE_Y];
		
		clearVisibleMap();
		
		calculateVisibleTiles();
		
		
		server = new ServerEngine();

		server.start();
		
		try
		{
			network = new NetClient(this, "localhost");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		network.send(core.shared.Message.TEST);
		
		/*
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		
		sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);*/
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
		map.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 1, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		
	//	MapProperties m =map.getLayers().get("Walls").
		
		mapRenderer.setView(camera);

		mapRenderer.render();
		
		
		
		worldStage.act();
		worldStage.draw();
		
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		
		 shapeRenderer.begin(ShapeType.Filled);
		 shapeRenderer.setColor(0, 1, 0, 1);
		 
		 
		 
		 
		 Vector3 test = new Vector3(cameraTileX*TILE_SIZE+16,cameraTileY*TILE_SIZE+16,1);
		 camera.project(test);
		 
		 

			
		 
		 shapeRenderer.circle(test.x, test.y, 25);
		 
		 shapeRenderer.setColor(0, 0, 0, 1);
		 for(int column = 0; column < VIEW_DISTANCE_X; column++)
		 {
			 
			 for(int row = 0; row < VIEW_DISTANCE_Y; row++)
			 {
				 if(OccluedTiles[row][column])
				 {
					 shapeRenderer.rect(test.x - VIEW_DISTANCE_X*TILE_SIZE + row*TILE_SIZE*2, test.y- VIEW_DISTANCE_Y*TILE_SIZE + column*TILE_SIZE*2, 64, 64);	
				 }
//				 if(!LosToTile(cameraTileX, cameraTileY, cameraTileX+row-(VIEW_DISTANCE_X/2), cameraTileY+column-(VIEW_DISTANCE_Y/2)))
//				 {
//				 }
			 }
		 }
		 
		 
		 
	//	 System.out.println(test.x);
		 shapeRenderer.end();
		
		 
		 
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = VIEW_DISTANCE_X*TILE_SIZE;
		camera.viewportWidth = VIEW_DISTANCE_Y*TILE_SIZE;
		

		camera.update();
		
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	
	

	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "CSCI-375-Project";
		cfg.useGL20 = false;
		cfg.width = VIEW_DISTANCE_X*TILE_SIZE*2;
		cfg.height = VIEW_DISTANCE_Y*TILE_SIZE*2;
		cfg.resizable = false;
		
		new LwjglApplication(new ClientEngine(), cfg);
	}
	
	public void MoveCameraRelative(int x, int y)
	{
		
		int nextTileX = cameraTileX + x;
		int nextTileY = cameraTileY + y;
		
		if(isCellPassable(nextTileX, nextTileY))
		{
		
			cameraTileX += x;
			cameraTileY += y;
			
			
			
			camera.position.x = cameraTileX*TILE_SIZE+16;
			camera.position.y = cameraTileY*TILE_SIZE+16;
			
			camera.update();
			
			cullingArea.set(
					(cameraTileX-VIEW_DISTANCE_X/2)*TILE_SIZE,
					(cameraTileY-VIEW_DISTANCE_Y/2)*TILE_SIZE,
					VIEW_DISTANCE_X*TILE_SIZE ,
					VIEW_DISTANCE_Y*TILE_SIZE
					);
			
			clearVisibleMap();
			
			calculateVisibleTiles();
			
			
		}
		
		else
		{}
		
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
	
	public void mouseEvent(int tileX, int tileY)
	{
		
		if( tileX < 0 || tileX > MAP_SIZE_X || tileY < 0 || tileY > MAP_SIZE_Y)
		{
			return;
			
		}
		
		
		for(Obj obj : ObjectArray.get(tileX).get(tileY))
		{
			
			if(Door.class.isAssignableFrom(obj.getClass()))
			{
				TiledMapTileLayer tiledLayer = (TiledMapTileLayer)map.getLayers().get("Doors");
				TiledMapTileLayer tiledLayer2 = (TiledMapTileLayer)map.getLayers().get("Floor");
				
				Door doorObj = (Door)obj;
							
				if(doorObj.dense && !doorObj.locked )
				{
					doorObj.dense = false;
					doorObj.opaque = false;
					TiledMapTile c = tiledLayer2.getCell(1,1).getTile();
					tiledLayer.getCell(tileX, tileY).setTile(c);
				}
				
				else
				{
					doorObj.dense = true;
					doorObj.opaque = true;
					TiledMapTile c = tiledLayer.getCell(7,11).getTile();
					tiledLayer.getCell(tileX, tileY).setTile(c);
				}
				
				calculateVisibleTiles();
				
			}
			
		}
		
	}
	
	public boolean LosToTile(int startx, int starty, int endx, int endy)
	{
		if( startx < 0 || startx > MAP_SIZE_X || starty < 0 || starty > MAP_SIZE_Y|| endx < 0 || endx > MAP_SIZE_X || endy < 0 || endy > MAP_SIZE_Y)
		{
			return false;
			
		}

		if(startx==endx)
		{
			if(starty==endy)
			{
				return true; //Light cannot be blocked on same tile
			}
			else
			{
				int direction = returnSign(endy-starty);
				starty +=direction;
				while(starty!=endy)
				{
					if(isCellOpaque(startx, starty))
					{
						return false;
					}
					starty+=direction;
				}
			}
		}


		return true;
		
	}
	
	public int returnSign (double x)
	{
		
		return ((x<0)?-1:1);
		
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
					
					worldStage.addActor(d);
					
				}
				
				c = tiledWallLayer.getCell(row,column);
				if(c != null)
				{
					Wall w = new Wall(row, column);
					ObjectArray.get(row).get(column).add(w);
					ObjectArrayByID.put(w.UID, w);
					
					Cell FloorTile = tiledFloorLayer.getCell(row,column);
					c.setTile(new StaticTiledMapTile(FloorTile.getTile().getTextureRegion()));
					
					worldStage.addActor(w);
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
	
	public void clearVisibleMap()
	{
		
		for(int i = 0; i < VIEW_DISTANCE_X; i++)
		{
			for(int j = 0 ; j < VIEW_DISTANCE_Y; j++)
			{
				OccluedTiles[i][j] = true;
			}
		}
	}
	
	public void calculateVisibleTiles()
	{
		
		FOV();
		
	}
	
	void FOV()
	{
	  double x;
	  double y;
	  double i;
	  clearVisibleMap();//Initially set all tiles to not visible.
	  for(i=0;i<360;i=i+0.5)
	  {
	    x=Math.cos((float)i*0.01745f);
	    y=Math.sin((float)i*0.01745f);
	    DoFov(x,y);
	  };
	};

	void DoFov(double x,double y)
	{
		int i;
			  
		float ox =  (float) (cameraTileX)+.5f;
		float oy =  (float) (cameraTileY)+.5f;
	  
		float cameraX =  7.5f;
		float cameraY =  7.5f;
		
		for(i=0;i<VIEW_DISTANCE_X;i++)
		{
			
			if(cameraX < 0 || cameraX >= 15 || cameraY < 0 || cameraY >= 15)
			{
				
				return;
				
			}
			
			OccluedTiles[(int)cameraX][(int)cameraY]=false;//Set the tile to visible.
			if(!isCellOpaque((int)ox,(int)oy)==false)
			{
				return;
			}
			
			ox+=x;
			oy+=y;
			cameraX+=x;
			cameraY+=y;
		}
	}

	
	
}













