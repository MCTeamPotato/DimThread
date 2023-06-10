package dimthread.mixin.api;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.class)
@SuppressWarnings("unused")
public interface GameRulesAccessor {
    @Invoker("register")
    static <T extends GameRules.Rule<T>> GameRules.Key<T> callRegister(String name, GameRules.Category category, GameRules.Type<T> type) {
        throw new UnsupportedOperationException("This shouldn't happen!");
    }
}
