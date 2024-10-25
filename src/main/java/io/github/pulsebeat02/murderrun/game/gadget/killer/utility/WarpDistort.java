package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class WarpDistort extends KillerGadget {

  public WarpDistort() {
    super(
      "warp_distort",
      Material.CHORUS_FRUIT,
      Message.WARP_DISTORT_NAME.build(),
      Message.WARP_DISTORT_LORE.build(),
      GameProperties.WARP_DISTORT_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final Item item = packet.getItem();

    final PlayerManager manager = game.getPlayerManager();
    final Collection<Survivor> survivors = manager.getAliveInnocentPlayers();
    final int size = survivors.size();
    if (size < 2) {
      return true;
    }
    item.remove();

    final GamePlayer[] players = this.getRandomPlayers(manager);
    final GamePlayer random = players[0];
    final GamePlayer random2 = players[1];

    final Location first = random.getLocation();
    final Location second = random2.getLocation();
    random.teleport(second);
    random2.teleport(first);

    final String sound = GameProperties.WARP_DISTORT_SOUND;
    final PlayerAudience randomAudience = random.getAudience();
    final PlayerAudience random2Audience = random2.getAudience();
    randomAudience.playSound(sound);
    random2Audience.playSound(sound);

    final Component msg = Message.WARP_DISTORT_ACTIVATE.build();
    randomAudience.sendMessage(msg);
    random2Audience.sendMessage(msg);

    return false;
  }

  private GamePlayer[] getRandomPlayers(final PlayerManager manager) {
    final GamePlayer random = manager.getRandomAliveInnocentPlayer();
    GamePlayer random2 = manager.getRandomAliveInnocentPlayer();
    while (random == random2) {
      random2 = manager.getRandomAliveInnocentPlayer();
    }
    return new GamePlayer[] { random, random2 };
  }
}
