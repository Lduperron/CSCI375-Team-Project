package gameCode.obj;

import static core.shared.ConfigOptions.TILE_SIZE;
import static core.shared.ConfigOptions.VIEW_DISTANCE_X;
import static core.shared.ConfigOptions.VIEW_DISTANCE_Y;

import gameCode.obj.item.Item;

import java.util.BitSet;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional;

import core.client.ClientEngine;
import core.client.animatedAssets;
import core.server.ServerEngine.ServerEngineReference;
import core.shared.ConfigOptions;
import core.shared.DistilledObject;
import core.shared.Position;

public class Obj extends Actor
{

	
	protected Obj(int x, int y)
	{
		
		this.setX(x*TILE_SIZE);
		this.setY(y*TILE_SIZE);
		this.setWidth(TILE_SIZE);
		this.setHeight(TILE_SIZE);
		
		this.tileXPosition = x;
		this.tileYPosition = y;

		// Objects are initiated on the server and then sent to the clients
		// The serverside field is not sent and defaults to false for the objects.
		this.ServerSide = true;

		lastMoveTime = System.currentTimeMillis();
		lastActionTime = System.currentTimeMillis();
//	    UniqueData.put("Name" , "Undefined Object");
//	    UniqueData.put("Desc" , "Undefined Description");
//	    UniqueData.put("Dense" , "false");
//	    UniqueData.put("Opaque" , "false");
		
	}
	
	
	float xPositionOffset = 0;
	float yPositionOffset = 0;
	
	public void setXOffset(float offset)
	{
		
		xPositionOffset = offset;
		
	}
	
	public void setYOffset(float offset)
	{
		
		yPositionOffset = offset;
		
	}
	
	public float getXOffset()
	{
		
		return xPositionOffset;  // not supported
	}
	
	
	public float getYOffset()
	{
		
		return yPositionOffset;// not supported
	}
	
	public float getXCameraOffset()
	{
		return tileXPosition - this.getX()/TILE_SIZE;
		
	}
	
	public float getYCameraOffset()
	{
		
		return tileYPosition - this.getY()/TILE_SIZE;
	}
	
	public void refreshTexture()
	{
		
		currentFrame = this.getTexture();
		
		
	}
	
	public void forceMove(int newX, int newY)
	{
		if(this.ServerSide) // only handle moving on the server, movement events are propagated to clients.
		{
			Position P = new Position();
			P.UID = this.UID;
			P.x = newX;
			P.y = newY;
			
			ServerEngineReference.getSelf().objectRelocate(P);
			
		}
	}
	
	public void move(int newX, int newY)
	{
		if(this.ServerSide) // only handle moving on the server, movement events are propagated to clients.
		{
			Position P = new Position();
			P.UID = this.UID;
			P.x = newX;
			P.y = newY;
			
			ServerEngineReference.getSelf().requestMove(P);
			
		}
	}
	
	
	
	public int tileXPosition;
	public int tileYPosition;
	
	
	@Optional(value = "Never")
	public long lastMoveTime;
	
	
	@Optional(value = "Never")
	public long lastActionTime;
	
	public BitSet TransparentPixels = new BitSet(TILE_SIZE * TILE_SIZE);

	@Optional(value = "Never")
	protected TextureRegion texture = ConfigOptions.texture;
	
//	HashMap<String, String> UniqueData = new HashMap<>();
	
	
	public String name = "Undefined Object";
	public String description = "Undefined Description";
	
	public boolean dense = false;
	public boolean opaque = false;
	
	public int UID = getObjUID.getUID();
	
	public boolean animated = false;
	
	float animatedTime = 0;
	
	
	public Animation currentAnimation;
	public boolean LoopAnimation = false;
	public static HashMap<String, Animation> Animations = new HashMap<String, Animation>();
	
	@Optional(value = "Never")
	public TextureRegion currentFrame = null;

	@Optional(value = "Never")
	public boolean ServerSide = false;
	
	public DistilledObject distill()
	{
		DistilledObject d = new DistilledObject();
		
		d.ContainedClass = this.getClass();
		
		
		
		d.X = (int) this.getX()/TILE_SIZE;
		d.Y = (int) this.getY()/TILE_SIZE;
		d.dUID = this.UID;
		
		
		
		return d;
		
		
	}
	
	
	
	public void onClick(Item attackedBy)
	{
		
		//System.out.println(this.ServerSide);
		
		//System.out.println(UID);
		//ClientEngine.Test.getSelf().removeFromWorld(UID);
		
	}
	
	public void rangedEvent(Position P)
	{
		
		return;
		
	}
	
	@Override
	public Actor hit(float x, float y, boolean touchable) 
	{
		
		 if (touchable && getTouchable() != Touchable.enabled)
		 {
		 		return null;
		 }
		 
		 else
		 {
			if(x >= 0 && x < getWidth() && y >= 0 && y < getHeight())
			{	 
				y = Math.abs(y - getWidth());
				
				int position = (int) ((int)y * getWidth() + x );
				
				if (!TransparentPixels.get(position)) 
				{
					return this;
				}
			
				
				
			}
		 }

		 return null;
	}
	
	
	public HashMap<String, Animation> getAnimations()
	{
		
		return Animations;
		
	}
	
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
	
	
	public void animate(String animationID, boolean Loop)
	{
		animatedTime = 0;
		currentAnimation = getAnimations().get(animationID);
		LoopAnimation = Loop;
		animated = true;
		
	}
	
	@Override 
    public void draw(Batch batch, float parentAlpha) 
    {
    	Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

		if(!animated)
		{
			batch.draw(currentFrame, getX() + getXOffset(), getY() + getYOffset(), getOriginX(), getOriginY(), 32 , 32 , getScaleX(), getScaleY(), getRotation());
			//batch.draw(currentFrame, test.x, test.y, getOriginX(), getOriginY(), 32 , 32 , getScaleX(), getScaleY(), getRotation());
		}
		else
		{
			animatedTime += Gdx.graphics.getDeltaTime();
			currentFrame = currentAnimation.getKeyFrame(animatedTime, LoopAnimation); 
	        batch.draw(currentFrame, getX(), getY(), 32, 32);
	        
	        if(currentAnimation.isAnimationFinished(animatedTime) && LoopAnimation == false)
	        {
	        	texture = currentFrame;
	        	TransparentPixels = currentAnimation.getKeyFrameTransparency(animatedTime);
	        	animated = false;
	        }
		}
    }
	
	
}
