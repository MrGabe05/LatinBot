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

package net.latinplay.latinbot.jda.api.events.guild;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.events.Event;

/**
 * Indicates that you joined a {@link Guild Guild} that is not yet available.
 * <b>This does not extend {@link GenericGuildEvent GenericGuildEvent}</b>
 *
 * <p>Can be used to retrieve id of new unavailable Guild.
 */
public class UnavailableGuildJoinedEvent extends Event
{
    private final long guildId;

    public UnavailableGuildJoinedEvent( JDA api, long responseNumber, long guildId)
    {
        super(api, responseNumber);
        this.guildId = guildId;
    }

    /**
     * The ID of the guild
     *
     * @return The ID of the guild
     */
    
    public String getGuildId()
    {
        return Long.toUnsignedString(guildId);
    }

    /**
     * The ID of the guild
     *
     * @return The ID of the guild
     */
    public long getGuildIdLong()
    {
        return guildId;
    }
}
