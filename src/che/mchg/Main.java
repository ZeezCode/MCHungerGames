package che.mchg;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
public class Main extends JavaPlugin implements Listener {
	static MCHGUtils util;
	static ArrayList<Player> players = new ArrayList<Player>();
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		getLogger().info(pdfFile.getName()+" has been enabled running "+pdfFile.getVersion()+".");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		util =  new MCHGUtils(this, getConfig().getInt("minPlayers"));
	}
	@EventHandler
	public void onPlayerConnect(PlayerJoinEvent e) {
		Player ply = e.getPlayer();
		if (Bukkit.getServer().getOnlinePlayers().size() >= getConfig().getInt("maxPlayers")) { //if amt of players on server >= max allowed then deny any new players
			ply.kickPlayer("This server has reached the maximum amount of players allowed.\nPlease join back later!");
			return;
		}
		players.add(ply);
		String msg = ChatColor.BLACK+"["+ChatColor.DARK_RED+"CHEHG"+ChatColor.BLACK+"]"+ChatColor.YELLOW+ply.getName()+ChatColor.WHITE+" has joined the game!";
		e.setJoinMessage(msg);
		if (util.checkCanBegin()) {
			util.startCountdown();
		}
	}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player ply = e.getPlayer();
		if (players.contains(ply))
			players.remove(ply);
		String msg = ChatColor.BLACK+"["+ChatColor.DARK_RED+"CHEHG"+ChatColor.BLACK+"]"+ChatColor.YELLOW+ply.getName()+ChatColor.WHITE+" has left the game.";
		e.setQuitMessage(msg);
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("hgkick")) {
            if (args.length==0) {
                    sender.sendMessage(ChatColor.RED+"Not enough args");
                    return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target==null) {
            		sender.sendMessage(ChatColor.RED+"Unable to find player");
                    return true;
            }
            target.kickPlayer("You have been kicked by "+sender.getName()+".");
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