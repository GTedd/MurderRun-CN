package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class MurderousWarp extends KillerGadget {

  public MurderousWarp() {
    super(
      "murderous_warp",
      Material.REDSTONE_BLOCK,
      Message.MURDEROUS_WARP_NAME.build(),
      Message.MURDEROUS_WARP_LORE.build(),
      GameProperties.MURDEROUS_WARP_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer random = manager.getRandomAliveInnocentPlayer();
    final Location first = random.getLocation();
    final Location second = player.getLocation();
    random.teleport(second);
    player.teleport(first);

    final PlayerAudience audienceRand = random.getAudience();
    audienceRand.playSound(GameProperties.MURDEROUS_WARP_SOUND);

    final Component msg = Message.WARP_DISTORT_ACTIVATE.build();
    audienceRand.sendMessage(msg);

    final PlayerAudience audienceKiller = player.getAudience();
    audienceKiller.sendMessage(msg);

    return false;
  }
}
