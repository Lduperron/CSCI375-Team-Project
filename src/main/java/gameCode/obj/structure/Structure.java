package gameCode.obj.structure;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import gameCode.obj.*;

public class Structure extends Obj
{
	Structure(int x, int y)
	{
		super(x , y);
		dense = true;
		opaque = true;
	}

	@Override
	public TextureRegion getTexture()
	{
		// TODO Auto-generated method stub
		return texture;
	}
	
}
