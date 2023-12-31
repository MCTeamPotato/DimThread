package dimthread.gamerule;

import dimthread.DimThread;
import dimthread.api.gamerule.GameRuleRegistry;
import net.minecraft.world.GameRules;

@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal", "unused"})
public abstract class GameRule<T extends GameRules.Rule<T>> {

	private GameRules.Key<T> key;
	private GameRules.Type<T> rule;

	public GameRule(String name, GameRules.Category category, GameRules.Type<T> rule) {
		this.key = GameRuleRegistry.register(DimThread.MOD_ID + "_" + name, category, rule);
		this.rule = rule;
	}

	public GameRules.Key<T> getKey() {
		return this.key;
	}

	public GameRules.Type<T> getRule() {
		return this.rule;
	}

}
