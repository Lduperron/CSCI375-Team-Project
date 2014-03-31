package gameCode.obj.structure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import core.shared.ConfigOptions;

public class Wall extends Structure
{

	public Wall(int x, int y)
	{
		super(x, y);
		// TODO Auto-generated constructor stub
	}
	
	protected Texture texture = ConfigOptions.texture2;
	
	
	@Override
	public Texture getTexture()
	{
		
		return texture;
		
	}


}
