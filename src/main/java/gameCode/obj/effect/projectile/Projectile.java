package gameCode.obj.effect.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import gameCode.obj.effect.Effect;


public class Projectile extends Effect
{

	protected Projectile(int x, int y)
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
