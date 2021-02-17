package com.mineteria.example;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public final class ExampleConfig {
  @Setting(value = "test", comment = "Test configuration property.")
  public boolean test = true;
}
