package io.github.pulsebeat02.murderrun.commmand.game;

import static java.util.Objects.requireNonNull;

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
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class GameCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;
  private GameInputSanitizer sanitizer;
  private GameManager manager;
  private InviteManager invites;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.sanitizer = new GameInputSanitizer(this);
    this.manager = new GameManager(plugin);
    this.invites = new InviteManager();
    this.plugin = plugin;
  }

  @Permission("murderrun.command.game.start")
  @CommandDescription("murderrun.command.game.start.info")
  @Command(value = "murder game start", requiredSender = CommandSender.class)
  public void startGame(final CommandSender sender) {

    final Audience audience = this.audiences.sender(sender);
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)
        || this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data))
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
      @Quoted final String id,
      final int min,
      final int max,
      final boolean quickJoinable) {

    final Audience audience = this.audiences.sender(sender);
    final PreGameManager temp = this.manager.getGameAsParticipant(sender);
    if (this.sanitizer.checkIfAlreadyInGame(audience, temp)
        || this.sanitizer.checkIfArenaValid(audience, arenaName)
        || this.sanitizer.checkIfLobbyValid(audience, lobbyName)
        || this.sanitizer.checkIfInvalidPlayerCounts(audience, min, max)) {
      return;
    }
    this.manager.createGame(sender, id, arenaName, lobbyName, min, max, quickJoinable);

    final Component message = Message.GAME_CREATED.build();
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.cancel")
  @CommandDescription("murderrun.command.game.cancel.info")
  @Command(value = "murder game cancel", requiredSender = Player.class)
  public void cancelGame(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)
        || this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data))) {
      return;
    }

    final String id = data.getId();
    this.manager.removeGame(id);

    final PreGamePlayerManager playerManager = data.getPlayerManager();
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
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)
        || this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data))
        || this.sanitizer.checkIfNotSamePlayer(audience, sender, invite)
        || this.sanitizer.checkIfInvitedAlreadyInGame(audience, invite, data)) {
      return;
    }
    this.invites.invitePlayer(sender, invite);

    final PreGameManager target = requireNonNull(this.manager.getGame(sender));
    final String id = target.getId();
    final String inviteDisplayName = invite.getDisplayName();
    final Component owner = Message.GAME_OWNER_INVITE.build(inviteDisplayName);
    final Component player = Message.GAME_PLAYER_INVITE.build(id);
    final Audience invited = this.audiences.player(invite);
    audience.sendMessage(owner);
    invited.sendMessage(player);
  }

  @Permission("murderrun.command.game.join")
  @CommandDescription("murderrun.command.game.join.info")
  @Command(value = "murder game join <id>", requiredSender = Player.class)
  public void joinGame(final Player sender, @Quoted final String id) {

    final Audience audience = this.audiences.player(sender);
    final PreGameManager temp = this.manager.getGame(sender);
    if (this.sanitizer.checkIfAlreadyInGame(audience, temp)
        || this.sanitizer.checkIfNotInvited(audience, sender, id)) {
      return;
    }

    final PreGameManager data = requireNonNull(this.manager.getGame(id));
    if (this.sanitizer.checkIfGameFull(sender, audience, this.manager, data)) {
      return;
    }

    final PreGamePlayerManager playerManager = data.getPlayerManager();
    final CommandSender leader = playerManager.getLeader();
    this.invites.removeInvite(leader, sender);
    this.sendJoinMessage(sender, data);
  }

  private void sendJoinMessage(final Player sender, final PreGameManager data) {
    final PreGamePlayerManager playerManager = data.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    final String name = sender.getDisplayName();
    final Component message = Message.GAME_JOIN.build(name);
    for (final Player player : participants) {
      final Audience member = this.audiences.player(player);
      member.sendMessage(message);
    }
  }

  @Permission("murderrun.command.game.list")
  @CommandDescription("murderrun.command.game.list.info")
  @Command(value = "murder game list", requiredSender = Player.class)
  public void listPlayers(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)) {
      return;
    }

    final List<String> names = this.constructPlayerList(requireNonNull(data));
    final Component message = Message.GAME_LIST.build(names);
    audience.sendMessage(message);
  }

  private List<String> constructPlayerList(final PreGameManager manager) {
    final PreGamePlayerManager playerManager = manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    final Collection<Player> murderers = playerManager.getMurderers();
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
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)
        || this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data))
        || this.sanitizer.checkIfOwnerOfCurrentGame(sender, audience, data)) {
      return;
    }
    this.manager.leaveGame(kick);
    this.invites.removeInvite(sender, kick);

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
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)
        || this.sanitizer.checkIfOwnerOfCurrentGame(sender, audience, requireNonNull(data))) {
      return;
    }
    this.manager.leaveGame(sender);

    sender.setHealth(0.0);

    final Component message = Message.GAME_LEFT.build();
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.set.killer")
  @CommandDescription("murderrun.command.game.set.killer.info")
  @Command(value = "murder game set murderer <murderer>", requiredSender = Player.class)
  public void setMurderer(final Player sender, final Player murderer) {

    final Audience audience = this.audiences.player(sender);
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)
        || this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data))) {
      return;
    }

    final String name = murderer.getDisplayName();
    final PreGamePlayerManager playerManager = data.getPlayerManager();
    playerManager.setPlayerToMurderer(murderer);

    final Component message = Message.GAME_SET_MURDERER.build(name);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.set.survivor")
  @CommandDescription("murderrun.command.game.set.survivor.info")
  @Command(value = "murder game set innocent <innocent>", requiredSender = Player.class)
  public void setInnocent(final Player sender, final Player innocent) {

    final Audience audience = this.audiences.player(sender);
    final PreGameManager data = this.manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)
        || this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data))) {
      return;
    }

    final String name = innocent.getDisplayName();
    final PreGamePlayerManager playerManager = data.getPlayerManager();
    playerManager.setPlayerToInnocent(innocent);

    final Component message = Message.GAME_SET_INNOCENT.build(name);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.quickjoin")
  @CommandDescription("murderrun.command.game.quickjoin.info")
  @Command(value = "murder game quickjoin", requiredSender = Player.class)
  public void quickJoinGame(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final PreGameManager temp = this.manager.getGame(sender);
    if (this.sanitizer.checkIfAlreadyInGame(audience, temp)
        || this.sanitizer.checkIfNoQuickJoinableGame(sender, audience, this.manager)) {
      return;
    }

    final PreGameManager data = requireNonNull(this.manager.getGame(sender));
    final PreGamePlayerManager playerManager = data.getPlayerManager();
    final CommandSender leader = playerManager.getLeader();
    this.invites.removeInvite(leader, sender);
    this.sendJoinMessage(sender, (data));
  }

  @Permission("murderrun.command.game.gui")
  @CommandDescription("murderrun.command.game.gui.info")
  @Command(value = "murder game gui", requiredSender = Player.class)
  public void openGameGui(final Player sender) {
    final PlayerListGui gui = new PlayerListGui(sender, this.manager);
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

  public GameManager getGameManager() {
    return this.manager;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public BukkitAudiences getAudiences() {
    return this.audiences;
  }

  public GameInputSanitizer getSanitizer() {
    return this.sanitizer;
  }

  public GameManager getManager() {
    return this.manager;
  }

  public InviteManager getInviteManager() {
    return this.invites;
  }
}
