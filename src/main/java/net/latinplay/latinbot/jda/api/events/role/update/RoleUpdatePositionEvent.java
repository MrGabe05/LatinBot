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
 * Indicates that a {@link Role Role} updated its position.
 *
 * <p>Can be used to retrieve the old position.
 *
 * <p>Identifier: {@code position}
 */
public class RoleUpdatePositionEvent extends GenericRoleUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "position";

    private final int oldPositionRaw;
    private final int newPositionRaw;

    public RoleUpdatePositionEvent( JDA api, long responseNumber,  Role role, int oldPosition, int oldPositionRaw)
    {
        super(api, responseNumber, role, oldPosition, role.getPosition(), IDENTIFIER);
        this.oldPositionRaw = oldPositionRaw;
        this.newPositionRaw = role.getPositionRaw();
    }

    /**
     * The old position
     *
     * @return The old position
     */
    public int getOldPosition()
    {
        return getOldValue();
    }

    /**
     * The old position
     *
     * @return The old position
     */
    public int getOldPositionRaw()
    {
        return oldPositionRaw;
    }

    /**
     * The new position
     *
     * @return The new position
     */
    public int getNewPosition()
    {
        return getNewValue();
    }

    /**
     * The new position
     *
     * @return The new position
     */
    public int getNewPositionRaw()
    {
        return newPositionRaw;
    }


    @Override
    public Integer getOldValue()
    {
        return super.getOldValue();
    }


    @Override
    public Integer getNewValue()
    {
        return super.getNewValue();
    }
}
