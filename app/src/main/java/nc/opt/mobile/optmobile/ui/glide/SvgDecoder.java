package nc.opt.mobile.optmobile.ui.glide;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 2761oli on 27/12/2017.
 */

public class SvgDecoder implements ResourceDecoder<InputStream, SVG> {
    @Override
    public boolean handles(InputStream source, Options options) throws IOException {
        return true;
    }

    public Resource<SVG> decode(InputStream source, int width, int height, Options options)
            throws IOException {
        try {
            SVG svg = SVG.getFromInputStream(source);
            return new SimpleResource<>(svg);
        } catch (SVGParseException ex) {
            throw new IOException("Cannot load SVG from stream", ex);
        }
    }
}
