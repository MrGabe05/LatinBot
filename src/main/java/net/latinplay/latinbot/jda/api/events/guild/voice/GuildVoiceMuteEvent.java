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
 * Indicates that a {@link Member Member} was (un-)muted.
 * <br>Combines {@link GuildVoiceGuildMuteEvent GuildVoiceGuildMuteEvent}
 * and {@link GuildVoiceSelfMuteEvent GuildVoiceSelfMuteEvent}!
 *
 * <p>Can be used to detect when a member is muted or un-muted.
 */
public class GuildVoiceMuteEvent extends GenericGuildVoiceEvent
{
    protected final boolean muted;

    public GuildVoiceMuteEvent( JDA api, long responseNumber,  Member member)
    {
        super(api, responseNumber, member);
        this.muted = member.getVoiceState().isMuted();
    }

    /**
     * Whether the member was muted in this event.
     *
     * @return True, if the member was muted with this event
     *         <br>False, if the member was un-muted in this event
     */
    public boolean isMuted()
    {
        return muted;
    }
}
