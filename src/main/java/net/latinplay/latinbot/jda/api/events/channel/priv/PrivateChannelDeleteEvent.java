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
package net.latinplay.latinbot.jda.api.events.channel.priv;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.PrivateChannel;
import net.latinplay.latinbot.jda.api.entities.User;
import net.latinplay.latinbot.jda.api.events.Event;

/**
 * Indicates that a {@link PrivateChannel Private Channel} was deleted.
 *
 * <p>Can be used to retrieve the issuing {@link User User}.
 */
public class PrivateChannelDeleteEvent extends Event
{
    protected final PrivateChannel channel;

    public PrivateChannelDeleteEvent( JDA api, long responseNumber,  PrivateChannel channel)
    {
        super(api, responseNumber);
        this.channel = channel;
    }

    /**
     * The target {@link User User}
     * <br>Shortcut for {@code getPrivateChannel().getUser()}
     *
     * @return The User
     */
    
    public User getUser()
    {
        return channel.getUser();
    }

    /**
     * The {@link PrivateChannel PrivateChannel}
     *
     * @return The PrivateChannel
     */
    
    public PrivateChannel getChannel()
    {
        return channel;
    }
}
