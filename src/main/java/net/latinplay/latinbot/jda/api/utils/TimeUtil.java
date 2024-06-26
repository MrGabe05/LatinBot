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

package net.latinplay.latinbot.jda.api.utils;

import net.latinplay.latinbot.jda.api.entities.ISnowflake;
import net.latinplay.latinbot.jda.api.entities.MessageHistory;
import net.latinplay.latinbot.jda.internal.utils.Checks;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil
{
    public static final long DISCORD_EPOCH = 1420070400000L;
    public static final long TIMESTAMP_OFFSET = 22;
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;

    /**
     * Converts the provided epoch millisecond timestamp to a Discord Snowflake.
     * <br>This can be used as a marker/pivot for {@link MessageHistory MessageHistory} creation.
     *
     * @param  millisTimestamp
     *         The epoch millis to convert
     *
     * @return Shifted epoch millis for Discord
     */
    public static long getDiscordTimestamp(long millisTimestamp)
    {
        return (millisTimestamp - DISCORD_EPOCH) << TIMESTAMP_OFFSET;
    }

    /**
     * Gets the creation-time of a JDA-entity by doing the reverse snowflake algorithm on its id.
     * This returns the creation-time of the actual entity on Discords side, not inside JDA.
     *
     * @param  entityId
     *         The id of the JDA entity where the creation-time should be determined for
     *
     * @return The creation time of the JDA entity as OffsetDateTime
     */

    public static OffsetDateTime getTimeCreated(long entityId)
    {
        long timestamp = (entityId >>> TIMESTAMP_OFFSET) + DISCORD_EPOCH;
        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmt.setTimeInMillis(timestamp);
        return OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId());
    }

    /**
     * Gets the creation-time of a JDA-entity by doing the reverse snowflake algorithm on its id.
     * This returns the creation-time of the actual entity on Discords side, not inside JDA.
     *
     * @param  entity
     *         The JDA entity where the creation-time should be determined for
     *
     * @throws IllegalArgumentException
     *         If the provided entity is {@code null}
     *
     * @return The creation time of the JDA entity as OffsetDateTime
     */

    public static OffsetDateTime getTimeCreated( ISnowflake entity)
    {
        Checks.notNull(entity, "Entity");
        return getTimeCreated(entity.getIdLong());
    }

    /**
     * Returns a prettier String-representation of a OffsetDateTime object
     *
     * @param  time
     *         The OffsetDateTime object to format
     *
     * @return The String of the formatted OffsetDateTime
     */

    public static String getDateTimeString( OffsetDateTime time)
    {
        return time.format(dtFormatter);
    }
}
