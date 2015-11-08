package che.mchg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
public class MCHGUtils {
	private Main plugin;
	private int min;
	public int number;
	public int countdownuid, fireworkCounter=0;
	public static boolean checkStart = false, countdownInProgress = false, gameHasEnded=false;
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
		for (Player ply : Bukkit.getOnlinePlayers()) {
			ply.setGameMode(GameMode.SURVIVAL);
			ply.setHealth(20.0);
			ply.setSaturation((float) 20);
			ply.setFoodLevel(20);
		}
		checkStart = true;
	}
	public void setLocation(Player ply, Location loc) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				ply.teleport(loc);
				freezePlayer(ply, loc);
			}
		}, 10L);
	}
	public void freezePlayer(Player ply, Location l) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				ply.teleport(l);
				if (number>=0) {
					freezePlayer(ply, l);
				}
			}
		}, 5L);
	}
	public void endGame() {
		gameHasEnded=true;
		Bukkit.broadcastMessage(getHGPrefix()+ChatColor.GOLD+"Congratulations! You've won the Hunger Games!");
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				for (Player ply : Bukkit.getServer().getOnlinePlayers()) {
					Firework fw = ply.getWorld().spawn(ply.getLocation(), Firework.class);
					FireworkMeta fwm = fw.getFireworkMeta();
					FireworkEffect effect = FireworkEffect.builder().withColor(Color.BLUE.mixColors(Color.YELLOW.mixColors(Color.GREEN))).with(Type.BALL_LARGE).withFade(Color.RED).build();    
					fwm.addEffects(effect);
					fwm.setPower(5);    
					fw.setFireworkMeta(fwm);
				}	
				if (fireworkCounter<3) {
					fireworkCounter++;
					run();
				}
			}
		}, 30L);
		kickAll();
	}
	public void kickAll() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				for (Player ply : Bukkit.getOnlinePlayers()) {
					ply.kickPlayer("Congratulations, you've won the game!\nThe server will reset now, join back soon.");
				}
				resetMap();
			}
		}, 10 * 20L);
	}	
	public void resetMap() {
		//reset map
	}
	public void startCountdown() {
		countdownInProgress = true;
		countdownuid = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
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
							countdownInProgress=false;
						}
						Bukkit.getScheduler().cancelTask(countdownuid);
						countdownuid=0;
					}
					else if (number%5==0 || number <=15) {
						getPlugin().getServer().broadcastMessage(getHGPrefix()+ChatColor.DARK_GREEN+"Game starting in "+number+" seconds...");
					}
					number--;
				}
			}
		}, 0L, 20L);
	}
	public boolean isNumber(String checked) {
		try {
			Integer.parseInt(checked);
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
}