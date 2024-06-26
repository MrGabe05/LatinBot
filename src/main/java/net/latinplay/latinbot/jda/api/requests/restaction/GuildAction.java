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

package net.latinplay.latinbot.jda.api.requests.restaction;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.Permission;
import net.latinplay.latinbot.jda.api.Region;
import net.latinplay.latinbot.jda.api.entities.*;
import net.latinplay.latinbot.jda.api.requests.RestAction;
import net.latinplay.latinbot.jda.api.utils.data.DataObject;
import net.latinplay.latinbot.jda.api.utils.data.SerializableData;
import net.latinplay.latinbot.jda.internal.requests.restaction.GuildActionImpl;
import net.latinplay.latinbot.jda.internal.requests.restaction.PermOverrideData;
import net.latinplay.latinbot.jda.internal.utils.Checks;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * {@link RestAction RestAction} extension
 * specifically designed to allow for the creation of {@link Guild Guilds}.
 * <br>This is available to all account types but may undergo certain restrictions by Discord.
 *
 * @since  3.4.0
 *
 * @see    JDA#createGuild(String)
 */
public interface GuildAction extends RestAction<Void>
{
    
    @Override
    GuildAction setCheck( BooleanSupplier checks);

    /**
     * Sets the voice {@link Region Region} of
     * the resulting {@link Guild Guild}.
     *
     * @param  region
     *         The {@link Region Region} to use
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided region is a VIP region as per {@link Region#isVip() Region.isVip()}
     *
     * @return The current GuildAction for chaining convenience
     */
    
    
    GuildAction setRegion( Region region);

    /**
     * Sets the {@link Icon Icon}
     * for the resulting {@link Guild Guild}
     *
     * @param  icon
     *         The {@link Icon Icon} to use
     *
     * @return The current GuildAction for chaining convenience
     */
    
    
    GuildAction setIcon( Icon icon);

    /**
     * Sets the name for the resulting {@link Guild Guild}
     *
     * @param  name
     *         The name to use
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is {@code null}, blank or not between 2-100 characters long
     *
     * @return The current GuildAction for chaining convenience
     */
    
    
    GuildAction setName( String name);

    /**
     * Sets the {@link Guild.VerificationLevel VerificationLevel}
     * for the resulting {@link Guild Guild}
     *
     * @param  level
     *         The {@link Guild.VerificationLevel VerificationLevel} to use
     *
     * @return The current GuildAction for chaining convenience
     */
    
    
    GuildAction setVerificationLevel( Guild.VerificationLevel level);

    /**
     * Sets the {@link Guild.NotificationLevel NotificationLevel}
     * for the resulting {@link Guild Guild}
     *
     * @param  level
     *         The {@link Guild.NotificationLevel NotificationLevel} to use
     *
     * @return The current GuildAction for chaining convenience
     */
    
    
    GuildAction setNotificationLevel( Guild.NotificationLevel level);

    /**
     * Sets the {@link Guild.ExplicitContentLevel ExplicitContentLevel}
     * for the resulting {@link Guild Guild}
     *
     * @param  level
     *         The {@link Guild.ExplicitContentLevel ExplicitContentLevel} to use
     *
     * @return The current GuildAction for chaining convenience
     */
    
    
    GuildAction setExplicitContentLevel( Guild.ExplicitContentLevel level);

    /**
     * Adds a {@link GuildChannel GuildChannel} to the resulting
     * Guild. This cannot be of type {@link ChannelType#CATEGORY CATEGORY}!
     *
     * @param  channel
     *         The {@link ChannelData ChannelData}
     *         to use for the construction of the GuildChannel
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided channel is {@code null}!
     *
     * @return The current GuildAction for chaining convenience
     */
    
    
    GuildAction addChannel( ChannelData channel);

    /**
     * Gets the {@link ChannelData ChannelData}
     * of the specified index. The index is 0 based on insertion order of {@link #addChannel(ChannelData)}!
     *
     * @param  index
     *         The 0 based index of the channel
     *
     * @throws java.lang.IndexOutOfBoundsException
     *         If the provided index is not in bounds
     *
     * @return The current GuildAction for chaining convenience
     */
    
    
    ChannelData getChannel(int index);

    /**
     * Removes the {@link ChannelData ChannelData}
     * at the specified index and returns the removed object.
     *
     * @param  index
     *         The index of the channel
     *
     * @throws java.lang.IndexOutOfBoundsException
     *         If the index is out of bounds
     *
     * @return The removed object
     */
    
    
    ChannelData removeChannel(int index);

    /**
     * Removes the provided {@link ChannelData ChannelData}
     * from this GuildAction if present.
     *
     * @param  data
     *         The ChannelData to remove
     *
     * @return The current GuildAction for chaining convenience
     */
    
    
    GuildAction removeChannel( ChannelData data);

    /**
     * Creates a new {@link ChannelData ChannelData}
     * instance and adds it to this GuildAction.
     *
     * @param  type
     *         The {@link ChannelType ChannelType} of the resulting GuildChannel
     *         <br>This may be of type {@link ChannelType#TEXT TEXT} or {@link ChannelType#VOICE VOICE}!
     * @param  name
     *         The name of the channel. This must be alphanumeric with underscores for type TEXT
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If provided with an invalid ChannelType</li>
     *             <li>If the provided name is {@code null} or blank</li>
     *             <li>If the provided name is not between 2-100 characters long</li>
     *             <li>If the type is TEXT and the provided name is not alphanumeric with underscores</li>
     *         </ul>
     *
     * @return The new ChannelData instance
     */
    
    
    ChannelData newChannel( ChannelType type,  String name);

    /**
     * Retrieves the {@link RoleData RoleData} for the
     * public role ({@link Guild#getPublicRole() Guild.getPublicRole()}) for the resulting Guild.
     * <br>The public role is also known in the official client as the {@code @everyone} role.
     *
     * <p><b>You can only change the permissions of the public role!</b>
     *
     * @return RoleData of the public role
     */
    
    
    RoleData getPublicRole();

    /**
     * Retrieves the {@link RoleData RoleData} for the
     * provided index.
     * <br>The public role is at the index 0 and all others are ordered by insertion order!
     *
     * @param  index
     *         The index of the role
     *
     * @throws java.lang.IndexOutOfBoundsException
     *         If the provided index is out of bounds
     *
     * @return RoleData of the provided index
     */
    
    
    RoleData getRole(int index);

    /**
     * Creates and add a new {@link RoleData RoleData} object
     * representing a Role for the resulting Guild.
     *
     * <p>This can be used in {@link GuildAction.ChannelData#addPermissionOverride(GuildAction.RoleData, long, long) ChannelData.addPermissionOverride(...)}.
     * <br>You may change any properties of this {@link RoleData RoleData} instance!
     *
     * @return RoleData for the new Role
     */
    
    
    RoleData newRole();

    /**
     * Mutable object containing information on a {@link Role Role}
     * of the resulting {@link Guild Guild} that is constructed by a GuildAction instance
     *
     * <p>This may be used in {@link GuildAction.ChannelData#addPermissionOverride(GuildAction.RoleData, long, long)}  ChannelData.addPermissionOverride(...)}!
     */
    class RoleData implements SerializableData
    {
        protected final long id;
        protected final boolean isPublicRole;

        protected Long permissions;
        protected String name;
        protected Integer color;
        protected Integer position;
        protected Boolean mentionable, hoisted;

        public RoleData(long id)
        {
            this.id = id;
            this.isPublicRole = id == 0;
        }

        /**
         * Sets the raw permission value for this Role
         *
         * @param  rawPermissions
         *         Raw permission value
         *
         * @throws java.lang.IllegalArgumentException
         *         If the provided permissions are negative or exceed the maximum permissions
         *
         * @return The current RoleData instance for chaining convenience
         */
        
        public RoleData setPermissionsRaw( Long rawPermissions)
        {
            if (rawPermissions != null)
            {
                Checks.notNegative(rawPermissions, "Raw Permissions");
                Checks.check(rawPermissions <= Permission.ALL_PERMISSIONS, "Provided permissions may not be greater than a full permission set!");
            }
            this.permissions = rawPermissions;
            return this;
        }

        /**
         * Adds the provided permissions to the Role
         *
         * @param  permissions
         *         The permissions to add
         *
         * @throws java.lang.IllegalArgumentException
         *         If any of the provided permissions is {@code null}
         *
         * @return The current RoleData instance for chaining convenience
         */
        
        public RoleData addPermissions( Permission... permissions)
        {
            Checks.notNull(permissions, "Permissions");
            for (Permission perm : permissions)
                Checks.notNull(perm, "Permissions");
            if (this.permissions == null)
                this.permissions = 0L;
            this.permissions |= Permission.getRaw(permissions);
            return this;
        }

        /**
         * Adds the provided permissions to the Role
         *
         * @param  permissions
         *         The permissions to add
         *
         * @throws java.lang.IllegalArgumentException
         *         If any of the provided permissions is {@code null}
         *
         * @return The current RoleData instance for chaining convenience
         */
        
        public RoleData addPermissions( Collection<Permission> permissions)
        {
            Checks.noneNull(permissions, "Permissions");
            if (this.permissions == null)
                this.permissions = 0L;
            this.permissions |= Permission.getRaw(permissions);
            return this;
        }

        /**
         * Sets the name for this Role
         *
         * @param  name
         *         The name
         *
         * @throws java.lang.IllegalStateException
         *         If this is the public role
         *
         * @return The current RoleData instance for chaining convenience
         */
        
        public RoleData setName( String name)
        {
            checkPublic("name");
            this.name = name;
            return this;
        }

        /**
         * Sets the color for this Role
         *
         * @param  color
         *         The color for this Role
         *
         * @throws java.lang.IllegalStateException
         *         If this is the public role
         *
         * @return The current RoleData instance for chaining convenience
         */
        
        public RoleData setColor( Color color)
        {
            checkPublic("color");
            this.color = color == null ? null : color.getRGB();
            return this;
        }

        /**
         * Sets the color for this Role
         *
         * @param  color
         *         The color for this Role, or {@code null} to unset
         *
         * @throws java.lang.IllegalStateException
         *         If this is the public role
         *
         * @return The current RoleData instance for chaining convenience
         */
        
        public RoleData setColor( Integer color)
        {
            checkPublic("color");
            this.color = color;
            return this;
        }

        /**
         * Sets the position for this Role
         *
         * @param  position
         *         The position
         *
         * @throws java.lang.IllegalStateException
         *         If this is the public role
         *
         * @return The current RoleData instance for chaining convenience
         */
        
        public RoleData setPosition( Integer position)
        {
            checkPublic("position");
            this.position = position;
            return this;
        }

        /**
         * Sets whether the Role is mentionable
         *
         * @param  mentionable
         *         Whether the role is mentionable
         *
         * @throws java.lang.IllegalStateException
         *         If this is the public role
         *
         * @return The current RoleData instance for chaining convenience
         */
        
        public RoleData setMentionable( Boolean mentionable)
        {
            checkPublic("mentionable");
            this.mentionable = mentionable;
            return this;
        }

        /**
         * Sets whether the Role is hoisted
         *
         * @param  hoisted
         *         Whether the role is hoisted
         *
         * @throws java.lang.IllegalStateException
         *         If this is the public role
         *
         * @return The current RoleData instance for chaining convenience
         */
        
        public RoleData setHoisted( Boolean hoisted)
        {
            checkPublic("hoisted");
            this.hoisted = hoisted;
            return this;
        }

        
        @Override
        public DataObject toData()
        {
            final DataObject o = DataObject.empty().put("id", Long.toUnsignedString(id));
            if (permissions != null)
                o.put("permissions", permissions);
            if (position != null)
                o.put("position", position);
            if (name != null)
                o.put("name", name);
            if (color != null)
                o.put("color", color & 0xFFFFFF);
            if (mentionable != null)
                o.put("mentionable", mentionable);
            if (hoisted != null)
                o.put("hoist", hoisted);
            return o;
        }

        protected void checkPublic(String comment)
        {
            if (isPublicRole)
                throw new IllegalStateException("Cannot modify " + comment + " for the public role!");
        }
    }

    /**
     * GuildChannel information used for the creation of {@link GuildChannel Channels} within
     * the construction of a {@link Guild Guild} via GuildAction.
     *
     * <p>Use with {@link #addChannel(ChannelData) GuildAction.addChannel(ChannelData)}.
     */
    class ChannelData implements SerializableData
    {
        protected final ChannelType type;
        protected final String name;

        protected final Set<PermOverrideData> overrides = new HashSet<>();

        protected Integer position;

        // Text only
        protected String topic;
        protected Boolean nsfw;
        // Voice only
        protected Integer bitrate, userlimit;

        /**
         * Constructs a data object containing information on
         * a {@link GuildChannel GuildChannel} to be used in the construction
         * of a {@link Guild Guild}!
         *
         * @param  type
         *         The {@link ChannelType ChannelType} of the resulting GuildChannel
         *         <br>This may be of type {@link ChannelType#TEXT TEXT} or {@link ChannelType#VOICE VOICE}!
         * @param  name
         *         The name of the channel. This must be alphanumeric with underscores for type TEXT
         *
         * @throws java.lang.IllegalArgumentException
         *         <ul>
         *             <li>If provided with an invalid ChannelType</li>
         *             <li>If the provided name is {@code null} or blank</li>
         *             <li>If the provided name is not between 2-100 characters long</li>
         *             <li>If the type is TEXT and the provided name is not alphanumeric with underscores</li>
         *         </ul>
         */
        public ChannelData(ChannelType type, String name)
        {
            Checks.notBlank(name, "Name");
            Checks.check(type == ChannelType.TEXT || type == ChannelType.VOICE, "Can only create channels of type TEXT or VOICE in GuildAction!");
            Checks.check(name.length() >= 2 && name.length() <= 100, "Channel name has to be between 2-100 characters long!");
            Checks.check(type == ChannelType.VOICE || name.matches("[a-zA-Z0-9-_]+"), "Channels of type TEXT must have a name in alphanumeric with underscores!");

            this.type = type;
            this.name = name;
        }

        /**
         * Sets the topic for this channel.
         * <br>These are only relevant to channels of type {@link ChannelType#TEXT TEXT}.
         *
         * @param  topic
         *         The topic for the channel
         *
         * @throws java.lang.IllegalArgumentException
         *         If the provided topic is bigger than 1024 characters
         *
         * @return This ChannelData instance for chaining convenience
         */
        
        public ChannelData setTopic( String topic)
        {
            if (topic != null && topic.length() > 1024)
                throw new IllegalArgumentException("Channel Topic must not be greater than 1024 in length!");
            this.topic = topic;
            return this;
        }

        /**
         * Sets the whether this channel should be marked NSFW.
         * <br>These are only relevant to channels of type {@link ChannelType#TEXT TEXT}.
         *
         * @param  nsfw
         *         Whether this channel should be marked NSFW
         *
         * @return This ChannelData instance for chaining convenience
         */
        
        public ChannelData setNSFW( Boolean nsfw)
        {
            this.nsfw = nsfw;
            return this;
        }

        /**
         * Sets the bitrate for this channel.
         * <br>These are only relevant to channels of type {@link ChannelType#VOICE VOICE}.
         *
         * @param  bitrate
         *         The bitrate for the channel (8000-96000)
         *
         * @throws java.lang.IllegalArgumentException
         *         If the provided bitrate is not between 8000-96000
         *
         * @return This ChannelData instance for chaining convenience
         */
        
        public ChannelData setBitrate( Integer bitrate)
        {
            if (bitrate != null)
            {
                Checks.check(bitrate >= 8000, "Bitrate must be greater than 8000.");
                Checks.check(bitrate <= 96000, "Bitrate must be less than 96000.");
            }
            this.bitrate = bitrate;
            return this;
        }

        /**
         * Sets the userlimit for this channel.
         * <br>These are only relevant to channels of type {@link ChannelType#VOICE VOICE}.
         *
         * @param  userlimit
         *         The userlimit for the channel (0-99)
         *
         * @throws java.lang.IllegalArgumentException
         *         If the provided userlimit is not between 0-99
         *
         * @return This ChannelData instance for chaining convenience
         */
        
        public ChannelData setUserlimit( Integer userlimit)
        {
            if (userlimit != null && (userlimit < 0 || userlimit > 99))
                throw new IllegalArgumentException("Userlimit must be between 0-99!");
            this.userlimit = userlimit;
            return this;
        }

        /**
         * Sets the position for this channel.
         *
         * @param  position
         *         The position for the channel
         *
         * @return This ChannelData instance for chaining convenience
         */
        
        public ChannelData setPosition( Integer position)
        {
            this.position = position;
            return this;
        }

        /**
         * Adds a {@link PermissionOverride PermissionOverride} to this channel
         * with the provided {@link RoleData RoleData}!
         * <br>Use {@link #newRole() GuildAction.newRole()} to retrieve an instance of RoleData.
         *
         * @param  role
         *         The target role
         * @param  allow
         *         The permissions to grant in the override
         * @param  deny
         *         The permissions to deny in the override
         *
         * @throws java.lang.IllegalArgumentException
         *         <ul>
         *             <li>If the provided role is {@code null}</li>
         *             <li>If the provided allow value is negative or exceeds maximum permissions</li>
         *             <li>If the provided deny value is negative or exceeds maximum permissions</li>
         *         </ul>
         *
         * @return This ChannelData instance for chaining convenience
         */
        
        public ChannelData addPermissionOverride( GuildActionImpl.RoleData role, long allow, long deny)
        {
            Checks.notNull(role, "Role");
            Checks.notNegative(allow, "Granted permissions value");
            Checks.notNegative(deny, "Denied permissions value");
            Checks.check(allow <= Permission.ALL_PERMISSIONS, "Specified allow value may not be greater than a full permission set");
            Checks.check(deny <= Permission.ALL_PERMISSIONS,  "Specified deny value may not be greater than a full permission set");
            this.overrides.add(new PermOverrideData(PermOverrideData.ROLE_TYPE, role.id, allow, deny));
            return this;
        }

        /**
         * Adds a {@link PermissionOverride PermissionOverride} to this channel
         * with the provided {@link GuildAction.RoleData RoleData}!
         * <br>Use {@link #newRole() GuildAction.newRole()} to retrieve an instance of RoleData.
         *
         * @param  role
         *         The target role
         * @param  allow
         *         The permissions to grant in the override
         * @param  deny
         *         The permissions to deny in the override
         *
         * @throws java.lang.IllegalArgumentException
         *         <ul>
         *             <li>If the provided role is {@code null}</li>
         *             <li>If any permission is {@code null}</li>
         *         </ul>
         *
         * @return This ChannelData instance for chaining convenience
         */
        
        public ChannelData addPermissionOverride( GuildActionImpl.RoleData role,  Collection<Permission> allow,  Collection<Permission> deny)
        {
            long allowRaw = 0;
            long denyRaw = 0;
            if (allow != null)
            {
                Checks.noneNull(allow, "Granted Permissions");
                allowRaw = Permission.getRaw(allow);
            }
            if (deny != null)
            {
                Checks.noneNull(deny, "Denied Permissions");
                denyRaw = Permission.getRaw(deny);
            }
            return addPermissionOverride(role, allowRaw, denyRaw);
        }

        
        @Override
        public DataObject toData()
        {
            final DataObject o = DataObject.empty();
            o.put("name", name);
            o.put("type", type.getId());
            if (topic != null)
                o.put("topic", topic);
            if (nsfw != null)
                o.put("nsfw", nsfw);
            if (bitrate != null)
                o.put("bitrate", bitrate);
            if (userlimit != null)
                o.put("user_limit", userlimit);
            if (position != null)
                o.put("position", position);
            if (!overrides.isEmpty())
                o.put("permission_overwrites", overrides);
            return o;
        }
    }
}
