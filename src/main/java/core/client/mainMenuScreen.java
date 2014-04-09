package core.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import core.client.ClientEngine;
import core.shared.Background;
/**
 * Main menu screen. 
 * @author the
 */
public class mainMenuScreen implements Screen{
	
	ClientEngine parentEngine;
	Texture backgroundImage;
	SpriteBatch batch;
	Stage mainMenuStage;
	InputMultiplexer multiplexer;
	
	private OrthographicCamera camera;
	
	
	
	public mainMenuScreen(ClientEngine parent){
		// Sets up links to our parent
		parentEngine = parent;
		batch = ClientEngine.primarySpriteBatch;
		
		
		// Loads the background image
		backgroundImage = ClientEngine.gameTextureManager.get(ClientEngine.Backgrounds.get(Background.MENUSCREEN));
		
		// Sets up the camera
	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    	    
	    // Initializes a stage to hold our buttons
	    mainMenuStage = new Stage();
	    
	    // Sets up an input multiplexer to handle our input to the buttons
	    multiplexer = new InputMultiplexer();
	    multiplexer.addProcessor(mainMenuStage);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
