package dto;

import lombok.Data;

import java.util.List;

@Data
public class ConditionParser {
    List<AlarmConditionDTO> alarmConditionDtoList;
    Integer measurementValue;
    String measurementName;
    boolean discrete;
    List<AssetMeasurementExtendedDto> assetMeasurementExtendedDtoList;

    @Override
    public String toString() {
        return "ConditionParser{" +
                "alarmConditionDtoList=" + alarmConditionDtoList +
                ", measurementValue=" + measurementValue +
                ", measurementName='" + measurementName + '\'' +
                ", discrete=" + discrete +
                ", assetMeasurementExtendedDtoList=" + assetMeasurementExtendedDtoList +
                '}';
    }
}
