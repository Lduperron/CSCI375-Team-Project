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
	
	public void setTarget(int x, int y)
	{
		tileXTarget=x;
		tileYTarget=y;
		
		
        double xDiff = tileXTarget - this.getTileXPosition();
        double yDiff = tileYTarget - this.getTileYPosition();
        targetAngle = (Math.atan2(yDiff, xDiff));
        
        xComponent = Math.cos(targetAngle);
        yComponent = Math.sin(targetAngle);
        
		
		xRollover += xComponent;
		yRollover += yComponent;
        
        
	}
	
	public void process()
	{
		int x = 0;
		int y = 0;
		
		xRollover += xComponent;
		yRollover += yComponent;
		
		if(xRollover >= 1)
		{
			
			xRollover--;
			x++;
			
		}
		else if(xRollover <= -1)
		{
			
			xRollover++;
			x--;
			
		}
		
		if(yRollover >= 1)
		{
			
			yRollover--;
			y++;
			
		}
		else if(yRollover <= -1)
		{
			
			yRollover++;
			y--;
			
		}
		
		this.move(x, y);
		
	}
	
	
	public int tileXTarget;
	public int tileYTarget;
	
	public double xRollover;
	public double yRollover;
	
	public double targetAngle;
	public double xComponent;
	public double yComponent;
	

}
