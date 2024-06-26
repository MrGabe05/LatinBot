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
import net.latinplay.latinbot.jda.api.entities.GuildVoiceState;
import net.latinplay.latinbot.jda.api.entities.Member;

/**
 * Indicates that a {@link Member Member} was (un-)suppressed.
 *
 * <p>Can be used to detect when a member is suppressed or un-suppressed.
 *
 * @see GuildVoiceState#isSuppressed() GuildVoiceState.isSuppressed()
 */
public class GuildVoiceSuppressEvent extends GenericGuildVoiceEvent
{
    protected final boolean suppressed;

    public GuildVoiceSuppressEvent( JDA api, long responseNumber,  Member member)
    {
        super(api, responseNumber, member);
        this.suppressed = member.getVoiceState().isSuppressed();
    }

    /**
     * Whether the member was suppressed in this event
     *
     * @return True, if the member was suppressed,
     *         <br>False, if the member was un-suppressed
     */
    public boolean isSuppressed()
    {
        return suppressed;
    }
}
