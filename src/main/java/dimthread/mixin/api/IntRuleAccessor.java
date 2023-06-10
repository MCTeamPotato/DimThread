package dimthread.mixin.api;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.GameRules;

@Mixin(GameRules.IntRule.class)
public interface IntRuleAccessor {
    @Accessor
    int getValue();

    @Accessor
    void setValue(int value);
}
