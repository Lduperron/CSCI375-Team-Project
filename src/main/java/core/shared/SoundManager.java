package core.shared;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class SoundManager {

	public class SoundPair
	{
		public SoundEffect effect;
		public FileHandle file;
		
		public SoundPair(SoundEffect s, FileHandle f)
		{
			this.effect = s;
			this.file = f;
		}
	}
	
	ArrayList<SoundPair> soundLibrary;
	
	public SoundManager()
	{
		soundLibrary = new ArrayList<SoundPair>();
		
		/*
		 *  List the audio pairs you want included in the game here.
		 */
		soundLibrary.add(new SoundPair(SoundEffect.LASER, Gdx.files.internal("assets/sound/laser.mp3")));
	}
	
	public void playSound(SoundEffect s)
	{
		// find the corresponding sound effect, and play it
		for (SoundPair p : soundLibrary)
		{
			if (p.effect == s)
			{
				Sound sound = Gdx.audio.newSound(p.file);
				sound.play();
			}
		}
	}
	
}
