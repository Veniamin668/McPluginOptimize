/*
 * Decompiled with CFR 0.152.
 */
package ru.venik.optimize;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.venik.optimize.bounty.BountyManager;
import ru.venik.optimize.cases.CaseManager;
import ru.venik.optimize.chat.ChatManager;
import ru.venik.optimize.cleanup.CleanupManager;
import ru.venik.optimize.commands.BountyCommand;
import ru.venik.optimize.commands.CaseCommand;
import ru.venik.optimize.commands.DonateCommand;
import ru.venik.optimize.commands.EconomyCommand;
import ru.venik.optimize.commands.HomeCommand;
import ru.venik.optimize.commands.KitCommand;
import ru.venik.optimize.commands.LobbyCommand;
import ru.venik.optimize.commands.MenuCommand;
import ru.venik.optimize.commands.MessageCommand;
import ru.venik.optimize.commands.ModeratorCommands;
import ru.venik.optimize.commands.OptimizeCommand;
import ru.venik.optimize.commands.RTPCommand;
import ru.venik.optimize.commands.SocialCommand;
import ru.venik.optimize.commands.TeleportCommand;
import ru.venik.optimize.commands.TradeCommand;
import ru.venik.optimize.commands.UtilityCommands;
import ru.venik.optimize.commands.WarpCommand;
import ru.venik.optimize.config.ConfigManager;
import ru.venik.optimize.donate.DonationManager;
import ru.venik.optimize.home.HomeManager;
import ru.venik.optimize.kit.KitManager;
import ru.venik.optimize.listeners.CaseListener;
import ru.venik.optimize.listeners.ChatListener;
import ru.venik.optimize.listeners.ModeratorListener;
import ru.venik.optimize.listeners.PvPListener;
import ru.venik.optimize.listeners.TradeListener;
import ru.venik.optimize.menu.MenuListener;
import ru.venik.optimize.menu.MenuManager;
import ru.venik.optimize.optimization.ChunkOptimizer;
import ru.venik.optimize.optimization.EntityOptimizer;
import ru.venik.optimize.optimization.FarmOptimizer;
import ru.venik.optimize.optimization.RedstoneOptimizer;
import ru.venik.optimize.performance.BlockProfiler;
import ru.venik.optimize.performance.PerformanceMonitor;
import ru.venik.optimize.pvp.PvPManager;
import ru.venik.optimize.rtp.RandomTPManager;
import ru.venik.optimize.scoreboard.ScoreboardManager;
import ru.venik.optimize.social.SocialManager;
import ru.venik.optimize.tab.TabListManager;
import ru.venik.optimize.teleport.TeleportManager;
import ru.venik.optimize.trade.TradeManager;
import ru.venik.optimize.warp.WarpManager;
import ru.venik.optimize.world.WorldManager;
import ru.venik.optimize.auth.AuthManager;
import ru.venik.optimize.auth.AuthListener;
import ru.venik.optimize.auth.LoginCommand;
import ru.venik.optimize.npc.NPCManager;
import ru.venik.optimize.npc.NPCCommand;
import ru.venik.optimize.chat.AntiSpamManager;
import ru.venik.optimize.auth.RegisterCommand;

public final class VenikOptimize
extends JavaPlugin {
    private static VenikOptimize instance;
    private ConfigManager configManager;
    private PerformanceMonitor performanceMonitor;
    private BlockProfiler blockProfiler;
    private EntityOptimizer entityOptimizer;
    private ChunkOptimizer chunkOptimizer;
    private RedstoneOptimizer redstoneOptimizer;
    private FarmOptimizer farmOptimizer;
    private CleanupManager cleanupManager;
    private TabListManager tabListManager;
    private ScoreboardManager scoreboardManager;
    private CaseManager caseManager;
    private DonationManager donationManager;
    private MenuManager menuManager;
    private MenuListener menuListener;
    private WarpManager warpManager;
    private HomeManager homeManager;
    private KitManager kitManager;
    private TeleportManager teleportManager;
    private ModeratorCommands moderatorCommands;
    private TradeManager tradeManager;
    private PvPManager pvpManager;
    private BountyManager bountyManager;
    private ChatManager chatManager;
    private SocialManager socialManager;
    private WorldManager worldManager;
    private RandomTPManager randomTPManager;
    private AuthManager authManager;
    private ru.venik.optimize.npc.NPCManager npcManager;
    private ru.venik.optimize.protection.IPManager ipManager;
    private ru.venik.optimize.chat.AntiSpamManager antiSpamManager;

    public void onEnable() {
        instance = this;
        this.getLogger().info("========================================");
        this.getLogger().info("VenikCraft v" + this.getDescription().getVersion() + " enabled!");
        this.getLogger().info("Minecraft 1.21.4 | Java 21");
        this.getLogger().info("========================================");
        this.configManager = new ConfigManager(this);
        this.getLogger().info("Configuration loaded");
        this.performanceMonitor = new PerformanceMonitor(this, this.configManager);
        if (this.configManager.isPerformanceEnabled()) {
            this.performanceMonitor.start();
            this.getLogger().info("Performance monitor started");
        }
        this.blockProfiler = new BlockProfiler(this, this.configManager);
        if (this.configManager.isBlockProfilerEnabled()) {
            this.blockProfiler.start();
            this.getLogger().info("Block profiler started");
        }
        this.entityOptimizer = new EntityOptimizer(this, this.configManager);
        if (this.configManager.isEntityOptimizationEnabled()) {
            this.entityOptimizer.start();
            this.getLogger().info("Entity optimizer started");
        }
        this.chunkOptimizer = new ChunkOptimizer(this, this.configManager);
        if (this.configManager.isChunkOptimizationEnabled()) {
            this.chunkOptimizer.start();
            this.getLogger().info("Chunk optimizer started");
        }
        this.redstoneOptimizer = new RedstoneOptimizer(this, this.configManager);
        if (this.configManager.isRedstoneOptimizationEnabled()) {
            this.redstoneOptimizer.start();
            this.getLogger().info("Redstone optimizer started");
        }
        this.farmOptimizer = new FarmOptimizer(this, this.configManager);
        this.farmOptimizer.start();
        this.getLogger().info("Farm optimizer started");
        this.cleanupManager = new CleanupManager(this, this.configManager);
        if (this.configManager.isCleanupEnabled()) {
            this.cleanupManager.start();
            this.getLogger().info("Cleanup manager started");
        }
        this.tabListManager = new TabListManager(this, this.configManager);
        if (this.configManager.getConfig().getBoolean("tab.enabled", true)) {
            this.tabListManager.start();
            this.getLogger().info("Tab list manager started");
        }
        this.scoreboardManager = new ScoreboardManager(this, this.configManager);
        if (this.configManager.getConfig().getBoolean("scoreboard.enabled", true)) {
            this.scoreboardManager.start();
            this.getLogger().info("Scoreboard manager started");
        }
        this.caseManager = new CaseManager(this, this.configManager);
        if (this.configManager.getConfig().getBoolean("cases.enabled", true)) {
            this.getLogger().info("Case manager initialized");
        }
        this.donationManager = new DonationManager(this, this.configManager);
        if (this.configManager.getConfig().getBoolean("donate.enabled", true)) {
            this.getLogger().info("Donation manager initialized");
        }
        this.menuManager = new MenuManager(this, this.configManager);
        this.menuListener = new MenuListener(this);
        this.getServer().getPluginManager().registerEvents((Listener)this.menuListener, (Plugin)this);
        this.getLogger().info("Menu system initialized");
        this.warpManager = new WarpManager(this, this.configManager);
        this.getLogger().info("Warp manager initialized");
        this.homeManager = new HomeManager(this, this.configManager);
        this.getLogger().info("Home manager initialized");
        this.kitManager = new KitManager(this, this.configManager);
        this.getLogger().info("Kit manager initialized");
        this.teleportManager = new TeleportManager(this, this.configManager);
        this.getLogger().info("Teleport manager initialized");
        this.moderatorCommands = new ModeratorCommands(this);
        this.getServer().getPluginManager().registerEvents((Listener)new ModeratorListener(this, this.moderatorCommands), (Plugin)this);
        this.getLogger().info("Moderator system initialized");
        this.tradeManager = new TradeManager(this, this.configManager);
        this.getServer().getPluginManager().registerEvents((Listener)new TradeListener(this), (Plugin)this);
        this.getLogger().info("Trade system initialized");
        this.pvpManager = new PvPManager(this, this.configManager);
        this.pvpManager.start();
        this.getServer().getPluginManager().registerEvents((Listener)new PvPListener(this), (Plugin)this);
        this.getLogger().info("PvP system initialized");
        this.bountyManager = new BountyManager(this, this.configManager);
        this.getLogger().info("Bounty system initialized");
        this.chatManager = new ChatManager(this, this.configManager);
        this.getServer().getPluginManager().registerEvents((Listener)new ChatListener(this), (Plugin)this);
        this.getLogger().info("Chat system initialized");
        this.socialManager = new SocialManager(this, this.configManager);
        this.getLogger().info("Social system initialized");
        this.worldManager = new WorldManager(this, this.configManager);
        this.worldManager.initializeWorlds();
        this.getLogger().info("World manager initialized");
        this.randomTPManager = new RandomTPManager(this, this.configManager);
        this.getLogger().info("Random TP system initialized");
        this.npcManager = new ru.venik.optimize.npc.NPCManager(this);


        /* Authentication system */
        this.authManager = new AuthManager(this);
        this.getServer().getPluginManager().registerEvents(new AuthListener(this, this.authManager), this);
        this.getCommand("login").setExecutor(new LoginCommand(this.authManager));
        this.getCommand("l").setExecutor(new LoginCommand(this.authManager));
        this.getCommand("register").setExecutor(new RegisterCommand(this.authManager));
        this.getLogger().info("Auth system initialized");

        /* NPC system */
        NPCManager npcManager = new NPCManager(this);
        npcManager.start();
        this.getCommand("npc").setExecutor(new NPCCommand(npcManager));
        this.getLogger().info("NPC system initialized");

        /* IP protection */
        this.ipManager = new ru.venik.optimize.protection.IPManager(this);
        this.getLogger().info("IP protection initialized");

        /* Anti-spam */
        this.antiSpamManager = new ru.venik.optimize.chat.AntiSpamManager(this);
        this.getLogger().info("Anti-spam initialized");
        OptimizeCommand optimizeCommand = new OptimizeCommand(this, this.performanceMonitor, this.blockProfiler, this.cleanupManager);
        this.getCommand("venikoptimize").setExecutor((CommandExecutor)optimizeCommand);
        this.getCommand("venikoptimize").setTabCompleter((TabCompleter)optimizeCommand);
        CaseCommand caseCommand = new CaseCommand(this, this.caseManager);
        this.getCommand("case").setExecutor((CommandExecutor)caseCommand);
        this.getCommand("case").setTabCompleter((TabCompleter)caseCommand);
        DonateCommand donateCommand = new DonateCommand(this, this.donationManager);
        this.getCommand("donate").setExecutor((CommandExecutor)donateCommand);
        this.getCommand("donate").setTabCompleter((TabCompleter)donateCommand);
        MenuCommand menuCommand = new MenuCommand(this);
        this.getCommand("menu").setExecutor((CommandExecutor)menuCommand);
        WarpCommand warpCommand = new WarpCommand(this);
        this.getCommand("warp").setExecutor((CommandExecutor)warpCommand);
        this.getCommand("warp").setTabCompleter((TabCompleter)warpCommand);
        HomeCommand homeCommand = new HomeCommand(this);
        this.getCommand("home").setExecutor((CommandExecutor)homeCommand);
        this.getCommand("home").setTabCompleter((TabCompleter)homeCommand);
        KitCommand kitCommand = new KitCommand(this);
        this.getCommand("kit").setExecutor((CommandExecutor)kitCommand);
        this.getCommand("kit").setTabCompleter((TabCompleter)kitCommand);
        TeleportCommand teleportCommand = new TeleportCommand(this);
        this.getCommand("tpa").setExecutor((CommandExecutor)teleportCommand);
        this.getCommand("tpa").setTabCompleter((TabCompleter)teleportCommand);
        this.getCommand("tpahere").setExecutor((CommandExecutor)teleportCommand);
        this.getCommand("tpaccept").setExecutor((CommandExecutor)teleportCommand);
        this.getCommand("tpdeny").setExecutor((CommandExecutor)teleportCommand);
        this.getCommand("back").setExecutor((CommandExecutor)teleportCommand);
        ModeratorCommands modCommands = this.moderatorCommands;
        this.getCommand("fly").setExecutor((CommandExecutor)modCommands);
        this.getCommand("fly").setTabCompleter((TabCompleter)modCommands);
        this.getCommand("god").setExecutor((CommandExecutor)modCommands);
        this.getCommand("god").setTabCompleter((TabCompleter)modCommands);
        this.getCommand("vanish").setExecutor((CommandExecutor)modCommands);
        this.getCommand("vanish").setTabCompleter((TabCompleter)modCommands);
        this.getCommand("v").setExecutor((CommandExecutor)modCommands);
        this.getCommand("speed").setExecutor((CommandExecutor)modCommands);
        this.getCommand("speed").setTabCompleter((TabCompleter)modCommands);
        this.getCommand("heal").setExecutor((CommandExecutor)modCommands);
        this.getCommand("feed").setExecutor((CommandExecutor)modCommands);
        UtilityCommands utilCommands = new UtilityCommands(this);
        this.getCommand("weather").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("time").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("clear").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("ci").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("repair").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("enchant").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("workbench").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("craft").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("enderchest").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("ec").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("gamemode").setExecutor((CommandExecutor)utilCommands);
        this.getCommand("gm").setExecutor((CommandExecutor)utilCommands);
        EconomyCommand economyCommand = new EconomyCommand(this);
        this.getCommand("pay").setExecutor((CommandExecutor)economyCommand);
        this.getCommand("pay").setTabCompleter((TabCompleter)economyCommand);
        this.getCommand("balance").setExecutor((CommandExecutor)economyCommand);
        this.getCommand("bal").setExecutor((CommandExecutor)economyCommand);
        this.getCommand("money").setExecutor((CommandExecutor)economyCommand);
        this.getCommand("baltop").setExecutor((CommandExecutor)economyCommand);
        this.getCommand("top").setExecutor((CommandExecutor)economyCommand);
        TradeCommand tradeCommand = new TradeCommand(this);
        this.getCommand("trade").setExecutor((CommandExecutor)tradeCommand);
        BountyCommand bountyCommand = new BountyCommand(this);
        this.getCommand("bounty").setExecutor((CommandExecutor)bountyCommand);
        SocialCommand socialCommand = new SocialCommand(this);
        this.getCommand("friend").setExecutor((CommandExecutor)socialCommand);
        this.getCommand("f").setExecutor((CommandExecutor)socialCommand);
        this.getCommand("ignore").setExecutor((CommandExecutor)socialCommand);
        MessageCommand messageCommand = new MessageCommand(this);
        this.getCommand("msg").setExecutor((CommandExecutor)messageCommand);
        this.getCommand("tell").setExecutor((CommandExecutor)messageCommand);
        this.getCommand("w").setExecutor((CommandExecutor)messageCommand);
        this.getCommand("r").setExecutor((CommandExecutor)messageCommand);
        this.getCommand("reply").setExecutor((CommandExecutor)messageCommand);
        RTPCommand rtpCommand = new RTPCommand(this);
        this.getCommand("rtp").setExecutor((CommandExecutor)rtpCommand);
        this.getCommand("wild").setExecutor((CommandExecutor)rtpCommand);
        LobbyCommand lobbyCommand = new LobbyCommand(this);
        this.getCommand("lobby").setExecutor((CommandExecutor)lobbyCommand);
        this.getLogger().info("Commands registered");
        this.getServer().getPluginManager().registerEvents((Listener)new CaseListener(this, this.caseManager), (Plugin)this);
        this.getLogger().info("Listeners registered");

        // register global listeners
        this.getServer().getPluginManager().registerEvents(new ru.venik.optimize.listeners.ChatListener(this), this);

        this.getLogger().info("========================================");
        this.getLogger().info("VenikCraft successfully loaded!");
        this.getLogger().info("========================================");
    }

    public void onDisable() {
        this.getLogger().info("Shutting down VenikCraft...");
        if (this.performanceMonitor != null) {
            this.performanceMonitor.stop();
        }
        if (this.blockProfiler != null) {
            this.blockProfiler.stop();
        }
        if (this.entityOptimizer != null) {
            this.entityOptimizer.stop();
        }
        if (this.chunkOptimizer != null) {
            this.chunkOptimizer.stop();
        }
        if (this.redstoneOptimizer != null) {
            this.redstoneOptimizer.stop();
        }
        if (this.farmOptimizer != null) {
            this.farmOptimizer.stop();
        }
        if (this.cleanupManager != null) {
            this.cleanupManager.stop();
        }
        if (this.tabListManager != null) {
            this.tabListManager.stop();
        }
        if (this.scoreboardManager != null) {
            this.scoreboardManager.stop();
        }
        if (this.pvpManager != null) {
            this.pvpManager.stop();
        }
        this.getLogger().info("VenikCraft disabled!");
        instance = null;
    }

    public static VenikOptimize getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public PerformanceMonitor getPerformanceMonitor() {
        return this.performanceMonitor;
    }

    public BlockProfiler getBlockProfiler() {
        return this.blockProfiler;
    }

    public EntityOptimizer getEntityOptimizer() {
        return this.entityOptimizer;
    }

    public ChunkOptimizer getChunkOptimizer() {
        return this.chunkOptimizer;
    }

    public RedstoneOptimizer getRedstoneOptimizer() {
        return this.redstoneOptimizer;
    }

    public FarmOptimizer getFarmOptimizer() {
        return this.farmOptimizer;
    }

    public CleanupManager getCleanupManager() {
        return this.cleanupManager;
    }

    public TabListManager getTabListManager() {
        return this.tabListManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    public CaseManager getCaseManager() {
        return this.caseManager;
    }

    public DonationManager getDonationManager() {
        return this.donationManager;
    }

    public MenuManager getMenuManager() {
        return this.menuManager;
    }

    public MenuListener getMenuListener() {
        return this.menuListener;
    }

    public WarpManager getWarpManager() {
        return this.warpManager;
    }

    public HomeManager getHomeManager() {
        return this.homeManager;
    }

    public KitManager getKitManager() {
        return this.kitManager;
    }

    public TeleportManager getTeleportManager() {
        return this.teleportManager;
    }

    public ModeratorCommands getModeratorCommands() {
        return this.moderatorCommands;
    }

    public TradeManager getTradeManager() {
        return this.tradeManager;
    }

    public PvPManager getPvPManager() {
        return this.pvpManager;
    }

    public BountyManager getBountyManager() {
        return this.bountyManager;
    }

    public ChatManager getChatManager() {
        return this.chatManager;
    }

    public AntiSpamManager getAntiSpamManager() {
        return this.antiSpamManager;
    }

    public ru.venik.optimize.protection.IPManager getIpManager() {
        return this.ipManager;
    }

    public SocialManager getSocialManager() {
        return this.socialManager;
    }

    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    public RandomTPManager getRandomTPManager() {
        return this.randomTPManager;
    }
}

