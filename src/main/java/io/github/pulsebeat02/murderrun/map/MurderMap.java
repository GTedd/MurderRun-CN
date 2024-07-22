package io.github.pulsebeat02.murderrun.map;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.map.event.GameEventManager;
import io.github.pulsebeat02.murderrun.map.part.CarPartManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public final class MurderMap {

  private final MurderGame game;
  private final CarPartManager carPartManager;
  private final GameEventManager eventManager;

  public MurderMap(final MurderGame game) {
    this.game = game;
    this.carPartManager = new CarPartManager(this);
    this.eventManager = new GameEventManager(this);
  }

  public void start() {
    this.resetMap();
    this.registerEvents();
    this.spawnParts();
  }

  public void stop() {
    this.unregisterEvents();
    this.stopExecutors();
  }

  private void stopExecutors() {
    this.carPartManager.shutdown();
  }

  private void unregisterEvents() {
    this.eventManager.unregisterEvents();
  }

  private void resetMap() {}

  private void spawnParts() {
    this.carPartManager.spawnParts();
  }

  private void registerEvents() {
    this.eventManager.registerEvents();
  }

  public MurderGame getGame() {
    return this.game;
  }

  public CarPartManager getCarPartManager() {
    return this.carPartManager;
  }
}
