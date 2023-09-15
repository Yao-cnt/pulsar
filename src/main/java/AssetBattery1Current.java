import com.fasterxml.jackson.databind.ObjectMapper;
import dto.AssetMeasurementListDTO;
import dto.MeasurementDTO;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

import java.util.Objects;

public class AssetBattery1Current implements Function<String, String> {
    @Override
    public String process(String input, Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String assetId = context.getFunctionName();
        AssetMeasurementListDTO assetMeasurementList = mapper.readValue(input, AssetMeasurementListDTO.class);
        MeasurementDTO current = assetMeasurementList.getMeasurementList().stream()
                .filter(m -> m.getMeasurementName().equalsIgnoreCase("current"))
                .findFirst().orElse(null);

        if (Objects.isNull(current)) {
            return context.newOutputMessage("Log", Schema.STRING)
                    .value("No current found for " + assetId)
                    .toString();
        }

        if (current.getMeasurementValue() < 380 && current.getMeasurementValue() > 72) {
            context.newOutputMessage("Log", Schema.STRING)
                    .value(assetId + " is good now")
                    .send();
            return null;
        } else {
            return "Warning!!!!! " + assetId + " has exceptional value which now is " + current.getMeasurementValue();
        }
    }
}
