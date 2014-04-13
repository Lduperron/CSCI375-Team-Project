package gameCode.obj.structure;

import gameCode.obj.item.Item;
import helpers.HelperFunctions;

import java.util.BitSet;
import java.util.HashMap;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

import core.client.PixmapTextureAtlas;

public class Door extends Structure
{
	public Door(int x, int y)
	{
		super(x, y);
		
		
		Animations.put("Opening", openAnimation);
		Animations.put("Closing", closingAnimation);
		Animations.put("Denied", deniedAnimation);
		
		currentFrame = texture;
		

		TransparentPixels = start.get(0);
		
		name = "Door";
		
	}
	
	
	
	
	public static HashMap<String, Animation> Animations = new HashMap<String, Animation>();

	
	static TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("assets/AutopackingTiles/Door/PackedDoor.atlas"));
	
	static PixmapTextureAtlas p = new PixmapTextureAtlas(Gdx.files.internal("assets/AutopackingTiles/Door/PackedDoor.png") , Gdx.files.internal("assets/AutopackingTiles/Door/PackedDoor.atlas"));
	
	static Array<BitSet> transp = p.createTransparencies("hightechsecurity_opening");
	static Array<BitSet> transc = p.createTransparencies("hightechsecurity_closing");
	static Array<BitSet> transd = p.createTransparencies("hightechsecurity_denied");
	
	
	public static TextureRegion texture = new TextureRegion(atlas.findRegion("hightechsecurity_closed"));
	static Array<BitSet> start = p.createTransparencies("hightechsecurity_closed");
	
	
	@Override
	public HashMap<String, Animation> getAnimations()
	{
		
		return Animations;
		
	}

	static Array<AtlasRegion> openingRegion = atlas.findRegions("hightechsecurity_opening");
	static Animation openAnimation = new Animation(0.1f, openingRegion, 0, transp);
	
	
	static Array<AtlasRegion> closingRegion = atlas.findRegions("hightechsecurity_closing");
	static Animation closingAnimation = new Animation(0.1f, closingRegion, 0, transc);
	
	static Array<AtlasRegion> deniedRegion = atlas.findRegions("hightechsecurity_denied");
	static Animation deniedAnimation = new Animation(0.1f, deniedRegion, 0, transd);
	
	public boolean locked = true;
	
	
	@Override
	public void collide(int colldier)
	{
		super.collide(colldier);
		
		this.onClick(null);
		return;
	}
	
	public void Open()
	{
		
		if(!this.dense)
		{
			
			return; // already open
			
		}
		
		
		final Door d = this;
		HelperFunctions.afterDelay(.5, this, new TweenCallback()
		{
			
			@Override
			public void onEvent(int type, BaseTween<?> source)
			{
				d.dense = false;
				d.opaque = false;
				
			}

		});
		

		this.animate("Opening", false);
		
		
	}
	public void Close()
	{
		
		if(this.dense)
		{
			
			return; // already closed
			
		}
		
		final Door d = this;
		HelperFunctions.afterDelay(.5, this, new TweenCallback()
		{
			
			@Override
			public void onEvent(int type, BaseTween<?> source)
			{
				d.dense = true;
				d.opaque = true;
				
			}

		});
		this.animate("Closing", false);
		
	}
	
	@Override
	public void onClick(Item attackedBy)
	{
		super.onClick(attackedBy);
		
		if(this.locked)
		{
			this.animate("Denied", false);
		}
		
		else
		{
			if(this.dense)
			{

				this.Open();
								
				final Door d = this;
				HelperFunctions.afterDelay(5, this, new TweenCallback()
				{
					
					@Override
					public void onEvent(int type, BaseTween<?> source)
					{
						d.Close();
						
					}

				});

				
			}
			else
			{
				this.Close();
			}
			
		
		}
				
	}
	
	
	
	@Override
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
}
