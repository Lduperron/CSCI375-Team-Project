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
	    TextButton NewGame = new TextButton("New Game" , parentEngine.buttonStyle);
	    NewGame.setPosition(800,500);
	    mainMenuStage.addActor(NewGame);
	    NewGame.addListener(new ClickListener() {
    		
    		
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) 
    	    {
    	    	parentEngine.screenRender=true;
    	    	Gdx.input.setInputProcessor(parentEngine.multiplexer);
    	    	
    	    };
    		
    	});
	    
	    TextButton Options = new TextButton("Options" , parentEngine.buttonStyle);
	    Options.setPosition(800,450);
	    mainMenuStage.addActor(Options);
	    Options.addListener(new ClickListener() {
    		
    		
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) 
    	    {
    	    	parentEngine.screenRender=false;
    	    	parentEngine.switchToNewScreen(ScreenEnumerations.Settings);
    	    	
    	    };
    		
    	});
	    TextButton Quit = new TextButton("Quit" , parentEngine.buttonStyle);
	    Quit.setPosition(800,400);
	    mainMenuStage.addActor(Quit);
	    Quit.addListener(new ClickListener() {
    		
    		
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) 
    	    {
    	    	Gdx.app.exit();
    	    };
    		
    	});
	}

	@Override
	public void render(float delta) {
		
		Gdx.graphics.getGL20().glClearColor( 0, 0, 0, 1 );
		Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
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
