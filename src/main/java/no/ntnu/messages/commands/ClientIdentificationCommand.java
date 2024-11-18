package no.ntnu.messages.commands;

import no.ntnu.Clients;
import no.ntnu.messages.Delimiters;

public class ClientIdentificationCommand extends Command implements Parameters{
    protected String id;
    protected Clients client;

    public ClientIdentificationCommand(Clients client, String id) {
        super("CLIENT_IDENTIFICATION");
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.client = client;
        this.id = id;
    }

    public ClientIdentificationCommand() {
        super("CLIENT_IDENTIFICATION");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Clients getClient() {
        return this.client;
    }

    public void setClient(Clients client) {
        this.client = client;
    }

    @Override
    public String toProtocolString() {
        String protocolString = this.getCommandString();
        protocolString += Delimiters.BODY_PARAMETERS_DELIMITER.getValue();
        protocolString += this.client;
        protocolString += Delimiters.BODY_PARAMETERS_DELIMITER.getValue();
        protocolString += this.id;
        return protocolString;
    }

    @Override
    public void setParameters(String[] parameters) {
        if (parameters.length != 2) {
            throw new IllegalArgumentException("Invalid number of parameters: " + parameters.length);
        }
        if (parameters[0] == null || parameters[1] == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        if (Clients.valueOf(parameters[0]) == null) {
            throw new IllegalArgumentException("Invalid client");
        }
        this.client = Clients.valueOf(parameters[0]);
        this.id = parameters[1];
    }
}
