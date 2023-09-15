import com.fasterxml.jackson.databind.ObjectMapper;
import dto.AssetMeasurementListDTO;
import dto.MeasurementDTO;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

import java.util.Objects;

public class AssetBattery1TempAndVol implements Function<String, String> {
    @Override
    public String process(String input, Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String assetId = context.getFunctionName();
        AssetMeasurementListDTO assetMeasurementList = mapper.readValue(input, AssetMeasurementListDTO.class);
        MeasurementDTO temperature = assetMeasurementList.getMeasurementList().stream()
                .filter(measurement -> measurement.getMeasurementName().equalsIgnoreCase("temperature"))
                .findFirst().orElse(null);

        MeasurementDTO voltage = assetMeasurementList.getMeasurementList().stream()
                .filter(measurement -> measurement.getMeasurementName().equalsIgnoreCase("voltage"))
                .findFirst().orElse(null);

        if (Objects.isNull(temperature) || Objects.isNull(voltage)) {
            context.newOutputMessage("Log", Schema.STRING)
                    .value("No temperature or voltage found for " + assetId)
                    .send();
            return null;
        }

        if (temperature.getMeasurementValue() < 75 && temperature.getMeasurementValue() > 25
                && voltage.getMeasurementValue() > 250 && voltage.getMeasurementValue() < 720) {
            context.newOutputMessage("Log", Schema.STRING)
                    .value("Temperature and voltage are good now for " + assetId)
                    .send();
            return null;
        } else {
            return "Warning!!!!! " + assetId + " temperature is " + temperature.getMeasurementValue() + " and voltage is " + voltage.getMeasurementValue();
        }
    }
}
