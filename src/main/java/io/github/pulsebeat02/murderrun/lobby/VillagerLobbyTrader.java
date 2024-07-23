package io.github.pulsebeat02.murderrun.lobby;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;

public final class VillagerLobbyTrader {

  private final Location location;
  private final List<MerchantRecipe> trades;

  public VillagerLobbyTrader(final Location location, final List<MerchantRecipe> trades) {
    this.location = location;
    this.trades = trades;
  }

  public void spawnVillager() {
    final World world = this.location.getWorld();
    final Entity entity = world.spawnEntity(this.location, EntityType.VILLAGER);
    if (entity instanceof final Villager villager) {
      villager.setAI(false);
      villager.setInvulnerable(true);
      villager.setGravity(false);
      villager.setProfession(Villager.Profession.CLERIC);
      villager.setAdult();
      villager.setRestocksToday(Integer.MAX_VALUE);
      villager.setCanPickupItems(false);
      villager.setVillagerLevel(5);
      villager.setRecipes(this.trades);
    }
  }
}
