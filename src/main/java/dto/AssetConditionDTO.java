package dto;

import lombok.Data;

import java.util.List;

@Data
public class AssetConditionDTO {
    private String assetId;
    private String latitude;
    private String longitude;
    private List<ConditionParser> conditionParserList;
}
