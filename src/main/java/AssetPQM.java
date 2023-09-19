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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        StringBuilder output = new StringBuilder("AssetId: " + assetId + " - ");
        MeasurementConditionListDTO measurementConditionListDTO = mapper.readValue(input, MeasurementConditionListDTO.class);
        measurementConditionListDTO.getConditionParserList().forEach(conditionParser -> {
            output.append(conditionParser.getMeasurementName()).append(": ")
                    .append(conditionParser.getMeasurementValue()).append(" when ")
                    .append(conditionParser.getConditionText()).append(". ")
                    .append(conditionParser.isDiscrete()).append("\n");
        });



        /*try (Connection connection = DataConnection.getConnection()) {
            System.out.println("Connection to database established");
            List<AlarmConditionDTO> alarmConditionList = fetchAlarmCondition(connection);
            for (AlarmConditionDTO alarmConditionDTO : alarmConditionList) {
                System.out.println(alarmConditionDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        return output.toString();
    }

    public boolean evaluateCondition(ConditionParser conditionParser, List<AssetMeasurementExtendedDto> extendedMeasurements) {
        Map<String,String> tokens =  this.createParserTokens(conditionParser, extendedMeasurements);
        StringBuffer conditionText = new StringBuffer(conditionParser.getConditionText());
        if (conditionText.indexOf(" Between ")>-1) {
            conditionText = conditionText.replace( conditionText.indexOf("and"), conditionText.indexOf("and") + 3," and " + conditionParser.getMeasurementName() + " < ");
        }
        return evaluateExpression(tokens, conditionText.toString());
    }

    private static boolean evaluateExpression(Map<String, String> tokens, String conditionText){
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
    }

    private Map<String,String> createParserTokens(ConditionParser conditionParser, List<AssetMeasurementExtendedDto> extendedMeasurements){
        Map<String, String> tokens = new HashMap<String, String>();
        tokens.put(conditionParser.getMeasurementName(), String.valueOf(conditionParser.getMeasurementValue()));

        String conditionText = conditionParser.getConditionText();
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

    private static List<AlarmConditionDTO> fetchAlarmCondition(Connection connection) throws SQLException {
        List<AlarmConditionDTO> alarmConditionDTOList = new ArrayList<>();
        String query = "SELECT * FROM lntds_alarm_conditions";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                alarmConditionDTOList.add(new AlarmConditionDTO(
                        resultSet.getInt("conditionId"),
                        resultSet.getBoolean("enabled"),
                        resultSet.getString("alarmCondition"),
                        resultSet.getInt("alarmId")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alarmConditionDTOList;
    }
}
