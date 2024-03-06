package org.example.searadar.mr2313.convert;

import org.example.searadar.mr2313.station.Mr231_3StationType;
import org.junit.Before;
import org.junit.Test;
import ru.oogis.searadar.api.message.InvalidMessage;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Test class for testing proper functionality of {@link Mr231_3Converter} class.
 */
public class Mr231_3ConverterTest {

    Mr231_3Converter converter;
    String ttm = "$RATTM,66,28.71,341.1,T,57.6,024.5,T,0.4,4.1,N,b,L,,457362,A*42";
    String rsd1 = "$RARSD,14.0,0.0,96.9,306.4,,,,,97.7,11.6,0.3,K,N,S*20";
    String rsd2 = "$RARSD,36.5,331.4,8.4,320.6,,,,,11.6,185.3,96.0,N,N,S*33";

    /**
     * Initializes an instance of {@link Mr231_3Converter} class for consecutive testing.
     */
    @Before
    public void initializeConverter(){
        Mr231_3StationType station = new Mr231_3StationType();
        converter = station.createConverter();
    }

    /**
     * Tests that converter correctly resolves subclasses of {@link SearadarStationMessage} class.
     */
    @Test
    public void messageTypeParsingTest() {
        List<SearadarStationMessage> messages = new ArrayList<>();

        messages.addAll(converter.convert(ttm));
        messages.addAll(converter.convert(rsd1));
        messages.addAll(converter.convert(rsd2));

        assertEquals(TrackedTargetMessage.class, messages.get(0).getClass());
        assertEquals(InvalidMessage.class, messages.get(1).getClass());
        assertEquals(RadarSystemDataMessage.class, messages.get(2).getClass());
    }

    /**
     * Tests correctness of message conversion to {@link TrackedTargetMessage} class.
     */
    @Test
    public void ttmConversionTest(){
        TrackedTargetMessage ttmMessage = (TrackedTargetMessage) converter.convert(ttm).get(0);
        assertEquals((int) 66, (int)  ttmMessage.getTargetNumber());
        assertEquals(28.71, ttmMessage.getDistance());
        assertEquals(341.1, ttmMessage.getBearing());
        assertEquals(57.6, ttmMessage.getSpeed());
        assertEquals(024.5, ttmMessage.getCourse());
        assertEquals(TargetStatus.LOST, ttmMessage.getStatus());
        assertEquals(IFF.FRIEND, ttmMessage.getIff());
        assertEquals(TargetType.UNKNOWN, ttmMessage.getType());
        assertTrue(ttmMessage.getMsgTime() < System.currentTimeMillis());
    }

    /**
     * Tests correctness of message conversion to {@link RadarSystemDataMessage} class.
     */
    @Test
    public void rsdConversionTest(){
        RadarSystemDataMessage rsdMessage = (RadarSystemDataMessage) converter.convert(rsd2).get(0);
        assertEquals(36.5, rsdMessage.getInitialDistance());
        assertEquals(331.4, rsdMessage.getInitialBearing());
        assertEquals(8.4, rsdMessage.getMovingCircleOfDistance());
        assertEquals(320.6, rsdMessage.getBearing());
        assertEquals(11.6, rsdMessage.getDistanceFromShip());
        assertEquals(185.3, rsdMessage.getBearing2());
        assertEquals(96.0, rsdMessage.getDistanceScale());
        assertEquals("N", rsdMessage.getDistanceUnit());
        assertEquals("N", rsdMessage.getDisplayOrientation());
        assertEquals("S", rsdMessage.getWorkingMode());
    }
}