package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
import java.awt.Color;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class JumpScareTrap extends SurvivorTrap {

  public JumpScareTrap() {
    super(
        "jump_scare",
        Material.BLACK_CONCRETE,
        Locale.JUMP_SCARE_TRAP_NAME.build(),
        Locale.JUMP_SCARE_TRAP_LORE.build(),
        Locale.JUMP_SCARE_TRAP_ACTIVATE.build(),
        32,
        Color.RED);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {

    final ItemStack before = this.setPumpkinItemStack(murderer);
    final GameScheduler scheduler = game.getScheduler();
    final Key key = SoundKeys.JUMP_SCARE.getSound().key();
    murderer.playSound(key, Source.MASTER, 1f, 1f);
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 1));

    scheduler.scheduleTask(() -> this.setBackHelmet(murderer, before), 2 * 20);
  }

  private void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
  }

  private @Nullable ItemStack setPumpkinItemStack(final GamePlayer player) {
    final ItemStack stack = new ItemStack(Material.CARVED_PUMPKIN);
    final PlayerInventory inventory = player.getInventory();
    final ItemStack before = inventory.getHelmet();
    inventory.setHelmet(stack);
    return before;
  }
}
