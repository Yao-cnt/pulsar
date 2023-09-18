package dto;

import lombok.Data;

@Data
public class MeasurementDTO {
    private String measurementName;
    private Integer measurementValue;
    private Boolean isDiscrete;

    @Override
    public String toString() {
        return "MeasurementDTO{" +
                "measurementName='" + measurementName + '\'' +
                ", measurementValue=" + measurementValue + '\'' +
                ", isDiscrete=" + isDiscrete + '\'' +
                '}';
    }
}
