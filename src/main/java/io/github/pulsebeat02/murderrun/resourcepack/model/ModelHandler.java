package io.github.pulsebeat02.murderrun.resourcepack.model;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.resourcepack.texture.CustomTexture;
import io.github.pulsebeat02.murderrun.resourcepack.texture.ItemTexture;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.model.*;
import team.unnamed.creative.texture.Texture;

public final class ModelHandler {

  public ModelHandler() {}

  public Model customItemModelGenerator() {
    final List<ItemOverride> list = new ArrayList<>();
    final ItemTexture[] textures = ItemTexture.values();
    for (final ItemTexture texture : textures) {
      final int id = texture.getCustomModelDataId();
      final Texture tex = texture.getTexture();
      final Key key = tex.key();
      final ItemOverride override = ItemOverride.of(key, ItemPredicate.customModelData(id));
      list.add(override);
    }
    return Model.model()
        .key(key("item/diamond"))
        .parent(key("item/generated"))
        .overrides(list)
        .build();
  }

  public Model customSwordGenerator() {
    final Texture texture = new CustomTexture("sword.png").build();
    final ItemPredicate predicate = ItemPredicate.customModelData(1);
    final Key key = texture.key();
    final ItemOverride override = ItemOverride.of(key, predicate);
    final List<ItemOverride> list = List.of(override);
    return Model.model()
        .key(key("item/diamond_sword"))
        .parent(key("item/generated"))
        .overrides(list)
        .build();
  }

  public Texture customJumpScareGenerator() {
    return new CustomTexture(key("minecraft", "misc/pumpkinblur.png"), "pumpkinblur.png").build();
  }
}
