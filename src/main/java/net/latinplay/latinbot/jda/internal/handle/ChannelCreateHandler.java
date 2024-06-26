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

package net.latinplay.latinbot.jda.internal.handle;

import net.latinplay.latinbot.jda.api.entities.ChannelType;
import net.latinplay.latinbot.jda.api.events.channel.category.CategoryCreateEvent;
import net.latinplay.latinbot.jda.api.events.channel.priv.PrivateChannelCreateEvent;
import net.latinplay.latinbot.jda.api.events.channel.store.StoreChannelCreateEvent;
import net.latinplay.latinbot.jda.api.events.channel.text.TextChannelCreateEvent;
import net.latinplay.latinbot.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.latinplay.latinbot.jda.api.utils.data.DataObject;
import net.latinplay.latinbot.jda.internal.JDAImpl;
import net.latinplay.latinbot.jda.internal.entities.EntityBuilder;
import net.latinplay.latinbot.jda.internal.requests.WebSocketClient;

public class ChannelCreateHandler extends SocketHandler
{
    public ChannelCreateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        ChannelType type = ChannelType.fromId(content.getInt("type"));

        long guildId = 0;
        JDAImpl jda = getJDA();
        if (type.isGuild())
        {
            guildId = content.getLong("guild_id");
            if (jda.getGuildSetupController().isLocked(guildId))
                return guildId;
        }

        EntityBuilder builder = jda.getEntityBuilder();
        switch (type)
        {
            case STORE:
            {
                builder.createStoreChannel(content, guildId);
                jda.handleEvent(
                    new StoreChannelCreateEvent(
                        jda, responseNumber,
                        builder.createStoreChannel(content, guildId)));
                break;
            }
            case TEXT:
            {
                jda.handleEvent(
                    new TextChannelCreateEvent(
                        jda, responseNumber,
                        builder.createTextChannel(content, guildId)));
                break;
            }
            case VOICE:
            {
                jda.handleEvent(
                    new VoiceChannelCreateEvent(
                        jda, responseNumber,
                        builder.createVoiceChannel(content, guildId)));
                break;
            }
            case CATEGORY:
            {
                jda.handleEvent(
                    new CategoryCreateEvent(
                        jda, responseNumber,
                        builder.createCategory(content, guildId)));
                break;
            }
            case PRIVATE:
            {
                jda.handleEvent(
                    new PrivateChannelCreateEvent(
                        jda, responseNumber,
                        builder.createPrivateChannel(content)));
                break;
            }
            case GROUP:
                WebSocketClient.LOG.warn("Received a CREATE_CHANNEL for a group which is not supported");
                return null;
            default:
                WebSocketClient.LOG.debug("Discord provided an CREATE_CHANNEL event with an unknown channel type! JSON: {}", content);
        }
        return null;
    }
}
