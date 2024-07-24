package io.github.pulsebeat02.murderrun.map;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.map.event.GameEventManager;
import io.github.pulsebeat02.murderrun.map.part.CarPartManager;

public final class MurderMap {

  private final MurderGame game;
  private final CarPartManager carPartManager;
  private final GameEventManager eventManager;
  private final MurderMapResetManager resetManager;

  public MurderMap(final MurderGame game) {
    this.game = game;
    this.carPartManager = new CarPartManager(this);
    this.eventManager = new GameEventManager(this);
    this.resetManager = new MurderMapResetManager(this);
  }

  public void start() {
    this.resetMap();
    this.registerEvents();
    this.spawnParts();
  }

  public void shutdown() {
    this.unregisterEvents();
    this.stopExecutors();
    this.resetWorld();
  }

  private void resetWorld() {
    this.resetManager.resetMap();
  }

  private void stopExecutors() {
    this.carPartManager.shutdownExecutor();
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
