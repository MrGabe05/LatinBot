/*
 * Copyright 2015-2019 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.latinplay.latinbot.jda.api.events.role.update;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Role;

/**
 * Indicates that a {@link Role Role} updated its hoist state.
 *
 * <p>Can be used to retrieve the hoist state.
 *
 * <p>Identifier: {@code hoist}
 */
public class RoleUpdateHoistedEvent extends GenericRoleUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "hoist";

    public RoleUpdateHoistedEvent( JDA api, long responseNumber,  Role role, boolean wasHoisted)
    {
        super(api, responseNumber, role, wasHoisted, !wasHoisted, IDENTIFIER);
    }

    /**
     * Whether the role was hoisted
     *
     * @return True, if the role was hoisted before this update
     */
    public boolean wasHoisted()
    {
        return getOldValue();
    }


    @Override
    public Boolean getOldValue()
    {
        return super.getOldValue();
    }


    @Override
    public Boolean getNewValue()
    {
        return super.getNewValue();
    }
}
