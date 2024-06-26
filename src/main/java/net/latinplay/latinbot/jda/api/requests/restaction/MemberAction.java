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

import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.Role;
import net.latinplay.latinbot.jda.api.entities.User;
import net.latinplay.latinbot.jda.api.requests.RestAction;

import java.util.Collection;
import java.util.function.BooleanSupplier;

/**
 * {@link RestAction RestAction} extension
 * specifically designed to allow bots to add {@link User Users} to Guilds.
 * <br>This requires an <b>OAuth2 Access Token</b> with the scope {@code guilds.join} to work!
 *
 * @since  3.7.0
 *
 * @see    Guild#addMember(String, User)
 * @see    Guild#addMember(String, long)
 * @see    Guild#addMember(String, String)
 * @see    <a href="https://discordapp.com/developers/docs/topics/oauth2" target="_blank">Discord OAuth2 Documentation</a>
 */
public interface MemberAction extends RestAction<Void>
{
    
    @Override
    MemberAction setCheck(BooleanSupplier checks);

    /**
     * The access token
     *
     * @return The access token
     */
    
    String getAccessToken();

    /**
     * The id of the user who will be added by this task
     *
     * @return The id of the user
     */
    
    String getUserId();

    /**
     * The user associated with the id
     *
     * @return Possibly-null user associated with the id
     */
    
    User getUser();

    /**
     * The {@link Guild Guild} to which the
     * user will be added.
     *
     * @return The Guild
     */
    
    Guild getGuild();

    /**
     * Sets the nickname of the user for the guild.
     * <br>This will then be visible with {@link Member#getNickname() Member.getNickname()}.
     *
     * @param  nick
     *         The nickname, or {@code null}
     *
     * @throws IllegalArgumentException
     *         If the provided nickname is longer than 32 characters
     *
     * @return The current MemberAction for chaining
     */
    
    
    MemberAction setNickname(String nick);

    /**
     * Sets the roles of the user for the guild.
     * <br>This will then be visible with {@link Member#getRoles() Member.getRoles()}.
     *
     * @param  roles
     *         The roles, or {@code null}
     *
     * @throws IllegalArgumentException
     *         If one of the provided roles is null or not from the same guild
     *
     * @return The current MemberAction for chaining
     */
    
    
    MemberAction setRoles(Collection<Role> roles);

    /**
     * Sets the roles of the user for the guild.
     * <br>This will then be visible with {@link Member#getRoles() Member.getRoles()}.
     *
     * @param  roles
     *         The roles, or {@code null}
     *
     * @throws IllegalArgumentException
     *         If one of the provided roles is null or not from the same guild
     *
     * @return The current MemberAction for chaining
     */
    
    
    MemberAction setRoles(Role... roles);

    /**
     * Whether the user should be voice muted in the guild.
     * <br>Default: {@code false}
     *
     * @param  mute
     *         Whether the user should be voice muted in the guild.
     *
     * @return The current MemberAction for chaining
     */
    
    
    MemberAction setMute(boolean mute);

    /**
     * Whether the user should be voice deafened in the guild.
     * <br>Default: {@code false}
     *
     * @param  deaf
     *         Whether the user should be voice deafened in the guild.
     *
     * @return The current MemberAction for chaining
     */
    
    
    MemberAction setDeafen(boolean deaf);
}
