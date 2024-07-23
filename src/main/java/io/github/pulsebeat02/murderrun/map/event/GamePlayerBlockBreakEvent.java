package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Optional;
import java.util.UUID;

public final class GamePlayerBlockBreakEvent implements Listener {

  private final MurderGame game;

  public GamePlayerBlockBreakEvent(final MurderGame game) {
    this.game = game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onBlockBreakEvent(final BlockBreakEvent event) {

    final Player player = event.getPlayer();
    final Optional<GamePlayer> optional = PlayerUtils.checkIfValidPlayer(this.game, player);
    if (optional.isEmpty()) {
      return;
    }

    final GamePlayer murderer = optional.get();
    final Location murdererLocation = player.getLocation();
    if (murderer instanceof Murderer) {
      AdventureUtils.playSoundForAllParticipantsAtLocation(
          this.game, murdererLocation, FXSound.CHAINSAW);
    }
  }
}
