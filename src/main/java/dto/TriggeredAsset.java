package dto;

import lombok.Data;

import java.util.List;

@Data
public class TriggeredAsset {
    private String assetId;
    private String latitude;
    private String longitude;
    private List<TriggeredMeasurement> triggeredMeasurements;

    @Override
    public String toString() {
        return "TriggeredAsset{" +
                "assetId='" + assetId + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", measurements=" + triggeredMeasurements + '\'' +
                '}';
    }
}
