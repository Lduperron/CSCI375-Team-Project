package core.shared;

public class Position
{
	public Position(int _x, int _y)
	{
		x = _x;
		y = _y;
		
	}
	public Position(Position P)
	{
		x = P.x;
		y = P.y;
		
	}
	
	
	public int x;
	public int y;
	
}
