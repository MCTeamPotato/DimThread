package dimthread.mixin.api;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRules.IntRule.class)
public interface IntRuleAccessor {
    @Accessor
    int getValue();

    @Accessor
    void setValue(int value);
}
