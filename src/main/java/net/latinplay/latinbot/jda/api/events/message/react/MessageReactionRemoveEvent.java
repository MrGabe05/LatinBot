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

package net.latinplay.latinbot.jda.api.events.message.react;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.MessageReaction;
import net.latinplay.latinbot.jda.api.entities.User;

/**
 * Indicates that a user removed the reaction on a message
 * 
 * <p>Can be used to detect when a reaction is removed from a message
 */
public class MessageReactionRemoveEvent extends GenericMessageReactionEvent
{
    public MessageReactionRemoveEvent( JDA api, long responseNumber,  User user,
                                       Member member,  MessageReaction reaction, long userId)
    {
        super(api, responseNumber, user, member, reaction, userId);
    }
}
