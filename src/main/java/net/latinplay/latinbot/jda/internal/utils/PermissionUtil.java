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
package net.latinplay.latinbot.jda.internal.utils;

import net.latinplay.latinbot.jda.api.Permission;
import net.latinplay.latinbot.jda.api.entities.*;
import net.latinplay.latinbot.jda.internal.entities.GuildImpl;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PermissionUtil
{
    /**
     * Checks if one given Member can interact with a 2nd given Member - in a permission sense (kick/ban/modify perms).
     * This only checks the Role-Position and does not check the actual permission (kick/ban/manage_role/...)
     *
     * @param  issuer
     *         The member that tries to interact with 2nd member
     * @param  target
     *         The member that is the target of the interaction
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return True, if issuer can interact with target in guild
     */
    public static boolean canInteract(Member issuer, Member target)
    {
        Checks.notNull(issuer, "Issuer Member");
        Checks.notNull(target, "Target Member");

        Guild guild = issuer.getGuild();
        if (!guild.equals(target.getGuild()))
            throw new IllegalArgumentException("Provided members must both be Member objects of the same Guild!");
        if(issuer.isOwner())
            return true;
        if(target.isOwner())
            return false;
        List<Role> issuerRoles = issuer.getRoles();
        List<Role> targetRoles = target.getRoles();
        return !issuerRoles.isEmpty() && (targetRoles.isEmpty() || canInteract(issuerRoles.get(0), targetRoles.get(0)));
    }

    /**
     * Checks if a given Member can interact with a given Role - in a permission sense (kick/ban/modify perms).
     * This only checks the Role-Position and does not check the actual permission (kick/ban/manage_role/...)
     *
     * @param  issuer
     *         The member that tries to interact with the role
     * @param  target
     *         The role that is the target of the interaction
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return True, if issuer can interact with target
     */
    public static boolean canInteract(Member issuer, Role target)
    {
        Checks.notNull(issuer, "Issuer Member");
        Checks.notNull(target, "Target Role");

        Guild guild = issuer.getGuild();
        if (!guild.equals(target.getGuild()))
            throw new IllegalArgumentException("Provided Member issuer and Role target must be from the same Guild!");
        if(issuer.isOwner())
            return true;
        List<Role> issuerRoles = issuer.getRoles();
        return !issuerRoles.isEmpty() && canInteract(issuerRoles.get(0), target);
    }

    /**
     * Checks if one given Role can interact with a 2nd given Role - in a permission sense (kick/ban/modify perms).
     * This only checks the Role-Position and does not check the actual permission (kick/ban/manage_role/...)
     *
     * @param  issuer
     *         The role that tries to interact with 2nd role
     * @param  target
     *         The role that is the target of the interaction
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return True, if issuer can interact with target
     */
    public static boolean canInteract(Role issuer, Role target)
    {
        Checks.notNull(issuer, "Issuer Role");
        Checks.notNull(target, "Target Role");

        if(!issuer.getGuild().equals(target.getGuild()))
            throw new IllegalArgumentException("The 2 Roles are not from same Guild!");
        return target.getPosition() < issuer.getPosition();
    }

    /**
     * Check whether the provided {@link Member Member} can use the specified {@link Emote Emote}.
     *
     * <p>If the specified Member is not in the emote's guild or the emote provided is fake this will return false.
     * Otherwise it will check if the emote is restricted to any roles and if that is the case if the Member has one of these.
     *
     * <p>In the case of an {@link Emote#isAnimated() animated} Emote, this will
     * check if the issuer is the currently logged in account, and then check if the account has
     * {@link SelfUser#isNitro() nitro}, and return false if it doesn't.
     * <br>For other accounts, this method will not take into account whether the emote is animated, as there is
     * no real way to check if the Member can interact with them.
     *
     * <br><b>Note</b>: This is not checking if the issuer owns the Guild or not.
     *
     * @param  issuer
     *         The member that tries to interact with the Emote
     * @param  emote
     *         The emote that is the target interaction
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return True, if the issuer can interact with the emote
     */
    public static boolean canInteract(Member issuer, Emote emote)
    {
        Checks.notNull(issuer, "Issuer Member");
        Checks.notNull(emote,  "Target Emote");

        if (!issuer.getGuild().equals(emote.getGuild()))
            throw new IllegalArgumentException("The issuer and target are not in the same Guild");

        // We don't need to check based on the fact it is animated if it's a BOT account
        // because BOT accounts cannot have nitro, and have access to animated Emotes naturally.
        if (emote.isAnimated() && !issuer.getUser().isBot())
        {
            // This is a currently logged in client, meaning we can check if they have nitro or not.
            // If this isn't the currently logged in account, we just check it like a normal emote,
            // since there is no way to verify if they have nitro or not.
            if (issuer.getUser() instanceof SelfUser)
            {
                // If they don't have nitro, we immediately return
                // false, otherwise we proceed with the remaining checks.
                if (!((SelfUser)issuer.getUser()).isNitro())
                    return false;
            }
        }

        return emote.canProvideRoles() && (emote.getRoles().isEmpty() // Emote restricted to roles -> check if the issuer has them
            || CollectionUtils.containsAny(issuer.getRoles(), emote.getRoles()));
    }

    /**
     * Checks whether the specified {@link Emote Emote} can be used by the provided
     * {@link User User} in the {@link MessageChannel MessageChannel}.
     *
     * @param  issuer
     *         The user that tries to interact with the Emote
     * @param  emote
     *         The emote that is the target interaction
     * @param  channel
     *         The MessageChannel this emote should be interacted within
     * @param  botOverride
     *         Whether bots can use non-managed emotes in other guilds
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return True, if the issuer can interact with the emote within the specified MessageChannel
     */
    public static boolean canInteract(User issuer, Emote emote, MessageChannel channel, boolean botOverride)
    {
        Checks.notNull(issuer,  "Issuer Member");
        Checks.notNull(emote,   "Target Emote");
        Checks.notNull(channel, "Target Channel");

        if (emote.getGuild() == null || !emote.getGuild().isMember(issuer))
            return false; // cannot use an emote if you're not in its guild
        Member member = emote.getGuild().getMemberById(issuer.getIdLong());
        if (!canInteract(member, emote))
            return false;
        // external means it is available outside of its own guild - works for bots or if its managed
        // currently we cannot check whether other users have nitro, we assume no here
        final boolean external = emote.isManaged() || (issuer.isBot() && botOverride) || isNitro(issuer);
        switch (channel.getType())
        {
            case TEXT:
                TextChannel text = (TextChannel) channel;
                member = text.getGuild().getMemberById(issuer.getIdLong());
                return emote.getGuild().equals(text.getGuild()) // within the same guild
                    || (external && member != null && member.hasPermission(text, Permission.MESSAGE_EXT_EMOJI)); // in different guild
            default:
                return external; // In Group or Private it only needs to be external
        }
    }

    private static boolean isNitro(User issuer)
    {
        return issuer instanceof SelfUser && ((SelfUser) issuer).isNitro();
    }

    /**
     * Checks whether the specified {@link Emote Emote} can be used by the provided
     * {@link User User} in the {@link MessageChannel MessageChannel}.
     *
     * @param  issuer
     *         The user that tries to interact with the Emote
     * @param  emote
     *         The emote that is the target interaction
     * @param  channel
     *         The MessageChannel this emote should be interacted within
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return True, if the issuer can interact with the emote within the specified MessageChannel
     */
    public static boolean canInteract(User issuer, Emote emote, MessageChannel channel)
    {
        return canInteract(issuer, emote, channel, true);
    }

    /**
     * Checks to see if the {@link Member Member} has the specified {@link Permission Permissions}
     * in the specified {@link Guild Guild}. This method properly deals with Owner status.
     *
     * <p><b>Note:</b> this is based on effective permissions, not literal permissions. If a member has permissions that would
     * enable them to do something without the literal permission to do it, this will still return true.
     * <br>Example: If a member has the {@link Permission#ADMINISTRATOR} permission, they will be able to
     * {@link Permission#MANAGE_SERVER} as well, even without the literal permissions.
     *
     * @param  member
     *         The {@link Member Member} whose permissions are being checked.
     * @param  permissions
     *         The {@link Permission Permissions} being checked for.
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is null
     *
     * @return True -
     *         if the {@link Member Member} effectively has the specified {@link Permission Permissions}.
     */
    public static boolean checkPermission(Member member, Permission... permissions)
    {
        Checks.notNull(member, "Member");
        Checks.notNull(permissions, "Permissions");

        long effectivePerms = getEffectivePermission(member);
        return isApplied(effectivePerms, Permission.ADMINISTRATOR.getRawValue())
                || isApplied(effectivePerms, Permission.getRaw(permissions));
    }

    /**
     * Checks to see if the {@link Member Member} has the specified {@link Permission Permissions}
     * in the specified {@link GuildChannel GuildChannel}. This method properly deals with
     * {@link PermissionOverride PermissionOverrides} and Owner status.
     *
     * <p><b>Note:</b> this is based on effective permissions, not literal permissions. If a member has permissions that would
     * enable them to do something without the literal permission to do it, this will still return true.
     * <br>Example: If a member has the {@link Permission#ADMINISTRATOR} permission, they will be able to
     * {@link Permission#MESSAGE_WRITE} in every channel.
     *
     * @param  member
     *         The {@link Member Member} whose permissions are being checked.
     * @param  channel
     *         The {@link GuildChannel GuildChannel} being checked.
     * @param  permissions
     *         The {@link Permission Permissions} being checked for.
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return True -
     *         if the {@link Member Member} effectively has the specified {@link Permission Permissions}.
     */
    public static boolean checkPermission(GuildChannel channel, Member member, Permission... permissions)
    {
        Checks.notNull(channel, "Channel");
        Checks.notNull(member, "Member");
        Checks.notNull(permissions, "Permissions");

        GuildImpl guild = (GuildImpl) channel.getGuild();
        checkGuild(guild, member.getGuild(), "Member");

        long effectivePerms = getEffectivePermission(channel, member);
        return isApplied(effectivePerms, Permission.getRaw(permissions));
    }

    /**
     * Gets the {@code long} representation of the effective permissions allowed for this {@link Member Member}
     * in this {@link Guild Guild}. This can be used in conjunction with
     * {@link Permission#getPermissions(long) Permission.getPermissions(int)} to easily get a list of all
     * {@link Permission Permissions} that this member has in this {@link Guild Guild}.
     *
     * <p><b>This only returns the Guild-level permissions!</b>
     *
     * @param  member
     *         The {@link Member Member} whose permissions are being checked.
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return The {@code long} representation of the literal permissions that
     *         this {@link Member Member} has in this {@link Guild Guild}.
     */
    public static long getEffectivePermission(Member member)
    {
        Checks.notNull(member, "Member");

        if (member.isOwner())
            return Permission.ALL_PERMISSIONS;
        //Default to binary OR of all global permissions in this guild
        long permission = member.getGuild().getPublicRole().getPermissionsRaw();
        for (Role role : member.getRoles())
        {
            permission |= role.getPermissionsRaw();
            if (isApplied(permission, Permission.ADMINISTRATOR.getRawValue()))
                return Permission.ALL_PERMISSIONS;
        }

        return permission;
    }

    /**
     * Gets the {@code long} representation of the effective permissions allowed for this {@link Member Member}
     * in this {@link GuildChannel GuildChannel}. This can be used in conjunction with
     * {@link Permission#getPermissions(long) Permission.getPermissions(long)} to easily get a list of all
     * {@link Permission Permissions} that this member can use in this {@link GuildChannel GuildChannel}.
     * <br>This functions very similarly to how {@link Role#getPermissionsRaw() Role.getPermissionsRaw()}.
     *
     * @param  channel
     *         The {@link GuildChannel GuildChannel} being checked.
     * @param  member
     *         The {@link Member Member} whose permissions are being checked.
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return The {@code long} representation of the effective permissions that this {@link Member Member}
     *         has in this {@link GuildChannel GuildChannel}.
     */
    public static long getEffectivePermission(GuildChannel channel, Member member)
    {
        Checks.notNull(channel, "Channel");
        Checks.notNull(member, "Member");

        Checks.check(channel.getGuild().equals(member.getGuild()), "Provided channel and provided member are not of the same guild!");

        if (member.isOwner())
        {
            // Owner effectively has all permissions
            return Permission.ALL_PERMISSIONS;
        }

        long permission = getEffectivePermission(member);
        final long admin = Permission.ADMINISTRATOR.getRawValue();
        if (isApplied(permission, admin))
            return Permission.ALL_PERMISSIONS;

        AtomicLong allow = new AtomicLong(0);
        AtomicLong deny = new AtomicLong(0);
        getExplicitOverrides(channel, member, allow, deny);
        permission = apply(permission, allow.get(), deny.get());
        final long viewChannel = Permission.VIEW_CHANNEL.getRawValue();

        //When the permission to view the channel is not applied it is not granted
        // This means that we have no access to this channel at all
        return isApplied(permission, viewChannel) ? permission : 0;
        /*
        // currently discord doesn't implicitly grant permissions that the user can grant others
        // so instead the user has to explicitly make an override to grant them the permission in order to be granted that permission
        // yes this makes no sense but what can i do, the devs don't like changing things apparently...
        // I've been told half a year ago this would be changed but nothing happens
        // so instead I'll just bend over for them so people get "correct" permission checks...
        //
        // only time will tell if something happens and I can finally re-implement this section wew
        final long managePerms = Permission.MANAGE_PERMISSIONS.getRawValue();
        final long manageChannel = Permission.MANAGE_CHANNEL.getRawValue();
        if ((permission & (managePerms | manageChannel)) != 0)
        {
            // In channels, MANAGE_CHANNEL and MANAGE_PERMISSIONS grant full text/voice permissions
            permission |= Permission.ALL_TEXT_PERMISSIONS | Permission.ALL_VOICE_PERMISSIONS;
        }
        */
    }

    /**
     * Gets the {@code long} representation of the effective permissions allowed for this {@link Role Role}
     * in this {@link GuildChannel GuildChannel}. This can be used in conjunction with
     * {@link Permission#getPermissions(long) Permission.getPermissions(long)} to easily get a list of all
     * {@link Permission Permissions} that this role can use in this {@link GuildChannel GuildChannel}.
     *
     * @param  channel
     *         The {@link GuildChannel GuildChannel} in which permissions are being checked.
     * @param  role
     *         The {@link Role Role} whose permissions are being checked.
     *
     * @throws IllegalArgumentException
     *         if any of the provided parameters is {@code null}
     *         or the provided entities are not from the same guild
     *
     * @return The {@code long} representation of the effective permissions that this {@link Role Role}
     *         has in this {@link GuildChannel GuildChannel}
     */
    public static long getEffectivePermission(GuildChannel channel, Role role)
    {
        Checks.notNull(channel, "Channel");
        Checks.notNull(role, "Role");

        Guild guild = channel.getGuild();
        if (!guild.equals(role.getGuild()))
            throw new IllegalArgumentException("Provided channel and role are not of the same guild!");

        long permissions = getExplicitPermission(channel, role);
        if (isApplied(permissions, Permission.ADMINISTRATOR.getRawValue()))
            return Permission.ALL_CHANNEL_PERMISSIONS;
        else if (!isApplied(permissions, Permission.VIEW_CHANNEL.getRawValue()))
            return 0;
        return permissions;
    }

    /**
     * Retrieves the explicit permissions of the specified {@link Member Member}
     * in its hosting {@link Guild Guild}.
     * <br>This method does not calculate the owner in.
     *
     * <p>All permissions returned are explicitly granted to this Member via its {@link Role Roles}.
     * <br>Permissions like {@link Permission#ADMINISTRATOR Permission.ADMINISTRATOR} do not
     * grant other permissions in this value.
     *
     * @param  member
     *         The non-null {@link Member Member} for which to get implicit permissions
     *
     * @throws IllegalArgumentException
     *         If the specified member is {@code null}
     *
     * @return Primitive (unsigned) long value with the implicit permissions of the specified member
     *
     * @since  3.1
     */
    public static long getExplicitPermission(Member member)
    {
        Checks.notNull(member, "Member");

        final Guild guild = member.getGuild();
        long permission = guild.getPublicRole().getPermissionsRaw();

        for (Role role : member.getRoles())
            permission |= role.getPermissionsRaw();

        return permission;
    }

    /**
     * Retrieves the explicit permissions of the specified {@link Member Member}
     * in its hosting {@link Guild Guild} and specific {@link GuildChannel GuildChannel}.
     * <br>This method does not calculate the owner in.
     * <b>Allowed permissions override denied permissions of {@link PermissionOverride PermissionOverrides}!</b>
     *
     * <p>All permissions returned are explicitly granted to this Member via its {@link Role Roles}.
     * <br>Permissions like {@link Permission#ADMINISTRATOR Permission.ADMINISTRATOR} do not
     * grant other permissions in this value.
     * <p>This factor in all {@link PermissionOverride PermissionOverrides} that affect this member
     * and only grants the ones that are explicitly given.
     *
     * @param  channel
     *         The target channel of which to check {@link PermissionOverride PermissionOverrides}
     * @param  member
     *         The non-null {@link Member Member} for which to get implicit permissions
     *
     * @throws IllegalArgumentException
     *         If any of the arguments is {@code null}
     *         or the specified entities are not from the same {@link Guild Guild}
     *
     * @return Primitive (unsigned) long value with the implicit permissions of the specified member in the specified channel
     *
     * @since  3.1
     */
    public static long getExplicitPermission(GuildChannel channel, Member member)
    {
        Checks.notNull(channel, "Channel");
        Checks.notNull(member, "Member");

        final Guild guild = member.getGuild();
        checkGuild(channel.getGuild(), guild, "Member");

        long permission = getExplicitPermission(member);

        AtomicLong allow = new AtomicLong(0);
        AtomicLong deny = new AtomicLong(0);

        // populates allow/deny
        getExplicitOverrides(channel, member, allow, deny);

        return apply(permission, allow.get(), deny.get());
    }

    /**
     * Retrieves the explicit permissions of the specified {@link Role Role}
     * in its hosting {@link Guild Guild} and specific {@link GuildChannel GuildChannel}.
     * <br><b>Allowed permissions override denied permissions of {@link PermissionOverride PermissionOverrides}!</b>
     *
     * <p>All permissions returned are explicitly granted to this Role.
     * <br>Permissions like {@link Permission#ADMINISTRATOR Permission.ADMINISTRATOR} do not
     * grant other permissions in this value.
     * <p>This factor in existing {@link PermissionOverride PermissionOverrides} if possible.
     *
     * @param  channel
     *         The target channel of which to check {@link PermissionOverride PermissionOverrides}
     * @param  role
     *         The non-null {@link Role Role} for which to get implicit permissions
     *
     * @throws IllegalArgumentException
     *         If any of the arguments is {@code null}
     *         or the specified entities are not from the same {@link Guild Guild}
     *
     * @return Primitive (unsigned) long value with the implicit permissions of the specified role in the specified channel
     *
     * @since  3.1
     */
    public static long getExplicitPermission(GuildChannel channel, Role role)
    {
        Checks.notNull(channel, "Channel");
        Checks.notNull(role, "Role");

        final Guild guild = role.getGuild();
        checkGuild(channel.getGuild(), guild, "Role");

        long permission = role.getPermissionsRaw() | guild.getPublicRole().getPermissionsRaw();
        PermissionOverride override = channel.getPermissionOverride(guild.getPublicRole());
        if (override != null)
            permission = apply(permission, override.getAllowedRaw(), override.getDeniedRaw());
        if (role.isPublicRole())
            return permission;

        override = channel.getPermissionOverride(role);

        return override == null
            ? permission
            : apply(permission, override.getAllowedRaw(), override.getDeniedRaw());
    }

    private static void getExplicitOverrides(GuildChannel channel, Member member, AtomicLong allow, AtomicLong deny)
    {
        PermissionOverride override = channel.getPermissionOverride(member.getGuild().getPublicRole());
        long allowRaw = 0;
        long denyRaw = 0;
        if (override != null)
        {
            denyRaw = override.getDeniedRaw();
            allowRaw = override.getAllowedRaw();
        }

        long allowRole = 0;
        long denyRole = 0;
        // create temporary bit containers for role cascade
        for (Role role : member.getRoles())
        {
            override = channel.getPermissionOverride(role);
            if (override != null)
            {
                // important to update role cascade not others
                denyRole |= override.getDeniedRaw();
                allowRole |= override.getAllowedRaw();
            }
        }
        // Override the raw values of public role then apply role cascade
        allowRaw = (allowRaw & ~denyRole) | allowRole;
        denyRaw = (denyRaw & ~allowRole) | denyRole;

        override = channel.getPermissionOverride(member);
        if (override != null)
        {
            // finally override the role cascade with member overrides
            final long oDeny = override.getDeniedRaw();
            final long oAllow = override.getAllowedRaw();
            allowRaw = (allowRaw & ~oDeny) | oAllow;
            denyRaw = (denyRaw & ~oAllow) | oDeny;
            // this time we need to exclude new allowed bits from old denied ones and OR the new denied bits as final overrides
        }
        // set as resulting values
        allow.set(allowRaw);
        deny.set(denyRaw);
    }

    /*
     * Check whether the specified permission is applied in the bits
     */
    private static boolean isApplied(long permissions, long perms)
    {
        return (permissions & perms) == perms;
    }

    private static long apply(long permission, long allow, long deny)
    {
        permission &= ~deny;  //Deny everything that the cascade of roles denied.
        permission |= allow;  //Allow all the things that the cascade of roles allowed
                              // The allowed bits override the denied ones!
        return permission;
    }

    private static void checkGuild(Guild o1, Guild o2, String name)
    {
        Checks.check(o1.equals(o2),
            "Specified %s is not in the same guild! (%s / %s)", name, o1, o2);
    }
}
