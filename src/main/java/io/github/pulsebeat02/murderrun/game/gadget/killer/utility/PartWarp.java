package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class PartWarp extends KillerGadget {

  public PartWarp() {
    super("part_warp", Material.REPEATER, Message.PART_WARP_NAME.build(), Message.PART_WARP_LORE.build(), GameProperties.PART_WARP_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final io.github.pulsebeat02.murderrun.game.map.Map map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> values = parts.values();
    final List<CarPart> shuffled = values.stream().collect(StreamUtils.toShuffledList());
    final CarPart part = this.getRandomCarPart(shuffled);
    final Item carPartItem = part.getItem();

    final Location location = player.getLocation();
    carPartItem.teleport(location);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.PART_WARP_SOUND);

    return false;
  }

  public CarPart getRandomCarPart(final List<CarPart> shuffled) {
    CarPart chosen = shuffled.getFirst();
    while (chosen.isPickedUp()) {
      shuffled.remove(chosen);
      chosen = shuffled.getFirst();
    }
    return chosen;
  }
}
