package io.github.pulsebeat02.murderrun.game.gadget.killer;

import io.github.pulsebeat02.murderrun.game.gadget.Trap;
import java.awt.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public abstract class KillerTrap extends Trap {

  public KillerTrap(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final Component announcement,
      final Color color) {
    super(name, material, itemName, itemLore, announcement, color);
  }
}