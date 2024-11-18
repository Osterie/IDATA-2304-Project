package no.ntnu.run;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.tools.Logger;

/**
 * Run a greenhouse simulation using command-line interface (no GUI).
 */
public class CommandLineGreenhouse {
  /**
   * Application entrypoint for the command-line version of the simulator.
   *
   * @param args Command line arguments, only the first one of them used: when it is "fake",
   *             emulate fake events, when it is either something else or not present,
   *             use real socket communication.
   */
  public static void main(String[] args) {
    Logger.info("Running greenhouse simulator in command line (without GUI)...");
    GreenhouseSimulator simulator = new GreenhouseSimulator();
    simulator.initialize();
    simulator.start();
  }
}
