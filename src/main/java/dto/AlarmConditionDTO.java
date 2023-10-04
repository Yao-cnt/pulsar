package dto;

import lombok.Data;

@Data
public class AlarmConditionDTO {
    private Integer conditionId;
    private boolean enabled;
    private String alarmCondition;
    private Integer alarmId;
    private String alarmName;
    private String alarmMessage;
    private String severity;
    private boolean autoClear;
    private String autoClearConditionText;
}
