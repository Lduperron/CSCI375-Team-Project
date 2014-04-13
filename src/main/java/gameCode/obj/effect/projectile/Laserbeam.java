package gameCode.obj.effect.projectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.shared.ConfigOptions;

public class Laserbeam extends Projectile
{

	public Laserbeam(int x, int y)
	{
		super(x, y);
		// TODO Auto-generated constructor stub
		
		name = "Laserbeam!";
		moveDelay = 50;
	}

	@Override
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
	
	@Override
	public void process()
	{
		super.process();
		
	}
	

	
	protected TextureRegion texture = ConfigOptions.cakeTexture;
}
