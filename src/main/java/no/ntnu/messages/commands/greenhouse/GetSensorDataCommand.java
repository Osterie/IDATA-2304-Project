package no.ntnu.messages.commands.greenhouse;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.responses.SuccessResponse;

/**
 * Command to get sensor data from a specified node.
 */
public class GetSensorDataCommand extends GreenhouseCommand {
    public GetSensorDataCommand() {
        super("GET_SENSOR_DATA");
    }

    /**
     * Executes the command to get sensor data from a specified node.
     * 
     * @param nodeLogic The node logic to execute the command on.
     * @param fromHeader The header of the message that triggered this command.
     * @return A message containing the sensor data.
     */
    @Override
    public Message execute(NodeLogic nodeLogic, MessageHeader fromHeader) {
        String sensorData = nodeLogic.getSensorData();
        SuccessResponse response = new SuccessResponse(this, sensorData);
        MessageBody body = new MessageBody(response);
        return new Message(fromHeader, body);
    }

    /**
     * Converts the command to a string.
     * 
     * @return The command as a string.
     */
    @Override
    public String toString() {
        return this.getTransmissionString();
    }
}
