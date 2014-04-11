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
import static core.shared.ConfigOptions.TILE_SIZE;


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
		
		mouseEventCoords.UID = -1;
		mouseEventCoords.x = (int) (screenCoords.x / TILE_SIZE);
		mouseEventCoords.y = (int) (screenCoords.y / TILE_SIZE);
				
		Obj o = (Obj) centralEngine.worldStage.hit(screenCoords.x, screenCoords.y, true);
		
		if(o != null) // Clicked on an object in the world
		{
			ClientEngine.network.send(Message.MOUSEEVENTTOSERVERONOBJECT, o.UID);	
		}
		else
		{ // Clicked on an empty tile
			
			ClientEngine.network.send(Message.MOUSEEVENTTOSERVERONTILE, mouseEventCoords);	
			
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