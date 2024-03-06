package org.example.searadar.mr2313.convert;

import org.apache.camel.Exchange;
import ru.oogis.searadar.api.convert.SearadarExchangeConverter;
import ru.oogis.searadar.api.message.*;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Converter Class for MR-231-3 Protocol Parsing.
 */
public class Mr231_3Converter implements SearadarExchangeConverter {
    private static final Double[] DISTANCE_SCALE = {0.125, 0.25, 0.5, 1.5, 3.0, 6.0, 12.0, 24.0, 48.0, 96.0};

    private String[] fields;
    private String msgType;

    /**
     * Converts MR-231-3 message in form of {@link Exchange} class object as required by {@link SearadarStationMessage} interface.
     * @param exchange Exchange class object containing MR-231-3 style message.
     * @return list containing a single instance of SearadarStationMessage that was acquired
     * by converting initial Exchange-format message.
     */
    @Override
    public List<SearadarStationMessage> convert(Exchange exchange) {
        return convert(exchange.getIn().getBody(String.class));
    }

    /**
     *
     * Converts MR-231-3 messages represented by String objects into a list containing single object
     * of {@link SearadarStationMessage} class. In accordance with MR-231-3 Protocol these messages can be of
     * 2 types each represented by its own class that extends SearadarStationMessage class if correct
     * and of {@link InvalidMessage} class if incorrect.
     * @param message MR-231-3 message represented by String object or literal.
     * @return list containing a single instance of SearadarStationMessage that was acquired
     * by converting initial the message.
     */
    public List<SearadarStationMessage> convert(String message) {

        List<SearadarStationMessage> msgList = new ArrayList<>();

        readFields(message);

        switch (msgType) {

            case "TTM" :
                msgList.add(getTTM());
                break;

            case "RSD" : {

                RadarSystemDataMessage rsd = getRSD();
                InvalidMessage invalidMessage = checkRSD(rsd);

                if (invalidMessage != null)  msgList.add(invalidMessage);
                else msgList.add(rsd);
                break;
            }

        }

        return msgList;
    }

    /**
     * Initializes private member variables {@link #fields} and {@link #msgType} with data from message.
     * @param msg String representation of MR-231-3 protocol message.
     */
    private void readFields(String msg) {

        String temp = msg.substring( 3, msg.indexOf("*") ).trim();

        fields = temp.split(Pattern.quote(","));
        msgType = fields[0];

    }

    /**
     * Constructs a {@link TrackedTargetMessage} object based on message data if its type is TTM.
     * @return ttm - instance of TrackedTargetMessage constructed according to
     * message data stored in {@link #fields}.
     */
    private TrackedTargetMessage getTTM() {

        TrackedTargetMessage ttm = new TrackedTargetMessage();
        Long msgRecTimeMillis = System.currentTimeMillis();

        ttm.setMsgTime(msgRecTimeMillis);
        TargetStatus status = TargetStatus.UNRELIABLE_DATA;
        IFF iff = IFF.UNKNOWN;
        TargetType type = TargetType.UNKNOWN;

        switch (fields[12]) {
            case "L" : status = TargetStatus.LOST;
                break;

            case "Q" : status = TargetStatus.UNRELIABLE_DATA;
                break;

            case "T" : status = TargetStatus.TRACKED;
                break;
        }

        switch (fields[11]) {
            case "b" : iff = IFF.FRIEND;
                break;

            case "p" : iff = IFF.FOE;
                break;

            case "d" : iff = IFF.UNKNOWN;
                break;
        }

        ttm.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        ttm.setTargetNumber(Integer.parseInt(fields[1]));
        ttm.setDistance(Double.parseDouble(fields[2]));
        ttm.setBearing(Double.parseDouble(fields[3]));
        ttm.setSpeed(Double.parseDouble(fields[5]));
        ttm.setCourse(Double.parseDouble(fields[6]));
        ttm.setStatus(status);
        ttm.setIff(iff);

        ttm.setType(type);

        return ttm;
    }

    /**
     * Constructs a {@link RadarSystemDataMessage} object based on message data if its type is RSD.
     * @return rsd - instance of RadarSystemDataMessage constructed according to
     * message data stored in {@link #fields}.
     */
    private RadarSystemDataMessage getRSD() {

        RadarSystemDataMessage rsd = new RadarSystemDataMessage();

        rsd.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        rsd.setInitialDistance(Double.parseDouble(fields[1]));
        rsd.setInitialBearing(Double.parseDouble(fields[2]));
        rsd.setMovingCircleOfDistance(Double.parseDouble(fields[3]));
        rsd.setBearing(Double.parseDouble(fields[4]));
        rsd.setDistanceFromShip(Double.parseDouble(fields[9]));
        rsd.setBearing2(Double.parseDouble(fields[10]));
        rsd.setDistanceScale(Double.parseDouble(fields[11]));
        rsd.setDistanceUnit(fields[12]);
        rsd.setDisplayOrientation(fields[13]);
        rsd.setWorkingMode(fields[14]);

        return rsd;
    }

    /**
     * Checks if {@link RadarSystemDataMessage} is correct by comparing its distance scale with
     * possible values.
     * @param rsd RadarSystemDataMessage object to be checked.
     * @return null if rsd is correct and instance of {@link InvalidMessage} if it is incorrect.
     */
    private InvalidMessage checkRSD(RadarSystemDataMessage rsd) {

        InvalidMessage invalidMessage = new InvalidMessage();
        String infoMsg = "";

        if (!Arrays.asList(DISTANCE_SCALE).contains(rsd.getDistanceScale())) {

            infoMsg = "RSD message. Wrong distance scale value: " + rsd.getDistanceScale();
            invalidMessage.setInfoMsg(infoMsg);
            return invalidMessage;
        }

        return null;
    }
}
