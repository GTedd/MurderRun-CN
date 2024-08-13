package io.github.pulsebeat02.murderrun.reflect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public final class FallbackPacketTools implements PacketToolAPI {

  @Override
  public byte[] toByteArray(final ItemStack item) {
    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
      dataOutput.writeObject(item);
      return outputStream.toByteArray();
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public ItemStack fromByteArray(final byte[] bytes) {
    try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
      return (ItemStack) dataInput.readObject();
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void sendGlowPacket(final Player watcher, final Entity glow) {
    // limited support
    glow.setGlowing(true);
  }

  @Override
  public void sendRemoveGlowPacket(final Player watcher, final Entity glow) {
    // limited support
    glow.setGlowing(false);
  }
}
