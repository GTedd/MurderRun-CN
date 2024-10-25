package io.github.pulsebeat02.murderrun.game.arena;

import io.github.pulsebeat02.murderrun.data.hibernate.converters.LocationConverter;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

@Entity
@Table(name = "arena")
public final class Arena implements Serializable {

  @Serial
  private static final long serialVersionUID = -6251041532325023867L;

  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "schematic")
  @Lob
  private final ArenaSchematic schematic;

  @Column(name = "name")
  private final String name;

  @Convert(converter = LocationConverter.class)
  @Column(name = "corners")
  private final Location[] corners;

  @Convert(converter = LocationConverter.class)
  @Column(name = "car_part_locations")
  private final Location[] carPartLocations;

  @Convert(converter = LocationConverter.class)
  @Column(name = "spawn")
  private final Location spawn;

  @Convert(converter = LocationConverter.class)
  @Column(name = "truck")
  private final Location truck;

  @SuppressWarnings("all") // for hibernate
  public Arena() {
    this.schematic = null;
    this.name = null;
    this.corners = null;
    this.carPartLocations = null;
    this.spawn = null;
    this.truck = null;
  }

  public Arena(
    final ArenaSchematic schematic,
    final String name,
    final Location[] corners,
    final Location[] carPartLocations,
    final Location spawn,
    final Location truck
  ) {
    this.schematic = schematic;
    this.name = name;
    this.corners = corners;
    this.carPartLocations = carPartLocations;
    this.spawn = spawn;
    this.truck = truck;
  }

  public String getName() {
    return this.name;
  }

  public Location getFirstCorner() {
    return this.corners[0];
  }

  public Location getSecondCorner() {
    return this.corners[1];
  }

  public Location getSpawn() {
    return this.spawn;
  }

  public Location getTruck() {
    return this.truck;
  }

  public Location[] getCorners() {
    return this.corners;
  }

  public ArenaSchematic getSchematic() {
    return this.schematic;
  }

  public BoundingBox createBox() {
    return BoundingBox.of(this.corners[0], this.corners[1]);
  }

  public Location[] getCarPartLocations() {
    return this.carPartLocations;
  }

  public Location getRandomItemLocation() {
    final int length = this.carPartLocations.length;
    if (length == 0) {
      return this.spawn;
    }

    final int index = RandomUtils.generateInt(length);
    final Location location = this.carPartLocations[index];
    final Location drop = location.clone();
    drop.add(0, 1.5, 0);

    return drop;
  }
}
