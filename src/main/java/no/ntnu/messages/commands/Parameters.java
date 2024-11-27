package no.ntnu.messages.commands;

/**
 * Interface to use when a transmission includes parameters.
 */
public interface Parameters {
    /**
     * Sets the parameters of the transmission.
     * 
     * @param parameters the parameters to set.
     */
    public void setParameters(String[] parameters);
}