package dto;

import lombok.Data;

@Data
public class AssetMeasurementExtendedDto {

    private Integer id;

    private Integer measurement;

    private Double value;

    private String interpreteMap;

    private String measurementName;

    private String measurementDisplayName;
}
