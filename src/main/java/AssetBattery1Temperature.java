import com.fasterxml.jackson.databind.ObjectMapper;
import dto.AssetMeasurementListDTO;
import dto.MeasurementDTO;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

import java.util.Objects;

public class AssetBattery1Temperature implements Function<String, String> {
    @Override
    public String process(String input, Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String assetId = context.getFunctionName();
        AssetMeasurementListDTO assetMeasurementList = mapper.readValue(input, AssetMeasurementListDTO.class);
        MeasurementDTO temperature = assetMeasurementList.getMeasurementList().stream()
                .filter(m -> m.getMeasurementName().equalsIgnoreCase("temperature"))
                .findFirst().orElse(null);

        if (Objects.isNull(temperature)) {
            return context.newOutputMessage("Log", Schema.STRING)
                    .value("No current found for " + assetId)
                    .toString();
        }

        if (temperature.getMeasurementValue() < 75 && temperature.getMeasurementValue() > 25) {
            context.newOutputMessage("Log", Schema.STRING)
                    .value(assetId + " is good now")
                    .send();
            return null;
        } else {
            return "Warning!!!!! " + assetId + " has exceptional value which now is " + temperature.getMeasurementValue();
        }
    }
}
