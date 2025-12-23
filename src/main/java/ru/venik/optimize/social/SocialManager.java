/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.social;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.venik.optimize.VenikOptimize;
import ru.venik.optimize.config.ConfigManager;

public class SocialManager {
    private final VenikOptimize plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Set<UUID>> friends = new ConcurrentHashMap<UUID, Set<UUID>>();
    private final Map<UUID, Set<UUID>> ignored = new ConcurrentHashMap<UUID, Set<UUID>>();
    private final Map<UUID, List<UUID>> friendRequests = new ConcurrentHashMap<UUID, List<UUID>>();

    public SocialManager(VenikOptimize plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void sendFriendRequest(Player sender, Player receiver) {
        if (receiver == null || !receiver.isOnline()) {
            sender.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        if (this.isFriend(sender, receiver)) {
            sender.sendMessage("\u00a7c\u0412\u044b \u0443\u0436\u0435 \u0434\u0440\u0443\u0437\u044c\u044f!");
            return;
        }
        this.friendRequests.computeIfAbsent(receiver.getUniqueId(), k -> new ArrayList()).add(sender.getUniqueId());
        sender.sendMessage("\u00a7a\u0417\u0430\u043f\u0440\u043e\u0441 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d \u0438\u0433\u0440\u043e\u043a\u0443 \u00a7e" + receiver.getName());
        receiver.sendMessage("\u00a7e" + sender.getName() + "\u00a77 \u0445\u043e\u0447\u0435\u0442 \u0434\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0432\u0430\u0441 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f!");
        receiver.sendMessage("\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435: \u00a7e/friend accept " + sender.getName() + " \u00a77\u0438\u043b\u0438 \u00a7c/friend deny " + sender.getName());
    }

    public void acceptFriendRequest(Player player, Player sender) {
        if (sender == null) {
            player.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        List<UUID> requests = this.friendRequests.get(player.getUniqueId());
        if (requests == null || !requests.contains(sender.getUniqueId())) {
            player.sendMessage("\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0437\u0430\u043f\u0440\u043e\u0441\u0430 \u043e\u0442 \u044d\u0442\u043e\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430!");
            return;
        }
        requests.remove(sender.getUniqueId());
        this.addFriend(player, sender);
        this.addFriend(sender, player);
        player.sendMessage("\u00a7a\u0412\u044b \u0441\u0442\u0430\u043b\u0438 \u0434\u0440\u0443\u0437\u044c\u044f\u043c\u0438 \u0441 \u00a7e" + sender.getName() + "\u00a7a!");
        sender.sendMessage("\u00a7a\u0412\u044b \u0441\u0442\u0430\u043b\u0438 \u0434\u0440\u0443\u0437\u044c\u044f\u043c\u0438 \u0441 \u00a7e" + player.getName() + "\u00a7a!");
    }

    public void denyFriendRequest(Player player, Player sender) {
        List<UUID> requests = this.friendRequests.get(player.getUniqueId());
        if (requests != null) {
            requests.remove(sender.getUniqueId());
        }
        player.sendMessage("\u00a7c\u0412\u044b \u043e\u0442\u043a\u043b\u043e\u043d\u0438\u043b\u0438 \u0437\u0430\u043f\u0440\u043e\u0441 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f \u043e\u0442 \u00a7e" + sender.getName());
    }

    public void removeFriend(Player player, Player friend) {
        if (!this.isFriend(player, friend)) {
            player.sendMessage("\u00a7c\u042d\u0442\u043e\u0442 \u0438\u0433\u0440\u043e\u043a \u043d\u0435 \u0432 \u0432\u0430\u0448\u0435\u043c \u0441\u043f\u0438\u0441\u043a\u0435 \u0434\u0440\u0443\u0437\u0435\u0439!");
            return;
        }
        this.friends.get(player.getUniqueId()).remove(friend.getUniqueId());
        this.friends.get(friend.getUniqueId()).remove(player.getUniqueId());
        player.sendMessage("\u00a7a\u0412\u044b \u0443\u0434\u0430\u043b\u0438\u043b\u0438 \u00a7e" + friend.getName() + "\u00a7a \u0438\u0437 \u0434\u0440\u0443\u0437\u0435\u0439!");
    }

    private void addFriend(Player player1, Player player2) {
        this.friends.computeIfAbsent(player1.getUniqueId(), k -> ConcurrentHashMap.newKeySet()).add(player2.getUniqueId());
    }

    public boolean isFriend(Player player1, Player player2) {
        return this.friends.getOrDefault(player1.getUniqueId(), Collections.emptySet()).contains(player2.getUniqueId());
    }

    public void ignorePlayer(Player player, Player target) {
        if (target == null) {
            player.sendMessage("\u00a7c\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
            return;
        }
        this.ignored.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet()).add(target.getUniqueId());
        player.sendMessage("\u00a7a\u0412\u044b \u0438\u0433\u043d\u043e\u0440\u0438\u0440\u0443\u0435\u0442\u0435 \u00a7e" + target.getName());
    }

    public void unignorePlayer(Player player, Player target) {
        Set<UUID> ignoredSet = this.ignored.get(player.getUniqueId());
        if (ignoredSet == null || !ignoredSet.contains(target.getUniqueId())) {
            player.sendMessage("\u00a7c\u0412\u044b \u043d\u0435 \u0438\u0433\u043d\u043e\u0440\u0438\u0440\u0443\u0435\u0442\u0435 \u044d\u0442\u043e\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430!");
            return;
        }
        ignoredSet.remove(target.getUniqueId());
        player.sendMessage("\u00a7a\u0412\u044b \u0431\u043e\u043b\u044c\u0448\u0435 \u043d\u0435 \u0438\u0433\u043d\u043e\u0440\u0438\u0440\u0443\u0435\u0442\u0435 \u00a7e" + target.getName());
    }

    public boolean isIgnored(Player player, Player target) {
        return this.ignored.getOrDefault(player.getUniqueId(), Collections.emptySet()).contains(target.getUniqueId());
    }

    public List<String> getFriendsList(Player player) {
        Set<UUID> friendsSet = this.friends.get(player.getUniqueId());
        if (friendsSet == null || friendsSet.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> friendsList = new ArrayList<String>();
        for (UUID friendUuid : friendsSet) {
            Player friend = Bukkit.getPlayer((UUID)friendUuid);
            if (friend != null && friend.isOnline()) {
                friendsList.add("\u00a7a" + friend.getName());
                continue;
            }
            friendsList.add("\u00a77" + (Bukkit.getOfflinePlayer((UUID)friendUuid).getName() != null ? Bukkit.getOfflinePlayer((UUID)friendUuid).getName() : "Unknown"));
        }
        return friendsList;
    }
}

