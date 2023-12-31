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
