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
        MeasurementConditionListDTO measurementConditionListDTO = mapper.readValue(input, MeasurementConditionListDTO.class);

        List<ConditionParser> conditionParserList = measurementConditionListDTO.getConditionParserList();
        conditionParserList.forEach(conditionParser -> {
            Measurement measurement1 = new Measurement();
            measurement1.setMeasurementName(conditionParser.getMeasurementName());
            measurement1.setMeasurementValue(conditionParser.getMeasurementValue());
            List<AlarmConditionDTO> alarmConditionDtoList = conditionParser.getAlarmConditionDtoList();
            List<AlarmCondition> alarmConditions = new ArrayList<>();
            alarmConditionDtoList.forEach(alarmConditionDTO -> {
                AlarmCondition alarmCondition = new AlarmCondition();
                boolean trigger = EvaluateAlarmCondition.evaluateCondition(conditionParser, alarmConditionDTO.getAlarmCondition(), conditionParser.getAssetMeasurementExtendedDtoList(), assetId);
                alarmCondition.setCondition(alarmConditionDTO.getAlarmCondition());
                alarmCondition.setDiscrete(conditionParser.isDiscrete());
                alarmCondition.setTriggered(trigger);
                alarmConditions.add(alarmCondition);
            });
            measurement1.setAlarmConditions(alarmConditions);
            measurements.add(measurement1);
        });
        assetCondition.setMeasurements(measurements);
        return mapper.writeValueAsString(assetCondition);
    }
}
