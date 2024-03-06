package org.example.searadar.mr2313.station;

import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.example.searadar.mr2313.convert.Mr231_3Converter;

import java.nio.charset.Charset;

/**
 * Station Type Class for MR-231-3 protocol parsing.
 * Used for obtaining MR-231-3 Protocol Converter Object that is, in its turn, used for parsing
 * and converting MR-231-3 messages to objects of SeaRadarMessage Class.
 */

public class Mr231_3StationType {

    private static final String STATION_TYPE = "МР-231-3";
    private static final String CODEC_NAME = "mr231-3";

    /**
     * Initializes textLineCodecFactory.
     */

    protected void doInitialize() {
        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(
                Charset.defaultCharset(),
                LineDelimiter.UNIX,
                LineDelimiter.CRLF
        );

    }


    /**
     * Returns a new instance of Mr231_3Converter Class.
     */

    public Mr231_3Converter createConverter() {
        return new Mr231_3Converter();
    }
}
