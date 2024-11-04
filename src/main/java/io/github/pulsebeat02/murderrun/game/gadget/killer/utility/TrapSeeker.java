/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public final class TrapSeeker extends KillerGadget {

  public TrapSeeker() {
    super(
      "trap_seeker",
      Material.CLOCK,
      Message.TRAP_SEEKER_NAME.build(),
      Message.TRAP_SEEKER_LORE.build(),
      GameProperties.TRAP_SEEKER_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleTrapSeeking(game, killer), 0, 20L);

    final PlayerAudience audience = killer.getAudience();
    final Component message = Message.TRAP_SEEKER_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.TRAP_SEEKER_SOUND);

    return false;
  }

  private void handleTrapSeeking(final Game game, final Killer killer) {
    final GadgetManager manager = game.getGadgetManager();
    final Location origin = killer.getLocation();
    final World world = requireNonNull(origin.getWorld());
    final double radius = GameProperties.TRAP_SEEKER_RADIUS;
    final Collection<Entity> entities = world.getNearbyEntities(origin, radius, radius, radius);
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final Set<Item> gadgets = new HashSet<>();

    for (final Entity entity : entities) {
      if (!(entity instanceof final Item item)) {
        continue;
      }

      final ItemStack stack = item.getItemStack();
      final Gadget gadget = mechanism.getGadgetFromStack(stack);
      if (gadget == null) {
        continue;
      }

      if (gadget instanceof KillerGadget) {
        return;
      }

      gadgets.add(item);
    }

    final Collection<Item> set = killer.getGlowingTraps();
    final MetadataManager metadata = killer.getMetadataManager();
    for (final Item item : gadgets) {
      if (!set.contains(item)) {
        set.add(item);
        metadata.setEntityGlowing(item, ChatColor.YELLOW, true);
      }
    }

    for (final Item entity : set) {
      if (!gadgets.contains(entity)) {
        set.remove(entity);
        metadata.setEntityGlowing(entity, ChatColor.YELLOW, false);
      }
    }
  }
}
