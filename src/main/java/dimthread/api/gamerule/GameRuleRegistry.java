package dimthread.api.gamerule;

import dimthread.mixin.api.GameRulesAccessor;
import net.minecraft.world.GameRules;

public final class GameRuleRegistry {
    /**
     * Registers a {@link GameRules.Rule}.
     *
     * @param name   the name of the rule
     * @param category the category of this rule
     * @param type the rule type
     * @param <T>  the type of rule
     * @return a rule key which can be used to query the value of the rule
     * @throws IllegalStateException if a rule of the same name already exists
     */
    public static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return GameRulesAccessor.callRegister(name, category, type);
    }
}
