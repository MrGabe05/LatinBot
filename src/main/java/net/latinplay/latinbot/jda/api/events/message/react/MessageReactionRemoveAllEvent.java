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
import net.latinplay.latinbot.jda.api.entities.MessageChannel;
import net.latinplay.latinbot.jda.api.events.message.GenericMessageEvent;

/**
 * Indicates the the reactions of a message have been cleared by a moderator
 *
 * <p>Can be used to detect when the reactions of a message are removed by a moderator
 */
public class MessageReactionRemoveAllEvent extends GenericMessageEvent
{
    public MessageReactionRemoveAllEvent( JDA api, long responseNumber, long messageId,  MessageChannel channel)
    {
        super(api, responseNumber, messageId, channel);
    }
}
