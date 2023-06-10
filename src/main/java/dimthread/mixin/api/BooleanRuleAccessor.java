package dimthread.mixin.api;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;

@Mixin(GameRules.BooleanRule.class)
@SuppressWarnings("unused")
public interface BooleanRuleAccessor {
    @Invoker
    static GameRules.Type<GameRules.BooleanRule> invokeCreate(boolean initialValue, BiConsumer<MinecraftServer, GameRules.BooleanRule> changeCallback) {
        throw new UnsupportedOperationException("This shouldn't happen!");
    }
}
