package com.nestedworld.nestedworld.utils.typeface;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Useful class typeface management
 * It make typeface easier to load
 * and it will improve the loading by putting the typeface under the cache
 */
public class FontManager {

    private static FontManager instance = null;
    private final AssetManager mgr;
    private final Map<String, Typeface> fonts;

    /*
    ** Singleton
     */
    public static FontManager getInstance(@NonNull final AssetManager mgr) {
        if (instance == null) {
            init(mgr);
        }
        return instance;
    }

    /*
    ** Constructor
     */
    private FontManager(@NonNull final AssetManager _mgr) {
        mgr = _mgr;
        fonts = new HashMap<>();
    }

    public static void init(@NonNull final AssetManager mgr) {
        instance = new FontManager(mgr);
    }

    /*
    ** Public method
     */
    public Typeface getFont(@NonNull final String asset) {
        if (fonts.containsKey(asset))
            return fonts.get(asset);

        Typeface font;

        try {
            font = Typeface.createFromAsset(mgr, asset);
            fonts.put(asset, font);
        } catch (Exception ignored) {
            return null;
        }

        if (font == null) {
            try {
                String fixedAsset = fixAssetFilename(asset);
                font = Typeface.createFromAsset(mgr, fixedAsset);
                fonts.put(asset, font);
                fonts.put(fixedAsset, font);
            } catch (Exception ignored) {

            }
        }
        return font;
    }

    /*
    ** Private method
     */
    private String fixAssetFilename(@NonNull final String asset) {
        // Empty font filename?
        // Just return it. We can't help.
        if (TextUtils.isEmpty(asset))
            return asset;

        // Make sure that the font ends in '.ttf' or '.ttc'
        if ((!asset.endsWith(".ttf")) && (!asset.endsWith(".ttc")))
            return (String.format("%s.ttf", asset));

        return asset;
    }
}