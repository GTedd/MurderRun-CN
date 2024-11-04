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
package io.github.pulsebeat02.murderrun.resourcepack.sound;

import java.util.HashSet;
import java.util.Set;

public final class Sounds {

  private static final Set<SoundResource> ALL = new HashSet<>();

  public static final SoundResource COUNTDOWN = of("countdown");
  public static final SoundResource CHAINSAW = of("chainsaw");
  public static final SoundResource DEATH = of("death");
  public static final SoundResource RELEASED_1 = of("released_1");
  public static final SoundResource RELEASED_2 = of("released_2");
  public static final SoundResource LOSS = of("loss");
  public static final SoundResource WIN = of("win");
  public static final SoundResource JUMP_SCARE = of("jump_scare");
  public static final SoundResource FART = of("fart");
  public static final SoundResource SUPPLY_DROP = of("supply_drop");
  public static final SoundResource FLASHBANG = of("flashbang");
  public static final SoundResource FLASHLIGHT = of("flashlight");
  public static final SoundResource BACKGROUND = of("background");
  public static final SoundResource REWIND = of("rewind");

  private static SoundResource of(final String name) {
    final SoundFile sound = new SoundFile(name);
    final SoundResource soundResource = new SoundResource(sound);
    ALL.add(soundResource);
    return soundResource;
  }

  public static Set<SoundResource> getAllSounds() {
    return ALL;
  }
}
