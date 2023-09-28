import dto.AssetMeasurementExtendedDto;
import dto.ConditionParser;
import utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.EvaluateInequality.evaluateInequality;

public class EvaluateAlarmCondition {
    public static boolean evaluateCondition(ConditionParser conditionParser,
                                            String alarmConditionText,
                                            List<AssetMeasurementExtendedDto> extendedMeasurements,
                                            String assetId) {
        Map<String,String> tokens =  createParserTokens(conditionParser, alarmConditionText, extendedMeasurements, assetId);
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

    private static Map<String,String> createParserTokens(ConditionParser conditionParser,
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
