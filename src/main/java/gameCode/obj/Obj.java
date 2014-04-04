package gameCode.obj;

import static core.shared.ConfigOptions.TILE_SIZE;
import static core.shared.ConfigOptions.VIEW_DISTANCE_X;
import static core.shared.ConfigOptions.VIEW_DISTANCE_Y;

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

import core.client.ClientEngine;
import core.client.animatedAssets;
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
		
		currentFrame = getTexture();
		
		
	    this.addListener(new ClickListener() {
	        @Override public void clicked(InputEvent event, float x, float y) {
	            // When you click the button it will print this value you assign.
	            // That way you will know 'which' button was clicked and can perform
	            // the correct action based on it.
	        	
	        	Obj object = (Obj)event.getTarget();
	        	
	            object.onClick();

	        };
	    });
		
		
		
	}

	public BitSet TransparentPixels = new BitSet(TILE_SIZE * TILE_SIZE);

	protected TextureRegion texture = ConfigOptions.texture;
	
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
	public TextureRegion currentFrame = null;

	
	public DistilledObject distill()
	{
		DistilledObject d = new DistilledObject();
		
		d.ContainedClass = this.getClass();
		d.X = (int) this.getX()/TILE_SIZE;
		d.Y = (int) this.getY()/TILE_SIZE;
		d.dUID = this.UID;
		
		return d;
		
		
	}
	
	public void onClick()
	{
		
		
		
	}
	
	
	static Vector3 cameraProjectVector;
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
			batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), 32 , 32 , getScaleX(), getScaleY(), getRotation());
			//batch.draw(currentFrame, test.x, test.y, getOriginX(), getOriginY(), 32 , 32 , getScaleX(), getScaleY(), getRotation());
		}
		else
		{
			animatedTime += Gdx.graphics.getDeltaTime();           // #15
			currentFrame = currentAnimation.getKeyFrame(animatedTime, LoopAnimation);  // #16
	        batch.draw(currentFrame, getX(), getY(), 32, 32);           // #17
	        
	        if(currentAnimation.isAnimationFinished(animatedTime) && LoopAnimation == false)
	        {
	        	texture = currentFrame;
	        	TransparentPixels = currentAnimation.getKeyFrameTransparency(animatedTime);
	        	animated = false;
	        }
		}
    }

	
}
