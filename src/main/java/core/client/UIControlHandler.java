package core.client;

import org.lwjgl.Sys;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;



public class UIControlHandler implements InputProcessor {

	ClientEngine centralEngine;
	
	public UIControlHandler(ClientEngine s)
	{
		
		centralEngine = s;
		
	}
	
	@Override
	public boolean keyDown(int keycode) 
	{

		if(keycode == Keys.UP)
		{       
			centralEngine.MoveCameraRelative(0, 1);
		}
		if(keycode == Keys.DOWN)
		{       
			centralEngine.MoveCameraRelative(0, -1);
		}
		if(keycode == Keys.LEFT)
		{       
			centralEngine.MoveCameraRelative(-1, 0);
		}
		if(keycode == Keys.RIGHT)
		{       
			centralEngine.MoveCameraRelative(1, 0);
		}
	
		return false;
	
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
//		Vector3 position = new Vector3(screenX, screenY, 1);
//		
//		
//		
//		centralEngine.camera.unproject(position);
//
//		centralEngine.mouseEvent((int)(position.x/core.shared.ConfigOptions.TILE_SIZE) , (int)(position.y/core.shared.ConfigOptions.TILE_SIZE));
		
		return false;
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