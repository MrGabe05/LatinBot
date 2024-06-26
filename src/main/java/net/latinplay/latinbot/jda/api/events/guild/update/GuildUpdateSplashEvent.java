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
 * Indicates that the splash of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when a guild splash changes and retrieve the old one
 *
 * <p>Identifier: {@code splash}
 */
public class GuildUpdateSplashEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "splash";

    public GuildUpdateSplashEvent( JDA api, long responseNumber,  Guild guild,  String oldSplashId)
    {
        super(api, responseNumber, guild, oldSplashId, guild.getSplashId(), IDENTIFIER);
    }

    /**
     * The old splash id
     *
     * @return The old splash id, or null
     */

    public String getOldSplashId()
    {
        return getOldValue();
    }

    /**
     * The url of the old splash
     *
     * @return The url of the old splash, or null
     */

    public String getOldSplashUrl()
    {
        return previous == null ? null : String.format(Guild.SPLASH_URL, guild.getId(), previous);
    }

    /**
     * The new splash id
     *
     * @return The new splash id, or null
     */

    public String getNewSplashId()
    {
        return getNewValue();
    }

    /**
     * The url of the new splash
     *
     * @return The url of the new splash, or null
     */

    public String getNewSplashUrl()
    {
        return next == null ? null : String.format(Guild.SPLASH_URL, guild.getId(), next);
    }
}
