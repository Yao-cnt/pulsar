import com.fasterxml.jackson.core.type.TypeReference;
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

        List<ConditionParser> conditionParserList = mapper.readValue(input, new TypeReference<ArrayList<ConditionParser>>() {});
        conditionParserList.forEach(conditionParser -> {
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
                        assetId);
                alarmCondition.setCondition(alarmConditionDTO.getAlarmCondition());
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
