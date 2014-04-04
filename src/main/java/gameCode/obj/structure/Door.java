package gameCode.obj.structure;

import java.util.BitSet;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

import core.client.PixmapTextureAtlas;
import core.client.ClientEngine.Test;

public class Door extends Structure
{
	public Door(int x, int y)
	{
		super(x, y);
		
		
		Animations.put("Opening", openAnimation);
		Animations.put("Closing", closingAnimation);
		
		currentFrame = texture;
		

		TransparentPixels = start.get(0);
		
		name = "Door";
		
	}
	
	
	
	public static HashMap<String, Animation> Animations = new HashMap<String, Animation>();

	
	static TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("assets/AutopackingTiles/Door/PackedDoor.atlas"));
	
	static PixmapTextureAtlas p = new PixmapTextureAtlas(Gdx.files.internal("assets/AutopackingTiles/Door/PackedDoor.png") , Gdx.files.internal("assets/AutopackingTiles/Door/PackedDoor.atlas"));
	
	static Array<BitSet> transp = p.createTransparencies("hightechsecurity_opening");
	static Array<BitSet> transc = p.createTransparencies("hightechsecurity_closing");
	
	
	public static TextureRegion texture = new TextureRegion(atlas.findRegion("hightechsecurity_closed"));
	static Array<BitSet> start = p.createTransparencies("hightechsecurity_closed");
	
	
	public HashMap<String, Animation> getAnimations()
	{
		
		return Animations;
		
	}

	static Array<AtlasRegion> openingRegion = atlas.findRegions("hightechsecurity_opening");
	static Animation openAnimation = new Animation(0.1f, openingRegion, 0, transp);
	
	
	static Array<AtlasRegion> closingRegion = atlas.findRegions("hightechsecurity_closing");
	static Animation closingAnimation = new Animation(0.1f, closingRegion, 0, transc);
	
	
	public boolean locked = true;
	
	
	
	
	@Override
	public void onClick()
	{
		
		if(this.animated)
		{
			return;
		}
		
		if(this.locked)
		{
			//this.animate("AccessDenied", false);
		}
		
		else
		{
			if(this.dense)
			{
				this.dense = false;
				this.opaque = false;
				this.animate("Opening", false);
				
			}
			else
			{
				this.dense = true;
				this.opaque = true;
				this.animate("Closing", false);
			}
			
			Test.getSelf().calculateVisibleTiles();
		}
				
	}
	
	
	
	@Override
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
}
