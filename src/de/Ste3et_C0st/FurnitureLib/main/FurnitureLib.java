package de.Ste3et_C0st.FurnitureLib.main;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.milkbowl.vault.Metrics;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.Ste3et_C0st.FurnitureLib.Command.TabCompleterHandler;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Database.DeSerializer;
import de.Ste3et_C0st.FurnitureLib.Database.Serializer;
import de.Ste3et_C0st.FurnitureLib.Database.SQLManager;
import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureEvents;
import de.Ste3et_C0st.FurnitureLib.LimitationManager.LimitationManager;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjectManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.ColorUtil;
import de.Ste3et_C0st.FurnitureLib.Utilitis.CraftingInv;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.ProtocolFields;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import de.Ste3et_C0st.FurnitureLib.main.Protection.ProtectionManager;

public class FurnitureLib extends JavaPlugin{

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger("Minecraft");
	private LocationUtil lUtil;
	private static FurnitureLib instance;
	private FurnitureManager manager;
	private ProtectionManager Pmanager;
	private LightManager lightMgr;
	private HashMap<String, List<String>> permissionKit = new HashMap<String, List<String>>();
	private boolean useGamemode = true, canSit = true, update = true, useParticle = true, useRegionMemberAccess = false, 
					autoPurge = false, removePurge = false, creativeInteract = true, creativePlace = true;
	private CraftingInv craftingInv;
	private LanguageManager lmanager;
	private SQLManager sqlManager;
	private LimitationManager limitManager;
	private ColorUtil colorManager;
	private Serializer serializeNew;
	private DeSerializer deSerializerNew;
	private Permission permission = null;
	private Updater updater;
	private BlockManager bmanager;
	private PublicMode mode;
	private EventType type;
	private WorldPool wPool;
	private ProtocolFields field = ProtocolFields.Spigot110;
	private ProjectManager pManager;
	private int purgeTime = 30;
	private long purgeTimeMS = 0;
	private int viewDistance = 100;
	public HashMap<Project, Long> deleteMap = new HashMap<Project, Long>();
	
	public LanguageManager getLangManager(){return this.lmanager;}
	public LightManager getLightManager(){return this.lightMgr;}
	public ProtectionManager getPermManager(){return this.Pmanager;}
	public LimitationManager getLimitManager(){return this.limitManager;}
	public ColorUtil getColorManager(){return this.colorManager;}
	public CraftingInv getCraftingInv(){return this.craftingInv;}
	public String getBukkitVersion() {return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public ObjectID getObjectID(String c, String plugin, Location loc){return new ObjectID(c, plugin, loc);}
	public Boolean useGamemode() {return useGamemode;}
	public Serializer getSerializer(){return serializeNew;}
	public DeSerializer getDeSerializer(){return deSerializerNew;}
	public EventType getDefaultEventType(){return this.type;}
	public PublicMode getDefaultPublicType(){return this.mode;}
	public PluginManager getPluginManager(){return this.getServer().getPluginManager();}
	public BlockManager getBlockManager() {return bmanager;}
	public SQLManager getSQLManager(){return this.sqlManager;}
	public ProtocolFields getField() {return this.field;}
	public WorldPool getWorldPool(){return this.wPool;}
	public HashMap<String, List<String>> getPermissionList(){return this.permissionKit;}
	public ProjectManager getProjectManager(){return this.pManager;}
	public int getPurgeTime(){return this.purgeTime;}
	public int getViewDistance(){return this.viewDistance;}
	public static FurnitureLib getInstance(){return instance;}
	public boolean isAutoPurge(){return this.autoPurge;}
	public boolean isPurgeRemove(){return this.removePurge;}
	public boolean canBuild(Player p, ObjectID id, EventType type){ return Pmanager.canBuild(p, id, type);}
	public boolean isUpdate(){return this.update;}
	public boolean isParticleEnable(){return this.useParticle;}
	public boolean hasPerm(Player p, String perm){return permission.has(p, perm);}
	public boolean hasPerm(CommandSender p, String perm){return permission.has(p, perm);}
	public boolean canSitting(){return this.canSit;}
	public boolean creativeInteract(){return this.creativeInteract;}
	public boolean creativePlace(){return this.creativePlace;}
	public boolean haveRegionMemberAccess(){return this.useRegionMemberAccess;}
	public boolean isDouble(String s){try{Double.parseDouble(s);}catch(NumberFormatException e){return false;}return true;}
	public boolean isBoolean(String s){try {Boolean.parseBoolean(s.toLowerCase());}catch (Exception e) {return false;}return true;}
	public boolean isInt(String s){try{Integer.parseInt(s);}catch(NumberFormatException e){return false;}return true;}
	
	public Updater getUpdater(){return updater;}
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		if(!isEnable("ProtocolLib", true)){send("§cProtocolLib not found");getPluginManager().disablePlugin(this);}else{
			if(!isEnable("Vault", true)){send("§cVault not found");getPluginManager().disablePlugin(this);}else{
				if(!setupPermissions()){send("§cNo Permission System found");getPluginManager().disablePlugin(this);}else{
					if(getServer().getBukkitVersion().startsWith("1.9") || getServer().getBukkitVersion().startsWith("1.10") || getServer().getBukkitVersion().startsWith("1.11")){
						instance = this;
						getConfig().addDefaults(YamlConfiguration.loadConfiguration(getResource("config.yml")));
						getConfig().options().copyDefaults(true);
						getConfig().options().copyHeader(true);
						saveConfig();
						field = ProtocolFields.getField(getServer().getBukkitVersion());
						this.lUtil = new LocationUtil();
						this.manager = new FurnitureManager();
						this.colorManager = new ColorUtil();
						this.serializeNew = new Serializer();
						this.deSerializerNew = new DeSerializer();
						this.lightMgr = new LightManager(this);
						this.lmanager = new LanguageManager(instance, getConfig().getString("config.Language"));
						this.useGamemode = !getConfig().getBoolean("config.Creative.RemoveItems");
						this.creativeInteract = getConfig().getBoolean("config.Creative.Interact");
						this.creativePlace = getConfig().getBoolean("config.Creative.Place");
						this.useRegionMemberAccess = getConfig().getBoolean("config.ProtectionLib.RegeionMemberAccess");
						this.canSit = !getConfig().getBoolean("config.DisableSitting");
						this.useParticle = getConfig().getBoolean("config.useParticles");
						this.purgeTime = getConfig().getInt("config.Purge.time");
						this.autoPurge = getConfig().getBoolean("config.Purge.autoPurge");
						this.removePurge = getConfig().getBoolean("config.Purge.removePurge");
						this.viewDistance = getConfig().getInt("config.viewDistance");
						this.updater = new Updater();
						this.wPool = new WorldPool();
						this.wPool.loadWorlds();
						this.purgeTimeMS = TimeUnit.DAYS.toMillis(purgeTime);
						this.pManager = new ProjectManager();
						new FurnitureEvents(instance, manager);
						getServer().getPluginManager().registerEvents(new ChunkOnLoad(), this);
						PluginCommand c = getCommand("furniture");
						c.setExecutor(new command(this));
						c.setTabCompleter(new TabCompleterHandler(this));
						this.Pmanager = new ProtectionManager(instance);
						send("==========================================");
						send("FurnitureLibary Version: §e" + this.getDescription().getVersion());
						send("Furniture Autor: §6" + this.getDescription().getAuthors().get(0));
						send("Furniture Website: §e" + this.getDescription().getWebsite());
						String s = getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion();
						boolean protocollib = isRightProtocollib(s);
						if(protocollib){
							send("Furniture start load");
							Boolean b = isEnable("ProtectionLib", false);
							send("Furniture find ProtectionLib: §e" + b.toString());
							createDefaultWatchers();
							this.sqlManager = new SQLManager(instance);
							this.sqlManager.initialize();
							this.loadIgnore();
							this.sqlManager.loadALL();
							this.craftingInv = new CraftingInv(this);
							this.limitManager = new LimitationManager(this);
							this.update = getConfig().getBoolean("config.CheckUpdate");
							this.bmanager = new BlockManager();
							PublicMode mode = PublicMode.valueOf(getConfig().getString("config.PlaceMode.Mode"));
							EventType type = EventType.valueOf(getConfig().getString("config.PlaceMode.Access"));
							if(mode!=null){this.mode = mode;}else{this.mode = PublicMode.PRIVATE;}
							if(type!=null){this.type = type;}else{this.type = EventType.INTERACT;}
							if(getConfig().getBoolean("config.timer.Enable")){int time = getConfig().getInt("config.timer.time");sqlManager.saveIntervall(time);}
							for(Player p : Bukkit.getOnlinePlayers()){if(p.isOp()){getUpdater().sendPlayer(p);}}
							loadPermissionKit();
							loadMetrics();
							this.pManager.loadProjectFiles();
							send("§2Furniture load finish :)");
							send("==========================================");
						}else{
							send("Furniture Lib deosn't find the correct ProtocolLib");
							send("Please Install Protocollib §c4.x");
							send("You can it download at: §6§lhttps://www.spigotmc.org/resources/protocollib.1997/");
							send("==========================================");
							getPluginManager().disablePlugin(this);
						}
					}else{
						send("§cYour Server version is not Supportet please use §c1.9.x");
						getPluginManager().disablePlugin(this);
					}
				}
			}
		}
	}
	
	private void loadMetrics(){
		try{if(getConfig().getBoolean("config.UseMetrics")){
			new Metrics(this).start();
		}}catch(Exception e){e.printStackTrace();}
	}
	
	private void loadIgnore() {
		config c = new config(getInstance());
		FileConfiguration configuration = c.getConfig("ignoredPlayers", "");
		if(configuration.isSet("ignoreList")){
			for(Object s : configuration.getStringList("ignoreList")){
				String str = (String) s;
				getFurnitureManager().getIgnoreList().add(UUID.fromString(str));
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void loadPermissionKit(){
		config c = new config(this);
		FileConfiguration file = c.getConfig("permissionKit.yml", "");
		if(file==null) return;
		file.addDefaults(YamlConfiguration.loadConfiguration(this.getResource("permissionKit.yml")));
		file.options().copyDefaults(true);
		file.options().copyHeader(true);
		c.saveConfig("permissionKit.yml", file, "");
		if(file.isSet("kit")){
			for(String s : file.getConfigurationSection("kit").getKeys(false)){
				String header = (String) s;
				if(file.isSet("kit." + header)){
					List<String> projectList = new ArrayList<String>();
					if(file.getStringList("kit." + header)!=null){
						projectList = file.getStringList("kit." + header);
					}
					permissionKit.put(header, projectList);
				}
			}
		}
	}
	
	private void saveIgnore(){
		List <String> ignoreList = new ArrayList<String>();
		for(UUID uuid : getFurnitureManager().getIgnoreList()){
			ignoreList.add(uuid.toString());
		}
		config c = new config(getInstance());
		FileConfiguration configuration = c.getConfig("ignoredPlayers", "");
		configuration.set("ignoreList", ignoreList);
		c.saveConfig("ignoredPlayers", configuration, "");
	}
	
	private boolean isRightProtocollib(String s){return s.startsWith("4");}
	public void send(String s){getServer().getConsoleSender().sendMessage(s);}
	
	public boolean checkPurge(ObjectID obj, UUID uuid, int purgeTime){
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		if(!player.hasPlayedBefore()) return false;
		long time = player.getLastPlayed();
		if(!isAfterDate(time, purgeTime))return false;
		if(removePurge){getFurnitureManager().remove(obj);return false;}
		obj.setSQLAction(SQLAction.REMOVE);
		return true;
	}
	
	public boolean isAfterDate(long time, int purgeTime){
		if(System.currentTimeMillis() - (time+purgeTimeMS) >0){return true;}
		return false;
	}
	
	public boolean checkPurge(ObjectID obj, UUID uuid){
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		if(!player.hasPlayedBefore()) return false;
		long time = player.getLastPlayed();
		if(!isAfterDate(time, purgeTime)){return false;}else{
			if(removePurge){
				getFurnitureManager().remove(obj);
				return true;
			}
			obj.setSQLAction(SQLAction.REMOVE);
			return true;
		}
	}
	
	public boolean checkPurge(ObjectID obj, OfflinePlayer player){
		if(!player.hasPlayedBefore()) return false;
		long time = player.getLastPlayed();
		if(!isAfterDate(time, purgeTime)){return false;}else{
			if(removePurge){
				getFurnitureManager().remove(obj);
				return true;
			}
			obj.setSQLAction(SQLAction.REMOVE);
			return true;
		}
	}
	
	private void createDefaultWatchers(){
		for(World w : Bukkit.getWorlds()){
			if(w!=null){
				getFurnitureManager().getDefaultWatcher(w, EntityType.ARMOR_STAND);
			}
		}
	}
	
	public void registerPluginFurnitures(Plugin plugin){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : manager.getObjectList()){
			if(obj==null) continue;
			if(objList.contains(obj)) continue;
			if(!objList.contains(obj)) objList.add(obj);
			if(obj.getPlugin()==null) continue;
			if(obj.getSQLAction().equals(SQLAction.REMOVE)) continue;
			if(obj.getPlugin().equalsIgnoreCase(plugin.getName())){
				spawn(obj.getProjectOBJ(), obj);
			}
		}
	}
	
	private boolean setupPermissions()
	{
	       RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	       if (permissionProvider != null) {
	           permission = permissionProvider.getProvider();
	       }
	       return (permission != null);
   }
	
	private boolean isEnable(String plugin, boolean shutdown){
		boolean b = getServer().getPluginManager().isPluginEnabled(plugin);
		if(!b && shutdown) getServer().getPluginManager().disablePlugin(this);
		return b;
	}
	
	@Override
	public void onDisable(){	
		
		getLogger().info("==========================================");
		getLogger().info("Furniture shutdown started");
		sqlManager.save();
		sqlManager.stop();
		instance = null;
		getLogger().info("ArmorStandPackets saved");
		if(!getFurnitureManager().getObjectList().isEmpty()){
			for(ObjectID obj : getFurnitureManager().getObjectList()){
				for(fEntity as : obj.getPacketList()){
					as.kill();
				}
			}
		}
		this.saveIgnore();
		getLogger().info("==========================================");
	}
	
	public void spawn(Project pro, Location l){
		Class<?> c = pro.getclass();
		ObjectID obj = new ObjectID(pro.getName(), pro.getPlugin().getName(), l);
		if(c==null ){return;}
		try {
			Constructor<?> ctor = c.getConstructor(ObjectID.class);
			ctor.newInstance(obj);
			obj.setFinish();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void spawn(Project pro, ObjectID obj){
		if(pro==null)return;
		if(pro.getClass()==null)return;
		if(obj==null)return;
		Class<?> c = pro.getclass();
		if(c==null ){return;}
			try {
			Constructor<?> ctor = c.getConstructor(ObjectID.class);
			ctor.newInstance(obj);
			obj.setFinish();
		} catch (Exception e) {e.printStackTrace();}
	}
}
