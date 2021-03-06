package me.winterguardian.core;

import me.winterguardian.core.message.CoreMessage;
import me.winterguardian.core.message.ErrorMessage;
import me.winterguardian.core.message.Message;
import me.winterguardian.core.shop.ItemPurchase;
import me.winterguardian.core.shop.PlayerPurchaseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Random;

public class HubCorePlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		CoreConfig config = new CoreConfig(new File(getDataFolder(), "config.yml"));
		if(!config.load())
			throw new RuntimeException("Could not load config for core!");

		Core.getCustomEntityManager().enable(this);
		Core.getBungeeMessager().enable(this);
		Core.getBungeeMessager().setSafe(false);
		Core.getGameplayManager().enable(this);
		Core.getUserDatasManager().enableDB(this, "com.mysql.jdbc.Driver", config.getURL(), config.getUsername(), config.getPassword());
		Core.getWand().enable(this, new Permission("HubCore.wand", "Donne accès à la sélection de terrain", PermissionDefault.OP), CoreMessage.WAND_POSITIONSET);
		Core.getShop().enable(this);

		final Permission createPermission = new Permission("HubCore.shop.create.item", "Permet de créer des panneaux d'achats d'items.", PermissionDefault.OP);
		final Permission vipPermission = new Permission("HubCore.buy.vip", "Permet d'acheter des items vips et avoir " + config.getVipReduction() + "% de réduction sur vos achats.", PermissionDefault.OP);
		//final Permission elitePermission = new Permission("HubCore.buy.elite", "Permet d'avoir % de réduction sur tout.", PermissionDefault.OP);


		if(Bukkit.getPluginManager().getPermission(createPermission.getName()) == null)
			Bukkit.getPluginManager().addPermission(createPermission);

		if(Bukkit.getPluginManager().getPermission(vipPermission.getName()) == null)
			Bukkit.getPluginManager().addPermission(vipPermission);

		//if(Bukkit.getPluginManager().getPermission(elitePermission.getName()) == null)
		//	Bukkit.getPluginManager().addPermission(elitePermission);

		Core.getShop().registerPurchaseType(new ItemPurchase("[shop]", "§f§lTouched", "§e§lAchat Item")
		{
			@Override
			public Permission getCreationPermission()
			{
				return createPermission;
			}

			@Override
			public Message getNotEnoughPointsMessage()
			{
				return CoreMessage.PURCHASE_NOTENOUGHPOINTS;
			}

			@Override
			public Message getPurchaseSuccessMessage()
			{
				return CoreMessage.PURCHASE_SUCCESS;
			}
		});

		Core.getShop().registerPurchaseType(new ItemPurchase("[shopvip]", "§f§lTouched", "§e§lAchat Item §6§lVip")
		{
			@Override
			public Permission getCreationPermission()
			{
				return createPermission;
			}

			@Override
			public Message getNotEnoughPointsMessage()
			{
				return CoreMessage.PURCHASE_NOTENOUGHPOINTS;
			}

			@Override
			public Message getPurchaseSuccessMessage()
			{
				return CoreMessage.PURCHASE_SUCCESS;
			}

			@Override
			public boolean canGive(String[] sign, Player player)
			{
				if(player.hasPermission(vipPermission))
					return true;

				ErrorMessage.COMMAND_INVALID_VIPRANK.say(player);
				return false;
			}
		});

		Bukkit.getPluginManager().registerEvents(new Listener()
		{
			private final Random random = new Random();

			@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
			public void onPlayerPurchase(PlayerPurchaseEvent event)
			{
				if(event.getPlayer().hasPermission(vipPermission))
				{
					event.getPurchase().setPrice(event.getPurchase().getPrice() * (100 - config.getVipReduction()) / 100);
					event.getPlayer().sendMessage("§6§lVip > §aVous payez §f" + event.getPurchase().getPrice() + " §aau lieu de §f" + event.getPurchase().getBasePrice() + "§a. (-" + config.getVipReduction() + "%)");
				}
				else if(random.nextFloat() < 0.05)
				{
					event.getPlayer().sendMessage("§eDevenez §6§lVip §eet obtenez §6" + config.getVipReduction() + "%§e de réduction sur §ltout§e en jeu !");
				}
			}
		}, this);

		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
			public void onLeavesDecay(LeavesDecayEvent event)
			{
				event.setCancelled(true);
			}
		}, this);
	}
	
	@Override
	public void onDisable()
	{
		HandlerList.unregisterAll(this);
		Core.disable(this);
	}
}
