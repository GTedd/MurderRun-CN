package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class EagleEye extends KillerGadget {

  public EagleEye() {
    super("eagle_eye", Material.FEATHER, Message.EAGLE_EYE_NAME.build(), Message.EAGLE_EYE_LORE.build(), GameProperties.EAGLE_EYE_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location[] corners = arena.getCorners();
    final Location average = MapUtils.getAverageLocation(corners[0], corners[1]);
    final World world = requireNonNull(average.getWorld());

    final Block highest = world.getHighestBlockAt(average);
    final Location location = highest.getLocation();
    final Location teleport = location.add(0, 50, 0);

    final Location previous = player.getLocation();
    player.setGravity(false);
    player.teleport(teleport);
    player.setAllowFlight(true);

    final float before = player.getFlySpeed();
    player.setFlySpeed(0.0f);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.resetState(player, previous, before), GameProperties.EAGLE_EYE_DURATION);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.EAGLE_EYE_SOUND);

    return false;
  }

  private void resetState(final GamePlayer gamePlayer, final Location previous, final float flySpeed) {
    gamePlayer.teleport(previous);
    gamePlayer.setGravity(true);
    gamePlayer.setAllowFlight(true);
    gamePlayer.setFlySpeed(flySpeed);
  }
}
