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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;

import core.client.ClientEngine;
import core.client.animatedAssets;
import core.shared.ConfigOptions;
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
		
	}

	public BitSet TransparentPixels = new BitSet(TILE_SIZE * TILE_SIZE);

	protected TextureRegion texture = ConfigOptions.texture;
	
	//public String name = "Undefined Object";
	public String description = "Undefined Description";
	
	public boolean dense = false;
	public boolean opaque = false;
	
	public int UID = getObjUID.getUID();
	
	public boolean animated = false;
	
	float animatedTime = 0;
	
	
	public Animation currentAnimation;
	public boolean LoopAnimation = false;
	public static HashMap<String, Animation> Animations = new HashMap<String, Animation>();
	public TextureRegion currentFrame;
	
	
	public void onClick()
	{
		
		
		
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
				int position = (int) (y * getHeight() + x);
				if (!TransparentPixels.get(position)) 
				{
					System.out.println("TEST");
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
		
		Vector3 test = new Vector3(getX(),getY(),1);
		ClientEngine.camera.project(test);
		
		
		if(!animated)
		{
			batch.draw(currentFrame, test.x, test.y, getOriginX(), getOriginY(), 64 , 64 , getScaleX(), getScaleY(), getRotation());
		}
		else
		{
			animatedTime += Gdx.graphics.getDeltaTime();           // #15
			currentFrame = currentAnimation.getKeyFrame(animatedTime, LoopAnimation);  // #16
	        batch.draw(currentFrame, test.x, test.y, 64, 64);           // #17
	        
	        if(currentAnimation.isAnimationFinished(animatedTime) && LoopAnimation == false)
	        {
	        	texture = currentFrame;
	        	TransparentPixels = currentAnimation.getKeyFrameTransparency(animatedTime);
	        	animated = false;
	        }
		}
    }

	
}
