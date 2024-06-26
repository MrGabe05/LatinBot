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

package net.latinplay.latinbot.jda.api.managers;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.entities.VoiceChannel;
import net.latinplay.latinbot.jda.api.hooks.VoiceDispatchInterceptor;

/**
 * Direct access to internal gateway communication.
 * <br>This should only be used if a {@link VoiceDispatchInterceptor VoiceDispatchInterceptor} has been provided.
 *
 * <p>For normal operation use {@link Guild#getAudioManager()} instead.
 */
public interface DirectAudioController
{
    /**
     * The associated JDA instance
     *
     * @return The JDA instance
     */
    
    JDA getJDA();

    /**
     * Requests a voice server endpoint for connecting to the voice gateway.
     *
     * @param channel
     *        The channel to connect to
     *
     * @see   #reconnect(VoiceChannel)
     */
    void connect( VoiceChannel channel);

    /**
     * Requests to terminate the connection to a voice channel.
     *
     * @param guild
     *        The guild we were connected to
     *
     * @see   #reconnect(VoiceChannel)
     */
    void disconnect( Guild guild);

    /**
     * Requests to reconnect to the voice channel in the target guild.
     *
     * @param channel
     *        The channel we were connected to
     */
    void reconnect( VoiceChannel channel);
}
