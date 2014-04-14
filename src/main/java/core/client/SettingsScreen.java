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

import core.shared.Background;

public class SettingsScreen implements Screen {

	final ClientEngine parentEngine;
	Texture backgroundImage;
	SpriteBatch batch;
	Stage settingsStage;
	InputMultiplexer multiplexer;
	
	private OrthographicCamera camera;
	
	public SettingsScreen(ClientEngine parent){
		
		parentEngine = parent;
		batch = ClientEngine.primarySpriteBatch;
		
		
		// Loads the background image
		backgroundImage = ClientEngine.gameTextureManager.get(ClientEngine.Backgrounds.get(Background.SETTINGS));
		
		// Sets up the camera
	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    	    
	    // Initializes a stage to hold our buttons
	    settingsStage = new Stage();
	    
	    // Sets up an input multiplexer to handle our input to the buttons
	    multiplexer = new InputMultiplexer();
	    multiplexer.addProcessor(settingsStage);
	    
	 // Creates our buttons
	    TextButton Announce = new TextButton("Under constructions!!!!" , parentEngine.buttonStyle);
	    Announce.setPosition(300,500);
	    settingsStage.addActor(Announce);
	    

	    TextButton Back = new TextButton("Back" , parentEngine.buttonStyle);
	    Back.setPosition(300,300);
	    settingsStage.addActor(Back);
	    Back.addListener(new ClickListener() {
    		
    		
    	    @Override
    	    public void clicked(InputEvent event, float x, float y) 
    	    {	
    	    	//parentEngine.screenRender=false;
    	    	parentEngine.switchToNewScreen(ScreenEnumerations.MainMenu);
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
		
		settingsStage.act();
		settingsStage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
		
	}

	@Override
	public void hide() {
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
