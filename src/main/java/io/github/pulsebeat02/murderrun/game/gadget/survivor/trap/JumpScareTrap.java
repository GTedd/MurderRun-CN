package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.awt.Color;
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
        Message.JUMP_SCARE_NAME.build(),
        Message.JUMP_SCARE_LORE.build(),
        Message.JUMP_SCARE_ACTIVATE.build(),
        32,
        Color.RED);
  }

  @Override
  public void onTrapActivate(
      final Game game, final GamePlayer murderer, final org.bukkit.entity.Item item) {
    final PlayerManager manager = game.getPlayerManager();
    final ItemStack before = this.setPumpkinItemStack(murderer);
    final GameScheduler scheduler = game.getScheduler();
    final PlayerAudience audience = murderer.getAudience();
    audience.playSound(Sounds.JUMP_SCARE);
    murderer.addPotionEffects(
        new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1),
        new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 1));
    scheduler.scheduleTask(() -> this.setBackHelmet(murderer, before), 2 * 20L);
    manager.playSoundForAllParticipants("entity.witch.celebrate");
  }

  private void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
  }

  private @Nullable ItemStack setPumpkinItemStack(final GamePlayer player) {
    final ItemStack stack = Item.create(Material.CARVED_PUMPKIN);
    final PlayerInventory inventory = player.getInventory();
    final ItemStack before = inventory.getHelmet();
    inventory.setHelmet(stack);
    return before;
  }
}
