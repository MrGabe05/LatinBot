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

package net.latinplay.latinbot.jda.api.entities;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.Permission;
import net.latinplay.latinbot.jda.api.events.message.react.MessageReactionAddEvent;
import net.latinplay.latinbot.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.latinplay.latinbot.jda.api.exceptions.InsufficientPermissionException;
import net.latinplay.latinbot.jda.api.exceptions.PermissionException;
import net.latinplay.latinbot.jda.api.requests.ErrorResponse;
import net.latinplay.latinbot.jda.api.requests.RestAction;
import net.latinplay.latinbot.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.latinplay.latinbot.jda.internal.requests.RestActionImpl;
import net.latinplay.latinbot.jda.internal.requests.Route;
import net.latinplay.latinbot.jda.internal.requests.restaction.pagination.ReactionPaginationActionImpl;
import net.latinplay.latinbot.jda.internal.utils.Checks;
import net.latinplay.latinbot.jda.internal.utils.EncodingUtil;

import java.util.Objects;

/**
 * An object representing a single MessageReaction from Discord.
 * This is an immutable object and is not updated by method calls or changes in Discord. A new snapshot instance
 * built from Discord is needed to see changes.
 *
 * @since  3.0
 *
 * @see    Message#getReactions()
 * @see    Message#getReactionByUnicode(String)
 * @see    Message#getReactionById(long)
 */
public class MessageReaction
{
    private final MessageChannel channel;
    private final ReactionEmote emote;
    private final long messageId;
    private final boolean self;
    private final int count;

    /**
     * Creates a new MessageReaction instance
     *
     * @param  channel
     *         The {@link MessageChannel} this Reaction was used in
     * @param  emote
     *         The {@link MessageReaction.ReactionEmote ReactionEmote} that was used
     * @param  messageId
     *         The message id this reaction is attached to
     * @param  self
     *         Whether we already reacted with this Reaction
     * @param  count
     *         The amount of people that reacted with this Reaction
     */
    public MessageReaction( MessageChannel channel,  ReactionEmote emote, long messageId, boolean self, int count)
    {
        this.channel = channel;
        this.emote = emote;
        this.messageId = messageId;
        this.self = self;
        this.count = count;
    }

    /**
     * The JDA instance of this Reaction
     *
     * @return The JDA instance of this Reaction
     */
    
    public JDA getJDA()
    {
        return channel.getJDA();
    }

    /**
     * Whether the currently logged in account has reacted with this reaction
     *
     * <p><b>This will always be false for events. Discord does not provide this information for reaction events.</b>
     * You can use {@link MessageChannel#retrieveMessageById(String)} to get this information on a complete message.
     *
     * @return True, if we reacted with this reaction
     */
    public boolean isSelf()
    {
        return self;
    }

    /**
     * Whether this reaction can provide a count via {@link #getCount()}.
     * <br>This is usually not provided for reactions coming from {@link MessageReactionAddEvent}
     * or similar.
     *
     * @return True, if a count is available
     *
     * @see    #getCount()
     */
    public boolean hasCount()
    {
        return count >= 0;
    }

    /**
     * The amount of users that already reacted with this Reaction
     * <br><b>This is not updated, it is a {@code final int} per Reaction instance</b>
     *
     * <p>This value is not available in events such as {@link MessageReactionAddEvent MessageReactionAddEvent}
     * and {@link MessageReactionRemoveEvent MessageReactionRemoveEvent} in which case an
     * {@link java.lang.IllegalStateException IllegalStateException} is thrown!
     *
     * @throws java.lang.IllegalStateException
     *         If this MessageReaction is from an event which does not provide a count
     *
     * @return The amount of users that reacted with this Reaction
     */
    public int getCount()
    {
        if (!hasCount())
            throw new IllegalStateException("Cannot retrieve count for this MessageReaction!");
        return count;
    }

    /**
     * The {@link ChannelType ChannelType}
     * this Reaction was used in.
     *
     * @return The ChannelType
     */
    
    public ChannelType getChannelType()
    {
        return channel.getType();
    }

    /**
     * Whether this Reaction was used in a {@link MessageChannel MessageChannel}
     * of the specified {@link ChannelType ChannelType}.
     *
     * @param  type
     *         The ChannelType to compare
     *
     * @return True, if this Reaction was used in a MessageChannel from the specified ChannelType
     */
    public boolean isFromType( ChannelType type)
    {
        return getChannelType() == type;
    }

    /**
     * The {@link Guild Guild} this Reaction was used in,
     * this might return {@code null} when this Reaction was not used in a MessageChannel
     * from the ChannelType {@link ChannelType#TEXT TEXT}!
     *
     * @return {@link Guild Guild} this Reaction was used in, or {@code null}
     */
    
    public Guild getGuild()
    {
        TextChannel channel = getTextChannel();
        return channel != null ? channel.getGuild() : null;
    }

    /**
     * The {@link TextChannel TextChannel} this Reaction was used in
     * or {@code null} if this is not from type {@link ChannelType#TEXT ChannelType.TEXT}!
     *
     * @return The {@link TextChannel TextChannel} or {@code null}
     */
    
    public TextChannel getTextChannel()
    {
        return getChannel() instanceof TextChannel ? (TextChannel) getChannel() : null;
    }

    /**
     * The {@link PrivateChannel PrivateChannel} this Reaction was used in
     * or {@code null} if this is not from type {@link ChannelType#PRIVATE ChannelType.PRIVATE}!
     *
     * @return The {@link PrivateChannel PrivateChannel} or {@code null}
     */
    
    public PrivateChannel getPrivateChannel()
    {
        return getChannel() instanceof PrivateChannel ? (PrivateChannel) getChannel() : null;
    }

    /**
     * The {@link MessageChannel MessageChannel}
     * this Reaction was used in.
     *
     * @return The channel this Reaction was used in
     */
    
    public MessageChannel getChannel()
    {
        return channel;
    }

    /**
     * The {@link MessageReaction.ReactionEmote ReactionEmote}
     * of this Reaction
     *
     * @return The final instance of this Reaction's Emote/Emoji
     */
    
    public ReactionEmote getReactionEmote()
    {
        return emote;
    }

    /**
     * The message id this reaction is attached to
     *
     * @return The message id this reaction is attached to
     */
    
    public String getMessageId()
    {
        return Long.toUnsignedString(messageId);
    }

    /**
     * The message id this reaction is attached to
     *
     * @return The message id this reaction is attached to
     */
    public long getMessageIdLong()
    {
        return messageId;
    }

    /**
     * Retrieves the {@link User Users} that
     * already reacted with this MessageReaction.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>If the message this reaction was attached to got deleted.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If the channel this reaction was used in got deleted.</li>
     *
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the channel/guild</li>
     * </ul>
     *
     * @return {@link ReactionPaginationAction ReactionPaginationAction}
     */
    
    
    public ReactionPaginationAction retrieveUsers()
    {
        return new ReactionPaginationActionImpl(this);
    }

    /**
     * Removes this Reaction from the Message.
     * <br>This will remove our own reaction as an overload
     * of {@link #removeReaction(User)}.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>If the message this reaction was attached to got deleted.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If the channel this reaction was used in got deleted.</li>
     *
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the channel/guild</li>
     * </ul>
     *
     * @return {@link RestAction RestAction} - Type: Void
     *         Nothing is returned on success
     */
    
    
    public RestAction<Void> removeReaction()
    {
        return removeReaction(getJDA().getSelfUser());
    }

    /**
     * Removes this Reaction from the Message.
     * <br>This will remove the reaction of the {@link User User}
     * provided.
     *
     * <p>If the provided User did not react with this Reaction this does nothing.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>If the message this reaction was attached to got deleted.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If the channel this reaction was used in got deleted.</li>
     *
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the channel/guild</li>
     * </ul>
     *
     * @param  user
     *         The User of which to remove the reaction
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided {@code user} is null.
     * @throws InsufficientPermissionException
     *         If the provided User is not us and we do not have permission to
     *         {@link Permission#MESSAGE_MANAGE manage messages}
     *         in the channel this reaction was used in
     * @throws PermissionException
     *         If the message is from another user in a {@link PrivateChannel PrivateChannel}
     *
     * @return {@link RestAction RestAction}
     *         Nothing is returned on success
     */
    
    
    public RestAction<Void> removeReaction( User user)
    {
        Checks.notNull(user, "User");
        boolean self = user.equals(getJDA().getSelfUser());
        if (!self)
        {
            if (channel.getType() == ChannelType.TEXT)
            {
                GuildChannel channel = (GuildChannel) this.channel;
                if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
                    throw new InsufficientPermissionException(channel, Permission.MESSAGE_MANAGE);
            }
            else
            {
                throw new PermissionException("Unable to remove Reaction of other user in non-text channel!");
            }
        }

        String code = emote.isEmote()
                    ? emote.getName() + ":" + emote.getId()
                    : EncodingUtil.encodeUTF8(emote.getName());
        String target = self ? "@me" : user.getId();
        Route.CompiledRoute route = Route.Messages.REMOVE_REACTION.compile(channel.getId(), getMessageId(), code, target);
        return new RestActionImpl<>(getJDA(), route);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof MessageReaction))
            return false;
        MessageReaction r = (MessageReaction) obj;
        return r.emote.equals(emote)
            && r.self == self
            && r.messageId == messageId;
    }

    @Override
    public String toString()
    {
        return "MR:(M:(" + messageId + ") / " + emote + ")";
    }

    /**
     * Represents an Emoji/Emote of a MessageReaction
     * <br>This is used to wrap both emojis and emotes
     */
    public static class ReactionEmote implements ISnowflake
    {
        private final JDA api;
        private final String name;
        private final long id;
        private final Emote emote;

        private ReactionEmote( String name,  JDA api)
        {
            this.name = name;
            this.api = api;
            this.id = 0;
            this.emote = null;
        }

        private ReactionEmote( Emote emote)
        {
            this.api = emote.getJDA();
            this.name = emote.getName();
            this.id = emote.getIdLong();
            this.emote = emote;
        }

        
        public static ReactionEmote fromUnicode( String name,  JDA api)
        {
            return new ReactionEmote(name, api);
        }

        
        public static ReactionEmote fromCustom( Emote emote)
        {
            return new ReactionEmote(emote);
        }

        /**
         * Whether this is an {@link Emote Emote} wrapper.
         * <br>This means {@link #getEmoji()} will throw {@link java.lang.IllegalStateException}.
         *
         * @return True, if {@link #getEmote()} can be used
         *
         * @see    #getEmote()
         */
        public boolean isEmote()
        {
            return emote != null;
        }

        /**
         * Whether this represents a unicode emoji.
         * <br>This means {@link #getEmote()}, {@link #getId()}, and {@link #getIdLong()} will not be available.
         *
         * @return True, if this represents a unicode emoji
         *
         * @see    #getEmoji()
         */
        public boolean isEmoji()
        {
            return emote == null;
        }

        /**
         * The name for this emote/emoji
         * <br>For unicode emojis this will be the unicode of said emoji rather than an alias like {@code :smiley:}.
         *
         * <p>For better use in consoles that do not support unicode emoji use {@link #getAsCodepoints()} for a more
         * readable representation of the emoji.
         *
         * <p>Custom emotes may return an empty string for this if the emote was deleted.
         *
         * @return The name for this emote/emoji
         */
        
        public String getName()
        {
            return name;
        }

        /**
         * Converts the unicode name into codepoint notation like {@code U+1F602}.
         *
         * @throws java.lang.IllegalStateException
         *         If this is not an emoji reaction, see {@link #isEmoji()}
         *
         * @return String containing the codepoint representation of the reaction emoji
         */
        
        public String getAsCodepoints()
        {
            if (!isEmoji())
                throw new IllegalStateException("Cannot get codepoint for custom emote reaction");
            return EncodingUtil.encodeCodepoints(name);
        }

        @Override
        public long getIdLong()
        {
            if (!isEmote())
                throw new IllegalStateException("Cannot get id for emoji reaction");
            return id;
        }

        /**
         * The unicode representing the emoji used for reacting.
         *
         * @throws java.lang.IllegalStateException
         *         If this is not an emoji reaction, see {@link #isEmoji()}
         *
         * @return The unicode for the emoji
         */
        
        public String getEmoji()
        {
            if (!isEmoji())
                throw new IllegalStateException("Cannot get emoji code for custom emote reaction");
            return getName();
        }

        /**
         * The instance of {@link Emote Emote}
         * for the Reaction instance.
         *
         * @throws java.lang.IllegalStateException
         *         If this is not a custom emote reaction, see {@link #isEmote()}
         *
         * @return The Emote for the Reaction instance
         */
        
        public Emote getEmote()
        {
            if (!isEmote())
                throw new IllegalStateException("Cannot get custom emote for emoji reaction");
            return emote;
        }

        /**
         * The current JDA instance for the Reaction
         *
         * @return The JDA instance of the Reaction
         */
        
        public JDA getJDA()
        {
            return api;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof ReactionEmote
                    && Objects.equals(((ReactionEmote) obj).id, id)
                    && ((ReactionEmote) obj).getName().equals(name);
        }

        @Override
        public String toString()
        {
            if (isEmoji())
                return "RE:" + getAsCodepoints();
            return "RE:" + getName() + "(" + getId() + ")";
        }
    }
}
