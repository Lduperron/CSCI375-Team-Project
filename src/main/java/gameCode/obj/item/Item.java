package gameCode.obj.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import gameCode.obj.Obj;

public class Item extends Obj
{

	protected Item(int x, int y)
	{
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
}
