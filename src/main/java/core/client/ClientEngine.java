package core.client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import gameCode.obj.Obj;
import gameCode.obj.getObjUID;
import gameCode.obj.mob.Mob;
import gameCode.obj.structure.Door;
import gameCode.obj.structure.Wall;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.Screen;

import core.network.NetClient;
import core.server.ServerEngine;
import core.shared.ConfigOptions;
import core.shared.DistilledObject;
import core.shared.Message;
import core.shared.Position;
import core.shared.Background;
import core.client.ConnectingScreen;
import core.client.ErrorScreen;
import core.client.HostingScreen;
import core.client.mainMenuScreen;
import core.client.PauseScreen;
import core.client.ScreenEnumerations;
import core.client.SettingsScreen;
import core.client.MenuNinePatch;
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

	public ArrayList<ArrayList<ArrayList<Obj>>> ObjectArray = new ArrayList<ArrayList<ArrayList<Obj>>>();
	public HashMap<Integer, Obj> ObjectArrayByID = new HashMap<Integer, Obj>();
	static HashMap<String, AssetDescriptor<Texture>> Textures;

	
	float cameraTileX = 2;
	float cameraTileY = 3;
	static SpriteBatch primarySpriteBatch;
	
	// Styles used in the game
	TextButtonStyle dialogueStyle;
	TextButtonStyle buttonStyle;
	TextButtonStyle radioButtonStyle;

	// Font used in the game
	static BitmapFont gameFont;
	
	// Used to display the 'loading' screen
	static FreeTypeFontGenerator generator;
	LabelStyle rawTextStyle;


	// Button ninepatch
	MenuNinePatch menuNinePatch;
	NinePatchDrawable ninePatchDrawable;
	
	//Declare all the screen
	mainMenuScreen MainMenuScreen;
	HostingScreen hostingScreen;
	PauseScreen pauseScreen;
	SettingsScreen settingsScreen;
	ConnectingScreen connectingScreen;
	ErrorScreen errorScreen;
	
	// Used for assets that will not be interacted with by the user
	static AssetManager gameTextureManager;
	static HashMap<Background, AssetDescriptor<Texture>> Backgrounds;

	
	Stage uiStage;
	Stage worldStage;
	
	ShapeRenderer occulsionTileRenderer;
	
	Obj controlledObject;
	
	boolean recaculateVisibleTiles = false;
	boolean refocusCamera = false;
	boolean[][] OccluedTiles;
	
	boolean[] pressedKeys = new boolean[256];
	
	//Obj[][][] ObjectArray = new Obj[MAP_SIZE_X][MAP_SIZE_Y][MAX_OBJECTS_PER_TILE];

	// Handles first-chance keyboard presses
	UIControlHandler uiControlHandler = new UIControlHandler(this);
	
	
	InputMultiplexer multiplexer = new InputMultiplexer();

	Rectangle cullingArea;
	
	
	TweenManager tweenManager = new TweenManager();
	
	
	
	
	List<Position>  QueuedEvents = new LinkedList<Position>();
	
	
	
	
	public static class Test
	{
		static ClientEngine Self;
		
		public static void setSelf(ClientEngine e)
		{
			
			Self = e;
			
		}
		
		public static ClientEngine getSelf()
		{
			
			return Self;
			
		}
		
	}
	
	
	
	@Override
	public void create() {		
		
		Test.setSelf(this);
		Backgrounds = new HashMap<Background, AssetDescriptor<Texture>>();
		gameTextureManager = new AssetManager();


		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		TmxMapLoader.Parameters p = new TmxMapLoader.Parameters();
		
		p.yUp = true;
		p.convertObjectToTileSpace = true;
		
		Texture.setEnforcePotImages(false);
		map = new TmxMapLoader().load("maps/Map.tmx" , p );
		

		mapRenderer = new OrthogonalTiledMapRenderer(map);
		camera = new OrthographicCamera();
		
		
		Tween.registerAccessor(Obj.class, new ObjTweener());
		

		
		occulsionTileRenderer = new ShapeRenderer();
		
		
		uiStage = new Stage();

		worldStage = new Stage();
		
		
		//backgrounds
		Backgrounds.put(Background.MENUSCREEN, new AssetDescriptor<Texture>(
				"backgrounds/spacebg.png", Texture.class));
		
		//AssetManager
		for (AssetDescriptor<Texture> theTexture : Backgrounds.values()) {
			gameTextureManager.load(theTexture);
		}
		
		gameTextureManager.finishLoading();
		generator = new FreeTypeFontGenerator(
		Gdx.files.internal("fonts/ubuntur.ttf"));
		gameFont = generator.generateFont(25);
		
		buttonStyle = new TextButtonStyle();
		buttonStyle.font = gameFont;
		buttonStyle.up = ninePatchDrawable;

		buttonStyle.down = buttonStyle.up;
		buttonStyle.over = buttonStyle.up;
		buttonStyle.fontColor = Color.WHITE;
		buttonStyle.downFontColor = Color.LIGHT_GRAY;
		
		
		
	    multiplexer.addProcessor(uiControlHandler);
	    multiplexer.addProcessor(uiStage);
	    multiplexer.addProcessor(worldStage);
	    Gdx.input.setInputProcessor(multiplexer);
		
		
		camera.position.x=cameraTileX*TILE_SIZE+16;
		camera.position.y=cameraTileY*TILE_SIZE+16;
		
		worldStage.setCamera(camera);



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
		network.send(Message.REQUESTSTATE);
		network.send(core.shared.Message.SPAWN);
		
		
		
		
		
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
	
	Position P = new Position();
	public void handleKeyPresses()
	{
		
		
		if(pressedKeys[Keys.UP])
		{       
			P.x = 0;
			P.y = 1;
			network.send(Message.REQUESTMOVE, P);
		}
		if(pressedKeys[Keys.DOWN])
		{       
			P.x = 0;
			P.y = -1;
			network.send(Message.REQUESTMOVE, P);
		}
		if(pressedKeys[Keys.RIGHT])
		{       
			P.y = 0;
			P.x = 1;
			network.send(Message.REQUESTMOVE, P);
		}
		if(pressedKeys[Keys.LEFT])
		{
			P.y = 0;
			P.x = -1;
			network.send(Message.REQUESTMOVE, P);
		}
		
		
		
	}
	
	
	public void focusCameraOnControlled()
	{
		refocusCamera = false;
		
		
		float x = controlledObject.getX();
		float y = controlledObject.getY();
		
		cameraTileX = x/TILE_SIZE;
		cameraTileY = y/TILE_SIZE;
		
		camera.position.x = x+TILE_SIZE/2;
		camera.position.y = y+TILE_SIZE/2;
		
		camera.update();
		
		cullingArea.set(
				(x-VIEW_DISTANCE_X/2*TILE_SIZE),
				(y-VIEW_DISTANCE_Y/2*TILE_SIZE),
				VIEW_DISTANCE_X*TILE_SIZE ,
				VIEW_DISTANCE_Y*TILE_SIZE
				);
		
		recaculateVisibleTiles = true;
		
		
	}
	
	
	
	Position TempPosition;
	@Override
	public void render() {		
		
		super.render();
		
		Gdx.gl.glClearColor(0, 0, 1, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);


		handleKeyPresses();
		
		// Access seemingly must be done on the rendering thread (which makes sense)
		// Synronizing access doesn't seem feasable - multiple accesses
		while(!QueuedEvents.isEmpty())
		{
			TempPosition = QueuedEvents.get(0);
			objectMove(TempPosition);
			QueuedEvents.remove(0);
			
			
		}
		
	
		//if(refocusCamera)
		if(controlledObject != null)
		{
			focusCameraOnControlled();
			
		}
		
		
		
		
		System.out.println(Gdx.graphics.getFramesPerSecond());
		
		
		
	//	MapProperties m =map.getLayers().get("Walls").
		

		
		
		
		mapRenderer.setView(camera);
		mapRenderer.render();
		worldStage.draw();
		
		
		
		//worldStage.act();

		
		 
	
		 
		 
		 Vector3 test = new Vector3(cameraTileX*TILE_SIZE+16,cameraTileY*TILE_SIZE+16,1);
		 camera.project(test);
		 
		 

		 
		 
		 
		 if(recaculateVisibleTiles)
		 {
			 calculateVisibleTiles();
		 }
		 
			
		 occulsionTileRenderer.begin(ShapeType.Filled);

		 occulsionTileRenderer.setColor(0, 0, 0, 1);
		 
		 for(int column = 0; column < VIEW_DISTANCE_X; column++)
		 {
			 
			 for(int row = 0; row < VIEW_DISTANCE_Y; row++)
			 {
				 if(OccluedTiles[row][column])
				 {
					 occulsionTileRenderer.rect(test.x - VIEW_DISTANCE_X*TILE_SIZE + row*TILE_SIZE*2, test.y- VIEW_DISTANCE_Y*TILE_SIZE + column*TILE_SIZE*2, 64, 64);	
				 }
//				 if(!LosToTile(cameraTileX, cameraTileY, cameraTileX+row-(VIEW_DISTANCE_X/2), cameraTileY+column-(VIEW_DISTANCE_Y/2)))
//				 {
//				 }
			 }
		 }
		 occulsionTileRenderer.end();
		 
		 
		tweenManager.update(Gdx.graphics.getDeltaTime());
		switchToNewScreen(ScreenEnumerations.MainMenu);

		 
		 
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
	
	



	
	public void MoveCameraRelative(int x, int y)
	{
//		
//		int nextTileX = cameraTileX + x;
//		int nextTileY = cameraTileY + y;
//		
//		if(isCellPassable(nextTileX, nextTileY))
//		{
//		
//			cameraTileX += x;
//			cameraTileY += y;
//			
//			
//			
//			camera.position.x = cameraTileX*TILE_SIZE+TILE_SIZE/2;
//			camera.position.y = cameraTileY*TILE_SIZE+TILE_SIZE/2;
//			
//			camera.update();
//			
//			cullingArea.set(
//					(cameraTileX-VIEW_DISTANCE_X/2)*TILE_SIZE,
//					(cameraTileY-VIEW_DISTANCE_Y/2)*TILE_SIZE,
//					VIEW_DISTANCE_X*TILE_SIZE ,
//					VIEW_DISTANCE_Y*TILE_SIZE
//					);
//			
//			recaculateVisibleTiles = true;
			
//			
//		}
//		
//		else
//		{}
		
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
//					Door d = new Door(row, column);
//					ObjectArray.get(row).get(column).add(d);
//					ObjectArrayByID.put(d.UID, d);
					
					Cell FloorTile = tiledFloorLayer.getCell(row,column);
					c.setTile(new StaticTiledMapTile(FloorTile.getTile().getTextureRegion()));
					
//					worldStage.addActor(d);
					
				}
				
				c = tiledWallLayer.getCell(row,column);
				if(c != null)
				{
//					Wall w = new Wall(row, column);
//					ObjectArray.get(row).get(column).add(w);
//					ObjectArrayByID.put(w.UID, w);
					
					Cell FloorTile = tiledFloorLayer.getCell(row,column);
					c.setTile(new StaticTiledMapTile(FloorTile.getTile().getTextureRegion()));
					
//					worldStage.addActor(w);
				}
				
			}
		}
			
		}
		
//		MapObjects DoorControls = map.getLayers().get("DoorControls").getObjects();
//		
//		
//		for(int i = 0; i < DoorControls.getCount(); i++)
//		{
//			
//			Object o = DoorControls.get(i);
//			
//			if(o.getClass() == RectangleMapObject.class)
//			{
//				
//				RectangleMapObject area = (RectangleMapObject) o;
//				
//				int x = (int) area.getRectangle().x;
//				int y = (int) area.getRectangle().y;
//				
//				MapProperties properties = area.getProperties();		
//
//				if(properties.containsKey("locked"))
//				{
//					for(Obj door : ObjectArray.get(x).get(y))
//					{
//						if(Door.class.isAssignableFrom(door.getClass()))
//						{
//							Door d = (Door)ObjectArrayByID.get(door.UID);
//							d.locked =Boolean.valueOf((String) properties.get("locked"));
//						}
//					}
//
//				}
//			}
//			
//		}		
//		
//		
	
public void switchToNewScreen(ScreenEnumerations newLevel) {
	switch (newLevel) {
	case ClientEngine:
		this.setScreen(null);

	case MainMenu:
		if (MainMenuScreen == null) {
			MainMenuScreen = new mainMenuScreen(this);
		}
		this.setScreen(MainMenuScreen);
		break;

	case Hosting:
		if (hostingScreen == null) {
			hostingScreen = new HostingScreen(this);
		}
		this.setScreen(hostingScreen);
		break;

	case Connecting:
		if (connectingScreen == null) {
			connectingScreen = new ConnectingScreen(this);
		}
		this.setScreen(connectingScreen);
		break;

	case Settings:
		if (settingsScreen == null) {
			settingsScreen = new SettingsScreen(this);
		}
		this.setScreen(settingsScreen);
		break;

	case PauseScreen:
		if (pauseScreen == null) {
			pauseScreen = new PauseScreen(this);
		}
		this.setScreen(pauseScreen);
		break;

	case ErrorScreen:
		if (errorScreen == null) {
			errorScreen = new ErrorScreen(this);
		}
		this.setScreen(errorScreen);
		break;

	default:
		return;

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
		recaculateVisibleTiles = false;
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
			  
		float ox =  (cameraTileX)+.5f;
		float oy =  (cameraTileY)+.5f;
	  
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

	
	
	public void addToWorld(Obj o)
	{
		
		o.refreshTexture();
	
		ObjectArray.get((int) o.getX() / TILE_SIZE).get((int) o.getY() / TILE_SIZE).add(o);
		ObjectArrayByID.put(o.UID, o);
		
	//	System.out.println(o.UID);
		
		worldStage.addActor(o);
		
		recaculateVisibleTiles = true;
		
	}
	
	public void removeFromWorld(int UID)
	{
		Obj o = ObjectArrayByID.get(UID);
		o.remove();
		ObjectArrayByID.remove(UID);
		ObjectArray.get((int) o.getX() / TILE_SIZE).get((int) o.getY() / TILE_SIZE).remove(o);
	
		recaculateVisibleTiles = true;
	}
	
	
	public void objectRelocate(Position P)
	{
		Obj o = ObjectArrayByID.get(P.UID);
		
		ObjectArray.get((int) o.getX() / TILE_SIZE).get((int) o.getY() / TILE_SIZE).remove(o);
		
		
		o.setX(P.x*TILE_SIZE);
		o.setY(P.y*TILE_SIZE);
		
		ObjectArray.get((int) o.getX() / TILE_SIZE).get((int) o.getY() / TILE_SIZE).add(o);
		
		
		if(P.UID == controlledObject.UID)
		{
			refocusCamera = true;
			
		}
	}
	
	public void queueObjectPosition(Position P)
	{
		
		QueuedEvents.add(P);
		
		
	}
	
	
	public void objectMove(Position P)
	{
	
		Obj o = ObjectArrayByID.get(P.UID);
		
		ObjectArray.get((int) o.getX() / TILE_SIZE).get((int) o.getY() / TILE_SIZE).remove(o);
		
//		o.setX(P.x*TILE_SIZE);
//		o.setY(P.y*TILE_SIZE);
				
		ObjectArray.get((int) o.getX() / TILE_SIZE).get((int) o.getY() / TILE_SIZE).add(o);
		

//		o.setX(P.x*TILE_SIZE);
//		o.setY(P.y*TILE_SIZE);
		
		// tl;dr tweener bad?
		Tween.to(o, ObjTweener.POSITION_XY , (float) (ConfigOptions.moveDelay/1000)).target(P.x*TILE_SIZE ,P.y*TILE_SIZE).ease(Linear.INOUT).start(tweenManager);
		 
		
		if(P.UID == controlledObject.UID)
		{
			
			refocusCamera = true;
			
		}
	}

	public void mouseEvent(int mouseEventUID)
	{
		Obj o = ObjectArrayByID.get(mouseEventUID);
		o.onClick();
		
	}
	
	
	
	public void addToWorld(DistilledObject distilled)
	{
		try
		{
			
			java.lang.reflect.Constructor<?> ctor = distilled.ContainedClass.getDeclaredConstructor(int.class, int.class);
			
			Obj m = (Obj) ctor.newInstance(distilled.X, distilled.Y);
			
			m.UID = distilled.dUID;
			
			ObjectArray.get(distilled.X).get(distilled.Y).add(m);
			ObjectArrayByID.put(m.UID, m);
			
			worldStage.addActor(m);
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	
	
	public void assignControl(int UID)
	{
		controlledObject = ObjectArrayByID.get(UID);
	}

	
	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "CSCI-375-Project";
		cfg.useGL20 = true;
		cfg.width = VIEW_DISTANCE_X*TILE_SIZE*2;
		cfg.height = VIEW_DISTANCE_Y*TILE_SIZE*2;
		
		
		cfg.resizable = false;
		cfg.vSyncEnabled = true;
		


		
		cfg.foregroundFPS = 60;
		
		
	    Settings settings = new Settings();
	    settings.maxWidth = 32;
	    settings.maxHeight = 32;
	    
	    
	    
	    
	    TexturePacker.processIfModified("assets/AutopackingTiles/Door/images", "assets/AutopackingTiles/Door/", "PackedDoor");
		
		
		new LwjglApplication(new ClientEngine(), cfg);
	}




	
}














