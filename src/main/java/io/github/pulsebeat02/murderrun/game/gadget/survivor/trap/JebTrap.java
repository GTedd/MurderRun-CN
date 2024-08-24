package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Sheep;

public final class JebTrap extends SurvivorTrap {

  private static final int SHEEP_COUNT = 15;

  public JebTrap() {
    super(
        "jeb",
        Material.CYAN_WOOL,
        Message.JEB_NAME.build(),
        Message.JEB_LORE.build(),
        Message.JEB_ACTIVATE.build(),
        16,
        Color.WHITE);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {

    final Location location = murderer.getLocation();
    final World world = requireNonNull(location.getWorld());
    for (int i = 0; i < SHEEP_COUNT; i++) {
      world.spawn(location, Sheep.class, sheep -> sheep.setCustomName("jeb_"));
    }

    murderer.playSound(key("entity.sheep.ambient"));
  }
}
