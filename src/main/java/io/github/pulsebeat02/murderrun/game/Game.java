package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class Game {

  private final MurderRun plugin;
  private final UUID gameID;
  private Map map;
  private GameSettings configuration;
  private PlayerManager playerManager;
  private GameStartupTool preparationManager;
  private GameCleanupTool endManager;
  private GameTimer murderGameTimer;
  private GameScheduler scheduler;
  private GameStatus status;
  private GadgetManager gadgetManager;

  public Game(final MurderRun plugin) {
    this.plugin = plugin;
    this.status = GameStatus.NOT_STARTED;
    this.gameID = UUID.randomUUID();
  }

  public GameSettings getConfiguration() {
    return this.configuration;
  }

  public void setConfiguration(final GameSettings configuration) {
    this.configuration = configuration;
  }

  public GameTimer getMurderTimeManager() {
    return this.murderGameTimer;
  }

  public void setMurderTimeManager(final GameTimer murderGameTimer) {
    this.murderGameTimer = murderGameTimer;
  }

  public void startGame(
      final GameSettings settings,
      final Collection<Player> murderers,
      final Collection<Player> participants) {
    this.status = GameStatus.IN_PROGRESS;
    this.configuration = settings;
    this.setMurdererCount(murderers);
    this.scheduler = new GameScheduler(this);
    this.map = new Map(this);
    this.playerManager = new PlayerManager(this);
    this.preparationManager = new GameStartupTool(this);
    this.endManager = new GameCleanupTool(this);
    this.murderGameTimer = new GameTimer();
    this.map.start();
    this.playerManager.start(murderers, participants);
    this.preparationManager.start();
    this.gadgetManager = new GadgetManager(this);
    this.gadgetManager.start();
  }

  private void setMurdererCount(final Collection<Player> murderers) {
    final GameSettings settings = this.getSettings();
    final int count = murderers.size();
    settings.setMurdererCount(count);
  }

  public GameSettings getSettings() {
    return this.configuration;
  }

  public void finishGame(final GameResult code) {
    this.status = GameStatus.FINISHED;
    this.endManager.start(code);
    this.playerManager.shutdown();
    this.map.shutdown();
  }

  public PlayerManager getPlayerManager() {
    return this.playerManager;
  }

  public void setPlayerManager(final PlayerManager playerManager) {
    this.playerManager = playerManager;
  }

  public GameStatus getStatus() {
    return this.status;
  }

  public void setStatus(final GameStatus status) {
    this.status = status;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Map getMurderMap() {
    return this.map;
  }

  public void setMurderMap(final Map map) {
    this.map = map;
  }

  public GameStartupTool getPreparationManager() {
    return this.preparationManager;
  }

  public void setPreparationManager(final GameStartupTool preparationManager) {
    this.preparationManager = preparationManager;
  }

  public GameCleanupTool getEndManager() {
    return this.endManager;
  }

  public void setEndManager(final GameCleanupTool endManager) {
    this.endManager = endManager;
  }

  public UUID getGameID() {
    return this.gameID;
  }

  public GameTimer getTimeManager() {
    return this.murderGameTimer;
  }

  public boolean isFinished() {
    return this.status == GameStatus.FINISHED;
  }

  public GameScheduler getScheduler() {
    return this.scheduler;
  }

  public GadgetManager getGadgetManager() {
    return this.gadgetManager;
  }
}