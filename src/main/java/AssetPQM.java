import config.DataConnection;
import dto.AlarmConditionDTO;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssetPQM implements Function<String, String> {
    @Override
    public String process(String input, Context context) throws Exception {



        try (Connection connection = DataConnection.getConnection()) {
            System.out.println("Connection to database established");
            List<AlarmConditionDTO> alarmConditionList = fetchAlarmCondition(connection);
            for (AlarmConditionDTO alarmConditionDTO : alarmConditionList) {
                System.out.println(alarmConditionDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<AlarmConditionDTO> fetchAlarmCondition(Connection connection) throws SQLException {
        List<AlarmConditionDTO> alarmConditionDTOList = new ArrayList<>();
        String query = "SELECT * FROM lntds_alarm_conditions";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                alarmConditionDTOList.add(new AlarmConditionDTO(
                        resultSet.getInt("conditionId"),
                        resultSet.getBoolean("enabled"),
                        resultSet.getString("alarmCondition"),
                        resultSet.getInt("alarmId")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alarmConditionDTOList;
    }
}
