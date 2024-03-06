package org.example.searadar.mr2313.station;

import org.example.searadar.mr2313.convert.Mr231_3Converter;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


/**
 * Test class for testing proper functionality of {@link Mr231_3StationType} class.
 */

public class Mr231_3StationTypeTest {

    /**
     * Tests creation of {@link Mr231_3Converter} by {@link Mr231_3StationType#createConverter() createConveter()} method
     */

    @Test
    public void createConverterTest() {
        Mr231_3StationType station = new Mr231_3StationType();
        assertEquals(Mr231_3Converter.class, station.createConverter().getClass());
    }

}