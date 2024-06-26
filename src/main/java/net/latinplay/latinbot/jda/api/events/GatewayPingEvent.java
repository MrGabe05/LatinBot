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

package net.latinplay.latinbot.jda.api.events;

import net.latinplay.latinbot.jda.api.JDA;

/**
 * Indicates that the gateway ping has been updated by the heartbeat cycle.
 * <br>You can always get the last ping update with {@link JDA#getGatewayPing()}.
 *
 * <p>Can be used to detect changes to the gateway ping.
 *
 * <p>Identifier: {@code gateway-ping}
 */
public class GatewayPingEvent extends Event implements UpdateEvent<JDA, Long>
{
    public static final String IDENTIFIER = "gateway-ping";
    private final long next, prev;

    public GatewayPingEvent( JDA api, long old)
    {
        super(api);
        this.next = api.getGatewayPing();
        this.prev = old;
    }

    /**
     * The new ping for the current JDA session
     *
     * @return The new ping in milliseconds
     */
    public long getNewPing()
    {
        return next;
    }

    /**
     * The previous ping for the current JDA session
     *
     * @return The previous ping in milliseconds, or -1 if no ping was available yet
     */
    public long getOldPing()
    {
        return prev;
    }

    
    @Override
    public String getPropertyIdentifier()
    {
        return IDENTIFIER;
    }

    
    @Override
    public JDA getEntity()
    {
        return getJDA();
    }

    
    @Override
    public Long getOldValue()
    {
        return prev;
    }

    
    @Override
    public Long getNewValue()
    {
        return next;
    }

    @Override
    public String toString()
    {
        return "GatewayUpdate[ping](" + getOldValue() + "->" + getNewValue() + ')';
    }
}
