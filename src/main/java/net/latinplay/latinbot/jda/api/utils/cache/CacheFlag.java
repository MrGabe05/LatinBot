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

package net.latinplay.latinbot.jda.api.utils.cache;

import net.latinplay.latinbot.jda.api.entities.ClientType;
import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.entities.Member;

/**
 * Flags used to enable cache services for JDA
 */
public enum CacheFlag
{
    /**
     * Enables cache for {@link Member#getActivities()}
     */
    ACTIVITY,
    /**
     * Enables cache for {@link Member#getVoiceState()}
     * <br>This will always be cached for self member.
     */
    VOICE_STATE,
    /**
     * Enables cache for {@link Guild#getEmoteCache()}
     */
    EMOTE,
    /**
     * Enables cache for {@link Member#getOnlineStatus(ClientType) Member.getOnlineStatus(ClientType)}
     */
    CLIENT_STATUS,
}
