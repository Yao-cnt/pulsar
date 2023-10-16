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
        TriggeredAsset triggeredAsset = new TriggeredAsset();
        List<TriggeredMeasurement> triggeredMeasurements = new ArrayList<>();

        AssetConditionDTO assetConditionDTO = mapper.readValue(input, AssetConditionDTO.class);
        triggeredAsset.setAssetId(assetConditionDTO.getAssetId());
        triggeredAsset.setLatitude(assetConditionDTO.getLatitude());
        triggeredAsset.setLongitude(assetConditionDTO.getLongitude());

        assetConditionDTO.getConditionParserList().forEach(conditionParser -> {
            TriggeredMeasurement triggeredMeasurement = new TriggeredMeasurement();
            triggeredMeasurement.setMeasurementName(conditionParser.getMeasurementName());
            triggeredMeasurement.setMeasurementValue(conditionParser.getMeasurementValue());
            List<AlarmConditionDTO> alarmConditionDtoList = conditionParser.getAlarmConditionDtoList();
            List<TriggeredAlarmCondition> triggeredAlarmConditions = new ArrayList<>();
            alarmConditionDtoList.forEach(alarmConditionDTO -> {
                TriggeredAlarmCondition triggeredAlarmCondition = new TriggeredAlarmCondition();
                boolean trigger = EvaluateAlarmCondition.evaluateCondition(
                        conditionParser,
                        alarmConditionDTO.getAlarmCondition(),
                        conditionParser.getAssetMeasurementExtendedDtoList(),
                        assetConditionDTO.getAssetId());
                triggeredAlarmCondition.setConditionId(alarmConditionDTO.getConditionId());
                triggeredAlarmCondition.setCondition(alarmConditionDTO.getAlarmCondition());
                triggeredAlarmCondition.setSeverity(alarmConditionDTO.getSeverity());
                triggeredAlarmCondition.setAlarmId(alarmConditionDTO.getAlarmId());
                triggeredAlarmCondition.setAlarmName(alarmConditionDTO.getAlarmName());
                triggeredAlarmCondition.setDiscrete(conditionParser.isDiscrete());
                triggeredAlarmCondition.setTriggered(trigger);
                triggeredAlarmConditions.add(triggeredAlarmCondition);
            });
            triggeredMeasurement.setTriggeredAlarmConditions(triggeredAlarmConditions);
            triggeredMeasurements.add(triggeredMeasurement);
        });
        triggeredAsset.setTriggeredMeasurements(triggeredMeasurements);
        return mapper.writeValueAsString(triggeredAsset);
    }
}
