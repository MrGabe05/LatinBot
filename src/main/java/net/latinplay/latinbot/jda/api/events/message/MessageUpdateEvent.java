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
package net.latinplay.latinbot.jda.api.events.message;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.Message;
import net.latinplay.latinbot.jda.api.entities.MessageChannel;
import net.latinplay.latinbot.jda.api.entities.User;

/**
 * Indicates that a Message was edited in a {@link MessageChannel MessageChannel}.
 * 
 * <p>Can be used to detect a Message is edited in either a private or guild channel. Providing a MessageChannel and Message.
 * <br>This also includes whether a message is being pinned.
 *
 * <p><b>JDA does not have a cache for messages and is not able to provide previous information due to limitations by the
 * Discord API!</b>
 */
public class MessageUpdateEvent extends GenericMessageEvent
{
    private final Message message;

    public MessageUpdateEvent( JDA api, long responseNumber,  Message message)
    {
        super(api, responseNumber, message.getIdLong(), message.getChannel());
        this.message = message;
    }

    /**
     * The {@link Message Message} that was updated
     * <br>Note: Messages in JDA are not updated, they are immutable and will not change their state.
     *
     * @return The updated Message
     */

    public Message getMessage()
    {
        return message;
    }

    /**
     * The author of the Message.
     *
     * @return The message author
     *
     * @see    User User
     */

    public User getAuthor()
    {
        return message.getAuthor();
    }

    /**
     * Member instance for the author of this message or {@code null} if this
     * was not in a Guild.
     *
     * @return The Member instance for the author or null
     */

    public Member getMember()
    {
        return  message.getMember();
    }
}
