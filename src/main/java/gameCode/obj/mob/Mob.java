package gameCode.obj.mob;

import java.lang.reflect.Field;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.shared.ConfigOptions;

import gameCode.obj.*;
import gameCode.obj.item.Item;
import helpers.Hand;

public class Mob extends Obj
{

	
	public Mob(int x, int y)
	{
		super(x, y);
		
		
		dense = true;
	}
	
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
	protected TextureRegion texture = ConfigOptions.texture;
	Boolean AIcontrolled = false; 

	
	
	public Item leftHand;
	public Item rightHand;
	
	public Hand ActiveHand = Hand.LEFT; 
	
}
