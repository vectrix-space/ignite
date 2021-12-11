package space.vectrix.example.mixin.core;

import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.vectrix.example.command.HelloCommand;

@Mixin(value = SimpleCommandMap.class)
public abstract class MixinSimpleCommandMap {

  @Shadow
  public abstract boolean register(String fallbackPrefix, Command command);

  @Inject(method = "setDefaultCommands()V", at = @At("TAIL"), remap = false)
  public void registerOwnCommands(CallbackInfo ci) {
    this.register("example", new HelloCommand("hello"));
  }
}



