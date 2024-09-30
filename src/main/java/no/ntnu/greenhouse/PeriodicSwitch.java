package no.ntnu.greenhouse;

import java.util.Timer;
import java.util.TimerTask;
import no.ntnu.tools.Logger;

/**
 * A dummy switch which periodically turns an actuator on and off. Used for manual testing.
 * Note: this class is used only for debugging, you can remove it in your final project!
 */
public class PeriodicSwitch {
  private final Timer timer;
  private final SensorActuatorNode node;
  private final int actuatorId;
  private final long delay;
  private final String name;

  /**
   * Create a periodic switcher.
   *
   * @param name       Name of the switch, used for debugging
   * @param node       The associated actuator node
   * @param actuatorId The ID of the actuator
   * @param m          The actuator will be turned on and off every m milliseconds
   */
  public PeriodicSwitch(String name, SensorActuatorNode node, int actuatorId, long m) {
    this.node = node;
    this.actuatorId = actuatorId;
    this.delay = m;
    this.name = name;

    timer = new Timer(name);
  }

  /**
   * Start the periodic actuator toggling.
   */
  public void start() {
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        Logger.info(" > " + name + ": toggle actuator " + actuatorId + " on node " + node.getId());
        if (node.isRunning()) {
          try {
            node.toggleActuator(actuatorId);
          } catch (Exception e) {
            Logger.error("Failed to toggle an actuator: " + e.getMessage());
            timer.cancel();
          }
        } else {
          Logger.info("   Node stopped, stopping the switch");
          timer.cancel();
        }
      }
    }, delay, delay);
  }

  /**
   * Stop the periodic actuator toggling.
   */
  public void stop() {
    Logger.info("-- Stopping " + this.name);
    timer.cancel();
  }
}
