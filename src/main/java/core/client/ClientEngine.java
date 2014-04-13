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
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
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
import core.shared.UidPair;
import static core.shared.ConfigOptions.MAP_SIZE_X;
import static core.shared.ConfigOptions.MAP_SIZE_Y;
import static core.shared.ConfigOptions.TILE_SIZE;
import static core.shared.ConfigOptions.VIEW_DISTANCE_X;
import static core.shared.ConfigOptions.VIEW_DISTANCE_Y;
import static core.shared.ConfigOptions.VIEW_DISTANCE_X_EXTENDED;
import static core.shared.ConfigOptions.VIEW_DISTANCE_Y_EXTENDED;

public class ClientEngine extends Game {

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

	// Declare all the screen
	mainMenuScreen MainMenuScreen;
	HostingScreen hostingScreen;
	PauseScreen pauseScreen;
	SettingsScreen settingsScreen;
	ConnectingScreen connectingScreen;
	ErrorScreen errorScreen;

	// Used for assets that will not be interacted with by the user
	static AssetManager gameTextureManager;
	static HashMap<Background, AssetDescriptor<Texture>> Backgrounds;

	float xCameraOffset;
	float yCameraOffset;

	// ***SidePanel
	private OrthographicCamera cameraSidePanel;
	private SpriteBatch batchSidePanel;
	ShapeRenderer shapeRenderer;

	// ***Inventory
	Stage inventoryStage;
	TextField inventoryTextBox;
	ScrollPane inventoryTextScroller;
	Table inventoryTextLabelContainingTable;
	Table inventoryTextScrollingTable;
	TextFieldStyle inputStyle;
	ScrollPaneStyle scrollPaneStyle;

	Texture gunItem;
	TextureRegion gunItemRegion;

	Stage uiStage;
	Stage worldStage;

	ShapeRenderer occulsionTileRenderer;

	Obj controlledObject;

	boolean recaculateVisibleTiles = false;
	boolean refocusCamera = false;
	boolean[][] OccluedTiles;

	boolean[] pressedKeys = new boolean[256];

	// Handles first-chance keyboard presses
	UIControlHandler uiControlHandler = new UIControlHandler(this);

	InputMultiplexer multiplexer = new InputMultiplexer();

	Rectangle cullingArea;

	public TweenManager tweenManager = new TweenManager();

	List<Position> QueuedEvents = new LinkedList<Position>();

	// fbackground music
	Music music;
	
	int playerHealth;


	public static class ClientEngineReference {
		static ClientEngine Self;

		public static void setSelf(ClientEngine e) {
			Self = e;
		}

		public static ClientEngine getSelf() {
			return Self;
		}

	}

	@Override
	public void create() {

		ClientEngineReference.setSelf(this);

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		TmxMapLoader.Parameters p = new TmxMapLoader.Parameters();

		p.yUp = true;
		p.convertObjectToTileSpace = true;

		GLTexture.setEnforcePotImages(false);
		map = new TmxMapLoader().load("maps/Map.tmx", p);

		mapRenderer = new OrthogonalTiledMapRenderer(map);
		camera = new OrthographicCamera();

		Tween.registerAccessor(Obj.class, new ObjTweener());

		occulsionTileRenderer = new ShapeRenderer();

		uiStage = new Stage();
		worldStage = new Stage();

		// backgrounds
		Backgrounds = new HashMap<Background, AssetDescriptor<Texture>>();
		gameTextureManager = new AssetManager();

		Backgrounds.put(Background.MENUSCREEN, new AssetDescriptor<Texture>(
				"backgrounds/spacebg.png", Texture.class));

		// AssetManager
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

		camera.position.x = cameraTileX * TILE_SIZE + 16;
		camera.position.y = cameraTileY * TILE_SIZE + 16;

		worldStage.setCamera(camera);

		cullingArea = new Rectangle((cameraTileX - VIEW_DISTANCE_X / 2)
				* TILE_SIZE, (cameraTileY - VIEW_DISTANCE_Y / 2) * TILE_SIZE,
				VIEW_DISTANCE_X * TILE_SIZE, VIEW_DISTANCE_Y * TILE_SIZE);
		worldStage.getRoot().setCullingArea(cullingArea);

		generateMapObjects();

		camera.zoom = 1;

		OccluedTiles = new boolean[(int) ((VIEW_DISTANCE_X + VIEW_DISTANCE_X_EXTENDED) * camera.zoom)][(int) ((VIEW_DISTANCE_Y + VIEW_DISTANCE_Y_EXTENDED) * camera.zoom)];

		clearVisibleMap();

		calculateVisibleTiles();

		server = new ServerEngine();

		server.start();

		try {
			network = new NetClient(this, "localhost");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		network.send(Message.REQUESTSTATE);
		network.send(core.shared.Message.SPAWN);

		worldStage.setViewport((float) (Gdx.graphics.getWidth() / 2),
				Gdx.graphics.getHeight(), true, 0, 0,
				(float) (Gdx.graphics.getWidth() * 0.63),
				Gdx.graphics.getHeight());


		
		/*
		 * camera = new OrthographicCamera(1, h/w); batch = new SpriteBatch();
		 * 
		 * texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		 * texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		 * 
		 * TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		 * 
		 * sprite = new Sprite(region); sprite.setSize(0.9f, 0.9f *
		 * sprite.getHeight() / sprite.getWidth());
		 * sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		 * sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		 */

		// ***SidePanel
		cameraSidePanel = new OrthographicCamera(w, h);

		shapeRenderer = new ShapeRenderer();
		batchSidePanel = new SpriteBatch();
		
		uiStage.setCamera(cameraSidePanel);

		uiStage.setViewport((float) (Gdx.graphics.getWidth() * 0.37),
				Gdx.graphics.getHeight(), true, 0, // viewPortX
				0, // viewPortY
				(float) (Gdx.graphics.getWidth() * 0.37), // viewPortWidth
				Gdx.graphics.getHeight() // viewPortHeight
		);

		rawTextStyle = new LabelStyle();
		rawTextStyle.font = gameFont;
		rawTextStyle.fontColor = Color.BLACK;

		Table table = new Table();
		uiStage.addActor(table);
		table.setPosition(200, 65);

		table.debug();
		table.align(Align.top | Align.center);
		table.setPosition((float) (Gdx.graphics.getWidth() * 0.08),
				(Gdx.graphics.getHeight() - 325));

		Label inventoryLabel = new Label("Inventory", rawTextStyle);
		inventoryLabel.setWrap(true); 
		inventoryLabel.setAlignment(Align.top | Align.center);
		table.add(inventoryLabel).minWidth(200).minHeight(150).fill();
		
		table.row();
		
		TextFieldStyle textStyle = new TextFieldStyle();
		textStyle.font = gameFont;
		textStyle.fontColor = Color.BLACK;
		TextField text = new TextField("", textStyle);
		text.setText("Test");
		text.setMessageText("Type here!");
		table.add(text).minWidth(200).minHeight(150).fill();

		table.pack();

		gunItem = new Texture(Gdx.files.internal("assets/tilesets/gun0.png"));

		gunItemRegion = new TextureRegion(gunItem, 0, 0, 32, 32);
		
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
		map.dispose();
	}

	Position P = new Position();

	public void handleKeyPresses() {

		if (pressedKeys[Keys.UP] || pressedKeys[Keys.W]) {
			P.UID = controlledObject.UID;
			P.x = 0;
			P.y = 1;
			network.send(Message.REQUESTMOVE, P);
		}
		if (pressedKeys[Keys.DOWN] || pressedKeys[Keys.S]) {
			P.UID = controlledObject.UID;
			P.x = 0;
			P.y = -1;
			network.send(Message.REQUESTMOVE, P);
		}
		if (pressedKeys[Keys.RIGHT] || pressedKeys[Keys.D]) {
			P.UID = controlledObject.UID;
			P.y = 0;
			P.x = 1;
			network.send(Message.REQUESTMOVE, P);
		}
		if (pressedKeys[Keys.LEFT] || pressedKeys[Keys.A]) {
			P.UID = controlledObject.UID;
			P.y = 0;
			P.x = -1;
			network.send(Message.REQUESTMOVE, P);
		}
	}

	public void focusCameraOnControlled() {
		refocusCamera = false;

		float x = controlledObject.getX();
		float y = controlledObject.getY();

		cameraTileX = x / TILE_SIZE;
		cameraTileY = y / TILE_SIZE;

		camera.position.x = x + TILE_SIZE / 2;
		camera.position.y = y + TILE_SIZE / 2;

		camera.update();

		cullingArea.set((x - VIEW_DISTANCE_X / 2 * TILE_SIZE),
				(y - VIEW_DISTANCE_Y / 2 * TILE_SIZE), VIEW_DISTANCE_X
						* TILE_SIZE, VIEW_DISTANCE_Y * TILE_SIZE);

		recaculateVisibleTiles = true;

	}

	Position TempPosition;

	@Override
	public void render() {

		if (controlledObject == null) {
			return; // ...whatever. Nothing to see here.
		}

		// Clear the screen
		Gdx.gl.glClearColor(0, 0, 1, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		super.render();

		handleKeyPresses();

		// Access seemingly must be done on the rendering thread (which makes
		// sense)
		// Synronizing access doesn't seem feasable - multiple accesses
		while (!QueuedEvents.isEmpty()) {
			TempPosition = QueuedEvents.get(0);
			objectMove(TempPosition);
			QueuedEvents.remove(0);

		}

		// if(refocusCamera)
		if (controlledObject != null) {
			focusCameraOnControlled();

			xCameraOffset = controlledObject.getXCameraOffset();
			yCameraOffset = controlledObject.getYCameraOffset();

		}

		Gdx.gl.glViewport(0, 0, (int) (Gdx.graphics.getWidth() * 0.63),
				Gdx.graphics.getHeight());

		mapRenderer.setView(camera);
		mapRenderer.render();
		worldStage.draw();

		if (recaculateVisibleTiles) {
			calculateVisibleTiles();
		}

		Vector3 test = new Vector3(cameraTileX * TILE_SIZE + 16, cameraTileY
				* TILE_SIZE + 16, 1);

		// Black out the tiles that are not in the line of sight
		occulsionTileRenderer.begin(ShapeType.Filled);
		occulsionTileRenderer.setColor(0, 0, 0, 1);
		occulsionTileRenderer.setProjectionMatrix(camera.combined);
		for (int column = 0; column < (VIEW_DISTANCE_X + VIEW_DISTANCE_X_EXTENDED)
				* camera.zoom; column++) {

			for (int row = 0; row < (VIEW_DISTANCE_Y + VIEW_DISTANCE_Y_EXTENDED)
					* camera.zoom; row++) {
				if (OccluedTiles[row][column]) {
					occulsionTileRenderer.rect(test.x
							- (VIEW_DISTANCE_X + VIEW_DISTANCE_X_EXTENDED)
							* camera.zoom * TILE_SIZE / 2 + row * TILE_SIZE
							+ xCameraOffset * TILE_SIZE, test.y
							- (VIEW_DISTANCE_Y + VIEW_DISTANCE_Y_EXTENDED)
							* camera.zoom * TILE_SIZE / 2 + column * TILE_SIZE
							+ yCameraOffset * TILE_SIZE, 32, 32);
				}
			}
		}
		occulsionTileRenderer.end();

		tweenManager.update(Gdx.graphics.getDeltaTime());

		// ***SidePanel
		Gdx.gl.glViewport((int) (Gdx.graphics.getWidth() * 0.63), 0,
				(int) (Gdx.graphics.getWidth() * 0.37),
				Gdx.graphics.getHeight());
		drawSidePanel();

		uiStage.draw();
		Table.drawDebug(uiStage);

		batchSidePanel.begin();
		batchSidePanel.draw(gunItemRegion,
				(float) (Gdx.graphics.getWidth() * 0.25),
				(float) (Gdx.graphics.getWidth() * 0.5), 0, 0, 32, 32,
				(float) 2.5, 1, 0);
		batchSidePanel.end();

	}

	// ***SidePanel
	private void drawSidePanel() {
		shapeRenderer.setProjectionMatrix(cameraSidePanel.combined);
		shapeRenderer.begin(ShapeType.Filled);
		// Note: the setColor is on a [0-1] scale
		shapeRenderer.setColor((float) 0.75, (float) 0.75, (float) 0.75,
				(float) 0);
		shapeRenderer.rect(-Gdx.graphics.getWidth(), -Gdx.graphics.getHeight(),
				Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 3);
		shapeRenderer.end();
	}

	@Override
	public void resize(int width, int height) {

		worldStage.setViewport((float) (Gdx.graphics.getWidth() / 2),
				Gdx.graphics.getHeight(), true, 0, 0,
				(float) (Gdx.graphics.getWidth() * 0.63),
				Gdx.graphics.getHeight());


		camera.viewportHeight = VIEW_DISTANCE_X * TILE_SIZE;
		camera.viewportWidth = VIEW_DISTANCE_Y * TILE_SIZE;

		camera.update();

	}

	@Override
	public void pause() {
		if (music != null)
			music.pause();
	}

	@Override
	public void resume() {
		if (music != null)
			music.play();
	}

	public void MoveCameraRelative(int x, int y) {

	}

	public boolean isCellPassable(int x, int y) {
		for (Obj obj : ObjectArray.get(x).get(y)) {
			if (obj.dense) {
				return false;
			}
		}
		return true;
	}

	public boolean isCellOpaque(int tileX, int tileY) {
		if (tileX < 0 || tileX > MAP_SIZE_X || tileY < 0 || tileY > MAP_SIZE_Y) {
			return true;
		}

		for (Obj obj : ObjectArray.get(tileX).get(tileY)) {
			if (obj.opaque) {
				return true;
			}
		}
		return false;
	}

	public void mouseEvent(int tileX, int tileY) {
		if (tileX < 0 || tileX > MAP_SIZE_X || tileY < 0 || tileY > MAP_SIZE_Y) {
			return;
		}

	}

	public boolean LosToTile(int startx, int starty, int endx, int endy) {
		if (startx < 0 || startx > MAP_SIZE_X || starty < 0
				|| starty > MAP_SIZE_Y || endx < 0 || endx > MAP_SIZE_X
				|| endy < 0 || endy > MAP_SIZE_Y) {
			return false;

		}

		if (startx == endx) {
			if (starty == endy) {
				return true; // Light cannot be blocked on same tile
			} else {
				int direction = returnSign(endy - starty);
				starty += direction;
				while (starty != endy) {
					if (isCellOpaque(startx, starty)) {
						return false;
					}
					starty += direction;
				}
			}
		}

		return true;

	}

	public int returnSign(double x) {
		return ((x < 0) ? -1 : 1);

	}

	public void generateMapObjects() {

		TiledMapTileLayer tiledDoorLayer = (TiledMapTileLayer) map.getLayers()
				.get("Doors");
		TiledMapTileLayer tiledFloorLayer = (TiledMapTileLayer) map.getLayers()
				.get("Floor");
		TiledMapTileLayer tiledWallLayer = (TiledMapTileLayer) map.getLayers()
				.get("Walls");

		for (int row = 0; row <= MAP_SIZE_X; row++) {

			ObjectArray.add(new ArrayList<ArrayList<Obj>>());

			for (int column = 0; column <= MAP_SIZE_Y; column++) {
				ObjectArray.get(row).add(new ArrayList<Obj>());

				Cell c = tiledDoorLayer.getCell(row, column);
				if (c != null) {
					// Door d = new Door(row, column);
					// ObjectArray.get(row).get(column).add(d);
					// ObjectArrayByID.put(d.UID, d);

					Cell FloorTile = tiledFloorLayer.getCell(row, column);
					c.setTile(new StaticTiledMapTile(FloorTile.getTile()
							.getTextureRegion()));

					// worldStage.addActor(d);

				}

				c = tiledWallLayer.getCell(row, column);
				if (c != null) {
					// Wall w = new Wall(row, column);
					// ObjectArray.get(row).get(column).add(w);
					// ObjectArrayByID.put(w.UID, w);

					Cell FloorTile = tiledFloorLayer.getCell(row, column);
					c.setTile(new StaticTiledMapTile(FloorTile.getTile()
							.getTextureRegion()));

					// worldStage.addActor(w);
				}

			}
		}

	}

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

	public void clearVisibleMap() {

		for (int i = 0; i < (VIEW_DISTANCE_X + VIEW_DISTANCE_X_EXTENDED)
				* camera.zoom; i++) {
			for (int j = 0; j < (VIEW_DISTANCE_Y + VIEW_DISTANCE_Y_EXTENDED)
					* camera.zoom; j++) {
				OccluedTiles[i][j] = true;
			}
		}
	}

	public void calculateVisibleTiles() {
		recaculateVisibleTiles = false;

		if (controlledObject != null) {
			FOV();
		}

	}

	void FOV() {
		double x;
		double y;
		double i;
		clearVisibleMap();// Initially set all tiles to not visible.
		for (i = 0; i < 360; i = i + 0.5) {
			x = Math.cos((float) i * 0.01745f);
			y = Math.sin((float) i * 0.01745f);
			DoFov(x, y);
		}
		;
	};

	void DoFov(double x, double y) {
		int i;

		float ox = controlledObject.tileXPosition + .5f;
		float oy = controlledObject.tileYPosition + .5f;

		float cameraX = (VIEW_DISTANCE_X + VIEW_DISTANCE_X_EXTENDED)
				* camera.zoom / 2.0f;
		float cameraY = (VIEW_DISTANCE_Y + VIEW_DISTANCE_Y_EXTENDED)
				* camera.zoom / 2.0f;

		for (i = 0; i < VIEW_DISTANCE_X * camera.zoom; i++) {

			if (cameraX < 0
					|| cameraX >= (VIEW_DISTANCE_X + VIEW_DISTANCE_X_EXTENDED)
							* camera.zoom
					|| cameraY < 0
					|| cameraY >= (VIEW_DISTANCE_Y + VIEW_DISTANCE_Y_EXTENDED)
							* camera.zoom) {

				return;

			}

			OccluedTiles[(int) cameraX][(int) cameraY] = false;// Set the tile
																// to visible.
			if (!isCellOpaque((int) ox, (int) oy) == false) {
				return;
			}

			ox += x;
			oy += y;
			cameraX += x;
			cameraY += y;
		}
	}

	public void addToWorld(Obj o) {

		o.refreshTexture();

		if (o.tileXPosition >= 0) {
			ObjectArray.get(o.tileXPosition).get(o.tileYPosition).add(o);
			worldStage.addActor(o);
		}

		ObjectArrayByID.put(o.UID, o);

		recaculateVisibleTiles = true;

	}

	public void removeFromWorld(int UID) {
		Obj o = ObjectArrayByID.get(UID);
		o.remove();
		ObjectArrayByID.remove(UID);

		if (o.tileXPosition >= 0) {
			ObjectArray.get(o.tileXPosition).get(o.tileYPosition).remove(o);
		}

		recaculateVisibleTiles = true;
	}

	public void objectRelocate(Position P) {
		Obj o = ObjectArrayByID.get(P.UID);

		ObjectArray.get((int) o.getX() / TILE_SIZE)
				.get((int) o.getY() / TILE_SIZE).remove(o);

		o.setX(P.x * TILE_SIZE);
		o.setY(P.y * TILE_SIZE);

		o.tileXPosition = P.x;
		o.tileYPosition = P.y;

		ObjectArray.get((int) o.getX() / TILE_SIZE)
				.get((int) o.getY() / TILE_SIZE).add(o);

		if (P.UID == controlledObject.UID) {
			refocusCamera = true;

		}
	}

	public void queueObjectPosition(Position P) {

		QueuedEvents.add(P);

	}

	public void objectMove(Position P) {

		Obj o = ObjectArrayByID.get(P.UID);

		ObjectArray.get(o.tileXPosition).get(o.tileYPosition).remove(o);

		o.tileXPosition = P.x;
		o.tileYPosition = P.y;

		ObjectArray.get(P.x).get(P.y).add(o);


		// tl;dr tweener bad?
		Tween.to(o, ObjTweener.POSITION_XY,
				(float) (o.moveDelay / 1000.0))
				.target(P.x * TILE_SIZE, P.y * TILE_SIZE).ease(Linear.INOUT)
				.start(tweenManager);

		if (P.UID == controlledObject.UID) {
			refocusCamera = true;

		}
	}
	
	

	public void mouseEvent(int mouseEventUID) {
		Obj o = ObjectArrayByID.get(mouseEventUID);

		o.onClick(null);

	}
	
	public void collisionEvent(UidPair uidPair)
	{
		Obj o = ObjectArrayByID.get(uidPair.first);
		if(o != null)
		{
			o.collide(uidPair.second);
		}
		
	}

	public void addToWorld(DistilledObject distilled) {
		try {

			java.lang.reflect.Constructor<?> ctor = distilled.ContainedClass
					.getDeclaredConstructor(int.class, int.class);

			Obj m = (Obj) ctor.newInstance(distilled.X, distilled.Y);

			m.UID = distilled.dUID;

			ObjectArray.get(distilled.X).get(distilled.Y).add(m);
			ObjectArrayByID.put(m.UID, m);

			worldStage.addActor(m);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void assignControl(int UID) {
		controlledObject = ObjectArrayByID.get(UID);
		
		// this is the last stage of loading -- initialize the music player
		initMusic();
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "CSCI-375-Project";
		cfg.useGL20 = true;
		cfg.width = VIEW_DISTANCE_X * TILE_SIZE * 2;
		// cfg.height = VIEW_DISTANCE_Y * TILE_SIZE * 2;
		cfg.height = 10 * 64;

		// cfg.resizable = false;
		cfg.vSyncEnabled = true;

		cfg.foregroundFPS = 60;

		Settings settings = new Settings();
		settings.maxWidth = 32;
		settings.maxHeight = 32;

		TexturePacker.processIfModified("assets/AutopackingTiles/Door/images",
				"assets/AutopackingTiles/Door/", "PackedDoor");

		new LwjglApplication(new ClientEngine(), cfg);
	}
	
	public void initMusic()
	{
		// setup looping background music
		music=Gdx.audio.newMusic(Gdx.files.internal("assets/music/bgmusic.mp3"));
		music.setLooping(true);
		music.setVolume(1.0f);
		music.play();
	}
	
	public void changeHealth(Integer hp)
	{
		playerHealth = hp;
		System.out.println("Life at "+hp+"%");
	}



}
