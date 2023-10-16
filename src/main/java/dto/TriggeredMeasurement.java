package dto;

import lombok.Data;

import java.util.List;

@Data
public class TriggeredMeasurement {
    private String measurementName;
    private Integer measurementValue;
    private List<TriggeredAlarmCondition> triggeredAlarmConditions;

    @Override
    public String toString() {
        return "TriggeredMeasurement{" +
                "measurementName='" + measurementName + '\'' +
                ", measurementValue=" + measurementValue + '\'' +
                ", alarmConditions=" + triggeredAlarmConditions + '\'' +
                '}';
    }
}
