package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;

import java.util.Collection;
import java.util.UUID;

import io.github.pulsebeat02.murderrun.map.MurderMap;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.entity.Player;

public final class MurderGame {

  private final MurderRun plugin;
  private final MurderMap murderMap;
  private final GameSettings configuration;
  private final PlayerManager playerManager;
  private final GamePreparationManager preparationManager;
  private final GameEndManager endManager;
  private final TimeManager timeManager;
  private final UUID gameID;
  private GameStatus status;

  public MurderGame(final MurderRun plugin) {
    this.plugin = plugin;
    this.murderMap = new MurderMap(this);
    this.playerManager = new PlayerManager(this);
    this.configuration = new GameSettings();
    this.preparationManager = new GamePreparationManager(this);
    this.endManager = new GameEndManager(this);
    this.timeManager = new TimeManager();
    this.status = GameStatus.NOT_STARTED;
    this.gameID = UUID.randomUUID();
  }

  public void startGame(final Collection<Player> murderers, final Collection<Player> participants) {
    this.status = GameStatus.IN_PROGRESS;
    this.murderMap.start();
    this.playerManager.start(murderers, participants);
    this.preparationManager.start();
  }

  public void finishGame(final GameWinCode code) {
    this.status = GameStatus.FINISHED;
    this.endManager.start(code);
    this.playerManager.shutdown();
    this.murderMap.shutdown();
  }

  public GameSettings getSettings() {
    return this.configuration;
  }

  public PlayerManager getPlayerManager() {
    return this.playerManager;
  }

  public GameStatus getStatus() {
    return this.status;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public MurderMap getMurderMap() {
    return this.murderMap;
  }

  public GamePreparationManager getPreparationManager() {
    return this.preparationManager;
  }

  public GameEndManager getEndManager() {
    return this.endManager;
  }

  public UUID getGameID() {
    return this.gameID;
  }

  public TimeManager getTimeManager() {
    return this.timeManager;
  }
}
