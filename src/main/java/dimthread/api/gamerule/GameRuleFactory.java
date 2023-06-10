package dimthread.api.gamerule;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dimthread.mixin.api.BooleanRuleAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;

public final class GameRuleFactory {
    /**
     * Creates a boolean rule type.
     *
     * @param defaultValue the default value of the game rule
     * @param changedCallback a callback that is invoked when the value of a game rule has changed
     * @return a boolean rule type
     */
    public static GameRules.Type<GameRules.BooleanRule> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanRule> changedCallback) {
        return BooleanRuleAccessor.invokeCreate(defaultValue, changedCallback);
    }

    /**
     * Creates an integer rule type.
     *
     * @param defaultValue the default value of the game rule
     * @param minimumValue the minimum value the game rule may accept
     * @param maximumValue the maximum value the game rule may accept
     * @param changedCallback a callback that is invoked when the value of a game rule has changed
     * @return an integer rule type
     */
    public static GameRules.Type<GameRules.IntRule> createIntRule(int defaultValue, int minimumValue, int maximumValue, /* @Nullable */ BiConsumer<MinecraftServer, GameRules.IntRule> changedCallback) {
        return new GameRules.Type<>(
                () -> IntegerArgumentType.integer(minimumValue, maximumValue),
                type -> new BoundedIntRule(type, defaultValue, minimumValue, maximumValue), // Internally use a bounded int rule
                changedCallback,
                GameRules.Visitor::visitInt
        );
    }
}
