package core.client;

import java.util.BitSet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class PixmapTextureAtlas implements Disposable {

	private TextureAtlas textureAtlas;
	private Pixmap textureAtlasPixmap;

	boolean shouldDispose = false;

	public PixmapTextureAtlas(FileHandle textureAtlasImageFile, FileHandle textureAtlasFile) {
		this(new TextureAtlas(textureAtlasFile), new Pixmap(textureAtlasImageFile));
		this.shouldDispose = true;
	}

	public PixmapTextureAtlas(TextureAtlas textureAtlas, Pixmap textureAtlasPixmap) {
		this.textureAtlas = textureAtlas;
		this.textureAtlasPixmap = textureAtlasPixmap;
	}

	public Pixmap createPixmap(String regionName) {
		AtlasRegion region = textureAtlas.findRegion(regionName);

		int width = MathUtils.nextPowerOfTwo(region.getRegionWidth());
		int height = MathUtils.nextPowerOfTwo(region.getRegionHeight());

		Pixmap regionPixmap = new Pixmap(width, height, textureAtlasPixmap.getFormat());

		int x = (width / 2) - (region.getRegionWidth() / 2);
		int y = (height / 2) - (region.getRegionHeight() / 2);

		regionPixmap.drawPixmap(textureAtlasPixmap, x, y, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());

		return regionPixmap;
	}

	public Array<Pixmap> createPixmaps(String regionName) {
		Array<AtlasRegion> regions = textureAtlas.findRegions(regionName);
		Array<Pixmap> Pixmaps = new Array<Pixmap>();
		for(int i = 0; i < regions.size; i++)
		{
			AtlasRegion region = regions.get(i);
			
			int width = MathUtils.nextPowerOfTwo(region.getRegionWidth());
			int height = MathUtils.nextPowerOfTwo(region.getRegionHeight());
	
			Pixmap regionPixmap = new Pixmap(width, height, textureAtlasPixmap.getFormat());
	
			int x = (width / 2) - (region.getRegionWidth() / 2);
			int y = (height / 2) - (region.getRegionHeight() / 2);
	
			regionPixmap.drawPixmap(textureAtlasPixmap, x, y, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());
		
			Pixmaps.add(regionPixmap);
		}
		
		
		return Pixmaps;
	}
	
	public Array<BitSet> createTransparencies (String regionName)
	{
		Array<Pixmap> Pixmaps = createPixmaps(regionName);
		Array<BitSet> transparencies = new Array<BitSet>();
		
		
		for(int i = 0; i < Pixmaps.size; i++)
		{
			BitSet transparency = new BitSet();
			Array<Boolean> test = new Array<Boolean>();
			for(int x = 0; x < Pixmaps.get(i).getWidth(); x++)
			{
				
				for(int y = 0; y < Pixmaps.get(i).getHeight(); y++)
				{
					int position = y * Pixmaps.get(i).getHeight() + x;
					
					int p = Pixmaps.get(i).getPixel(x, y);
					
					if(( p & 0x000000ff) != 0 )
					{
						test.add(false);
						transparency.clear(position);
					
					}
					else
					{
						
						
						test.add(true);
						transparency.set(position);
						
					}
					
					
				}
				
			}
			
			transparencies.add(transparency);
			
		}
		
		return transparencies;
		
		
		
	}
	
	
	@Override
	public void dispose() {
		if (shouldDispose) {
			textureAtlas.dispose();
			textureAtlasPixmap.dispose();
		}
	}

}

