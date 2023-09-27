package dto;

import lombok.Data;

@Data
public class AlarmCondition {
    private String condition;
    private boolean isDiscrete;
    private boolean isTriggered;

    @Override
    public String toString() {
        return "AlarmCondition{" +
                "condition='" + condition + '\'' +
                ", isDiscrete=" + isDiscrete + '\'' +
                ", isTriggered=" + isTriggered + '\'' +
                '}';
    }
}
