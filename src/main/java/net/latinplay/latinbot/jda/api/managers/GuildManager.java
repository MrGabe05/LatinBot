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

import net.latinplay.latinbot.jda.api.Region;
import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.entities.Icon;
import net.latinplay.latinbot.jda.api.entities.TextChannel;
import net.latinplay.latinbot.jda.api.entities.VoiceChannel;


/**
 * Manager providing functionality to update one or more fields for a {@link Guild Guild}.
 *
 * <p><b>Example</b>
 * <pre>{@code
 * manager.setName("Official JDA Guild")
 *        .setIcon(null)
 *        .queue();
 * manager.reset(GuildManager.NAME | GuildManager.ICON)
 *        .setName("Minn's Meme Den")
 *        .setExplicitContentLevel(Guild.ExplicitContentLevel.HIGH)
 *        .queue();
 * }</pre>
 *
 * @see Guild#getManager()
 */
public interface GuildManager extends Manager<GuildManager>
{
    /** Used to reset the name field */
    long NAME   = 0x1;
    /** Used to reset the region field */
    long REGION = 0x2;
    /** Used to reset the icon field */
    long ICON   = 0x4;
    /** Used to reset the splash field */
    long SPLASH = 0x8;
    /** Used to reset the afk channel field */
    long AFK_CHANNEL    = 0x10;
    /** Used to reset the afk timeout field */
    long AFK_TIMEOUT    = 0x20;
    /** Used to reset the system channel field */
    long SYSTEM_CHANNEL = 0x40;
    /** Used to reset the mfa level field */
    long MFA_LEVEL      = 0x80;
    /** Used to reset the default notification level field */
    long NOTIFICATION_LEVEL     = 0x100;
    /** Used to reset the explicit content level field */
    long EXPLICIT_CONTENT_LEVEL = 0x200;
    /** Used to reset the verification level field */
    long VERIFICATION_LEVEL     = 0x400;
    /** Used to reset the banner field */
    long BANNER                 = 0x800;
    /** Used to reset the vanity code field */
    long VANITY_URL   = 0x1000;
    /** Used to reset the description field */
    long DESCRIPTION  = 0x2000;

    /**
     * Resets the fields specified by the provided bit-flag pattern.
     * You can specify a combination by using a bitwise OR concat of the flag constants.
     * <br>Example: {@code manager.reset(GuildManager.NAME | GuildManager.ICON);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #NAME}</li>
     *     <li>{@link #ICON}</li>
     *     <li>{@link #REGION}</li>
     *     <li>{@link #SPLASH}</li>
     *     <li>{@link #AFK_CHANNEL}</li>
     *     <li>{@link #AFK_TIMEOUT}</li>
     *     <li>{@link #SYSTEM_CHANNEL}</li>
     *     <li>{@link #MFA_LEVEL}</li>
     *     <li>{@link #NOTIFICATION_LEVEL}</li>
     *     <li>{@link #EXPLICIT_CONTENT_LEVEL}</li>
     *     <li>{@link #VERIFICATION_LEVEL}</li>
     * </ul>
     *
     * @param  fields
     *         Integer value containing the flags to reset.
     *
     * @return GuildManager for chaining convenience
     */
    
    @Override
    GuildManager reset(long fields);

    /**
     * Resets the fields specified by the provided bit-flag patterns.
     * You can specify a combination by using a bitwise OR concat of the flag constants.
     * <br>Example: {@code manager.reset(GuildManager.NAME, GuildManager.ICON);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #NAME}</li>
     *     <li>{@link #ICON}</li>
     *     <li>{@link #REGION}</li>
     *     <li>{@link #SPLASH}</li>
     *     <li>{@link #AFK_CHANNEL}</li>
     *     <li>{@link #AFK_TIMEOUT}</li>
     *     <li>{@link #SYSTEM_CHANNEL}</li>
     *     <li>{@link #MFA_LEVEL}</li>
     *     <li>{@link #NOTIFICATION_LEVEL}</li>
     *     <li>{@link #EXPLICIT_CONTENT_LEVEL}</li>
     *     <li>{@link #VERIFICATION_LEVEL}</li>
     * </ul>
     *
     * @param  fields
     *         Integer values containing the flags to reset.
     *
     * @return GuildManager for chaining convenience
     */
    
    @Override
    GuildManager reset(long... fields);

    /**
     * The {@link Guild Guild} object of this Manager.
     * Useful if this Manager was returned via a create function
     *
     * @return The {@link Guild Guild} of this Manager
     */
    
    Guild getGuild();

    /**
     * Sets the name of this {@link Guild Guild}.
     *
     * @param  name
     *         The new name for this {@link Guild Guild}
     *
     * @throws IllegalArgumentException
     *         If the provided name is {@code null} or not between 2-100 characters long
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setName( String name);

    /**
     * Sets the {@link Region Region} of this {@link Guild Guild}.
     *
     * @param  region
     *         The new region for this {@link Guild Guild}
     *
     * @throws IllegalArgumentException
     *         If the provided region is a {@link Region#isVip() VIP Region} but the guild does not support VIP regions.
     *         Use {@link Guild#getFeatures() Guild#getFeatures()} to check if VIP regions are supported.
     *
     * @return GuildManager for chaining convenience
     *
     * @see    Region#isVip()
     * @see    Guild#getFeatures()
     */
    
    
    GuildManager setRegion( Region region);

    /**
     * Sets the {@link Icon Icon} of this {@link Guild Guild}.
     *
     * @param  icon
     *         The new icon for this {@link Guild Guild}
     *         or {@code null} to reset
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setIcon(Icon icon);

    /**
     * Sets the Splash {@link Icon Icon} of this {@link Guild Guild}.
     *
     * @param  splash
     *         The new splash for this {@link Guild Guild}
     *         or {@code null} to reset
     *
     * @throws java.lang.IllegalStateException
     *         If the guild's {@link Guild#getFeatures() features} does not include {@code INVITE_SPLASH}
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setSplash(Icon splash);

    /**
     * Sets the AFK {@link VoiceChannel VoiceChannel} of this {@link Guild Guild}.
     *
     * @param  afkChannel
     *         The new afk channel for this {@link Guild Guild}
     *         or {@code null} to reset
     *
     * @throws IllegalArgumentException
     *         If the provided channel is not from this guild
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setAfkChannel(VoiceChannel afkChannel);

    /**
     * Sets the system {@link TextChannel TextChannel} of this {@link Guild Guild}.
     *
     * @param  systemChannel
     *         The new system channel for this {@link Guild Guild}
     *         or {@code null} to reset
     *
     * @throws IllegalArgumentException
     *         If the provided channel is not from this guild
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setSystemChannel(TextChannel systemChannel);

    /**
     * Sets the afk {@link Guild.Timeout Timeout} of this {@link Guild Guild}.
     *
     * @param  timeout
     *         The new afk timeout for this {@link Guild Guild}
     *
     * @throws IllegalArgumentException
     *         If the provided timeout is {@code null}
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setAfkTimeout( Guild.Timeout timeout);

    /**
     * Sets the {@link Guild.VerificationLevel Verification Level} of this {@link Guild Guild}.
     *
     * @param  level
     *         The new Verification Level for this {@link Guild Guild}
     *
     * @throws IllegalArgumentException
     *         If the provided level is {@code null} or UNKNOWN
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setVerificationLevel( Guild.VerificationLevel level);

    /**
     * Sets the {@link Guild.NotificationLevel Notification Level} of this {@link Guild Guild}.
     *
     * @param  level
     *         The new Notification Level for this {@link Guild Guild}
     *
     * @throws IllegalArgumentException
     *         If the provided level is {@code null} or UNKNOWN
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setDefaultNotificationLevel( Guild.NotificationLevel level);

    /**
     * Sets the {@link Guild.MFALevel MFA Level} of this {@link Guild Guild}.
     *
     * @param  level
     *         The new MFA Level for this {@link Guild Guild}
     *
     * @throws IllegalArgumentException
     *         If the provided level is {@code null} or UNKNOWN
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setRequiredMFALevel( Guild.MFALevel level);

    /**
     * Sets the {@link Guild.ExplicitContentLevel Explicit Content Level} of this {@link Guild Guild}.
     *
     * @param  level
     *         The new MFA Level for this {@link Guild Guild}
     *
     * @throws IllegalArgumentException
     *         If the provided level is {@code null} or UNKNOWN
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setExplicitContentLevel( Guild.ExplicitContentLevel level);

    /**
     * Sets the Banner {@link Icon Icon} of this {@link Guild Guild}.
     *
     * @param  banner
     *         The new banner for this {@link Guild Guild}
     *         or {@code null} to reset
     *
     * @throws java.lang.IllegalStateException
     *         If the guild's {@link Guild#getFeatures() features} does not include {@code BANNER}
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setBanner(Icon banner);

    /**
     * Sets the Vanity Code {@link Icon Icon} of this {@link Guild Guild}.
     *
     * @param  code
     *         The new vanity code for this {@link Guild Guild}
     *         or {@code null} to reset
     *
     * @throws java.lang.IllegalStateException
     *         If the guild's {@link Guild#getFeatures() features} does not include {@code VANITY_URL}
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setVanityCode(String code);

    /**
     * Sets the Description {@link Icon Icon} of this {@link Guild Guild}.
     *
     * @param  description
     *         The new description for this {@link Guild Guild}
     *         or {@code null} to reset
     *
     * @throws java.lang.IllegalStateException
     *         If the guild's {@link Guild#getFeatures() features} does not include {@code VERIFIED}
     *
     * @return GuildManager for chaining convenience
     */
    
    
    GuildManager setDescription(String description);
}
