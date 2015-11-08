package che.mchg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
public class MCHGUtils {
	private Main plugin;
	private int min;
	public int number;
	MCHGUtils(Main pl, int minP) {
		this.plugin = pl;
		this.min = minP;
		this.number = pl.getConfig().getInt("countdownLength");
	}
	public String getHGPrefix() {
		return ChatColor.BLACK+"["+ChatColor.DARK_RED+"CHEHG"+ChatColor.BLACK+"] "+ChatColor.RESET;
	}
	public boolean checkCanBegin() {
		 return this.plugin.getServer().getOnlinePlayers().size() >= this.min;
	}
	public Main getPlugin() {
		return this.plugin;
	}
	public MCHGUtils getThis() {
		return this;
	}
	public void resetCounter() {
		this.number = this.plugin.getConfig().getInt("countdownLength");
	}
	public void startGame() {
		for (int i=0; i<10; i++) {
			Bukkit.broadcastMessage(getHGPrefix()+ChatColor.GREEN+"---LIVE---");
		}
	}
	public void startCountdown() {
		this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
			  public void run() {
				  if (number>=0) {
					  if (number==0) {
						  if (getThis().checkCanBegin()) {
							  getThis().startGame();
						  }
						  else {
							  getPlugin().getServer().broadcastMessage(getHGPrefix()+ChatColor.RED+"There are not enough players to start the game!");
							  getPlugin().getServer().broadcastMessage(getHGPrefix()+ChatColor.RED+"The countdown will restart when there are enough players to begin the game.");
							  resetCounter();
							  number=-1;
						  }
					  }
					  else if (number%5==0 || number <=15) {
						  getPlugin().getServer().broadcastMessage(getHGPrefix()+ChatColor.DARK_GREEN+"Game starting in "+number+" seconds...");
					  }
					  number--;
				  }
			  }
		}, 0L, 20L);
	}
}