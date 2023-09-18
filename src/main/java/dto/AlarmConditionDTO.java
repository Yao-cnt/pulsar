package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmConditionDTO {
    private Integer conditionId;
    private boolean enabled;
    private String alarmCondition;
    private Integer alarmId;
}
