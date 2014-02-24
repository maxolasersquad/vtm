package org.oscim.layers.tile.vector;

import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.layers.tile.TileLoader;
import org.oscim.layers.tile.TileManager;
import org.oscim.map.Map;

public class OsmTileLayer extends VectorTileLayer {

	protected final static int MAX_ZOOMLEVEL = 17;
	protected final static int MIN_ZOOMLEVEL = 2;
	protected final static int CACHE_LIMIT = 150;

	public OsmTileLayer(Map map) {
		super(map, MIN_ZOOMLEVEL, MAX_ZOOMLEVEL, CACHE_LIMIT);
	}

	@Override
	protected TileLoader createLoader(TileManager tm) {
		return new OsmTileLoader(tm);
	}

	static class OsmTileLoader extends VectorTileLoader {

		public OsmTileLoader(TileManager tileManager) {
			super(tileManager);
		}

		/* Replace tags that should only be matched by key in RenderTheme
		 * to avoid caching RenderInstructions for each way of the same type
		 * only with different name.
		 * Maybe this should be done within RenderTheme, also allowing
		 * to set these replacement rules in theme file. */
		private static final TagReplacement[] mTagReplacement = {
		        new TagReplacement(Tag.KEY_NAME),
		        new TagReplacement(Tag.KEY_HOUSE_NUMBER),
		        new TagReplacement(Tag.KEY_REF),
		        new TagReplacement(Tag.KEY_HEIGHT),
		        new TagReplacement(Tag.KEY_MIN_HEIGHT)
		};

		protected boolean filterTags(TagSet tagSet) {
			Tag[] tags = tagSet.tags;

			mFilteredTags.clear();

			O: for (int i = 0, n = tagSet.numTags; i < n; i++) {
				Tag t = tags[i];

				for (TagReplacement replacement : mTagReplacement) {
					if (t.key == replacement.key) {
						mFilteredTags.add(replacement.tag);
						continue O;
					}
				}

				mFilteredTags.add(t);
			}

			return true;
		}
	}
}