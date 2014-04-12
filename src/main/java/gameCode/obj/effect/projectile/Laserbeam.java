package gameCode.obj.effect.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.shared.ConfigOptions;

public class Laserbeam extends Projectile
{

	public Laserbeam(int x, int y)
	{
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
	
	protected TextureRegion texture = ConfigOptions.cakeTexture;
}
