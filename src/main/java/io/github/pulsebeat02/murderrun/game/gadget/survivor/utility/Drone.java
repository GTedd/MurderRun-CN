package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Drone extends SurvivorGadget {

  public Drone() {
    super("drone", Material.PHANTOM_MEMBRANE, Message.DRONE_NAME.build(), Message.DRONE_LORE.build(), GameProperties.DRONE_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location origin = player.getLocation();
    final Location clone = origin.clone();
    clone.add(0, 20, 0);

    player.setGameMode(GameMode.SPECTATOR);
    player.teleport(clone);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.resetPlayer(player, origin), GameProperties.DRONE_DURATION);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.DRONE_SOUND);

    return false;
  }

  private void resetPlayer(final GamePlayer player, final Location origin) {
    player.teleport(origin);
    player.setGameMode(GameMode.SURVIVAL);
  }
}
