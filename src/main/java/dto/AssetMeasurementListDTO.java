package dto;

import lombok.Data;

import java.util.List;

@Data
public class AssetMeasurementListDTO {
    private List<MeasurementDTO> measurementList;
}
