package core.shared;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class ConfigOptions
{
	
	
	double moveDelay = 20; // In miliseconds.
	
	
	public static final int TILE_SIZE = 32;
	public static final int VIEW_DISTANCE_X = 15;
	public static final int VIEW_DISTANCE_Y = 15;
	
	public static final int MAP_SIZE_X = 300;
	public static final int MAP_SIZE_Y = 300;
	
	public static final int MAX_OBJECTS_PER_TILE = 100;
	
	
	public static Texture texture = new Texture((Gdx.files.internal("data/libgdx.png")));
	public static Texture texture2 = new Texture((Gdx.files.internal("tilesets/tmw_desert_spacing - Copy.png")));
}

