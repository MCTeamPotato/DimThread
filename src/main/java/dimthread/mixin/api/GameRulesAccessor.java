/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
