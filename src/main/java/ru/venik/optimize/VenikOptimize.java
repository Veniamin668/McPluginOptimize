package ru.venik.optimize;

import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.venik.optimize.auth.AuthListener;
import ru.venik.optimize.auth.AuthManager;
import ru.venik.optimize.auth.LoginCommand;
import ru.venik.optimize.auth.RegisterCommand;
import ru.venik.optimize.bounty.BountyManager;
import ru.venik.optimize.cases.CaseManager;
import ru.venik.optimize.chat.AntiSpamManager;
import ru.venik.optimize.chat.ChatManager;
import ru.venik.optimize.cleanup.CleanupManager;
import ru.venik.optimize.commands.*;
import ru.venik.optimize.config.ConfigManager;
import ru.venik.optimize.donate.DonationManager;
import ru.venik.optimize.home.HomeManager;
import ru.venik.optimize.kit.KitManager;
import ru.venik.optimize.listeners.*;
import ru.venik.optimize.menu.MenuListener;
import ru.venik.optimize.menu.MenuManager;
import ru.venik.optimize.npc.NPCCommand;
import ru.venik.optimize.npc.NPCManager;
import ru.venik.optimize.optimization.ChunkOptimizer;
import ru.venik.optimize.optimization.EntityOptimizer;
import ru.venik.optimize.optimization.FarmOptimizer;
import ru.venik.optimize.optimization.RedstoneOptimizer;
import ru.venik.optimize.performance.BlockProfiler;
import ru.venik.optimize.performance.PerformanceMonitor;
import ru.venik.optimize.protection.IPManager;
import ru.venik.optimize.pvp.PvPManager;
import ru.venik.optimize.rtp.RandomTPManager;
import ru.venik.optimize.scoreboard.ScoreboardManager;
import ru.venik.optimize.social.SocialManager;
import ru.venik.optimize.tab.TabListManager;
import ru.venik.optimize.teleport.TeleportManager;
import ru.venik.optimize.trade.TradeManager;
import ru.venik.optimize.warp.WarpManager;
import ru.venik.optimize.world.WorldManager;

public final class VenikOptimize extends JavaPlugin {

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
    private NPCManager npcManager;
    private IPManager ipManager;
    private AntiSpamManager antiSpamManager;

    @Override
    public void onEnable() {
        instance = this;

        logHeader("Enabling VenikCraft");

        initConfig();
        initPerformance();
        initOptimization();
        initCoreSystems();
        initWorlds();
        initRandomTP();
        initAuth();
        initNpc();
        initProtection();
        initAntiSpam();

        registerCommands();
        registerListeners();

        logFooter("VenikCraft successfully loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down VenikCraft...");

        if (performanceMonitor != null) performanceMonitor.stop();
        if (blockProfiler != null) blockProfiler.stop();
        if (entityOptimizer != null) entityOptimizer.stop();
        if (chunkOptimizer != null) chunkOptimizer.stop();
        if (redstoneOptimizer != null) redstoneOptimizer.stop();
        if (farmOptimizer != null) farmOptimizer.stop();
        if (cleanupManager != null) cleanupManager.stop();
        if (tabListManager != null) tabListManager.stop();
        if (scoreboardManager != null) scoreboardManager.stop();
        if (pvpManager != null) pvpManager.stop();

        getLogger().info("VenikCraft disabled!");
        instance = null;
    }

    // ------------------------------------------------------------
    // INIT SECTIONS
    // ------------------------------------------------------------

    private void initConfig() {
        configManager = new ConfigManager(this);
        getLogger().info("Configuration loaded");
    }

    private void initPerformance() {
        // constructor requires: JavaPlugin
        performanceMonitor = new PerformanceMonitor(this);
        if (configManager.isPerformanceEnabled()) {
            performanceMonitor.start();
            getLogger().info("Performance monitor started");
        }

        // constructor requires: JavaPlugin
        blockProfiler = new BlockProfiler(this);
        if (configManager.isBlockProfilerEnabled()) {
            blockProfiler.start();
            getLogger().info("Block profiler started");
        }
    }

    private void initOptimization() {
        // EntityOptimizer(JavaPlugin, int, boolean, double, boolean, long)
        int maxMobsPerChunk = configManager.getConfig().getInt("optimization.entities.max-mobs-per-chunk", 30);
        boolean removeHostile = configManager.getConfig().getBoolean("optimization.entities.remove-hostile-on-low-tps", true);
        double checkRadius = configManager.getConfig().getDouble("optimization.entities.check-radius", 64.0);
        boolean entityDebug = configManager.getConfig().getBoolean("optimization.entities.debug", false);
        long entityInterval = configManager.getConfig().getLong("optimization.entities.interval-ticks", 200L);

        entityOptimizer = new EntityOptimizer(this, maxMobsPerChunk, removeHostile, checkRadius, entityDebug, entityInterval);
        if (configManager.isEntityOptimizationEnabled()) {
            entityOptimizer.start();
            getLogger().info("Entity optimizer started");
        }

        // ChunkOptimizer(JavaPlugin, int, long, boolean)
        int unloadRadius = configManager.getConfig().getInt("optimization.chunks.unload-radius", 4);
        long chunkInterval = configManager.getConfig().getLong("optimization.chunks.interval-ticks", 200L);
        boolean aggressiveChunks = configManager.getConfig().getBoolean("optimization.chunks.aggressive", false);

        chunkOptimizer = new ChunkOptimizer(this, unloadRadius, chunkInterval, aggressiveChunks);
        if (configManager.isChunkOptimizationEnabled()) {
            chunkOptimizer.start();
            getLogger().info("Chunk optimizer started");
        }

        // RedstoneOptimizer(JavaPlugin, int, int, boolean, boolean)
        int maxClocks = configManager.getConfig().getInt("optimization.redstone.max-clocks", 100);
        int maxPowerPerTick = configManager.getConfig().getInt("optimization.redstone.max-power-per-tick", 500);
        boolean disableClocks = configManager.getConfig().getBoolean("optimization.redstone.disable-clocks", true);
        boolean redstoneDebug = configManager.getConfig().getBoolean("optimization.redstone.debug", false);

        redstoneOptimizer = new RedstoneOptimizer(this, maxClocks, maxPowerPerTick, disableClocks, redstoneDebug);
        if (configManager.isRedstoneOptimizationEnabled()) {
            redstoneOptimizer.start();
            getLogger().info("Redstone optimizer started");
        }

        // FarmOptimizer(JavaPlugin, boolean, boolean, int, double)
        boolean limitAnimals = configManager.getConfig().getBoolean("optimization.farms.limit-animals", true);
        boolean limitCrops = configManager.getConfig().getBoolean("optimization.farms.limit-crops", true);
        int maxPerChunk = configManager.getConfig().getInt("optimization.farms.max-per-chunk", 200);
        double farmRadius = configManager.getConfig().getDouble("optimization.farms.check-radius", 64.0);

        farmOptimizer = new FarmOptimizer(this, limitAnimals, limitCrops, maxPerChunk, farmRadius);
        farmOptimizer.start();
        getLogger().info("Farm optimizer started");

        // CleanupManager(JavaPlugin, ConfigManager) по логу: ctor совпадает
        cleanupManager = new CleanupManager(this, configManager);
        if (configManager.isCleanupEnabled()) {
            cleanupManager.start();
            getLogger().info("Cleanup manager started");
        }
    }

    private void initCoreSystems() {
        // TabListManager(JavaPlugin, long, boolean, boolean, String, String, String, String, String)
        long tabInterval = configManager.getConfig().getLong("tab.update-interval", 20L);
        boolean tabEnabled = configManager.getConfig().getBoolean("tab.enabled", true);
        boolean tabShowTps = configManager.getConfig().getBoolean("tab.show-tps", true);
        String tabHeader = configManager.getConfig().getString("tab.default-header", "&6&l=== VenikCraft ===");
        String tabFooter = configManager.getConfig().getString("tab.default-footer",
                "&7Сервер работает на Minecraft 1.21.4\n&fИгроков онлайн: {online}");
        String tabNamePrefix = configManager.getConfig().getString("tab.name-prefix", "");
        String tabNameSuffix = configManager.getConfig().getString("tab.name-suffix", "");
        String tabUnknown = configManager.getConfig().getString("tab.unknown-format", "");

        tabListManager = new TabListManager(this, tabInterval, tabEnabled, tabShowTps,
                tabHeader, tabFooter, tabNamePrefix, tabNameSuffix, tabUnknown);
        if (tabEnabled) {
            tabListManager.start();
            getLogger().info("Tab list manager started");
        }

        // ScoreboardManager(JavaPlugin), start(long)
        scoreboardManager = new ScoreboardManager(this);
        boolean sbEnabled = configManager.getConfig().getBoolean("scoreboard.enabled", true);
        long sbInterval = configManager.getConfig().getLong("scoreboard.update-interval", 20L);
        if (sbEnabled) {
            scoreboardManager.start(sbInterval);
            getLogger().info("Scoreboard manager started");
        }

        caseManager = new CaseManager(this, configManager);
        if (configManager.getConfig().getBoolean("cases.enabled", true)) {
            getLogger().info("Case manager initialized");
        }

        // DonationManager(JavaPlugin)
        donationManager = new DonationManager(this);
        if (configManager.getConfig().getBoolean("donate.enabled", true)) {
            getLogger().info("Donation manager initialized");
        }

        // WarpManager(JavaPlugin, long, boolean)
        long warpCooldown = configManager.getConfig().getLong("warp.cooldown-seconds", 5L);
        boolean warpCooldownEnabled = configManager.getConfig().getBoolean("warp.cooldown-enabled", true);
        warpManager = new WarpManager(this, warpCooldown, warpCooldownEnabled);
        getLogger().info("Warp manager initialized");

        // HomeManager(int,int,int)
        int maxHomes = configManager.getConfig().getInt("home.max-homes", 3);
        int maxHomesVip = configManager.getConfig().getInt("home.max-homes-vip", maxHomes);
        int maxHomesPremium = configManager.getConfig().getInt("home.max-homes-premium", maxHomesVip);
        homeManager = new HomeManager(maxHomes, maxHomesVip, maxHomesPremium);
        getLogger().info("Home manager initialized");

        // KitManager()
        kitManager = new KitManager();
        getLogger().info("Kit manager initialized");

        // TeleportManager(JavaPlugin)
        teleportManager = new TeleportManager(this);
        getLogger().info("Teleport manager initialized");

        // MenuManager(WarpManager, KitManager, DonationManager, PerformanceMonitor)
        menuManager = new MenuManager(warpManager, kitManager, donationManager, performanceMonitor);
        menuListener = new MenuListener(this);
        getServer().getPluginManager().registerEvents(menuListener, this);
        getLogger().info("Menu system initialized");

        // ModeratorCommands(JavaPlugin)
        moderatorCommands = new ModeratorCommands(this);
        // ModeratorListener(ModeratorCommands)
        getServer().getPluginManager().registerEvents(new ModeratorListener(moderatorCommands), this);
        getLogger().info("Moderator system initialized");

        // TradeManager(JavaPlugin)
        tradeManager = new TradeManager(this);
        // TradeListener(TradeManager)
        getServer().getPluginManager().registerEvents(new TradeListener(tradeManager), this);
        getLogger().info("Trade system initialized");

        // PvPManager(JavaPlugin)
        pvpManager = new PvPManager(this);
        pvpManager.start();
        // PvPListener(PvPManager, BountyManager)
        bountyManager = new BountyManager(this, configManager);
        getServer().getPluginManager().registerEvents(new PvPListener(pvpManager, bountyManager), this);
        getLogger().info("PvP system initialized");

        getLogger().info("Bounty system initialized");

        // ChatManager(JavaPlugin)
        chatManager = new ChatManager(this);
        getLogger().info("Chat system initialized");

        // SocialManager(JavaPlugin)
        socialManager = new SocialManager(this);
        getLogger().info("Social system initialized");
    }

    private void initWorlds() {
        // WorldManager(JavaPlugin)
        worldManager = new WorldManager(this);
        worldManager.initializeWorlds();
        getLogger().info("World manager initialized");
    }

    private void initRandomTP() {
        // RandomTPManager(JavaPlugin, int, int, long)
        int minDistance = configManager.getConfig().getInt("rtp.min-distance", 1000);
        int maxDistance = configManager.getConfig().getInt("rtp.max-distance", 10000);
        long cooldownSeconds = configManager.getConfig().getLong("rtp.cooldown-seconds", 300L);
        randomTPManager = new RandomTPManager(this, minDistance, maxDistance, cooldownSeconds);
        getLogger().info("Random TP system initialized");
    }

    private void initAuth() {
        authManager = new AuthManager(this);
        getServer().getPluginManager().registerEvents(new AuthListener(this, authManager), this);

        // LoginCommand(JavaPlugin, AuthManager)
        registerSimpleCommand("login", new LoginCommand(this, authManager));
        registerSimpleCommand("l", new LoginCommand(this, authManager));
        // RegisterCommand(JavaPlugin, AuthManager)
        registerSimpleCommand("register", new RegisterCommand(this, authManager));

        getLogger().info("Auth system initialized");
    }

    private void initNpc() {
        npcManager = new NPCManager(this);
        npcManager.start();
        registerSimpleCommand("npc", new NPCCommand(npcManager));
        getLogger().info("NPC system initialized");
    }

    private void initProtection() {
        ipManager = new IPManager(this);
        getLogger().info("IP protection initialized");
    }

    private void initAntiSpam() {
        antiSpamManager = new AntiSpamManager(this);
        getLogger().info("Anti-spam initialized");
    }

    // ------------------------------------------------------------
    // COMMANDS / LISTENERS
    // ------------------------------------------------------------

    private void registerCommands() {

        OptimizeCommand optimizeCommand = new OptimizeCommand(this, performanceMonitor, blockProfiler, cleanupManager);
        registerCommandWithTab("venikoptimize", optimizeCommand);

        CaseCommand caseCommand = new CaseCommand(this, caseManager);
        registerCommandWithTab("case", caseCommand);

        // DonateCommand(DonationManager)
        DonateCommand donateCommand = new DonateCommand(donationManager);
        registerCommandWithTab("donate", donateCommand);

        registerSimpleCommand("menu", new MenuCommand(this));

        // WarpCommand(WarpManager, MenuManager)
        WarpCommand warpCommand = new WarpCommand(warpManager, menuManager);
        registerCommandWithTab("warp", warpCommand);

        // HomeCommand(HomeManager)
        HomeCommand homeCommand = new HomeCommand(homeManager);
        registerCommandWithTab("home", homeCommand);

        // KitCommand(KitManager, MenuManager)
        KitCommand kitCommand = new KitCommand(kitManager, menuManager);
        registerCommandWithTab("kit", kitCommand);

        // TeleportCommand(TeleportManager)
        TeleportCommand teleportCommand = new TeleportCommand(teleportManager);
        registerCommandWithTab("tpa", teleportCommand);
        registerSimpleCommand("tpahere", teleportCommand);
        registerSimpleCommand("tpaccept", teleportCommand);
        registerSimpleCommand("tpdeny", teleportCommand);
        registerSimpleCommand("back", teleportCommand);

        ModeratorCommands modCommands = moderatorCommands;
        registerCommandWithTab("fly", modCommands);
        registerCommandWithTab("god", modCommands);
        registerCommandWithTab("vanish", modCommands);
        registerSimpleCommand("v", modCommands);
        registerCommandWithTab("speed", modCommands);
        registerSimpleCommand("heal", modCommands);
        registerSimpleCommand("feed", modCommands);

        UtilityCommands utilCommands = new UtilityCommands(this);
        registerSimpleCommand("weather", utilCommands);
        registerSimpleCommand("time", utilCommands);
        registerSimpleCommand("clear", utilCommands);
        registerSimpleCommand("ci", utilCommands);
        registerSimpleCommand("repair", utilCommands);
        registerSimpleCommand("enchant", utilCommands);
        registerSimpleCommand("workbench", utilCommands);
        registerSimpleCommand("craft", utilCommands);
        registerSimpleCommand("enderchest", utilCommands);
        registerSimpleCommand("ec", utilCommands);
        registerSimpleCommand("gamemode", utilCommands);
        registerSimpleCommand("gm", utilCommands);

        // EconomyCommand(JavaPlugin, DonationManager)
        EconomyCommand economyCommand = new EconomyCommand(this, donationManager);
        registerCommandWithTab("pay", economyCommand);
        registerSimpleCommand("balance", economyCommand);
        registerSimpleCommand("bal", economyCommand);
        registerSimpleCommand("money", economyCommand);
        registerSimpleCommand("baltop", economyCommand);
        registerSimpleCommand("top", economyCommand);

        // TradeCommand(TradeManager)
        TradeCommand tradeCommand = new TradeCommand(tradeManager);
        registerSimpleCommand("trade", tradeCommand);

        // BountyCommand(JavaPlugin, BountyManager)
        BountyCommand bountyCommand = new BountyCommand(this, bountyManager);
        registerSimpleCommand("bounty", bountyCommand);

        // SocialCommand(SocialManager)
        SocialCommand socialCommand = new SocialCommand(socialManager);
        registerSimpleCommand("friend", socialCommand);
        registerSimpleCommand("f", socialCommand);
        registerSimpleCommand("ignore", socialCommand);

        // MessageCommand(ChatManager)
        MessageCommand messageCommand = new MessageCommand(chatManager);
        registerSimpleCommand("msg", messageCommand);
        registerSimpleCommand("tell", messageCommand);
        registerSimpleCommand("w", messageCommand);
        registerSimpleCommand("r", messageCommand);
        registerSimpleCommand("reply", messageCommand);

        // RTPCommand(JavaPlugin) или (RandomTPManager) — по логу ошибки ctor от plugin
        RTPCommand rtpCommand = new RTPCommand(this);
        registerSimpleCommand("rtp", rtpCommand);
        registerSimpleCommand("wild", rtpCommand);

        LobbyCommand lobbyCommand = new LobbyCommand(this);
        registerSimpleCommand("lobby", lobbyCommand);

        getLogger().info("Commands registered");
    }

    private void registerListeners() {

        getServer().getPluginManager().registerEvents(
                new CaseListener(this, caseManager), this);

        getServer().getPluginManager().registerEvents(
                new ChatListener(this), this);

        getLogger().info("Listeners registered");
    }

    private void registerSimpleCommand(String name, Object executor) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) {
            getLogger().warning("Command not found in plugin.yml: " + name);
            return;
        }
        cmd.setExecutor((org.bukkit.command.CommandExecutor) executor);
    }

    private void registerCommandWithTab(String name, Object handler) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) {
            getLogger().warning("Command not found in plugin.yml: " + name);
            return;
        }
        cmd.setExecutor((org.bukkit.command.CommandExecutor) handler);
        cmd.setTabCompleter((org.bukkit.command.TabCompleter) handler);
    }

    // ------------------------------------------------------------
    // LOG HELPERS
    // ------------------------------------------------------------

    private void logHeader(String msg) {
        getLogger().info("========================================");
        getLogger().info(msg + " v" + getDescription().getVersion());
        getLogger().info("Minecraft " + getServer().getBukkitVersion());
        getLogger().info("========================================");
    }

    private void logFooter(String msg) {
        getLogger().info("========================================");
        getLogger().info(msg + "!");
        getLogger().info("========================================");
    }

    // ------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------

    public static VenikOptimize getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }

    public BlockProfiler getBlockProfiler() {
        return blockProfiler;
    }

    public EntityOptimizer getEntityOptimizer() {
        return entityOptimizer;
    }

    public ChunkOptimizer getChunkOptimizer() {
        return chunkOptimizer;
    }

    public RedstoneOptimizer getRedstoneOptimizer() {
        return redstoneOptimizer;
    }

    public FarmOptimizer getFarmOptimizer() {
        return farmOptimizer;
    }

    public CleanupManager getCleanupManager() {
        return cleanupManager;
    }

    public TabListManager getTabListManager() {
        return tabListManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public CaseManager getCaseManager() {
        return caseManager;
    }

    public DonationManager getDonationManager() {
        return donationManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public MenuListener getMenuListener() {
        return menuListener;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public ModeratorCommands getModeratorCommands() {
        return moderatorCommands;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }

    public PvPManager getPvPManager() {
        return pvpManager;
    }

    public BountyManager getBountyManager() {
        return bountyManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public SocialManager getSocialManager() {
        return socialManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public RandomTPManager getRandomTPManager() {
        return randomTPManager;
    }

    public IPManager getIpManager() {
        return ipManager;
    }

    public AntiSpamManager getAntiSpamManager() {
        return antiSpamManager;
    }
}
