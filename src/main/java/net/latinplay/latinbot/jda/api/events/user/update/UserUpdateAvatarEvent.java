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
import net.latinplay.latinbot.jda.api.entities.User;

/**
 * Indicates that the Avatar of a {@link User User} changed.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve the User who changed their avatar and their previous Avatar ID/URL.
 *
 * <p>Identifier: {@code avatar}
 */
public class UserUpdateAvatarEvent extends GenericUserUpdateEvent<String>
{
    public static final String IDENTIFIER = "avatar";

    public UserUpdateAvatarEvent( JDA api, long responseNumber,  User user,  String oldAvatar)
    {
        super(api, responseNumber, user, oldAvatar, user.getAvatarId(), IDENTIFIER);
    }

    /**
     * The previous avatar id
     *
     * @return The previous avatar id
     */

    public String getOldAvatarId()
    {
        return getOldValue();
    }

    /**
     * The previous avatar url
     *
     * @return The previous avatar url
     */

    public String getOldAvatarUrl()
    {
        return previous == null ? null : String.format(User.AVATAR_URL, getUser().getId(), previous, previous.startsWith("a_") ? "gif" : "png");
    }

    /**
     * The new avatar id
     *
     * @return The new avatar id
     */

    public String getNewAvatarId()
    {
        return getNewValue();
    }

    /**
     * The url of the new avatar
     *
     * @return The url of the new avatar
     */

    public String getNewAvatarUrl()
    {
        return next == null ? null : String.format(User.AVATAR_URL, getUser().getId(), next, next.startsWith("a_") ? "gif" : "png");
    }
}
