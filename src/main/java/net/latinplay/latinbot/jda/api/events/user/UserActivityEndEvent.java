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

package net.latinplay.latinbot.jda.api.events.user;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.JDABuilder;
import net.latinplay.latinbot.jda.api.entities.Activity;
import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.User;
import net.latinplay.latinbot.jda.api.events.user.update.GenericUserPresenceEvent;

/**
 * Indicates that a {@link User User} has stopped an {@link Activity}
 * in a {@link Guild}.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>This is fired for every {@link Guild} the user is part of. If the title of a stream
 * changes a start event is fired before an end event which will replace the activity.
 *
 * <p>The activities of the {@link Member} are updated before all start/end events are fired.
 * This means you can check {@link Member#getActivities()} when handling this event and it
 * will already contain all new activities, even ones that have not yet fired the start event.
 *
 * <p>To check whether the activity has concluded rather than was replaced due to an update
 * of one of its properties such as name you can check {@link Member#getActivities()}.
 * Iterate the list of activities and check if an activity of the same {@link Activity#getType() type}
 * exists, if that is the case it was replaced and not finished.
 */
public class UserActivityEndEvent extends GenericUserEvent implements GenericUserPresenceEvent
{
    private final Activity oldActivity;
    private final Member member;

    public UserActivityEndEvent( JDA api, long responseNumber,  Member member,  Activity oldActivity)
    {
        super(api, responseNumber, member.getUser());
        this.oldActivity = oldActivity;
        this.member = member;
    }

    /**
     * The old activity
     *
     * @return The old activity
     */

    public Activity getOldActivity()
    {
        return oldActivity;
    }


    @Override
    public Guild getGuild()
    {
        return member.getGuild();
    }


    @Override
    public Member getMember()
    {
        return member;
    }
}
