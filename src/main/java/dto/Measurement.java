package dto;

import lombok.Data;

import java.util.List;

@Data
public class Measurement {
    private String measurementName;
    private Integer measurementValue;
    private List<AlarmCondition> alarmConditions;

    @Override
    public String toString() {
        return "Measurement{" +
                "measurementName='" + measurementName + '\'' +
                ", measurementValue=" + measurementValue + '\'' +
                ", alarmConditions=" + alarmConditions + '\'' +
                '}';
    }
}
