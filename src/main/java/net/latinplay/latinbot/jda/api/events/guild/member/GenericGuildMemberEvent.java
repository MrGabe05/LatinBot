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
package net.latinplay.latinbot.jda.api.events.guild.member;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.User;
import net.latinplay.latinbot.jda.api.events.guild.GenericGuildEvent;

/**
 * Indicates that a {@link Guild Guild} member event is fired.
 * <br>Every GuildMemberEvent is an instance of this event and can be casted.
 *
 * <p>Can be used to detect any GuildMemberEvent.
 */
public abstract class GenericGuildMemberEvent extends GenericGuildEvent
{
    private final Member member;

    public GenericGuildMemberEvent( JDA api, long responseNumber,  Member member)
    {
        super(api, responseNumber, member.getGuild());
        this.member = member;
    }

    /**
     * The {@link User User} instance
     * <br>Shortcut for {@code getMember().getUser()}
     *
     * @return The User instance
     */

    public User getUser()
    {
        return getMember().getUser();
    }

    /**
     * The {@link Member Member} instance
     *
     * @return The Member instance
     */

    public Member getMember()
    {
        return member;
    }
}
