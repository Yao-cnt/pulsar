package dto;

import lombok.Data;

@Data
public class TriggeredAlarmCondition {
    private Integer conditionId;
    private String condition;
    private String severity;
    private Integer alarmId;
    private String alarmName;
    private boolean isDiscrete;
    private boolean isTriggered;

    @Override
    public String toString() {
        return "TriggeredAlarmCondition{" +
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
