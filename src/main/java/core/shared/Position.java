package core.shared;

public class Position
{
	public Position(int _UID, int _x, int _y)
	{
		UID = _UID;
		x = _x;
		y = _y;
		
	}
	public Position(Position P)
	{
		UID = P.UID;
		x = P.x;
		y = P.y;
		
	}
	
	public Position()
	{
	}

	public int UID;
	public int x;
	public int y;
	
}
