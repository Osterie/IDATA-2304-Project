package no.ntnu.gui.common;

import javafx.scene.layout.Region;

/**
 * Common GUI tools.
 */
public class GuiTools {
  private static final double HUGE_HEIGHT = 5000;

  /**
   * Not allowed to create instances of this class.
   */
  private GuiTools() {
  }

  /**
   * Ensure that this node always try to get as much vertical space as is available to it
   * (proportionally to other siblings).
   *
   * @param region The region to stretch vertically
   */
  public static void stretchVertically(Region region) {
    region.setPrefHeight(HUGE_HEIGHT);
  }
}
