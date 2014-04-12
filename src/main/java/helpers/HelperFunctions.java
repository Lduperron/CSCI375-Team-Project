package helpers;

import core.client.ClientEngine.ClientEngineReference;
import core.server.ServerEngine.ServerEngineReference;
import gameCode.obj.Obj;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

public class HelperFunctions
{

	public HelperFunctions()
	{
		// TODO Auto-generated constructor stub
	}

	
	public static void afterDelay(double d, Obj caller, TweenCallback code)
	{
		if(caller.ServerSide)
		{
			
			Tween.call(code).delay((float) d).start(ServerEngineReference.getSelf().tweenManager);
			
		}
		else
		{
			
			Tween.call(code).delay((float) d).start(ClientEngineReference.getSelf().tweenManager);
			
		}
		
		
		
	}
}
