package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConditionParser {
    String conditionText;
    Integer measurementValue;
    String measurementName;
    boolean discrete;

    @Override
    public String toString() {
        return "ConditionParser{" +
                "conditionText='" + conditionText + '\'' +
                ", measurementValue=" + measurementValue + '\'' +
                ", measurementName='" + measurementName + '\'' +
                ", discrete=" + discrete + '\'' +
                '}';
    }
}
