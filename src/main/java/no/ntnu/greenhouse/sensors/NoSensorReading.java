package no.ntnu.greenhouse.sensors;

import no.ntnu.greenhouse.SensorType;

public class NoSensorReading extends SensorReading {
  public NoSensorReading() {
    super(SensorType.NONE);
  }


  // TODO use to string instead
  @Override
  public String getFormatted() {
    return "NoSensor";
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof NoSensorReading;
  }
}
