/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize.trade;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradeSession {
    private final Player player1;
    private final Player player2;
    private final Inventory inventory1;
    private final Inventory inventory2;
    private boolean player1Confirmed = false;
    private boolean player2Confirmed = false;

    public TradeSession(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.inventory1 = Bukkit.createInventory(null, (int)54, (String)("\u00a76\u0422\u043e\u0440\u0433\u043e\u0432\u043b\u044f: " + player1.getName()));
        this.inventory2 = Bukkit.createInventory(null, (int)54, (String)("\u00a76\u0422\u043e\u0440\u0433\u043e\u0432\u043b\u044f: " + player2.getName()));
        ItemStack confirmButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = confirmButton.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("\u00a7a\u00a7l\u041f\u041e\u0414\u0422\u0412\u0415\u0420\u0414\u0418\u0422\u042c");
            meta.setLore(List.of("\u00a77\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0434\u043b\u044f \u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u044f"));
            confirmButton.setItemMeta(meta);
        }
        this.inventory1.setItem(45, confirmButton);
        this.inventory2.setItem(45, confirmButton);
    }

    public void openInventories() {
        this.player1.openInventory(this.inventory1);
        this.player2.openInventory(this.inventory2);
    }

    public void confirm(Player player) {
        if (player.equals((Object)this.player1)) {
            this.player1Confirmed = true;
            this.player2.sendMessage("\u00a7e" + this.player1.getName() + " \u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0434\u0438\u043b \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e!");
        } else {
            this.player2Confirmed = true;
            this.player1.sendMessage("\u00a7e" + this.player2.getName() + " \u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0434\u0438\u043b \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e!");
        }
    }

    public boolean isBothConfirmed() {
        return this.player1Confirmed && this.player2Confirmed;
    }

    public void cancel() {
        this.player1.closeInventory();
        this.player2.closeInventory();
    }

    public void close() {
        this.player1.closeInventory();
        this.player2.closeInventory();
    }

    public List<ItemStack> getPlayer1Items() {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < 36; ++i) {
            ItemStack item = this.inventory1.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            items.add(item);
        }
        return items;
    }

    public List<ItemStack> getPlayer2Items() {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < 36; ++i) {
            ItemStack item = this.inventory2.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            items.add(item);
        }
        return items;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getOtherPlayer(Player player) {
        return player.equals((Object)this.player1) ? this.player2 : this.player1;
    }

    public Inventory getInventory1() {
        return this.inventory1;
    }

    public Inventory getInventory2() {
        return this.inventory2;
    }
}

