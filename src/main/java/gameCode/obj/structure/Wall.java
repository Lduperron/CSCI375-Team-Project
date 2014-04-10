package gameCode.obj.structure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.shared.ConfigOptions;

public class Wall extends Structure
{

	public Wall(int x, int y)
	{
		super(x, y);
		
		
		
		name = "Wall";
	}
	
	protected static TextureRegion texture = ConfigOptions.texture2;
	
	
	@Override
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
	
	@Override
	public void onClick()
	{
		
		this.move(this.tileXPosition+1, this.tileYPosition);
		
	}


}
