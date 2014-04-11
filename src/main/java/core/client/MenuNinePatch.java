package core.client;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;

import core.client.ClientEngine;

public class MenuNinePatch extends NinePatch {
    private static MenuNinePatch instance;
    
    MenuNinePatch(Texture t)
    {
    	
    	super(t, 8, 8, 8, 8);
    	instance = this;
    }
    
    public void init(Texture t)
    {
    	
    	instance = new MenuNinePatch(t);
    }
    
    public static MenuNinePatch getInstance()
    {
    	if(instance == null)
    	{
    		instance = new MenuNinePatch(ClientEngine.gameTextureManager.get(ClientEngine.Textures
    				.get("dialogueNinePatchTexture")));
    	}
    	
            return instance;
    }
}