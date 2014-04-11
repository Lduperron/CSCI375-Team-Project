package helpers;

public enum Hand
{

	LEFT,
	RIGHT;
	
	public Hand switchHand(Hand currentHand)
	{
		if(currentHand == LEFT)
		{
			
			return RIGHT;
			
		}
		else if(currentHand == RIGHT)
		{
			
			return LEFT;
			
		}
		return null; //this can be expanded to have aliens with more than two hands.  Maybe.  ...
	}
	
}
