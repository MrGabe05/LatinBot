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

package net.latinplay.latinbot.jda.api.events.channel.text.update;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Category;
import net.latinplay.latinbot.jda.api.entities.TextChannel;

/**
 * Indicates that a {@link TextChannel TextChannel}'s parent changed.
 *
 * <p>Can be used to detect that the parent of a TextChannel changes.
 *
 * <p>Identifier: {@code parent}
 */
public class TextChannelUpdateParentEvent extends GenericTextChannelUpdateEvent<Category>
{
    public static final String IDENTIFIER = "parent";

    public TextChannelUpdateParentEvent( JDA api, long responseNumber,  TextChannel channel,  Category oldParent)
    {
        super(api, responseNumber, channel, oldParent, channel.getParent(), IDENTIFIER);
    }

    /**
     * The old parent {@link Category Category}
     *
     * @return The old parent category, or null
     */
    
    public Category getOldParent()
    {
        return getOldValue();
    }

    /**
     * The new parent {@link Category Category}
     *
     * @return The new parent category, or null
     */
    
    public Category getNewParent()
    {
        return getNewValue();
    }
}
