import com.fasterxml.jackson.databind.ObjectMapper;
import dto.*;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.EvaluateInequality.evaluateInequality;

public class AssetPQM implements Function<String, String> {

    @Override
    public String process(String input, Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AssetCondition assetCondition = new AssetCondition();
        List<Measurement> measurements = new ArrayList<>();

        String assetId = context.getFunctionName();
        assetCondition.setAssetId(assetId);
        StringBuilder asset = new StringBuilder("Asset Id: " + assetId).append("\n");
        MeasurementConditionListDTO measurementConditionListDTO = mapper.readValue(input, MeasurementConditionListDTO.class);

        List<ConditionParser> conditionParserList = measurementConditionListDTO.getConditionParserList();
        conditionParserList.forEach(conditionParser -> {
            Measurement measurement1 = new Measurement();
            measurement1.setMeasurementName(conditionParser.getMeasurementName());
            measurement1.setMeasurementValue(conditionParser.getMeasurementValue());
            StringBuilder measurement = new StringBuilder("\n")
                    .append("Measurement Name: ").append(conditionParser.getMeasurementName()).append("\n")
                    .append("Measurement Value: ").append(conditionParser.getMeasurementValue()).append("\n");
            List<AlarmConditionDTO> alarmConditionDtoList = conditionParser.getAlarmConditionDtoList();
            List<AlarmCondition> alarmConditions = new ArrayList<>();
            alarmConditionDtoList.forEach(alarmConditionDTO -> {
                AlarmCondition alarmCondition = new AlarmCondition();
                boolean trigger = evaluateCondition(conditionParser, alarmConditionDTO.getAlarmCondition(), conditionParser.getAssetMeasurementExtendedDtoList(), assetId);
                alarmCondition.setCondition(alarmConditionDTO.getAlarmCondition());
                alarmCondition.setDiscrete(conditionParser.isDiscrete());
                alarmCondition.setTriggered(trigger);
                alarmConditions.add(alarmCondition);
                measurement.append("Alarm Condition: ").append(alarmConditionDTO.getAlarmCondition()).append("\n")
                        .append("If Discrete: ").append(conditionParser.isDiscrete()).append("\n")
                        .append("If Triggered: ").append(trigger).append("\n");
            });
            measurement1.setAlarmConditions(alarmConditions);
            asset.append(measurement);
            measurements.add(measurement1);
        });
        assetCondition.setMeasurements(measurements);
        return mapper.writeValueAsString(assetCondition);

        //return asset.toString();
    }

    //TODO: evaluate condition need to move to a separate class
    public boolean evaluateCondition(ConditionParser conditionParser,
                                     String alarmConditionText,
                                     List<AssetMeasurementExtendedDto> extendedMeasurements,
                                     String assetId) {
        Map<String,String> tokens =  this.createParserTokens(conditionParser, alarmConditionText, extendedMeasurements, assetId);
        StringBuffer conditionText = new StringBuffer(alarmConditionText);
        if (conditionText.indexOf(" Between ")>-1) {
            conditionText = conditionText.replace(conditionText.indexOf("and"), conditionText.indexOf("and") + 3,"and " + assetId + "." + conditionParser.getMeasurementName() + " <");
        }
        return evaluateExpression(tokens, conditionText.toString());
    }

    private static boolean evaluateExpression(Map<String, String> tokens, String conditionText) {
        String patternString = StringUtils.join(tokens.keySet(),"|");
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(conditionText);

        StringBuffer sbContextText = new StringBuffer();
        while (matcher.find()) {
            String repString = tokens.get(matcher.group(0));
            if (repString != null)
                matcher = matcher.appendReplacement(sbContextText,repString);
        }
        matcher.appendTail(sbContextText);

        /* Evaluate  the expression. */
        if (sbContextText.indexOf(" and ")>-1) {
            String[] inequalitiesArray = sbContextText.toString().split(" and ");
            boolean raiseAlarm1 = evaluateInequality(inequalitiesArray[0]);
            boolean raiseAlarm2 = evaluateInequality(inequalitiesArray[1]);
            return raiseAlarm1 && raiseAlarm2;
        } else {
            return evaluateInequality(sbContextText.toString());
        }
    }

    private Map<String,String> createParserTokens(ConditionParser conditionParser,
                                                  String alarmConditionText,
                                                  List<AssetMeasurementExtendedDto> extendedMeasurements,
                                                  String assetId){
        Map<String, String> tokens = new HashMap<>();
        tokens.put(assetId + "." + conditionParser.getMeasurementName(), String.valueOf(conditionParser.getMeasurementValue()));

        if (alarmConditionText.contains(" Between ")) {
            tokens.put("Between", ">");
        } else {
            //Use alarm measurement extended logic to fetch for digital data.
            if (conditionParser.isDiscrete()) {
                tokens.put("Is", "==");
                if (!extendedMeasurements.isEmpty()) {
                    extendedMeasurements.forEach(extendedMeasurement -> {
                        tokens.put(extendedMeasurement.getInterpreteMap(), extendedMeasurement.getValue().toString());
                    });
                }
            }
        }
        return tokens;
    }
}
