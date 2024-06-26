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

import net.latinplay.latinbot.jda.api.AccountType;
import net.latinplay.latinbot.jda.api.Permission;
import net.latinplay.latinbot.jda.api.entities.*;
import net.latinplay.latinbot.jda.api.exceptions.InsufficientPermissionException;

import java.util.Collection;

/**
 * Manager providing functionality to update one or more fields for a {@link GuildChannel GuildChannel}.
 *
 * <p><b>Example</b>
 * <pre>{@code
 * manager.setName("github-log")
 *        .setTopic("logs for github commits")
 *        .setNSFW(false)
 *        .queue();
 * manager.reset(ChannelManager.TOPIC | ChannelManager.NAME)
 *        .setName("nsfw-commits")
 *        .setTopic(null)
 *        .setNSFW(true)
 *        .queue();
 * }</pre>
 *
 * @see GuildChannel#getManager()
 */
public interface ChannelManager extends Manager<ChannelManager>
{
    /** Used to reset the name field */
    long NAME       = 0x1;
    /** Used to reset the parent field */
    long PARENT     = 0x2;
    /** Used to reset the topic field */
    long TOPIC      = 0x4;
    /** Used to reset the position field */
    long POSITION   = 0x8;
    /** Used to reset the nsfw field */
    long NSFW       = 0x10;
    /** Used to reset the userlimit field */
    long USERLIMIT  = 0x20;
    /** Used to reset the bitrate field */
    long BITRATE    = 0x40;
    /** Used to reset the permission field */
    long PERMISSION = 0x80;
    /** Used to reset the rate-limit per user field */
    long SLOWMODE   = 0x100;

    /**
     * Resets the fields specified by the provided bit-flag pattern.
     * You can specify a combination by using a bitwise OR concat of the flag constants.
     * <br>Example: {@code manager.reset(ChannelManager.NAME | ChannelManager.PARENT);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #NAME}</li>
     *     <li>{@link #PARENT}</li>
     *     <li>{@link #TOPIC}</li>
     *     <li>{@link #POSITION}</li>
     *     <li>{@link #NSFW}</li>
     *     <li>{@link #SLOWMODE}</li>
     *     <li>{@link #USERLIMIT}</li>
     *     <li>{@link #BITRATE}</li>
     *     <li>{@link #PERMISSION}</li>
     * </ul>
     *
     * @param  fields
     *         Integer value containing the flags to reset.
     *
     * @return ChannelManager for chaining convenience
     */
    
    @Override
    ChannelManager reset(long fields);

    /**
     * Resets the fields specified by the provided bit-flag patterns.
     * <br>Example: {@code manager.reset(ChannelManager.NAME, ChannelManager.PARENT);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #NAME}</li>
     *     <li>{@link #PARENT}</li>
     *     <li>{@link #TOPIC}</li>
     *     <li>{@link #POSITION}</li>
     *     <li>{@link #NSFW}</li>
     *     <li>{@link #USERLIMIT}</li>
     *     <li>{@link #BITRATE}</li>
     *     <li>{@link #PERMISSION}</li>
     * </ul>
     *
     * @param  fields
     *         Integer values containing the flags to reset.
     *
     * @return ChannelManager for chaining convenience
     */
    
    @Override
    ChannelManager reset(long... fields);

    /**
     * The {@link GuildChannel GuildChannel} that will
     * be modified by this Manager instance
     *
     * @return The {@link GuildChannel GuildChannel}
     */
    
    GuildChannel getChannel();

    /**
     * The {@link ChannelType ChannelType}
     *
     * @return The ChannelType
     */
    
    default ChannelType getType()
    {
        return getChannel().getType();
    }

    /**
     * The {@link Guild Guild} this Manager's
     * {@link GuildChannel GuildChannel} is in.
     * <br>This is logically the same as calling {@code getChannel().getGuild()}
     *
     * @return The parent {@link Guild Guild}
     */
    
    default Guild getGuild()
    {
        return getChannel().getGuild();
    }

    /**
     * Clears the overrides added via {@link #putPermissionOverride(IPermissionHolder, Collection, Collection)}.
     *
     * @return ChannelManager for chaining convenience
     */
    
    
    ChannelManager clearOverridesAdded();

    /**
     * Clears the overrides removed via {@link #removePermissionOverride(IPermissionHolder)}.
     *
     * @return ChannelManager for chaining convenience
     */
    
    
    ChannelManager clearOverridesRemoved();

    /**
     * Adds an override for the specified {@link IPermissionHolder IPermissionHolder}
     * with the provided raw bitmasks as allowed and denied permissions. If the permission holder already
     * had an override on this channel it will be replaced instead.
     *
     * @param  permHolder
     *         The permission holder
     * @param  allow
     *         The bitmask to grant
     * @param  deny
     *         The bitmask to deny
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided permission holder is {@code null}
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_PERMISSIONS Permission.MANAGE_PERMISSIONS}
     *         in this channel
     *
     * @return ChannelManager for chaining convenience
     *
     * @see    #putPermissionOverride(IPermissionHolder, Collection, Collection)
     * @see    Permission#getRaw(Permission...) Permission.getRaw(Permission...)
     */
    
    
    ChannelManager putPermissionOverride( IPermissionHolder permHolder, long allow, long deny);

    /**
     * Adds an override for the specified {@link IPermissionHolder IPermissionHolder}
     * with the provided permission sets as allowed and denied permissions. If the permission holder already
     * had an override on this channel it will be replaced instead.
     * <br>Example: {@code putPermissionOverride(guild.getSelfMember(), EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ), null)}
     *
     * @param  permHolder
     *         The permission holder
     * @param  allow
     *         The permissions to grant, or null
     * @param  deny
     *         The permissions to deny, or null
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided permission holder is {@code null}
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_PERMISSIONS Permission.MANAGE_PERMISSIONS}
     *         in this channel
     *
     * @return ChannelManager for chaining convenience
     *
     * @see    #putPermissionOverride(IPermissionHolder, long, long)
     * @see    java.util.EnumSet EnumSet
     */
    
    
    default ChannelManager putPermissionOverride( IPermissionHolder permHolder,  Collection<Permission> allow,  Collection<Permission> deny)
    {
        long allowRaw = allow == null ? 0 : Permission.getRaw(allow);
        long denyRaw  = deny  == null ? 0 : Permission.getRaw(deny);
        return putPermissionOverride(permHolder, allowRaw, denyRaw);
    }

    /**
     * Removes the {@link PermissionOverride PermissionOverride} for the specified
     * {@link IPermissionHolder IPermissionHolder}. If no override existed for this member
     * this does nothing.
     *
     * @param  permHolder
     *         The permission holder
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided permission holder is {@code null}
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_PERMISSIONS Permission.MANAGE_PERMISSIONS}
     *         in this channel
     *
     * @return ChannelManager for chaining convenience
     */
    
    
    ChannelManager removePermissionOverride( IPermissionHolder permHolder);

    /**
     * Syncs all {@link PermissionOverride PermissionOverrides} of this GuildChannel with
     * its parent ({@link Category Category}).
     *
     * <p>After this operation, all {@link PermissionOverride PermissionOverrides}
     * will be exactly the same as the ones from the parent.
     * <br><b>That means that all current PermissionOverrides are lost!</b>
     *
     * <p>This behaves as if calling {@link #sync(GuildChannel)} with this GuildChannel's {@link GuildChannel#getParent() Parent}.
     *
     * @throws  java.lang.IllegalStateException
     *          If this GuildChannel has no parent
     * @throws InsufficientPermissionException
     *          If the currently logged in account does not have {@link Permission#MANAGE_PERMISSIONS Permission.MANAGE_PERMISSIONS}
     *          in this channel
     *
     * @return  ChannelManager for chaining convenience
     *
     * @see     <a href="https://discordapp.com/developers/docs/topics/permissions#permission-syncing" target="_blank">Discord Documentation - Permission Syncing</a>
     */
    
    
    default ChannelManager sync()
    {
        if (getChannel().getParent() == null)
            throw new IllegalStateException("sync() requires a parent category");
        return sync(getChannel().getParent());
    }

    /**
     * Syncs all {@link PermissionOverride PermissionOverrides} of this GuildChannel with
     * the given ({@link GuildChannel GuildChannel}).
     *
     * <p>After this operation, all {@link PermissionOverride PermissionOverrides}
     * will be exactly the same as the ones from the syncSource.
     * <br><b>That means that all current PermissionOverrides are lost!</b>
     *
     * <p>This will only work for Channels of the same {@link Guild Guild}!.
     *
     * @param   syncSource
     *          The GuildChannel from where all PermissionOverrides should be copied from
     *
     * @throws  java.lang.IllegalArgumentException
     *          If the given snySource is {@code null}, this GuildChannel or from a different Guild.
     * @throws InsufficientPermissionException
     *          If the currently logged in account does not have {@link Permission#MANAGE_PERMISSIONS Permission.MANAGE_PERMISSIONS}
     *          in this channel
     *
     * @return  ChannelManager for chaining convenience
     *
     * @see     <a href="https://discordapp.com/developers/docs/topics/permissions#permission-syncing" target="_blank">Discord Documentation - Permission Syncing</a>
     */
    
    
    ChannelManager sync( GuildChannel syncSource);

    /**
     * Sets the <b><u>name</u></b> of the selected {@link GuildChannel GuildChannel}.
     *
     * <p>A channel name <b>must not</b> be {@code null} nor empty or more than 100 characters long!
     * <br>TextChannel names may only be populated with alphanumeric (with underscore and dash).
     *
     * <p><b>Example</b>: {@code mod-only} or {@code generic_name}
     * <br>Characters will automatically be lowercased by Discord for text channels!
     *
     * @param  name
     *         The new name for the selected {@link GuildChannel GuildChannel}
     *
     * @throws IllegalArgumentException
     *         If the provided name is {@code null} or not between 1-100 characters long
     *
     * @return ChannelManager for chaining convenience
     */
    
    
    ChannelManager setName( String name);

    /**
     * Sets the <b><u>{@link Category Parent Category}</u></b>
     * of the selected {@link GuildChannel GuildChannel}.
     *
     *
     * @param  category
     *         The new parent for the selected {@link GuildChannel GuildChannel}
     *
     * @throws IllegalStateException
     *         If the target is a category itself
     * @throws IllegalArgumentException
     *         If the provided category is not from the same Guild
     *
     * @return ChannelManager for chaining convenience
     *
     * @since  3.4.0
     */
    
    
    ChannelManager setParent( Category category);

    /**
     * Sets the <b><u>position</u></b> of the selected {@link GuildChannel GuildChannel}.
     *
     * <p><b>To modify multiple channels you should use
     * <code>Guild.{@link Guild#modifyTextChannelPositions() modifyTextChannelPositions()}</code>
     * instead! This is not the same as looping through channels and using this to update positions!</b>
     *
     * @param  position
     *         The new position for the selected {@link GuildChannel GuildChannel}
     *
     * @return ChannelManager for chaining convenience
     */
    
    
    ChannelManager setPosition(int position);

    /**
     * Sets the <b><u>topic</u></b> of the selected {@link TextChannel TextChannel}.
     *
     * <p>A channel topic <b>must not</b> be more than {@code 1024} characters long!
     * <br><b>This is only available to {@link TextChannel TextChannels}</b>
     *
     * @param  topic
     *         The new topic for the selected {@link TextChannel TextChannel},
     *         {@code null} or empty String to reset
     *
     * @throws UnsupportedOperationException
     *         If the selected {@link GuildChannel GuildChannel}'s type is not {@link ChannelType#TEXT TEXT}
     * @throws IllegalArgumentException
     *         If the provided topic is greater than {@code 1024} in length
     *
     * @return ChannelManager for chaining convenience
     */
    
    
    ChannelManager setTopic( String topic);

    /**
     * Sets the <b><u>nsfw flag</u></b> of the selected {@link TextChannel TextChannel}.
     *
     * @param  nsfw
     *         The new nsfw flag for the selected {@link TextChannel TextChannel},
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have the Permission {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL}
     * @throws IllegalStateException
     *         If the selected {@link GuildChannel GuildChannel}'s type is not {@link ChannelType#TEXT TEXT}
     *
     * @return ChannelManager for chaining convenience
     */
    
    
    ChannelManager setNSFW(boolean nsfw);

    /**
     * Sets the <b><u>slowmode</u></b> of the selected {@link TextChannel TextChannel}.
     * <br>Provide {@code 0} to reset the slowmode of the {@link TextChannel TextChannel}
     *
     * <p>A channel slowmode <b>must not</b> be negative nor greater than {@link TextChannel#MAX_SLOWMODE TextChannel.MAX_SLOWMODE}!
     * <br><b>This is only available to {@link TextChannel TextChannels}</b>
     *
     * <p>Note that only {@link AccountType#CLIENT CLIENT} type accounts are
     * affected by slowmode, and that {@link AccountType#BOT BOT} accounts
     * are immune to the restrictions.
     * <br>Having {@link Permission#MESSAGE_MANAGE MESSAGE_MANAGE} or
     * {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL} permission also
     * grants immunity to slowmode.
     *
     * @param  slowmode
     *         The new slowmode for the selected {@link TextChannel TextChannel}
     *
     * @throws IllegalStateException
     *         If the selected {@link GuildChannel GuildChannel}'s type is not {@link ChannelType#TEXT TEXT}
     * @throws IllegalArgumentException
     *         If the provided slowmode is negative or greater than {@link TextChannel#MAX_SLOWMODE TextChannel.MAX_SLOWMODE}
     *
     * @return ChannelManager for chaining convenience
     */
    
    
    ChannelManager setSlowmode(int slowmode);

    /**
     * Sets the <b><u>user-limit</u></b> of the selected {@link VoiceChannel VoiceChannel}.
     * <br>Provide {@code 0} to reset the user-limit of the {@link VoiceChannel VoiceChannel}
     *
     * <p>A channel user-limit <b>must not</b> be negative nor greater than {@code 99}!
     * <br><b>This is only available to {@link VoiceChannel VoiceChannels}</b>
     *
     * @param  userLimit
     *         The new user-limit for the selected {@link VoiceChannel VoiceChannel}
     *
     * @throws IllegalStateException
     *         If the selected {@link GuildChannel GuildChannel}'s type is not {@link ChannelType#VOICE VOICE}
     * @throws IllegalArgumentException
     *         If the provided user-limit is negative or greater than {@code 99}
     *
     * @return ChannelManager for chaining convenience
     */
    
    
    ChannelManager setUserLimit(int userLimit);

    /**
     * Sets the <b><u>bitrate</u></b> of the selected {@link VoiceChannel VoiceChannel}.
     * <br>The default value is {@code 64000}
     *
     * <p>A channel bitrate <b>must not</b> be less than {@code 8000} nor greater than {@link Guild#getMaxBitrate()}!
     * <br><b>This is only available to {@link VoiceChannel VoiceChannels}</b>
     *
     * @param  bitrate
     *         The new bitrate for the selected {@link VoiceChannel VoiceChannel}
     *
     * @throws IllegalStateException
     *         If the selected {@link GuildChannel GuildChannel}'s type is not {@link ChannelType#VOICE VOICE}
     * @throws IllegalArgumentException
     *         If the provided bitrate is less than 8000 or greater than {@link Guild#getMaxBitrate()}.
     *
     * @return ChannelManager for chaining convenience
     *
     * @see    Guild#getFeatures()
     */
    
    
    ChannelManager setBitrate(int bitrate);
}
