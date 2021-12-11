package space.vectrix.example.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class HelloCommand extends BukkitCommand {


  public HelloCommand(String name) {
    super(name);
    this.setPermission("example.hello");

  }

  @Override
  public boolean execute(CommandSender commandSender, String currentAlias, String[] args) {
    if (!testPermission(commandSender)) {
      return true;
    } else {
      commandSender.sendMessage("Hello " + commandSender.getName());
    }
    return false;
  }
}
