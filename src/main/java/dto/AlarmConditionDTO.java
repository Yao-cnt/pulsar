package dto;

import lombok.Data;

@Data
public class AlarmConditionDTO {
    private Integer conditionId;
    private boolean enabled;
    private String alarmCondition;
    private String alarmMessage;
    private String severity;
    private boolean autoClear;
    private String autoClearConditionText;
}
