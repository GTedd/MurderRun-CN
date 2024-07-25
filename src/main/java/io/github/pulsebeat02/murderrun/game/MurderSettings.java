package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.lobby.MurderLobby;

public final class MurderSettings {

  private MurderArena arena;
  private MurderLobby lobby;
  private int murdererCount;
  private int carPartCount;

  public MurderSettings() {}

  public int getMurdererCount() {
    return this.murdererCount;
  }

  public void setMurdererCount(final int murdererCount) {
    this.murdererCount = murdererCount;
  }

  public int getCarPartCount() {
    return this.carPartCount;
  }

  public void setCarPartCount(final int carPartCount) {
    this.carPartCount = carPartCount;
  }

  public MurderLobby getLobby() {
    return this.lobby;
  }

  public void setLobby(final MurderLobby lobbySpawn) {
    this.lobby = lobbySpawn;
  }

  public MurderArena getArena() {
    return this.arena;
  }

  public void setArena(final MurderArena arena) {
    this.arena = arena;
  }
}