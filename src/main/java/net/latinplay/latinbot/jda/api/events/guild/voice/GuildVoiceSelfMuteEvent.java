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

package net.latinplay.latinbot.jda.api.events.guild.voice;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Member;

/**
 * Indicates that a {@link Member Member} (un-)muted itself.
 *
 * <p>Can be used to detect when a member deafens or un-mutes itself.
 */
public class GuildVoiceSelfMuteEvent extends GenericGuildVoiceEvent
{
    protected final boolean selfMuted;

    public GuildVoiceSelfMuteEvent( JDA api, long responseNumber,  Member member)
    {
        super(api, responseNumber, member);
        this.selfMuted = member.getVoiceState().isSelfMuted();
    }

    /**
     * Whether the member muted itself in this event
     *
     * @return True, if the member muted itself,
     *         <br>False, if the member un-muted itself
     */
    public boolean isSelfMuted()
    {
        return selfMuted;
    }
}
