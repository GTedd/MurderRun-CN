package io.github.pulsebeat02.murderrun.game.stage;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.GameTimer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import net.kyori.adventure.text.Component;

public final class GameCleanupTool {

  private final Game game;

  public GameCleanupTool(final Game game) {
    this.game = game;
  }

  public void start(final GameResult winCode) {
    this.initiateEndingSequence(winCode);
  }

  private void initiateEndingSequence(final GameResult winCode) {
    this.stopTimer();
    switch (winCode) {
      case INNOCENTS -> this.handleInnocentVictory();
      case MURDERERS -> this.handleKillerVictory();
      default -> {} // do nothing
    }
  }

  private void handleKillerVictory() {
    this.announceMurdererVictory();
    this.announceMurdererTime();
  }

  private void handleInnocentVictory() {
    this.announceInnocentVictory();
    this.invalidateTimer();
  }

  private void stopTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.stopTimer();
  }

  private void announceInnocentVictory() {
    final Component innocentMessage = Message.INNOCENT_VICTORY_INNOCENT.build();
    final Component murdererMessage = Message.INNOCENT_VICTORY_MURDERER.build();
    final Component subtitle = empty();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllInnocents(innocentMessage, subtitle);
    manager.showTitleForAllMurderers(murdererMessage, subtitle);
    manager.playSoundForAllInnocents(Sounds.WIN);
    manager.playSoundForAllMurderers(Sounds.LOSS);
  }

  private void invalidateTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.invalidateElapsedTime();
  }

  private void announceMurdererVictory() {
    final Component innocentMessage = Message.MURDERER_VICTORY_INNOCENT.build();
    final Component murdererMessage = Message.MURDERER_VICTORY_MURDERER.build();
    final Component subtitle = empty();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllInnocents(innocentMessage, subtitle);
    manager.showTitleForAllMurderers(murdererMessage, subtitle);
    manager.playSoundForAllInnocents(Sounds.LOSS);
    manager.playSoundForAllMurderers(Sounds.WIN);
  }

  private void announceMurdererTime() {
    final GameTimer timer = this.game.getTimeManager();
    final long timeElapsed = timer.getElapsedTime();
    final Component message = Message.FINAL_TIME.build(timeElapsed);
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(message);
  }

  public Game getGame() {
    return this.game;
  }
}
