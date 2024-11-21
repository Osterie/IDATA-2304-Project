package no.ntnu.messages.responses;

import no.ntnu.messages.Transmission;

public abstract class Response extends Transmission {

    public Response(String responseData) {
        super(responseData);
    }
    
}
