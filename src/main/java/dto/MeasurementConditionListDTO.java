package dto;

import lombok.Data;

import java.util.List;

@Data
public class MeasurementConditionListDTO {
    private List<ConditionParser> conditionParserList;
}
