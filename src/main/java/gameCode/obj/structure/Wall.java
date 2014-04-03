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
		// TODO Auto-generated constructor stub
	}
	
	protected static TextureRegion texture = ConfigOptions.texture2;
	
	
	@Override
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}


}
