package dto;

import lombok.Data;

@Data
public class AlarmCondition {
    private String condition;
    private String severity;
    private boolean isDiscrete;
    private boolean isTriggered;

    @Override
    public String toString() {
        return "AlarmCondition{" +
                "condition='" + condition + '\'' +
                ", severity='" + severity + '\'' +
                ", isDiscrete=" + isDiscrete + '\'' +
                ", isTriggered=" + isTriggered + '\'' +
                '}';
    }
}
