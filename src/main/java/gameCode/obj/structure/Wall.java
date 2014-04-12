package gameCode.obj.structure;

import gameCode.obj.item.Item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.shared.ConfigOptions;

public class Wall extends Structure
{

	public Wall(int x, int y)
	{
		super(x, y);
		
		
		
		name = "Wall";
	}
	
	protected TextureRegion texture = ConfigOptions.texture2;
	
	
	@Override
	public TextureRegion getTexture()
	{
		
		return texture;
		
	}
	
	@Override
	public void onClick(Item attackedBy)
	{
		
		this.move(1, 0);
		
	}


}
