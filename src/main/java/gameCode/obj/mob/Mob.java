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
		this.dense = true;
	}
	
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
	protected TextureRegion texture = ConfigOptions.texture;
	protected TextureRegion upTexture, rightTexture, downTexture, leftTexture;
	protected Boolean AIcontrolled = false; 

	//@Override
	//public void bulletImpact(int bulletUID) {};
	
	public Item leftHand;
	public Item rightHand;
	
	public Hand ActiveHand = Hand.LEFT; 
	
	public void setAIcontrolled(Boolean ai)
	{
		this.AIcontrolled = ai;
	}
	
	public void setTexture(TextureRegion tr)
	{
		this.texture = tr;
	}
	
	public Boolean isAIcontrolled()
	{
		return this.AIcontrolled;
	}
	
	public void setTextures(TextureRegion u, TextureRegion r, TextureRegion d, TextureRegion l)
	{
		this.upTexture = u;
		this.rightTexture = r;
		this.downTexture = d;
		this.leftTexture = l;
	}
	
	public void setDirection(Direction d)
	{
		System.out.println(d);
		switch (d)
		{
			case UP:
				this.texture = this.upTexture;
				break;
			
			case RIGHT:
				this.texture = this.rightTexture;
				break;
				
			case DOWN:
				this.texture = this.downTexture;
				break;
			
			case LEFT:
			default:
				this.texture = this.leftTexture;
				break;
		}
	}
}
