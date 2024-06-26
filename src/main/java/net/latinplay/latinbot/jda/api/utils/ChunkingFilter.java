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

import net.latinplay.latinbot.jda.api.JDABuilder;
import net.latinplay.latinbot.jda.api.sharding.DefaultShardManagerBuilder;
import net.latinplay.latinbot.jda.internal.utils.Checks;

/**
 * Filter function for member chunking of guilds.
 * <br>The filter decides based on the provided guild id whether chunking should be done
 * on guild initialization.
 *
 * @since 4.1.0
 *
 * @see   #ALL
 * @see   #NONE
 *
 * @see   JDABuilder#setChunkingFilter(ChunkingFilter) JDABuilder.setChunkingFilter(ChunkingFilter)
 * @see   DefaultShardManagerBuilder#setChunkingFilter(ChunkingFilter) DefaultShardManagerBuilder.setChunkingFilter(ChunkingFilter)
 */
@FunctionalInterface
public interface ChunkingFilter
{
    /** Chunk all guilds (default) */
    ChunkingFilter ALL = (x) -> true;
    /** Do not chunk any guilds (lazy loading) */
    ChunkingFilter NONE = (x) -> false;

    /**
     * Decide whether the specified guild should chunk members.
     *
     * @param  guildId
     *         The guild id
     *
     * @return True, if this guild should chunk
     */
    boolean filter(long guildId);

    /**
     * Factory method to chunk a whitelist of guild ids.
     * <br>All guilds that are not mentioned will use lazy loading.
     *
     * <p>This is useful to only chunk specific guilds like the hub server of a bot.
     *
     * @param  ids
     *         The ids that should be chunked
     *
     * @return The resulting filter
     */
    
    static ChunkingFilter include( long... ids)
    {
        Checks.notNull(ids, "ID array");
        return (guild) -> {
            for (long id : ids)
            {
                if (id == guild)
                    return true;
            }
            return false;
        };
    }

    /**
     * Factory method to disable chunking for a blacklist of guild ids.
     * <br>All guilds that are not mentioned will use chunking.
     *
     * <p>This is useful when the bot is only in one very large server that
     * takes most of its memory and should be ignored instead.
     *
     * @param  ids
     *         The ids that should not be chunked
     *
     * @return The resulting filter
     */
    
    static ChunkingFilter exclude( long... ids)
    {
        Checks.notNull(ids, "ID array");
        return (guild) -> {
            for (long id : ids)
            {
                if (id == guild)
                    return false;
            }
            return true;
        };
    }
}
