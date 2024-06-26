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

import net.latinplay.latinbot.jda.api.AccountType;
import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.Permission;
import net.latinplay.latinbot.jda.api.exceptions.InsufficientPermissionException;
import net.latinplay.latinbot.jda.api.exceptions.PermissionException;
import net.latinplay.latinbot.jda.api.requests.ErrorResponse;
import net.latinplay.latinbot.jda.api.requests.RestAction;
import net.latinplay.latinbot.jda.api.requests.restaction.AuditableRestAction;
import net.latinplay.latinbot.jda.api.requests.restaction.ChannelAction;
import net.latinplay.latinbot.jda.api.requests.restaction.WebhookAction;
import net.latinplay.latinbot.jda.api.utils.MiscUtil;
import net.latinplay.latinbot.jda.internal.utils.Checks;

import java.util.Collection;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.List;

/**
 * Represents a Discord Text GuildChannel.
 * <br>Adds additional functionality and information for text channels in Discord.
 *
 * <p>This is a {@link GuildChannel GuildChannel} capable of sending messages.
 *
 * @see GuildChannel
 * @see MessageChannel
 * @see VoiceChannel
 * @see Category
 *
 * @see   Guild#getTextChannelCache()
 * @see   Guild#getTextChannels()
 * @see   Guild#getTextChannelsByName(String, boolean)
 * @see   Guild#getTextChannelById(long)
 *
 * @see   JDA#getTextChannelCache()
 * @see   JDA#getTextChannels()
 * @see   JDA#getTextChannelsByName(String, boolean)
 * @see   JDA#getTextChannelById(long)
 */
public interface TextChannel extends GuildChannel, MessageChannel, IMentionable
{
    /**
     * The maximum duration of slowmode in seconds
     */
    int MAX_SLOWMODE = 21600;

    /**
     * The topic set for this TextChannel.
     * <br>If no topic has been set, this returns null.
     *
     * @return Possibly-null String containing the topic of this TextChannel.
     */
    
    String getTopic();
    
    /**
    * Whether or not this channel is considered as "NSFW" (Not-Safe-For-Work)
    * 
    * @return True, If this TextChannel is considered NSFW by the official Discord Client
    */
    boolean isNSFW();

    /**
     * The slowmode set for this TextChannel.
     * <br>If slowmode is set this returns an {@code int} between 1 and {@link TextChannel#MAX_SLOWMODE TextChannel.MAX_SLOWMODE}.
     * <br>If not set this returns {@code 0}.
     *
     * <p>Note that only {@link AccountType#CLIENT CLIENT} type accounts are
     * affected by slowmode, and that {@link AccountType#BOT BOT} accounts
     * are immune to the restrictions.
     * <br>Having {@link Permission#MESSAGE_MANAGE MESSAGE_MANAGE} or
     * {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} permission also
     * grants immunity to slowmode.
     *
     * @return The slowmode for this TextChannel, between 1 and {@link TextChannel#MAX_SLOWMODE TextChannel.MAX_SLOWMODE}, or {@code 0} if no slowmode is set.
     */
    int getSlowmode();

    
    @Override
    ChannelAction<TextChannel> createCopy( Guild guild);

    
    @Override
    ChannelAction<TextChannel> createCopy();

    /**
     * Retrieves the {@link Webhook Webhooks} attached to this TextChannel.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>if this channel was deleted</li>
     *
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>if we were removed from the guild</li>
     * </ul>
     *
     * @return {@link RestAction} - Type: List{@literal <}{@link Webhook Webhook}{@literal >}
     *         <br>Retrieved an immutable list of Webhooks attached to this channel
     */
    
    
    RestAction<List<Webhook>> retrieveWebhooks();

    /**
     * Creates a new {@link Webhook Webhook}.
     *
     * <p>Possible {@link ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The webhook could not be created due to a permission discrepancy</li>
     *
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The {@link Permission#VIEW_CHANNEL VIEW_CHANNEL} permission was removed</li>
     * </ul>
     *
     * @param  name
     *         The default name for the new Webhook.
     *
     * @throws PermissionException
     *         If you do not hold the permission {@link Permission#MANAGE_WEBHOOKS Manage Webhooks}
     * @throws IllegalArgumentException
     *         If the provided name is {@code null}, blank or not
     *         between 2-100 characters in length
     *
     * @return A specific {@link WebhookAction WebhookAction}
     *         <br>This action allows to set fields for the new webhook before creating it
     */
    
    
    WebhookAction createWebhook( String name);

    /**
     * Bulk deletes a list of messages.
     * <b>This is not the same as calling {@link Message#delete()} in a loop.</b>
     * <br>This is much more efficient, but it has a different ratelimit. You may call this once per second per Guild.
     *
     * <p>Must be at least 2 messages and not be more than 100 messages at a time.
     * <br>If you only have 1 message, use the {@link Message#delete()} method instead.
     *
     * <br><p>You must have the Permission {@link Permission#MESSAGE_MANAGE MESSAGE_MANAGE} in this channel to use
     * this function.
     *
     * <p>This method is best used when using {@link MessageHistory MessageHistory} to delete a large amount
     * of messages. If you have a large amount of messages but only their message Ids, please use {@link #deleteMessagesByIds(Collection)}
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>if this channel was deleted</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>if any of the provided messages does not exist</li>
     *
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>if we were removed from the guild</li>
     *
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The send request was attempted after the account lost
     *         {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE} in the channel.</li>
     * </ul>
     *
     * @param  messages
     *         The collection of messages to delete.
     *
     * @throws IllegalArgumentException
     *         If the size of the list less than 2 or more than 100 messages.
     * @throws InsufficientPermissionException
     *         If this account does not have {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE}
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     *
     * @see    #deleteMessagesByIds(Collection)
     * @see    #purgeMessages(List)
     */
    
    
    RestAction<Void> deleteMessages( Collection<Message> messages);

    /**
     * Bulk deletes a list of messages.
     * <b>This is not the same as calling {@link MessageChannel#deleteMessageById(String)} in a loop.</b>
     * <br>This is much more efficient, but it has a different ratelimit. You may call this once per second per Guild.
     *
     * <p>Must be at least 2 messages and not be more than 100 messages at a time.
     * <br>If you only have 1 message, use the {@link Message#delete()} method instead.
     *
     * <br><p>You must have {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE} in this channel to use
     * this function.
     *
     * <p>This method is best used when you have a large amount of messages but only their message Ids. If you are using
     * {@link MessageHistory MessageHistory} or have {@link Message Message}
     * objects, it would be easier to use {@link #deleteMessages(java.util.Collection)}.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>if this channel was deleted</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>if any of the provided messages does not exist</li>
     *
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>if we were removed from the guild</li>
     *
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The send request was attempted after the account lost
     *         {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE} in the channel.</li>
     * </ul>
     *
     * @param  messageIds
     *         The message ids for the messages to delete.
     *
     * @throws java.lang.IllegalArgumentException
     *         If the size of the list less than 2 or more than 100 messages.
     * @throws java.lang.NumberFormatException
     *         If any of the provided ids cannot be parsed by {@link Long#parseLong(String)}
     * @throws InsufficientPermissionException
     *         If this account does not have {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE}
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     *
     * @see    #deleteMessages(Collection)
     * @see    #purgeMessagesById(List)
     */
    
    
    RestAction<Void> deleteMessagesByIds( Collection<String> messageIds);

    /**
     * Deletes a {@link Webhook Webhook} attached to this channel
     * by the {@code id} specified.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_WEBHOOK}
     *     <br>The provided id does not refer to a WebHook present in this TextChannel, either due
     *         to it not existing or having already been deleted.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>if this channel was deleted</li>
     *
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>if we were removed from the guild</li>
     *
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The send request was attempted after the account lost
     *         {@link Permission#MANAGE_WEBHOOKS Permission.MANAGE_WEBHOOKS} in the channel.</li>
     * </ul>
     *
     * @param  id
     *         The not-null id for the target Webhook.
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided {@code id} is {@code null} or empty.
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have
     *         {@link Permission#MANAGE_WEBHOOKS Permission.MANAGE_WEBHOOKS} in this channel.
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    
    
    AuditableRestAction<Void> deleteWebhookById( String id);

    /**
     * Attempts to remove all reactions from a message with the specified {@code messageId} in this TextChannel
     * <br>This is useful for moderator commands that wish to remove all reactions at once from a specific message.
     *
     * <p>The following {@link ErrorResponse ErrorResponses} are possible:
     * <ul>
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The clear-reactions request was attempted after the account lost access to the {@link TextChannel TextChannel}
     *         due to {@link Permission#MESSAGE_READ Permission.MESSAGE_READ} being revoked, or the
     *         account lost access to the {@link Guild Guild}
     *         typically due to being kicked or removed.</li>
     *
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The clear-reactions request was attempted after the account lost {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE}
     *         in the {@link TextChannel TextChannel} when adding the reaction.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *         The clear-reactions request was attempted after the Message had been deleted.</li>
     * </ul>
     *
     * @param  messageId
     *         The not-empty valid message id
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have
     *         {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE} in this channel.
     * @throws java.lang.IllegalArgumentException
     *         If the provided {@code id} is {@code null} or empty.
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    
    
    RestAction<Void> clearReactionsById( String messageId);

    /**
     * Attempts to remove all reactions from a message with the specified {@code messageId} in this TextChannel
     * <br>This is useful for moderator commands that wish to remove all reactions at once from a specific message.
     *
     * <p>The following {@link ErrorResponse ErrorResponses} are possible:
     * <ul>
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The clear-reactions request was attempted after the account lost access to the {@link TextChannel TextChannel}
     *         due to {@link Permission#MESSAGE_READ Permission.MESSAGE_READ} being revoked, or the
     *         account lost access to the {@link Guild Guild}
     *         typically due to being kicked or removed.</li>
     *
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The clear-reactions request was attempted after the account lost {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE}
     *         in the {@link TextChannel TextChannel} when adding the reaction.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *         The clear-reactions request was attempted after the Message had been deleted.</li>
     * </ul>
     *
     * @param  messageId
     *         The message id
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have
     *         {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE} in this channel.
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    
    
    default RestAction<Void> clearReactionsById(long messageId)
    {
        return clearReactionsById(Long.toUnsignedString(messageId));
    }

    /**
     * Attempts to remove the reaction from a message represented by the specified {@code messageId}
     * in this MessageChannel.
     *
     * <p>The unicode provided has to be a unicode representation of the emoji
     * that is supposed to be represented by the Reaction.
     * <br>To retrieve the characters needed you can use an api or
     * the official discord client by escaping the emoji (\:emoji-name:)
     * and copying the resulting emoji from the sent message.
     *
     * <p>This method encodes the provided unicode for you.
     * <b>Do not encode the emoji before providing the unicode.</b>
     *
     * <p>The following {@link ErrorResponse ErrorResponses} are possible:
     * <ul>
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The request was attempted after the account lost access to the
     *         {@link Guild Guild}
     *         typically due to being kicked or removed, or after {@link Permission#MESSAGE_READ Permission.MESSAGE_READ}
     *         was revoked in the {@link TextChannel TextChannel}
     *     <br>Also can happen if the account lost the {@link Permission#MESSAGE_HISTORY Permission.MESSAGE_HISTORY}</li>
     *
     *
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The request was attempted after the account lost
     *         {@link Permission#MESSAGE_ADD_REACTION Permission.MESSAGE_ADD_REACTION} in the
     *         {@link TextChannel TextChannel}.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_EMOJI}
     *     <br>The provided unicode character does not refer to a known emoji unicode character.
     *     <br>Proper unicode characters for emojis can be found at
     *         <a href="http://unicode.org/emoji/charts/full-emoji-list.html" target="_blank">http://unicode.org/emoji/charts/full-emoji-list.html</a></li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The provided {@code messageId} is unknown in this MessageChannel, either due to the id being invalid, or
     *         the message it referred to has already been deleted.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>The request was attempted after the channel was deleted.</li>
     * </ul>
     *
     * @param  messageId
     *         The messageId to remove the reaction from
     * @param  unicode
     *         The unicode characters of the emoji
     * @param  user
     *         The target user of which to remove from
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If provided {@code messageId} is {@code null} or empty.</li>
     *             <li>If provided {@code unicode} is {@code null} or empty.</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have
     *         {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE} in this channel.
     *
     * @return {@link RestAction}
     */
    
    
    RestAction<Void> removeReactionById( String messageId,  String unicode,  User user);

    /**
     * Attempts to remove the reaction from a message represented by the specified {@code messageId}
     * in this MessageChannel.
     *
     * <p>The unicode provided has to be a unicode representation of the emoji
     * that is supposed to be represented by the Reaction.
     * <br>To retrieve the characters needed you can use an api or
     * the official discord client by escaping the emoji (\:emoji-name:)
     * and copying the resulting emoji from the sent message.
     *
     * <p>This method encodes the provided unicode for you.
     * <b>Do not encode the emoji before providing the unicode.</b>
     *
     * <p>The following {@link ErrorResponse ErrorResponses} are possible:
     * <ul>
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The request was attempted after the account lost access to the
     *         {@link Guild Guild}
     *         typically due to being kicked or removed, or after {@link Permission#MESSAGE_READ Permission.MESSAGE_READ}
     *         was revoked in the {@link TextChannel TextChannel}
     *     <br>Also can happen if the account lost the {@link Permission#MESSAGE_HISTORY Permission.MESSAGE_HISTORY}</li>
     *
     *
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The request was attempted after the account lost
     *         {@link Permission#MESSAGE_ADD_REACTION Permission.MESSAGE_ADD_REACTION} in the
     *         {@link TextChannel TextChannel}.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_EMOJI}
     *     <br>The provided unicode character does not refer to a known emoji unicode character.
     *     <br>Proper unicode characters for emojis can be found at
     *         <a href="http://unicode.org/emoji/charts/full-emoji-list.html" target="_blank">http://unicode.org/emoji/charts/full-emoji-list.html</a></li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The provided {@code messageId} is unknown in this MessageChannel, either due to the id being invalid, or
     *         the message it referred to has already been deleted.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>The request was attempted after the channel was deleted.</li>
     * </ul>
     *
     * @param  messageId
     *         The messageId to remove the reaction from
     * @param  unicode
     *         The unicode characters of the emoji
     * @param  user
     *         The target user of which to remove from
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If provided {@code messageId} is {@code null} or empty.</li>
     *             <li>If provided {@code unicode} is {@code null} or empty.</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have
     *         {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE} in this channel.
     *
     * @return {@link RestAction}
     */
    
    
    default RestAction<Void> removeReactionById(long messageId,  String unicode,  User user)
    {
        return removeReactionById(Long.toUnsignedString(messageId), unicode, user);
    }

    /**
     * Attempts to remove the reaction from a message represented by the specified {@code messageId}
     * in this MessageChannel.
     *
     * <p><b>An Emote is not the same as an emoji!</b>
     * <br>Emotes are custom guild-specific images unlike global unicode emojis!
     *
     * <p>The following {@link ErrorResponse ErrorResponses} are possible:
     * <ul>
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The request was attempted after the account lost access to the
     *         {@link Guild Guild}
     *         typically due to being kicked or removed, or after {@link Permission#MESSAGE_READ Permission.MESSAGE_READ}
     *         was revoked in the {@link TextChannel TextChannel}
     *     <br>Also can happen if the account lost the {@link Permission#MESSAGE_HISTORY Permission.MESSAGE_HISTORY}</li>
     *
     *
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The request was attempted after the account lost
     *         {@link Permission#MESSAGE_ADD_REACTION Permission.MESSAGE_ADD_REACTION} in the
     *         {@link TextChannel TextChannel}.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_EMOJI}
     *     <br>The provided unicode character does not refer to a known emoji unicode character.
     *     <br>Proper unicode characters for emojis can be found at
     *         <a href="http://unicode.org/emoji/charts/full-emoji-list.html" target="_blank">http://unicode.org/emoji/charts/full-emoji-list.html</a></li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The provided {@code messageId} is unknown in this MessageChannel, either due to the id being invalid, or
     *         the message it referred to has already been deleted.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>The request was attempted after the channel was deleted.</li>
     * </ul>
     *
     * @param  messageId
     *         The messageId to remove the reaction from
     * @param  emote
     *         The emote to remove
     * @param  user
     *         The target user of which to remove from
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If provided {@code messageId} is {@code null} or empty.</li>
     *             <li>If provided {@code emote} is {@code null}.</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have
     *         {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE} in this channel.
     *
     * @return {@link RestAction}
     */
    
    
    default RestAction<Void> removeReactionById( String messageId,  Emote emote,  User user)
    {
        Checks.notNull(emote, "Emote");
        return removeReactionById(messageId, emote.getName() + ":" + emote.getId(), user);
    }

    /**
     * Attempts to remove the reaction from a message represented by the specified {@code messageId}
     * in this MessageChannel.
     *
     * <p><b>An Emote is not the same as an emoji!</b>
     * <br>Emotes are custom guild-specific images unlike global unicode emojis!
     *
     * <p>The following {@link ErrorResponse ErrorResponses} are possible:
     * <ul>
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>The request was attempted after the account lost access to the
     *         {@link Guild Guild}
     *         typically due to being kicked or removed, or after {@link Permission#MESSAGE_READ Permission.MESSAGE_READ}
     *         was revoked in the {@link TextChannel TextChannel}
     *     <br>Also can happen if the account lost the {@link Permission#MESSAGE_HISTORY Permission.MESSAGE_HISTORY}</li>
     *
     *
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The request was attempted after the account lost
     *         {@link Permission#MESSAGE_ADD_REACTION Permission.MESSAGE_ADD_REACTION} in the
     *         {@link TextChannel TextChannel}.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_EMOJI}
     *     <br>The provided unicode character does not refer to a known emoji unicode character.
     *     <br>Proper unicode characters for emojis can be found at
     *         <a href="http://unicode.org/emoji/charts/full-emoji-list.html" target="_blank">http://unicode.org/emoji/charts/full-emoji-list.html</a></li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_MESSAGE UNKNOWN_MESSAGE}
     *     <br>The provided {@code messageId} is unknown in this MessageChannel, either due to the id being invalid, or
     *         the message it referred to has already been deleted.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>The request was attempted after the channel was deleted.</li>
     * </ul>
     *
     * @param  messageId
     *         The messageId to remove the reaction from
     * @param  emote
     *         The emote to remove
     * @param  user
     *         The target user of which to remove from
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If provided {@code messageId} is {@code null} or empty.</li>
     *             <li>If provided {@code emote} is {@code null}.</li>
     *         </ul>
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have
     *         {@link Permission#MESSAGE_MANAGE Permission.MESSAGE_MANAGE} in this channel.
     *
     * @return {@link RestAction}
     */
    
    
    default RestAction<Void> removeReactionById(long messageId,  Emote emote,  User user)
    {
        return removeReactionById(Long.toUnsignedString(messageId), emote, user);
    }

    /**
     * Whether we can send messages in this channel.
     * <br>This is an overload of {@link #canTalk(Member)} with the SelfMember.
     * <br>Checks for both {@link Permission#MESSAGE_READ Permission.MESSAGE_READ} and
     * {@link Permission#MESSAGE_WRITE Permission.MESSAGE_WRITE}.
     *
     * @return True, if we are able to read and send messages in this channel
     */
    boolean canTalk();

    /**
     * Whether the specified {@link Member}
     * can send messages in this channel.
     * <br>Checks for both {@link Permission#MESSAGE_READ Permission.MESSAGE_READ} and
     * {@link Permission#MESSAGE_WRITE Permission.MESSAGE_WRITE}.
     *
     * @param  member
     *         The Member to check
     *
     * @return True, if the specified member is able to read and send messages in this channel
     */
    boolean canTalk( Member member);

    @Override
    default void formatTo(Formatter formatter, int flags, int width, int precision)
    {
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean alt = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        String out;

        if (alt)
            out = "#" + (upper ?  getName().toUpperCase(formatter.locale()) : getName());
        else
            out = getAsMention();

        MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
    }
}
