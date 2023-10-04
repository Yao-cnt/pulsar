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

        ConditionParserList conditionParserList = mapper.readValue(input, ConditionParserList.class);
        assetCondition.setAssetId(conditionParserList.getAssetId());
        assetCondition.setLatitude(conditionParserList.getLatitude());
        assetCondition.setLongitude(conditionParserList.getLongitude());

        conditionParserList.getConditionParserList().forEach(conditionParser -> {
            Measurement measurement = new Measurement();
            measurement.setMeasurementName(conditionParser.getMeasurementName());
            measurement.setMeasurementValue(conditionParser.getMeasurementValue());
            List<AlarmConditionDTO> alarmConditionDtoList = conditionParser.getAlarmConditionDtoList();
            List<AlarmCondition> alarmConditions = new ArrayList<>();
            alarmConditionDtoList.forEach(alarmConditionDTO -> {
                AlarmCondition alarmCondition = new AlarmCondition();
                boolean trigger = EvaluateAlarmCondition.evaluateCondition(
                        conditionParser,
                        alarmConditionDTO.getAlarmCondition(),
                        conditionParser.getAssetMeasurementExtendedDtoList(),
                        conditionParserList.getAssetId());
                alarmCondition.setCondition(alarmConditionDTO.getAlarmCondition());
                alarmCondition.setSeverity(alarmConditionDTO.getSeverity());
                alarmCondition.setAlarmId(alarmConditionDTO.getAlarmId());
                alarmCondition.setAlarmName(alarmConditionDTO.getAlarmName());
                alarmCondition.setDiscrete(conditionParser.isDiscrete());
                alarmCondition.setTriggered(trigger);
                alarmConditions.add(alarmCondition);
            });
            measurement.setAlarmConditions(alarmConditions);
            measurements.add(measurement);
        });
        assetCondition.setMeasurements(measurements);
        return mapper.writeValueAsString(assetCondition);
    }
}
