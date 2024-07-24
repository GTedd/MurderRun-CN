package io.github.pulsebeat02.murderrun.lobby;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public final class MurderLobbyManager {

  private final Map<String, MurderLobby> lobbies;

  public MurderLobbyManager() {
    this.lobbies = new HashMap<>();
  }

  public void addLobby(final String name, final Location spawn) {
    final MurderLobby lobby = new MurderLobby(spawn);
    this.lobbies.put(name, lobby);
  }

  public void removeLobby(final String name) {
    this.lobbies.remove(name);
  }

  public Map<String, MurderLobby> getLobbies() {
    return this.lobbies;
  }
}
