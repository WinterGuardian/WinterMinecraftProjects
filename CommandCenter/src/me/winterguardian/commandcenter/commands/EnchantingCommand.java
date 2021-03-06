package me.winterguardian.commandcenter.commands;

import me.winterguardian.core.command.AutoRegistrationCommand;
import me.winterguardian.core.message.ErrorMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 1541869 on 2015-11-19.
 */
public class EnchantingCommand extends AutoRegistrationCommand
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = null;

        if(!(sender instanceof Player) && args.length == 0)
        {
            ErrorMessage.COMMAND_INVALID_SENDER.say(sender);
            return true;
        }
        else if(args.length == 0)
            player = (Player)sender;
        else
            player = Bukkit.getPlayer(args[0]);

        if(player == null)
        {
            ErrorMessage.COMMAND_INVALID_PLAYER.say(sender);
            return true;
        }

        player.openEnchanting(null, true);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return "enchanting";
    }

    @Override
    public String getDescription()
    {
        return "Permet d'utiliser une table d'enchantement";
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("enchanttable", "enchantmenttable", "enchantementtable", "tableenchant");
    }

    @Override
    public Permission getPermission()
    {
        return new Permission("CommandCenter.enchanting", getDescription(), PermissionDefault.OP);
    }

    @Override
    public String getUsage()
    {
        return "/workbench [player]";
    }
}

