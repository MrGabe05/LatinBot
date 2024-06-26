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

package net.latinplay.latinbot.jda.api.events.user.update;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.JDABuilder;
import net.latinplay.latinbot.jda.api.OnlineStatus;
import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.User;

/**
 * Indicates that the {@link OnlineStatus OnlineStatus} of a {@link User User} changed.
 * <br>As with any presence updates this happened for a {@link Member Member} in a Guild!
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions} to be enabled.
 *
 * <p>Can be used to retrieve the User who changed their status and their previous status.
 *
 * <p>Identifier: {@code status}
 */
public class UserUpdateOnlineStatusEvent extends GenericUserUpdateEvent<OnlineStatus> implements GenericUserPresenceEvent
{
    public static final String IDENTIFIER = "status";

    private final Guild guild;
    private final Member member;

    public UserUpdateOnlineStatusEvent( JDA api, long responseNumber,  Member member,  OnlineStatus oldOnlineStatus)
    {
        super(api, responseNumber, member.getUser(), oldOnlineStatus, member.getOnlineStatus(), IDENTIFIER);
        this.guild = member.getGuild();
        this.member = member;
    }


    @Override
    public Guild getGuild()
    {
        return guild;
    }


    @Override
    public Member getMember()
    {
        return member;
    }

    /**
     * The old status
     *
     * @return The old status
     */

    public OnlineStatus getOldOnlineStatus()
    {
        return getOldValue();
    }

    /**
     * The new status
     *
     * @return The new status
     */

    public OnlineStatus getNewOnlineStatus()
    {
        return getNewValue();
    }


    @Override
    public OnlineStatus getOldValue()
    {
        return super.getOldValue();
    }


    @Override
    public OnlineStatus getNewValue() {
        return super.getNewValue();
    }
}
