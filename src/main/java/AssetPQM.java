import com.fasterxml.jackson.databind.ObjectMapper;
import dto.AlarmConditionDTO;
import dto.AssetMeasurementExtendedDto;
import dto.ConditionParser;
import dto.MeasurementConditionListDTO;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssetPQM implements Function<String, String> {
    @Override
    public String process(String input, Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String assetId = context.getFunctionName();
        StringBuilder asset = new StringBuilder("Asset Id: " + assetId).append("\n");
        MeasurementConditionListDTO measurementConditionListDTO = mapper.readValue(input, MeasurementConditionListDTO.class);

        List<ConditionParser> conditionParserList = measurementConditionListDTO.getConditionParserList();
        conditionParserList.forEach(conditionParser -> {
            StringBuilder measurement = new StringBuilder("Measurement Name: ")
                    .append(conditionParser.getMeasurementName()).append("\n")
                    .append("Measurement Value: ").append(conditionParser.getMeasurementValue()).append("\n");
            List<AlarmConditionDTO> alarmConditionDtoList = conditionParser.getAlarmConditionDtoList();
            alarmConditionDtoList.forEach(alarmConditionDTO -> {
                //evaluateCondition(conditionParser, alarmConditionDTO.getAlarmCondition(), conditionParser.getAssetMeasurementExtendedDtoList());
                measurement.append("Alarm Condition: ").append(alarmConditionDTO.getAlarmCondition())
                        .append("If Discrete: ").append(conditionParser.isDiscrete()).append("\n");
            });
            asset.append(measurement);
        });

        return asset.toString();
    }

    public boolean evaluateCondition(ConditionParser conditionParser,
                                     String alarmConditionText,
                                     List<AssetMeasurementExtendedDto> extendedMeasurements) {
        Map<String,String> tokens =  this.createParserTokens(conditionParser, alarmConditionText, extendedMeasurements);
        StringBuffer conditionText = new StringBuffer(alarmConditionText);
        if (conditionText.indexOf(" Between ")>-1) {
            conditionText = conditionText.replace( conditionText.indexOf("and"), conditionText.indexOf("and") + 3," and " + conditionParser.getMeasurementName() + " < ");
        }
        //ExpressionParser parser = new SpelExpressionParser();
        //return evaluateExpression(tokens, conditionText.toString());
        return true;
    }

    /*private static boolean evaluateExpression(Map<String, String> tokens, String conditionText){
        ExpressionParser parser = new SpelExpressionParser();
        boolean raiseAlarm;
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

        //Evaluate  the expression.
        Expression exp = parser.parseExpression(sbContextText.toString());
        raiseAlarm = exp.getValue(Boolean.class);
        return raiseAlarm;
    }*/

    private Map<String,String> createParserTokens(ConditionParser conditionParser,
                                                  String alarmConditionText,
                                                  List<AssetMeasurementExtendedDto> extendedMeasurements){
        Map<String, String> tokens = new HashMap<>();
        tokens.put(conditionParser.getMeasurementName(), String.valueOf(conditionParser.getMeasurementValue()));

        String conditionText = alarmConditionText;
        if (conditionText.contains(" Between ")) {
            tokens.put("Between", " > ");
        } else {
            //Use alarm measurement extended logic to fetch for digital data.
            if (conditionParser.isDiscrete()) {
                tokens.put("Is", "==");
                if (!CollectionUtils.isEmpty(extendedMeasurements)) {
                    extendedMeasurements.forEach(extendedMeasurement -> {
                        tokens.put(extendedMeasurement.getInterpreteMap(), extendedMeasurement.getValue().toString());
                    });
                }
            }
        }
        return tokens;
    }
}
