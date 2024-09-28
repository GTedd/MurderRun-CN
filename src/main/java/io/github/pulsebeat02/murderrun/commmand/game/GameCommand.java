package io.github.pulsebeat02.murderrun.commmand.game;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.commmand.AnnotationCommandFeature;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.GameManager;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGamePlayerManager;
import io.github.pulsebeat02.murderrun.gui.game.PlayerListGui;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.type.tuple.Triplet;

public final class GameCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;
  private GameInputSanitizer sanitizer;
  private GameManager manager;

  private Multimap<Player, Player> invites;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.sanitizer = new GameInputSanitizer(this);
    this.manager = new GameManager(plugin);
    this.invites = HashMultimap.create();
    this.plugin = plugin;
  }

  @Permission("murderrun.command.game.start")
  @CommandDescription("murderrun.command.game.start.info")
  @Command(value = "murder game start", requiredSender = CommandSender.class)
  public void startGame(final CommandSender sender) {

    final Audience audience = this.audiences.sender(sender);
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)
        || this.sanitizer.checkIfNotOwner(sender, audience, data)
        || this.sanitizer.checkIfGameAlreadyStarted(audience, data)
        || this.sanitizer.checkIfNotEnoughPlayers(audience, data)) {
      return;
    }
    data.startGame();

    final Component message = Message.GAME_START.build();
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.create")
  @CommandDescription("murderrun.command.game.create.info")
  @Command(
      value = "murder game create <arenaName> <lobbyName> <id> <min> <max> <quickJoinable>",
      requiredSender = CommandSender.class)
  public void createGame(
      final CommandSender sender,
      @Argument(suggestions = "arena-suggestions") @Quoted final String arenaName,
      @Argument(suggestions = "lobby-suggestions") @Quoted final String lobbyName,
      final String id,
      final int min,
      final int max,
      final boolean quickJoinable) {

    final Audience audience = this.audiences.sender(sender);
    final PreGameManager temp = this.manager.getGameAsParticipant(sender);
    if (this.sanitizer.checkIfAlreadyInGame(audience, temp)
        || this.sanitizer.checkIfArenaValid(audience, arenaName)
        || this.sanitizer.checkIfLobbyValid(audience, lobbyName)) {
      return;
    }
    this.manager.createGame(sender, id, arenaName, lobbyName, min, max, quickJoinable);

    final Component message = Message.GAME_CREATED.build();
    audience.sendMessage(message);
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public void setPlugin(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public BukkitAudiences getAudiences() {
    return this.audiences;
  }

  public void setAudiences(final BukkitAudiences audiences) {
    this.audiences = audiences;
  }

  @Permission("murderrun.command.game.cancel")
  @CommandDescription("murderrun.command.game.cancel.info")
  @Command(value = "murder game cancel", requiredSender = Player.class)
  public void cancelGame(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)
        || this.sanitizer.checkIfNotOwner(sender, audience, data)) {
      return;
    }

    final String id = data.getId();
    this.manager.removeGame(id);

    final PreGamePlayerManager playerManager = data.getManager();
    final Collection<Player> participants = playerManager.getParticipants();
    final Component ownerMessage = Message.GAME_CANCEL.build();
    final Component kickedMessage = Message.GAME_PLAYER_KICK.build();
    for (final Player player : participants) {
      final Audience kicked = this.audiences.player(player);
      kicked.sendMessage(kickedMessage);
    }
    audience.sendMessage(ownerMessage);
  }

  @Permission("murderrun.command.game.invite")
  @CommandDescription("murderrun.command.game.invite.info")
  @Command(value = "murder game invite <invite>", requiredSender = Player.class)
  public void invitePlayer(final Player sender, final Player invite) {

    final Audience audience = this.audiences.player(sender);
    final Triplet<PreGameManager, Boolean, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data)
        || this.checkIfNotOwner(audience, data)
        || this.checkIfNotSamePlayer(audience, sender, invite)
        || this.checkIfInvitedAlreadyInGame(audience, invite, data)) {
      return;
    }

    final String senderDisplayName = sender.getDisplayName();
    final String inviteDisplayName = invite.getDisplayName();

    final Collection<Player> outgoing = this.invites.get(invite);
    outgoing.add(sender);

    final Component owner = Message.GAME_OWNER_INVITE.build(inviteDisplayName);
    final Component player = Message.GAME_PLAYER_INVITE.build(senderDisplayName);
    final Audience invited = this.audiences.player(invite);
    audience.sendMessage(owner);
    invited.sendMessage(player);
  }

  private boolean checkIfInvitedAlreadyInGame(
      final Audience audience,
      final Player invite,
      final Triplet<PreGameManager, Boolean, Boolean> triplet) {

    final PreGameManager first = triplet.first();
    final Triplet<PreGameManager, Boolean, Boolean> otherPlayerData = this.games.get(invite);
    if (otherPlayerData == null) {
      return false;
    }

    final PreGameManager other = otherPlayerData.first();
    if (other == first) {
      final Component message = Message.GAME_INVITE_ALREADY_ERROR.build();
      audience.sendMessage(message);
      return true;
    }

    return false;
  }

  private boolean checkIfNotSamePlayer(
      final Audience audience, final Player sender, final Player invite) {
    if (sender == invite) {
      final Component message = Message.GAME_INVITE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @Permission("murderrun.command.game.join")
  @CommandDescription("murderrun.command.game.join.info")
  @Command(value = "murder game join <owner>", requiredSender = Player.class)
  public void joinGame(final Player sender, final Player owner) {

    final Audience audience = this.audiences.player(sender);
    final Triplet<PreGameManager, Boolean, Boolean> data = this.games.get(sender);
    if (this.checkIfAlreadyInGame(audience, data)
        || this.checkIfNotInvited(audience, sender, owner)) {
      return;
    }

    final Collection<Player> invitations = this.invites.get(sender);
    final Triplet<PreGameManager, Boolean, Boolean> ownerData = this.games.get(owner);
    if (ownerData == null) {
      return;
    }

    final PreGameManager manager = ownerData.first();
    manager.addParticipantToLobby(sender, false);
    invitations.remove(sender);

    final Triplet<PreGameManager, Boolean, Boolean> gamePair = Triplet.of(manager, false, false);
    this.games.put(sender, gamePair);

    final Collection<Player> participants = manager.getParticipants();
    final String name = sender.getDisplayName();
    final Component message = Message.GAME_JOIN.build(name);
    for (final Player player : participants) {
      final Audience member = this.audiences.player(player);
      member.sendMessage(message);
    }
  }

  private boolean checkIfAlreadyInGame(
      final Audience audience, final @Nullable Triplet<PreGameManager, Boolean, Boolean> data) {
    if (data != null) {
      final Component message = Message.GAME_JOIN_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean checkIfNotInvited(
      final Audience audience, final Player sender, final Player owner) {
    final Collection<Player> invitations = this.invites.get(sender);
    if (!invitations.contains(owner)) {
      final Component message = Message.GAME_INVALID_INVITE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @Permission("murderrun.command.game.list")
  @CommandDescription("murderrun.command.game.list.info")
  @Command(value = "murder game list", requiredSender = Player.class)
  public void listPlayers(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final Triplet<PreGameManager, Boolean, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data)) {
      return;
    }

    final PreGameManager manager = data.first();
    final List<String> names = this.constructPlayerList(manager);
    final Component message = Message.GAME_LIST.build(names);
    audience.sendMessage(message);
  }

  private List<String> constructPlayerList(final PreGameManager manager) {
    final Collection<Player> participants = manager.getParticipants();
    final Collection<Player> murderers = manager.getMurderers();
    final List<String> names = new ArrayList<>();
    for (final Player player : participants) {
      String name = player.getDisplayName();
      name += murderers.contains(player) ? " (Killer)" : "";
      names.add(name);
    }
    return names;
  }

  @Permission("murderrun.command.game.kick")
  @CommandDescription("murderrun.command.game.kick.info")
  @Command(value = "murder game kick <kick>", requiredSender = Player.class)
  public void kickPlayer(final Player sender, final Player kick) {

    final Audience audience = this.audiences.player(sender);
    final Triplet<PreGameManager, Boolean, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data)
        || this.checkIfNotOwner(audience, data)
        || this.checkIfOwnerOfCurrentGame(audience, data)) {
      return;
    }

    final PreGameManager manager = data.first();
    manager.removeParticipantFromLobby(kick);
    this.games.remove(kick);

    final Collection<Player> invited = this.invites.get(sender);
    invited.remove(kick);

    final String name = kick.getDisplayName();
    final Audience player = this.audiences.player(kick);
    final Component ownerMessage = Message.GAME_OWNER_KICK.build(name);
    final Component kickedMessage = Message.GAME_PLAYER_KICK.build();
    audience.sendMessage(ownerMessage);
    player.sendMessage(kickedMessage);
  }

  @Permission("murderrun.command.game.leave")
  @CommandDescription("murderrun.command.game.leave.info")
  @Command(value = "murder game leave", requiredSender = Player.class)
  public void leaveGame(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final Triplet<PreGameManager, Boolean, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfOwnerOfCurrentGame(audience, data)) {
      return;
    }

    final PreGameManager manager = data.first();
    manager.removeParticipantFromLobby(sender);
    this.games.remove(sender);
    sender.setHealth(0.0);

    final Component message = Message.GAME_LEFT.build();
    audience.sendMessage(message);
  }

  private boolean checkIfOwnerOfCurrentGame(
      final Audience audience, final Triplet<PreGameManager, Boolean, Boolean> data) {
    final boolean owner = data.second();
    if (owner) {
      final Component message = Message.GAME_LEAVE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @Permission("murderrun.command.game.set.killer")
  @CommandDescription("murderrun.command.game.set.killer.info")
  @Command(value = "murder game set murderer <murderer>", requiredSender = Player.class)
  public void setMurderer(final Player sender, final Player murderer) {

    final Audience audience = this.audiences.player(sender);
    final Triplet<PreGameManager, Boolean, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfNotOwner(audience, data)) {
      return;
    }

    final String name = murderer.getDisplayName();
    final PreGameManager manager = data.first();
    manager.setPlayerToMurderer(murderer);

    final Component message = Message.GAME_SET_MURDERER.build(name);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.set.survivor")
  @CommandDescription("murderrun.command.game.set.survivor.info")
  @Command(value = "murder game set innocent <innocent>", requiredSender = Player.class)
  public void setInnocent(final Player sender, final Player innocent) {

    final Audience audience = this.audiences.player(sender);
    final Triplet<PreGameManager, Boolean, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfNotOwner(audience, data)) {
      return;
    }

    final String name = innocent.getDisplayName();
    final PreGameManager manager = data.first();
    manager.setPlayerToInnocent(innocent);

    final Component message = Message.GAME_SET_INNOCENT.build(name);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.gui")
  @CommandDescription("murderrun.command.game.gui.info")
  @Command(value = "murder game gui", requiredSender = Player.class)
  public void openGameGui(final Player sender) {
    final PlayerListGui gui = new PlayerListGui(sender, this.games);
    gui.update();
    gui.show(sender);
  }

  @Suggestions("arena-suggestions")
  public List<String> arenaSuggestions(
      final CommandContext<CommandSender> context, final String input) {
    final ArenaManager manager = this.plugin.getArenaManager();
    return new ArrayList<>(manager.getArenaNames());
  }

  @Suggestions("lobby-suggestions")
  public List<String> lobbySuggestions(
      final CommandContext<CommandSender> context, final String input) {
    final LobbyManager manager = this.plugin.getLobbyManager();
    return new ArrayList<>(manager.getLobbyNames());
  }
}