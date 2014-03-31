package gameCode.obj;

import static core.shared.ConfigOptions.TILE_SIZE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

import core.client.ClientEngine;
import core.shared.ConfigOptions;
import core.shared.Position;

public class Obj extends Actor
{
	protected Obj(int x, int y)
	{
		
		this.setX(x);
		this.setY(y);
		this.setWidth(32);
		this.setHeight(32);
		
	}
	
	public Texture getTexture()
	{
		
		return texture;
		
	}

	protected Texture texture = ConfigOptions.texture;
	
	//public String name = "Undefined Object";
	public String description = "Undefined Description";
	
	public boolean dense = false;
	public boolean opaque = false;
	
	public int UID = getObjUID.getUID();
	
	public Position loc;
	
	
	
	
	
	
	
	
	
	@Override 
    public void draw(Batch batch, float parentAlpha) 
    {
    	Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		
		Vector3 test = new Vector3(getX()*TILE_SIZE,getY()*TILE_SIZE,1);
		ClientEngine.camera.project(test);
		
		batch.draw(getTexture(), test.x, test.y, getOriginX(), getOriginY(), 64 , 64 , getScaleX(), getScaleY(), getRotation(), 0, 0, texture.getWidth(), texture.getHeight(), false,  false);
		
    }

	
}
