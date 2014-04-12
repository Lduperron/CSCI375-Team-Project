package gameCode.obj.item.weapon;

import core.shared.Position;
import gameCode.obj.*;
import gameCode.obj.effect.projectile.Laserbeam;
import gameCode.obj.item.Item;

public class Weapon extends Item
{

	public Weapon(int x, int y)
	{
		super(x, y);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public void rangedEvent(Position P)
	{
		
		if(this.ServerSide)
		{
			
			Laserbeam l = new Laserbeam(this.getTileXPosition(), this.getTileYPosition());			
			
		}
		
		return;
		
	}




}
