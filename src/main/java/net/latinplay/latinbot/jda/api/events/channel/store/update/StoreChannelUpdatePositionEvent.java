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

package net.latinplay.latinbot.jda.api.events.channel.store.update;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.StoreChannel;

/**
 * Indicates that a {@link StoreChannel StoreChannel}'s position changed.
 *
 * <p>Can be used to detect when a StoreChannel position changes and get its previous position.
 *
 * <p>Identifier: {@code position}
 */
public class StoreChannelUpdatePositionEvent extends GenericStoreChannelUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "position";

    public StoreChannelUpdatePositionEvent( JDA api, long responseNumber,  StoreChannel channel, int prev)
    {
        super(api, responseNumber, channel, prev, channel.getPositionRaw(), IDENTIFIER);
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
     * The new position
     *
     * @return The new position
     */
    public int getNewPosition()
    {
        return getNewValue();
    }
}
