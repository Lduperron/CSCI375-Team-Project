package core.shared;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ConfigOptions
{
	
	
	public static final int TILE_SIZE = 32;
	public static final int VIEW_DISTANCE_X = 15;
	public static final int VIEW_DISTANCE_Y = 15;
	
	public static final int VIEW_DISTANCE_X_EXTENDED = 2;
	public static final int VIEW_DISTANCE_Y_EXTENDED = 2;
	
	public static final int MAP_SIZE_X = 300;
	public static final int MAP_SIZE_Y = 300;
	
	public static final int MAX_OBJECTS_PER_TILE = 100;
	
	
	public static Texture texturereg = new Texture((Gdx.files.internal("data/libgdx.png")));
	public static TextureRegion texture = new TextureRegion(texturereg);
	
	/*
	 * Character textures -- one for up, right, left, down
	 */
	public static Texture charReg = new Texture((Gdx.files.internal("tilesets/characters.png")));
	
	public static TextureRegion charUp = new TextureRegion(charReg, 96, 96, 32, 32);
	public static TextureRegion charRight = new TextureRegion(charReg, 160, 64, 32, 32);
	public static TextureRegion charDown = new TextureRegion(charReg, 96, 0, 32, 32);
	public static TextureRegion charLeft = new TextureRegion(charReg, 96, 32, 32, 32);
	
	public static Texture enemyReg = new Texture((Gdx.files.internal("tilesets/enemies.png")));
	
	public static TextureRegion[] enemyUp = new TextureRegion[] 
	{ 
		new TextureRegion(enemyReg, 160, 224, 32, 32), 
		new TextureRegion(enemyReg, 192, 224, 32, 32)
	};
	
	public static TextureRegion[] enemyRight = new TextureRegion[]
	{
		new TextureRegion(enemyReg, 160, 192, 32, 32),
		new TextureRegion(enemyReg, 192, 192, 32, 32)
	};
	public static TextureRegion[] enemyDown = new TextureRegion[]
	{
		new TextureRegion(enemyReg, 96, 128, 32, 32),
		new TextureRegion(enemyReg, 192, 128, 32, 32)
	};
	public static TextureRegion[] enemyLeft = new TextureRegion[]
	{
		new TextureRegion(enemyReg, 128, 160, 32, 32),
		new TextureRegion(enemyReg, 192, 160, 32, 32)
	};
	
	public static Texture cakeTex = new Texture((Gdx.files.internal("tilesets/laser.png")));

	public static TextureRegion cakeTexture = new TextureRegion(cakeTex);
	
	
	public static Texture texture2reg = new Texture((Gdx.files.internal("tilesets/wall.png")));
	public static TextureRegion texture2 = new TextureRegion(texture2reg);



}

