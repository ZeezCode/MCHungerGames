package che.mchg;

import org.bukkit.ChatColor;

public class MCHGUtils {
	private Main plugin;
	private int min;
	MCHGUtils(Main pl, int minP) {
		this.plugin = pl;
		this.min = minP;
	}
	public String getHGPrefix() {
		return ChatColor.BLACK+"["+ChatColor.DARK_RED+"CHEHG"+ChatColor.BLACK+"] "+ChatColor.RESET;
	}
	public void checkCanBegin() {
		if (!(this.plugin.getServer().getOnlinePlayers().size() >= this.min)) {
			startCountdown();
		}
	}
	public void startCountdown() {
		
	}
}