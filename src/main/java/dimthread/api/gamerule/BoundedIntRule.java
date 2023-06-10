package dimthread.api.gamerule;

import dimthread.mixin.api.IntRuleAccessor;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("DataFlowIssue")
public final class BoundedIntRule extends GameRules.IntRule {
    private static final Logger LOGGER = LogManager.getLogger(GameRuleRegistry.class);

    private final int minimumValue;
    private final int maximumValue;

    public BoundedIntRule(GameRules.Type<GameRules.IntRule> type, int initialValue, int minimumValue, int maximumValue) {
        super(type, initialValue);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    @Override
    protected void deserialize(String value) {
        final int i = BoundedIntRule.parseInt(value);

        if (this.minimumValue > i || this.maximumValue < i) {
            LOGGER.warn("Failed to parse integer {}. Was out of bounds {} - {}", value, this.minimumValue, this.maximumValue);
            return;
        }

        ((IntRuleAccessor) (Object) this).setValue(i);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean validate(String input) {
        try {
            int value = Integer.parseInt(input);

            if (this.minimumValue > value || this.maximumValue < value) {
                return false;
            }

            ((IntRuleAccessor) (Object) this).setValue(value);
            return true;
        } catch (NumberFormatException var3) {
            return false;
        }
    }

    @Override
    protected GameRules.IntRule copy() {
        return new BoundedIntRule(this.type, ((IntRuleAccessor) (Object) this).getValue(), this.minimumValue, this.maximumValue);
    }

    private static int parseInt(String input) {
        if (!input.isEmpty()) {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException var2) {
                LOGGER.warn("Failed to parse integer {}", input);
            }
        }

        return 0;
    }
}
