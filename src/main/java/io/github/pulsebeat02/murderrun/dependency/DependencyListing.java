package io.github.pulsebeat02.murderrun.dependency;

import io.github.pulsebeat02.murderrun.reflect.versioning.ServerEnvironment;
import java.util.*;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DependencyListing {

  private static final Map<String, DependencyListing> DEPENDENCY_LISTINGS = new HashMap<>();
  private static final Dependency CITIZENS = new UrlDependency(
    "Citizens",
    "Citizens-2.0.36-b3621",
    "https://ci.citizensnpcs.co/job/Citizens2/3621/artifact/dist/target/Citizens-2.0.36-b3621.jar"
  );

  private static final Dependency WORLD_EDIT = new UrlDependency(
    "WorldEdit",
    "worldedit-bukkit-7.3.9-SNAPSHOT",
    "https://ci.enginehub.org/repository/download/bt10/25583:id/worldedit-bukkit-7.3.9-SNAPSHOT-dist.jar?branch=ot/feature/1.21.2&guest=1"
  );

  private static final DependencyListing V1_21_R3 = create("V1_21_R3", CITIZENS, WORLD_EDIT);

  private static DependencyListing create(final String revision, final Dependency... deps) {
    final Collection<Dependency> dependencies = List.of(deps);
    final DependencyListing listing = new DependencyListing(revision, dependencies);
    DEPENDENCY_LISTINGS.put(listing.revision, listing);
    return listing;
  }

  public static @Nullable DependencyListing getCurrentListing() {
    final String revision = ServerEnvironment.getNMSRevision();
    final String upper = revision.toUpperCase();
    return DEPENDENCY_LISTINGS.get(upper);
  }

  private final String revision;
  private final Collection<Dependency> dependencies;

  DependencyListing(final String revision, final Collection<Dependency> dependencies) {
    this.revision = revision;
    this.dependencies = dependencies;
  }

  public String getRevision() {
    return this.revision;
  }

  public Collection<Dependency> getDependencies() {
    return this.dependencies;
  }
}
