package core.client;

import gameCode.obj.Obj;

import org.lwjgl.Sys;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import core.shared.Message;
import core.shared.Position;



public class UIControlHandler implements InputProcessor {

	ClientEngine centralEngine;
	
	public UIControlHandler(ClientEngine s)
	{
		
		centralEngine = s;
		
	}
	
	@Override
	public boolean keyDown(int keycode) 
	{
		
		centralEngine.pressedKeys[keycode] = true;

//		if(keycode == Keys.UP)
//		{       
//			centralEngine.MoveCameraRelative(0, 1);
//		}
//		if(keycode == Keys.DOWN)
//		{       
//			centralEngine.MoveCameraRelative(0, -1);
//		}
//		if(keycode == Keys.LEFT)
//		{       
//			centralEngine.MoveCameraRelative(-1, 0);
//		}
//		if(keycode == Keys.RIGHT)
//		{       
//			centralEngine.MoveCameraRelative(1, 0);
//		}
	
		return true;
	
	}

	@Override
	public boolean keyUp(int keycode) {
		
		centralEngine.pressedKeys[keycode] = false;
		
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		

		
		return false;
	}
	
	Position mouseEventCoords = new Position();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		Vector2 screenCoords = new Vector2(screenX, screenY);
		
		
		centralEngine.worldStage.screenToStageCoordinates(screenCoords);
		
		mouseEventCoords.x = (int) screenCoords.x;
		mouseEventCoords.y = (int) screenCoords.y;
				
		Obj o = (Obj) centralEngine.worldStage.hit(screenCoords.x, screenCoords.y, true);
		
		if(o != null)
		{
			ClientEngine.network.send(Message.MOUSEEVENTTOSERVER, o.UID);	
		}
		
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}