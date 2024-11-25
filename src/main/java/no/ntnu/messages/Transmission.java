package no.ntnu.messages;

/**
 * An abstract transmission sent or received.
 */
public abstract class Transmission {

    protected final String protocolString;

    
    /**
     * Constructor for Transmission.
     * 
     * @param protocolString the string representation of the transmission. For example "GET_INFORMATION".
     */
    public Transmission(String protocolString) {
        this.protocolString = protocolString;
        
    }

    /**
     * Get the transmission string.
     * 
     * @return the transmission string.
     */
    public String getTransmissionString(){
        return this.protocolString;
    }

    /**
     * Abstract method for converting transmission to string.
     * Ensures that all transmissions implement the toString method.
     */
    public abstract String toString();
}
