package no.ntnu.messages;

/**
 * An abstract transmission sent or received.
 */
public abstract class Transmission {
    
    public Transmission(String protocolString) {
        this.setTransmissionString(protocolString);
    }

    protected String protocolString;

    public String getTransmissionString(){
        return this.protocolString;
    }

    private void setTransmissionString(String protocolString){
        this.protocolString = protocolString;
    }

    /**
     * Abstract method for converting transmission to string.
     * Ensures that all transmissions implement the toString method.
     */
    public abstract String toString();
}
