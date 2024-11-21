package no.ntnu.messages.responses;

import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.Command;

public abstract class Response extends Transmission {
    private String responseData;
    private Command command;

    protected Response(String responseProtocolString, Command command, String responseData){
        super(responseProtocolString);
        this.command = command;
        this.responseData = responseData;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getResponseData() {
        return responseData;
    }

    @Override
    public String toProtocolString() {
        String protocolString = this.getTransmissionString();
        protocolString += Delimiters.BODY_FIELD_PARAMETERS.getValue();
        protocolString += this.command.getTransmissionString();
        protocolString += Delimiters.BODY_FIELD_PARAMETERS.getValue();
        protocolString += this.responseData;
        return protocolString;
    }
}
