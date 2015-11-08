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
		if (Bukkit.getServer().getOnlinePlayers().size() >= getConfig().getInt("maxPlayers")) {
			ply.kickPlayer("This server has reached the maximum amount of players allowed.\nPlease join back later!");
			return;
		}
		players.add(ply);
		String msg = ChatColor.BLACK+"["+ChatColor.DARK_RED+"CHEHG"+ChatColor.BLACK+"]"+ChatColor.YELLOW+ply.getName()+ChatColor.WHITE+" has joined the game!";
		e.setJoinMessage(msg);
		util.checkCanBegin();
	}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player ply = e.getPlayer();
		if (players.contains(ply))
			players.remove(ply);
		String msg = ChatColor.BLACK+"["+ChatColor.DARK_RED+"CHEHG"+ChatColor.BLACK+"]"+ChatColor.YELLOW+ply.getName()+ChatColor.WHITE+" has left the game.";
		e.setQuitMessage(msg);
	}
}