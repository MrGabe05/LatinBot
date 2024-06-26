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
package net.latinplay.latinbot.jda.api.exceptions;

import net.latinplay.latinbot.jda.annotations.DeprecatedSince;
import net.latinplay.latinbot.jda.annotations.ForRemoval;
import net.latinplay.latinbot.jda.api.entities.Guild;

/**
 * Indicates that a {@link Guild Guild} is not {@link Guild#isAvailable() available}
 * <br>Thrown when an operation requires a Guild to be available and {@link Guild#isAvailable() Guild#isAvailable()} is {@code false}
 *
 * @deprecated This will be removed in favor of a better system which does not keep unavailable guilds in cache in the first place.
 */
@Deprecated
@ForRemoval
@DeprecatedSince("4.1.0")
public class GuildUnavailableException extends RuntimeException
{
    /**
     * Creates a new GuildUnavailableException instance
     */
    public GuildUnavailableException()
    {
        this("This operation is not possible due to the Guild being temporarily unavailable");
    }

    /**
     * Creates a new GuildUnavailableException instance
     *
     * @param reason
     *        The reason for this Exception
     */
    public GuildUnavailableException(String reason)
    {
        super(reason);
    }
}
