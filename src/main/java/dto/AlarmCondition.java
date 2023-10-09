package dto;

import lombok.Data;

@Data
public class AlarmCondition {
    private Integer conditionId;
    private String condition;
    private String severity;
    private Integer alarmId;
    private String alarmName;
    private boolean isDiscrete;
    private boolean isTriggered;

    @Override
    public String toString() {
        return "AlarmCondition{" +
                "conditionId=" + conditionId + '\'' +
                "condition='" + condition + '\'' +
                ", severity='" + severity + '\'' +
                ", alarmId=" + alarmId + '\'' +
                ", alarmName='" + alarmName + '\'' +
                ", isDiscrete=" + isDiscrete + '\'' +
                ", isTriggered=" + isTriggered + '\'' +
                '}';
    }
}
