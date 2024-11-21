package no.ntnu.messages;

/**
 * An abstract transmission sent or received.
 */
public abstract class Transmission {
    
    public Transmission(String transmissionString) {
        this.setTransmissionString(transmissionString);
    }

    protected String transmissionString;

    public String getTransmissionString(){
        return this.transmissionString;
    }

    private void setTransmissionString(String transmissionString){
        this.transmissionString = transmissionString;
    }

    /**
     * Abstract method for converting transmission to string
     */
    public abstract String toProtocolString();
}
