package gameCode.obj.item.weapon;

import core.shared.Position;
import gameCode.obj.*;
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
		
		System.err.println("Bang");
		
		
		return;
		
	}
	

}
