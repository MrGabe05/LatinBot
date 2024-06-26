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

import net.latinplay.latinbot.jda.api.entities.*;
import net.latinplay.latinbot.jda.api.events.channel.category.CategoryDeleteEvent;
import net.latinplay.latinbot.jda.api.events.channel.priv.PrivateChannelDeleteEvent;
import net.latinplay.latinbot.jda.api.events.channel.store.StoreChannelDeleteEvent;
import net.latinplay.latinbot.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.latinplay.latinbot.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.latinplay.latinbot.jda.api.utils.data.DataObject;
import net.latinplay.latinbot.jda.internal.JDAImpl;
import net.latinplay.latinbot.jda.internal.entities.GuildImpl;
import net.latinplay.latinbot.jda.internal.entities.UserImpl;
import net.latinplay.latinbot.jda.internal.requests.WebSocketClient;
import net.latinplay.latinbot.jda.internal.utils.cache.SnowflakeCacheViewImpl;

public class ChannelDeleteHandler extends SocketHandler
{
    public ChannelDeleteHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        ChannelType type = ChannelType.fromId(content.getInt("type"));

        long guildId = 0;
        if (type.isGuild())
        {
            guildId = content.getLong("guild_id");
            if (getJDA().getGuildSetupController().isLocked(guildId))
                return guildId;
        }

        GuildImpl guild = (GuildImpl) getJDA().getGuildById(guildId);
        final long channelId = content.getLong("id");

        switch (type)
        {
            case STORE:
            {
                StoreChannel channel = getJDA().getStoreChannelsView().remove(channelId);
                if (channel == null || guild == null)
                {
                    WebSocketClient.LOG.debug("CHANNEL_DELETE attempted to delete a store channel that is not yet cached. JSON: {}", content);
                    return null;
                }

                guild.getStoreChannelView().remove(channelId);
                getJDA().handleEvent(
                    new StoreChannelDeleteEvent(
                        getJDA(), responseNumber,
                        channel));
                break;
            }
            case TEXT:
            {
                TextChannel channel = getJDA().getTextChannelsView().remove(channelId);
                if (channel == null || guild == null)
                {
                    WebSocketClient.LOG.debug("CHANNEL_DELETE attempted to delete a text channel that is not yet cached. JSON: {}", content);
                    return null;
                }

                guild.getTextChannelsView().remove(channel.getIdLong());
                getJDA().handleEvent(
                    new TextChannelDeleteEvent(
                        getJDA(), responseNumber,
                        channel));
                break;
            }
            case VOICE:
            {
                VoiceChannel channel = getJDA().getVoiceChannelsView().remove(channelId);
                if (channel == null || guild == null)
                {
                    WebSocketClient.LOG.debug("CHANNEL_DELETE attempted to delete a voice channel that is not yet cached. JSON: {}", content);
                    return null;
                }

                // This is done in the AudioWebSocket already
//                //We use this instead of getAudioManager(Guild) so we don't create a new instance. Efficiency!
//                AudioManagerImpl manager = (AudioManagerImpl) getJDA().getAudioManagersView().get(guild.getIdLong());
//                if (manager != null && manager.isConnected()
//                        && manager.getConnectedChannel().getIdLong() == channel.getIdLong())
//                {
//                    manager.closeAudioConnection(ConnectionStatus.DISCONNECTED_CHANNEL_DELETED);
//                }
                guild.getVoiceChannelsView().remove(channel.getIdLong());
                getJDA().handleEvent(
                    new VoiceChannelDeleteEvent(
                        getJDA(), responseNumber,
                        channel));
                break;
            }
            case CATEGORY:
            {
                Category category = getJDA().getCategoriesView().remove(channelId);
                if (category == null || guild == null)
                {
                    WebSocketClient.LOG.debug("CHANNEL_DELETE attempted to delete a category channel that is not yet cached. JSON: {}", content);
                    return null;
                }

                guild.getCategoriesView().remove(channelId);
                getJDA().handleEvent(
                    new CategoryDeleteEvent(
                        getJDA(), responseNumber,
                        category));
                break;
            }
            case PRIVATE:
            {
                SnowflakeCacheViewImpl<PrivateChannel> privateView = getJDA().getPrivateChannelsView();
                PrivateChannel channel = privateView.remove(channelId);

                if (channel == null)
                    channel = getJDA().getFakePrivateChannelMap().remove(channelId);
                if (channel == null)
                {
//                    getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, () -> handle(responseNumber, allContent));
                    WebSocketClient.LOG.debug(
                            "CHANNEL_DELETE attempted to delete a private channel that is not yet cached. JSON: {}",
                            content
                    );
                    return null;
                }

                if (channel.getUser().isFake())
                    getJDA().getFakeUserMap().remove(channel.getUser().getIdLong());
                ((UserImpl) channel.getUser()).setPrivateChannel(null);
                getJDA().handleEvent(
                    new PrivateChannelDeleteEvent(
                        getJDA(), responseNumber,
                        channel));
                break;
            }
            case GROUP:
                WebSocketClient.LOG.warn("Received a CHANNEL_DELETE for a channel of type GROUP which is not supported!");
                return null;
            default:
                WebSocketClient.LOG.debug("CHANNEL_DELETE provided an unknown channel type. JSON: {}", content);
        }
        getJDA().getEventCache().clear(EventCache.Type.CHANNEL, channelId);
        if (guild != null)
            guild.pruneChannelOverrides(channelId);
        return null;
    }
}
