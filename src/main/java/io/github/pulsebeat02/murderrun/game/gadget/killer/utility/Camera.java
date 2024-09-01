package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.misc.CameraGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Camera extends KillerGadget {

  private CameraGadget gadget;

  public Camera() {
    super(
        "killer_camera",
        Material.OBSERVER,
        Message.KILLER_CAMERA_NAME.build(),
        Message.KILLER_CAMERA_LORE.build(),
        GameProperties.KILLER_CAMERA_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {
    if (this.gadget == null) {
      this.gadget = new CameraGadget(this);
    }
    return this.gadget.handleCamera(game, player, item, remove);
  }
}
