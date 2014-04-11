package core.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import core.client.ClientEngine;
import core.shared.Background;
import core.client.ScreenEnumerations;
/**
 * Main menu screen. 
 * @author the
 */

public class mainMenuScreen implements Screen{
	
	final ClientEngine parentEngine;
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
	    
	 // Creates our buttons
	    TextButton NewGame = new TextButton("Connect" , parentEngine.buttonStyle);
	    mainMenuStage.addActor(NewGame);
	    NewGame.addListener(new ClickListener() {
    		
    		
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) 
    	    {
    	    	parentEngine.switchToNewScreen(ScreenEnumerations.MainMenu);
    	    	
    	    };
    		
    	});
	}

	@Override
	public void render(float delta) {
		
		Gdx.graphics.getGL20().glClearColor( 0, 0, 0, 1 );
		Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		
		//camera = new OrthographicCamera();
		camera.update();
		System.out.println("Test1");
		System.out.println(camera.combined.toString());
		batch.setProjectionMatrix(camera.combined);
		System.out.println("Test2");
		batch.begin();
		batch.disableBlending();
		
		batch.draw(backgroundImage , 0 ,0 , Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.enableBlending();
		batch.end();
		
		mainMenuStage.act();
		mainMenuStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Gdx.input.setInputProcessor(multiplexer);
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		Gdx.input.setInputProcessor(null);
		
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
