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

package net.latinplay.latinbot.jda.api.events.channel.store;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.StoreChannel;
import net.latinplay.latinbot.jda.api.events.Event;

/**
 * Indicates that a {@link StoreChannel StoreChannel} event was fired.
 * <br>Every StoreChannelEvent is an instance of this event and can be casted.
 *
 * <p>Can be used to detect any StoreChannelEvent.
 */
public abstract class GenericStoreChannelEvent extends Event
{
    protected final StoreChannel channel;

    public GenericStoreChannelEvent( JDA api, long responseNumber,  StoreChannel channel)
    {
        super(api, responseNumber);
        this.channel = channel;
    }

    /**
     * The {@link StoreChannel}.
     *
     * @return The channel
     */
    
    public StoreChannel getChannel()
    {
        return channel;
    }
}
