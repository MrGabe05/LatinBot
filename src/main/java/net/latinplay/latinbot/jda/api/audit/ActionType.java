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

package net.latinplay.latinbot.jda.api.audit;

import net.latinplay.latinbot.jda.api.entities.*;

/**
 * ActionTypes for {@link AuditLogEntry AuditLogEntry} instances
 * <br>Found via {@link AuditLogEntry#getType() AuditLogEntry.getType()}
 */
public enum ActionType
{
    /**
     * An Administrator updated {@link Guild Guild} information.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#GUILD_AFK_CHANNEL GUILD_AFK_CHANNEL}</li>
     *     <li>{@link AuditLogKey#GUILD_AFK_TIMEOUT GUILD_AFK_TIMEOUT}</li>
     *     <li>{@link AuditLogKey#GUILD_EXPLICIT_CONTENT_FILTER GUILD_EXPLICIT_CONTENT_FILTER}</li>
     *     <li>{@link AuditLogKey#GUILD_ICON GUILD_ICON}</li>
     *     <li>{@link AuditLogKey#GUILD_MFA_LEVEL GUILD_MFA_LEVEL}</li>
     *     <li>{@link AuditLogKey#GUILD_NAME GUILD_NAME}</li>
     *     <li>{@link AuditLogKey#GUILD_NOTIFICATION_LEVEL GUILD_NOTIFICATION_LEVEL}</li>
     *     <li>{@link AuditLogKey#GUILD_OWNER GUILD_OWNER}</li>
     *     <li>{@link AuditLogKey#GUILD_REGION GUILD_REGION}</li>
     *     <li>{@link AuditLogKey#GUILD_SPLASH GUILD_SPLASH}</li>
     *     <li>{@link AuditLogKey#GUILD_SYSTEM_CHANNEL GUILD_SYSTEM_CHANNEL}</li>
     * </ul>
     */
    GUILD_UPDATE(1, TargetType.GUILD),


    /**
     * An Administrator created a {@link GuildChannel GuildChannel}
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#CHANNEL_BITRATE CHANNEL_BITRATE} (VoiceChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_USER_LIMIT CHANNEL_USER_LIMIT} (VoiceChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_TOPIC CHANNEL_TOPIC} (TextChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_SLOWMODE CHANNEL_SLOWMODE} (TextChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_NSFW CHANNEL_NSFW} (TextChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_OVERRIDES CHANNEL_OVERRIDES}</li>
     *     <li>{@link AuditLogKey#CHANNEL_NAME CHANNEL_NAME}</li>
     *     <li>{@link AuditLogKey#CHANNEL_TYPE CHANNEL_TYPE}</li>
     *     <li>{@link AuditLogKey#ID ID}</li>
     * </ul>
     */
    CHANNEL_CREATE(10, TargetType.CHANNEL),

    /**
     * An Administrator updated {@link GuildChannel GuildChannel} information.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#CHANNEL_BITRATE CHANNEL_BITRATE} (VoiceChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_USER_LIMIT CHANNEL_USER_LIMIT} (VoiceChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_TOPIC CHANNEL_TOPIC} (TextChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_SLOWMODE CHANNEL_SLOWMODE} (TextChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_NSFW CHANNEL_NSFW} (TextChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_NAME CHANNEL_NAME}</li>
     *     <li>{@link AuditLogKey#CHANNEL_TYPE CHANNEL_TYPE}</li>
     * </ul>
     */
    CHANNEL_UPDATE(11, TargetType.CHANNEL),

    /**
     * An Administrator deleted a {@link GuildChannel GuildChannel}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#CHANNEL_BITRATE CHANNEL_BITRATE} (VoiceChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_USER_LIMIT CHANNEL_USER_LIMIT} (VoiceChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_TOPIC CHANNEL_TOPIC} (TextChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_SLOWMODE CHANNEL_SLOWMODE} (TextChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_NSFW CHANNEL_NSFW} (TextChannel only)</li>
     *     <li>{@link AuditLogKey#CHANNEL_OVERRIDES CHANNEL_OVERRIDES}</li>
     *     <li>{@link AuditLogKey#CHANNEL_NAME CHANNEL_NAME}</li>
     *     <li>{@link AuditLogKey#CHANNEL_TYPE CHANNEL_TYPE}</li>
     *     <li>{@link AuditLogKey#ID ID}</li>
     * </ul>
     */
    CHANNEL_DELETE(12, TargetType.CHANNEL),

    /**
     * An Administrator created a {@link PermissionOverride PermissionOverride}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#OVERRIDE_ALLOW OVERRIDE_ALLOW}</li>
     *     <li>{@link AuditLogKey#OVERRIDE_DENY OVERRIDE_DENY}</li>
     *     <li>{@link AuditLogKey#OVERRIDE_TYPE OVERRIDE_TYPE}</li>
     *     <li>{@link AuditLogKey#ID ID}</li>
     * </ul>
     *
     * <h2>Possible Options</h2>
     * <ul>
     *     <li>{@link AuditLogOption#ROLE ROLE}</li>
     *     <li>{@link AuditLogOption#USER USER}</li>
     * </ul>
     */
    CHANNEL_OVERRIDE_CREATE(13, TargetType.CHANNEL),

    /**
     * An Administrator updated {@link PermissionOverride PermissionOverride} information.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#OVERRIDE_ALLOW OVERRIDE_ALLOW}</li>
     *     <li>{@link AuditLogKey#OVERRIDE_DENY OVERRIDE_DENY}</li>
     * </ul>
     */
    CHANNEL_OVERRIDE_UPDATE(14, TargetType.CHANNEL),

    /**
     * An Administrator deleted a {@link PermissionOverride PermissionOverride}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#OVERRIDE_ALLOW OVERRIDE_ALLOW}</li>
     *     <li>{@link AuditLogKey#OVERRIDE_DENY OVERRIDE_DENY}</li>
     *     <li>{@link AuditLogKey#OVERRIDE_TYPE OVERRIDE_TYPE}</li>
     *     <li>{@link AuditLogKey#ID ID}</li>
     * </ul>
     *
     * <h2>Possible Options</h2>
     * <ul>
     *     <li>{@link AuditLogOption#ROLE ROLE}</li>
     *     <li>{@link AuditLogOption#USER USER}</li>
     * </ul>
     */
    CHANNEL_OVERRIDE_DELETE(15, TargetType.CHANNEL),


    /**
     * An Administrator has kicked a member.
     */
    KICK( 20, TargetType.MEMBER),

    /**
     * An Administrator has pruned members for inactivity.
     *
     * <h2>Possible Options</h2>
     * <ul>
     *     <li>{@link AuditLogOption#DELETE_MEMBER_DAYS DELETE_MEMBER_DAYS}</li>
     *     <li>{@link AuditLogOption#MEMBERS_REMOVED MEMBERS_REMOVED}</li>
     * </ul>
     */
    PRUNE(21, TargetType.MEMBER),

    /**
     * An Administrator has banned a user.
     */
    BAN(  22, TargetType.MEMBER),

    /**
     * An Administrator has unbanned a user.
     */
    UNBAN(23, TargetType.MEMBER),


    /**
     * A {@link Member Member} was either updated by an administrator or
     * the member updated itself.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#MEMBER_NICK MEMBER_NICK}</li>
     * </ul>
     */
    MEMBER_UPDATE(     24, TargetType.MEMBER),

    /**
     * An Administrator updated the roles of a member.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#MEMBER_ROLES_ADD MEMBER_ROLES_ADD}</li>
     *     <li>{@link AuditLogKey#MEMBER_ROLES_REMOVE MEMBER_ROLES_REMOVE}</li>
     * </ul>
     */
    MEMBER_ROLE_UPDATE(25, TargetType.MEMBER),

    /**
     * One or more members were moved from one voice channel to another by an Administrator
     *
     * <h2>Possible Options</h2>
     * <ul>
     *     <li>{@link AuditLogOption#COUNT} The amount of users moved</li>
     *     <li>{@link AuditLogOption#CHANNEL} The target channel</li>
     * </ul>
     */
    MEMBER_VOICE_MOVE(26, TargetType.MEMBER),

    /**
     * One or more members were disconnected from a voice channel by an Administrator
     *
     * <h2>Possible Options</h2>
     * <ul>
     *     <li>{@link AuditLogOption#COUNT} The amount of users who were disconnected</li>
     * </ul>
     */
    MEMBER_VOICE_KICK(27, TargetType.MEMBER),

    /**
     * An Administrator has added a bot to the server.
     */
    BOT_ADD(28, TargetType.MEMBER),


    /**
     * An Administrator has created a {@link Role Role}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#ROLE_COLOR ROLE_COLOR}</li>
     *     <li>{@link AuditLogKey#ROLE_HOISTED ROLE_HOISTED}</li>
     *     <li>{@link AuditLogKey#ROLE_MENTIONABLE ROLE_MENTIONABLE}</li>
     *     <li>{@link AuditLogKey#ROLE_NAME ROLE_NAME}</li>
     *     <li>{@link AuditLogKey#ROLE_PERMISSIONS ROLE_PERMISSIONS}</li>
     *     <li>{@link AuditLogKey#ID ID}</li>
     * </ul>
     */
    ROLE_CREATE(30, TargetType.ROLE),

    /**
     * An Administrator has updated a {@link Role Role}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#ROLE_COLOR ROLE_COLOR}</li>
     *     <li>{@link AuditLogKey#ROLE_HOISTED ROLE_HOISTED}</li>
     *     <li>{@link AuditLogKey#ROLE_MENTIONABLE ROLE_MENTIONABLE}</li>
     *     <li>{@link AuditLogKey#ROLE_NAME ROLE_NAME}</li>
     *     <li>{@link AuditLogKey#ROLE_PERMISSIONS ROLE_PERMISSIONS}</li>
     * </ul>
     */
    ROLE_UPDATE(31, TargetType.ROLE),

    /**
     * An Administrator has deleted a {@link Role Role}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#ROLE_COLOR ROLE_COLOR}</li>
     *     <li>{@link AuditLogKey#ROLE_HOISTED ROLE_HOISTED}</li>
     *     <li>{@link AuditLogKey#ROLE_MENTIONABLE ROLE_MENTIONABLE}</li>
     *     <li>{@link AuditLogKey#ROLE_NAME ROLE_NAME}</li>
     *     <li>{@link AuditLogKey#ROLE_PERMISSIONS ROLE_PERMISSIONS}</li>
     *     <li>{@link AuditLogKey#ID ID}</li>
     * </ul>
     */
    ROLE_DELETE(32, TargetType.ROLE),


    /**
     * Someone has created an {@link Invite Invite}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#INVITE_CHANNEL INVITE_CHANNEL}</li>
     *     <li>{@link AuditLogKey#INVITE_CODE INVITE_CODE}</li>
     *     <li>{@link AuditLogKey#INVITE_INVITER INVITE_INVITER}</li>
     *     <li>{@link AuditLogKey#INVITE_MAX_AGE INVITE_MAX_AGE}</li>
     *     <li>{@link AuditLogKey#INVITE_MAX_USES INVITE_MAX_USES}</li>
     *     <li>{@link AuditLogKey#INVITE_USES INVITE_USES}</li>
     * </ul>
     */
    INVITE_CREATE(40, TargetType.INVITE),

    /**
     * An {@link Invite Invite} has been updated.
     */
    INVITE_UPDATE(41, TargetType.INVITE),

    /**
     * An Administrator has deleted an {@link Invite Invite}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#INVITE_CHANNEL INVITE_CHANNEL}</li>
     *     <li>{@link AuditLogKey#INVITE_CODE INVITE_CODE}</li>
     *     <li>{@link AuditLogKey#INVITE_INVITER INVITE_INVITER}</li>
     *     <li>{@link AuditLogKey#INVITE_MAX_AGE INVITE_MAX_AGE}</li>
     *     <li>{@link AuditLogKey#INVITE_MAX_USES INVITE_MAX_USES}</li>
     *     <li>{@link AuditLogKey#INVITE_USES INVITE_USES}</li>
     * </ul>
     */
    INVITE_DELETE(42, TargetType.INVITE),


    /**
     * An Administrator has created a {@link Webhook Webhook}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#WEBHOOK_CHANNEL WEBHOOK_CHANNEL}</li>
     *     <li>{@link AuditLogKey#WEBHOOK_ICON WEBHOOK_ICON}</li>
     *     <li>{@link AuditLogKey#WEBHOOK_NAME WEBHOOK_NAME}</li>
     *     <li>{@link AuditLogKey#ID ID}</li>
     * </ul>
     */
    WEBHOOK_CREATE(50, TargetType.WEBHOOK),

    /**
     * An Administrator has updated a {@link Webhook Webhook}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#WEBHOOK_CHANNEL WEBHOOK_CHANNEL}</li>
     *     <li>{@link AuditLogKey#WEBHOOK_ICON WEBHOOK_ICON}</li>
     *     <li>{@link AuditLogKey#WEBHOOK_NAME WEBHOOK_NAME}</li>
     *     <li>{@link AuditLogKey#ID ID}</li>
     * </ul>
     */
    WEBHOOK_UPDATE(51, TargetType.WEBHOOK),

    /**
     * An Administrator has deleted a {@link Webhook Webhook}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#WEBHOOK_CHANNEL WEBHOOK_CHANNEL}</li>
     *     <li>{@link AuditLogKey#WEBHOOK_ICON WEBHOOK_ICON}</li>
     *     <li>{@link AuditLogKey#WEBHOOK_NAME WEBHOOK_NAME}</li>
     *     <li>{@link AuditLogKey#ID ID}</li>
     * </ul>
     */
    WEBHOOK_REMOVE(52, TargetType.WEBHOOK),


    /**
     * An Administrator created an {@link Emote Emote}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#EMOTE_NAME EMOTE_NAME}</li>
     * </ul>
     */
    EMOTE_CREATE(60, TargetType.EMOTE),

    /**
     * An Administrator updated an {@link Emote Emote}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#EMOTE_NAME EMOTE_NAME}</li>
     *     <li>{@link AuditLogKey#EMOTE_ROLES_ADD EMOTE_ROLES_ADD}</li>
     *     <li>{@link AuditLogKey#EMOTE_ROLES_REMOVE EMOTE_ROLES_REMOVE}</li>
     * </ul>
     */
    EMOTE_UPDATE(61, TargetType.EMOTE),

    /**
     * An Administrator deleted an {@link Emote Emote}.
     *
     * <h2>Possible Keys</h2>
     * <ul>
     *     <li>{@link AuditLogKey#EMOTE_NAME EMOTE_NAME}</li>
     * </ul>
     */
    EMOTE_DELETE(62, TargetType.EMOTE),


    /**
     * A message was created.
     */
    MESSAGE_CREATE(70, TargetType.UNKNOWN),

    /**
     * A message was updated.
     */
    MESSAGE_UPDATE(71, TargetType.UNKNOWN),

    /**
     * An Administrator has deleted one or more {@link Message Messages}.
     *
     * <h2>Possible Options</h2>
     * <ul>
     *     <li>{@link AuditLogOption#COUNT COUNT}</li>
     * </ul>
     */
    MESSAGE_DELETE(72, TargetType.MEMBER),

    /**
     * An Administrator has performed a bulk delete of messages in a channel
     *
     * <h2>Possible Options</h2>
     * <ul>
     *     <li>{@link AuditLogOption#COUNT}</li>
     * </ul>
     */
    MESSAGE_BULK_DELETE(73, TargetType.CHANNEL),

    /**
     * An Administrator has pinned a message in the channel
     *
     * <h2>Possible Options</h2>
     * <ul>
     *     <li>{@link AuditLogOption#CHANNEL}</li>
     *     <li>{@link AuditLogOption#MESSAGE}</li>
     * </ul>
     */
    MESSAGE_PIN(74, TargetType.CHANNEL),

    /**
     * An Administrator has unpinned a message in the channel
     *
     * <h2>Possible Options</h2>
     * <ul>
     *     <li>{@link AuditLogOption#CHANNEL}</li>
     *     <li>{@link AuditLogOption#MESSAGE}</li>
     * </ul>
     */
    MESSAGE_UNPIN(75, TargetType.CHANNEL),

    /**
     * An Administrator has added an integration to the guild
     */
    INTEGRATION_CREATE(80, TargetType.INTEGRATION),

    /**
     * An Administrator has updated an integration of the guild
     */
    INTEGRATION_UPDATE(81, TargetType.INTEGRATION),

    /**
     * An Administrator has removed an integration from the guild
     */
    INTEGRATION_DELETE(82, TargetType.INTEGRATION),

    UNKNOWN(-1, TargetType.UNKNOWN);

    private final int key;
    private final TargetType target;

    ActionType(int key, TargetType target)
    {
        this.key = key;
        this.target = target;
    }

    /**
     * The raw key used to identify types within the api.
     *
     * @return Raw key for this ActionType
     */
    public int getKey()
    {
        return key;
    }

    /**
     * The expected {@link TargetType TargetType}
     * for this ActionType
     *
     * @return {@link TargetType TargetType}
     */
    public TargetType getTargetType()
    {
        return target;
    }

    public static ActionType from(int key)
    {
        for (ActionType type : values())
        {
            if (type.key == key)
                return type;
        }
        return UNKNOWN;
    }
}
