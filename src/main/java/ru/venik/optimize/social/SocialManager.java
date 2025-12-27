package ru.venik.optimize.social;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SocialManager:
 *  - друзья
 *  - игнор
 *  - запросы в друзья
 */
public class SocialManager {

    private final JavaPlugin plugin;

    // UUID -> Set<UUID>
    private final Map<UUID, Set<UUID>> friends = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> ignored = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> friendRequests = new ConcurrentHashMap<>();

    public SocialManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    // FRIEND REQUESTS
    // ------------------------------------------------------------

    public void sendFriendRequest(Player sender, Player receiver) {

        if (receiver == null || !receiver.isOnline()) {
            sender.sendMessage("§cИгрок не найден!");
            return;
        }

        if (sender.equals(receiver)) {
            sender.sendMessage("§cВы не можете добавить себя!");
            return;
        }

        if (isFriend(sender, receiver)) {
            sender.sendMessage("§cВы уже друзья!");
            return;
        }

        Set<UUID> requests = friendRequests.computeIfAbsent(receiver.getUniqueId(),
                k -> ConcurrentHashMap.newKeySet());

        if (requests.contains(sender.getUniqueId())) {
            sender.sendMessage("§cВы уже отправили запрос этому игроку!");
            return;
        }

        requests.add(sender.getUniqueId());

        sender.sendMessage("§aЗапрос отправлен игроку §e" + receiver.getName());
        receiver.sendMessage("§e" + sender.getName() + " §7хочет добавить вас в друзья!");
        receiver.sendMessage("§7Используйте: §e/friend accept " + sender.getName() +
                " §7или §c/friend deny " + sender.getName());
    }

    public void acceptFriendRequest(Player player, Player sender) {

        if (sender == null) {
            player.sendMessage("§cИгрок не найден!");
            return;
        }

        Set<UUID> requests = friendRequests.get(player.getUniqueId());

        if (requests == null || !requests.remove(sender.getUniqueId())) {
            player.sendMessage("§cУ вас нет запроса от этого игрока!");
            return;
        }

        addFriend(player.getUniqueId(), sender.getUniqueId());
        addFriend(sender.getUniqueId(), player.getUniqueId());

        player.sendMessage("§aВы стали друзьями с §e" + sender.getName());
        sender.sendMessage("§aВы стали друзьями с §e" + player.getName());

        if (requests.isEmpty()) {
            friendRequests.remove(player.getUniqueId());
        }
    }

    public void denyFriendRequest(Player player, Player sender) {

        Set<UUID> requests = friendRequests.get(player.getUniqueId());

        if (requests != null) {
            requests.remove(sender.getUniqueId());
            if (requests.isEmpty()) friendRequests.remove(player.getUniqueId());
        }

        player.sendMessage("§cВы отклонили запрос от §e" + sender.getName());
    }

    // ------------------------------------------------------------
    // FRIENDS
    // ------------------------------------------------------------

    public void removeFriend(Player player, Player friend) {

        if (!isFriend(player, friend)) {
            player.sendMessage("§cЭтот игрок не в вашем списке друзей!");
            return;
        }

        friends.get(player.getUniqueId()).remove(friend.getUniqueId());
        friends.get(friend.getUniqueId()).remove(player.getUniqueId());

        player.sendMessage("§aВы удалили §e" + friend.getName() + " §aиз друзей!");
        friend.sendMessage("§c" + player.getName() + " удалил вас из друзей.");
    }

    private void addFriend(UUID p1, UUID p2) {
        friends.computeIfAbsent(p1, k -> ConcurrentHashMap.newKeySet()).add(p2);
    }

    public boolean isFriend(Player p1, Player p2) {
        return friends.getOrDefault(p1.getUniqueId(), Collections.emptySet())
                .contains(p2.getUniqueId());
    }

    // ------------------------------------------------------------
    // IGNORE
    // ------------------------------------------------------------

    public void ignorePlayer(Player player, Player target) {

        if (player.equals(target)) {
            player.sendMessage("§cВы не можете игнорировать себя!");
            return;
        }

        ignored.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet())
                .add(target.getUniqueId());

        player.sendMessage("§aВы игнорируете §e" + target.getName());
    }

    public void unignorePlayer(Player player, Player target) {

        Set<UUID> set = ignored.get(player.getUniqueId());

        if (set == null || !set.remove(target.getUniqueId())) {
            player.sendMessage("§cВы не игнорируете этого игрока!");
            return;
        }

        player.sendMessage("§aВы больше не игнорируете §e" + target.getName());
    }

    public boolean isIgnored(Player player, Player target) {
        return ignored.getOrDefault(player.getUniqueId(), Collections.emptySet())
                .contains(target.getUniqueId());
    }

    // ------------------------------------------------------------
    // LISTS
    // ------------------------------------------------------------

    public List<String> getFriendsList(Player player) {

        Set<UUID> set = friends.get(player.getUniqueId());
        if (set == null || set.isEmpty()) return Collections.emptyList();

        List<String> list = new ArrayList<>();

        for (UUID id : set) {

            Player online = Bukkit.getPlayer(id);
            if (online != null) {
                list.add("§a" + online.getName());
                continue;
            }

            OfflinePlayer off = Bukkit.getOfflinePlayer(id);
            list.add("§7" + (off.getName() != null ? off.getName() : "Unknown"));
        }

        return list;
    }
}
