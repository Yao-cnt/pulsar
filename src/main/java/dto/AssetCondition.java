package dto;

import lombok.Data;

import java.util.List;

@Data
public class AssetCondition {
    private String assetId;
    private String latitude;
    private String longitude;
    private List<Measurement> measurements;

    @Override
    public String toString() {
        return "AssetCondition{" +
                "assetId='" + assetId + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", measurements=" + measurements + '\'' +
                '}';
    }
}
