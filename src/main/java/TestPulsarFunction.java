import com.fasterxml.jackson.databind.ObjectMapper;
import dto.MeasurementDTO;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

public class TestPulsarFunction implements Function<String, String> {
    @Override
    public String process(String input, Context context) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MeasurementDTO measurement = mapper.readValue(input, MeasurementDTO.class);
        switch (measurement.getMeasurementName()) {
            case "Temperature" -> {
                if (measurement.getMeasurementValue() > 50) {
                    return "Warning!!!!! Temperature is greater than 50 which now is " + measurement.getMeasurementValue();
                } else {
                    return "Temperature is good now which value is " + measurement.getMeasurementValue();
                }
            }
            case "Voltage" -> {
                if (measurement.getMeasurementValue() > 480) {
                    return "Warning!!!!! Voltage is greater than 480 which now is " + measurement.getMeasurementValue();
                } else {
                    return "Voltage is good now which value is " + measurement.getMeasurementValue();
                }
            }
            case "Current" -> {
                if (measurement.getMeasurementValue() > 200) {
                    return "Warning!!!!! Current is greater than 200 which now is " + measurement.getMeasurementValue();
                } else {
                    return "Current is good now which value is " + measurement.getMeasurementValue();
                }
            }
            default -> {
                return "Measurement name is not valid";
            }
        }
    }
}
