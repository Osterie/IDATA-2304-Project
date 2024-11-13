package no.ntnu.greenhouse;

import no.ntnu.messages.Message;

public class NodeLogic {
    private final SensorActuatorNode node;

    public NodeLogic(SensorActuatorNode node) {
        this.node = node;
    }

    public SensorActuatorNode getNode(){
        return this.node;
    }

    public int getId() {
        return this.node.getId();
    }
}
