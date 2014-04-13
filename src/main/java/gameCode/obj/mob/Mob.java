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
		// TODO Auto-generated constructor stub
	}
	
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
	protected TextureRegion texture = ConfigOptions.texture;
	protected Boolean AIcontrolled = false; 

	//@Override
	//public void bulletImpact(int bulletUID) {};
	
	public Item leftHand;
	public Item rightHand;
	
	public Hand ActiveHand = Hand.LEFT; 
	
}
