package ru.venik.optimize.npc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommand implements CommandExecutor {
    private final NPCManager manager;

    public NPCCommand(NPCManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("venikoptimize.npc")) {
            player.sendMessage("No permission.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("/npc create <id> [randomize]");
            player.sendMessage("/npc remove <id>");
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("create") && args.length >= 2) {
            String id = args[1];
            boolean randomize = args.length >= 3 ? Boolean.parseBoolean(args[2]) : true;
            Location loc = player.getLocation();
            manager.createAndSave(id, loc, randomize);
            player.sendMessage("NPC " + id + " created.");
            return true;
        } else if (sub.equals("remove") && args.length >= 2) {
            String id = args[1];
            manager.remove(id);
            player.sendMessage("NPC " + id + " removed.");
            return true;
        }
        return true;
    }
}
