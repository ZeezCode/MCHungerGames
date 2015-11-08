package che.mchg;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
public class Main extends JavaPlugin implements Listener {
	static MCHGUtils util;
	static ArrayList<Player> players = new ArrayList<Player>();
	HashMap<Player, Integer> playersPositions = new HashMap<Player, Integer>();
	HashMap<Integer, Boolean> spawnPositions = new HashMap<Integer, Boolean>();
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		getLogger().info(pdfFile.getName()+" has been enabled running "+pdfFile.getVersion()+".");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		util =  new MCHGUtils(this, getConfig().getInt("minPlayers"));
		for (int i=1; i<=getConfig().getInt("maxPlayers"); i++) {
			spawnPositions.put(i, false);
		}
	}
	@EventHandler
	public void onPlayerConnect(PlayerJoinEvent e) {
		Player ply = e.getPlayer();
		if (Bukkit.getServer().getOnlinePlayers().size() >= getConfig().getInt("maxPlayers")) {
			ply.kickPlayer("This server has reached the maximum amount of players allowed.\nPlease join back later!");
			return;
		}
		if(MCHGUtils.checkStart==true) {
			ply.kickPlayer("Sorry sir/ma'am, the game has already started. \n You may not join until the next round commences.");
		}
		players.add(ply);
		if(players.size()==getConfig().getInt("minPlayers")) {
			util.startCountdown();
		}
		String msg = ChatColor.BLACK+"["+ChatColor.DARK_RED+"CHEHG"+ChatColor.BLACK+"]"+ChatColor.YELLOW+ply.getName()+ChatColor.WHITE+" has joined the game!";
		e.setJoinMessage(msg);
		boolean didFind=false;
		for (int i=1; i<=getConfig().getInt("maxPlayers") && didFind==false; i++) {
			if (spawnPositions.get(i)==false) {
				double x, y, z;
				x = getConfig().getInt("spawnPositions."+i+".x");
				y = getConfig().getInt("spawnPositions."+i+".y");
				z = getConfig().getInt("spawnPositions."+i+".z");
				Location pos = new Location(ply.getWorld(), x, y, z);
				util.setLocation(ply, pos);
				playersPositions.put(ply, i);
				didFind=true;
				spawnPositions.remove(i);
				spawnPositions.put(i, true);
			}
		}
		ply.setGameMode(GameMode.CREATIVE);
		if (util.checkCanBegin() && MCHGUtils.countdownInProgress==false)
			util.startCountdown();
	}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player ply = e.getPlayer();
		if (players.contains(ply))
			players.remove(ply);
		String msg = ChatColor.BLACK+"["+ChatColor.DARK_RED+"CHEHG"+ChatColor.BLACK+"]"+ChatColor.YELLOW+ply.getName()+ChatColor.WHITE+" has left the game.";
		e.setQuitMessage(msg);
		spawnPositions.remove(playersPositions.get(ply));
		spawnPositions.put(playersPositions.get(ply), false);
		playersPositions.remove(ply);
		if (MCHGUtils.checkStart && players.size()==1) {
			util.endGame();
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType()==Material.GLASS) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(players.contains(e.getEntity())) {
			players.remove(e.getEntity());
		}
		e.getEntity().kickPlayer("Sorry scrub, but you ain't good enough for this pile of beans \n R.I.P.");
		if(players.size()==1) {
			util.endGame();
		}
	}
	@EventHandler
	public void serverListPing(ServerListPingEvent e) {
		if (MCHGUtils.checkStart==false && MCHGUtils.countdownInProgress==false) {
			e.setMotd(ChatColor.GOLD+"Game Status: "+ChatColor.GREEN+"WAITING FOR PLAYERS");
		}
		else if (MCHGUtils.checkStart==true && MCHGUtils.gameHasEnded==false) {
			e.setMotd(ChatColor.GOLD+"Game Status: "+ChatColor.DARK_RED+"IN PROGRESS");
		}
		else if (MCHGUtils.countdownInProgress==true && MCHGUtils.gameHasEnded==false) {
			e.setMotd(ChatColor.GOLD+"Game Status: "+ChatColor.YELLOW+"BEGINNING, COUNTDOWN IN PROGRESS");
		}
		else if (MCHGUtils.gameHasEnded==true) {
			e.setMotd(ChatColor.GOLD+"Game Status: "+ChatColor.DARK_RED+"SERVER RESETTING...");
		}
		else {
			e.setMotd(ChatColor.GOLD+"Game Status: "+ChatColor.DARK_AQUA+"UNKNOWN (ERROR)");
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		//hgkick
		if(label.equalsIgnoreCase("hgkick") && sender instanceof Player) {
			Player ply = (Player) sender;
			if (args.length==0) {
				ply.sendMessage(ChatColor.RED+"Not enough args");
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if (target==null) {
				ply.sendMessage(ChatColor.RED+"Unable to find player");
				return true;
			}
			target.kickPlayer("You have been kicked by an admin, "+sender.getName());
		}
		else if (label.equalsIgnoreCase("setspawnpos")) {
			if (sender instanceof Player) {
				Player ply = (Player) sender;
				if (args.length==0) {
					ply.sendMessage(ChatColor.RED+"You must provide a number.");
					ply.sendMessage(ChatColor.RED+"Usage: /setspawnpos [number(1-"+getConfig().getInt("maxPlayers")+")]");
					return true;
				}
				if (util.isNumber(args[0])) {
					int uid = Integer.parseInt(args[0]);
					double x, y, z;
					x = ply.getLocation().getX();
					y = ply.getLocation().getY();
					z = ply.getLocation().getZ();
					getConfig().set("spawnPositions."+uid+".x", x);
					getConfig().set("spawnPositions."+uid+".y", y);
					getConfig().set("spawnPositions."+uid+".z", z);
					saveConfig();
					ply.sendMessage(util.getHGPrefix()+ChatColor.GREEN+"You have successfully set the spawn point for position "+uid+".");
				}
				else {
					ply.sendMessage(ChatColor.RED+args[0]+" is not a valid integer.");
					ply.sendMessage(ChatColor.RED+"Usage: /setspawnpos [number(1-"+getConfig().getInt("maxPlayers")+")]");
				}
			}
			else {
				sender.sendMessage(ChatColor.RED+"You must be a player to use this command.");
			}
		}
		return true;
	}
}