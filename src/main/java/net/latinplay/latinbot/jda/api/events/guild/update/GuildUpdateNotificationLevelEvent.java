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

package net.latinplay.latinbot.jda.api.events.guild.update;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Guild;

/**
 * Indicates that the {@link Guild.NotificationLevel NotificationLevel} of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when a NotificationLevel changes and retrieve the old one
 *
 * <p>Identifier: {@code notification_level}
 */
public class GuildUpdateNotificationLevelEvent extends GenericGuildUpdateEvent<Guild.NotificationLevel>
{
    public static final String IDENTIFIER = "notification_level";

    public GuildUpdateNotificationLevelEvent( JDA api, long responseNumber,  Guild guild,  Guild.NotificationLevel oldNotificationLevel)
    {
        super(api, responseNumber, guild, oldNotificationLevel, guild.getDefaultNotificationLevel(), IDENTIFIER);
    }

    /**
     * The old {@link Guild.NotificationLevel NotificationLevel}
     *
     * @return The old NotificationLevel
     */

    public Guild.NotificationLevel getOldNotificationLevel()
    {
        return getOldValue();
    }

    /**
     * The new {@link Guild.NotificationLevel NotificationLevel}
     *
     * @return The new NotificationLevel
     */

    public Guild.NotificationLevel getNewNotificationLevel()
    {
        return getNewValue();
    }


    @Override
    public Guild.NotificationLevel getOldValue()
    {
        return super.getOldValue();
    }


    @Override
    public Guild.NotificationLevel getNewValue()
    {
        return super.getNewValue();
    }
}
