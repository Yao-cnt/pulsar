import com.fasterxml.jackson.databind.ObjectMapper;
import dto.*;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

import java.util.ArrayList;
import java.util.List;

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
                boolean trigger = EvaluateAlarmCondition.evaluateCondition(conditionParser, alarmConditionDTO.getAlarmCondition(), conditionParser.getAssetMeasurementExtendedDtoList(), assetId);
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
}
