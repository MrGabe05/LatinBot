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

package net.latinplay.latinbot.jda.api.utils.cache;

import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.entities.ISnowflake;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.Role;
import net.latinplay.latinbot.jda.api.utils.MiscUtil;

import java.util.Collection;
import java.util.List;

/**
 * {@link CacheView CacheView} implementation
 * specifically to combine {@link Member Member} cache views.
 *
 * <p>This is done because Members do not implement {@link ISnowflake ISnowflake} as
 * they are not globally unique but only unique per {@link Guild Guild}!
 *
 * @see CacheView CacheView for details on Efficient Memory Usage
 */
public interface UnifiedMemberCacheView extends CacheView<Member>
{
    /**
     * Retrieves all member represented by the provided ID.
     *
     * @param  id
     *         The ID of the members
     *
     * @return Possibly-empty unmodifiable list of member for the specified ID
     */
    
    List<Member> getElementsById(long id);

    /**
     * Retrieves all member represented by the provided ID.
     *
     * @param  id
     *         The ID of the members
     *
     * @throws java.lang.NumberFormatException
     *         If the provided String is {@code null} or
     *         cannot be resolved to an unsigned long id
     *
     * @return Possibly-empty unmodifiable list of member for the specified ID
     */
    
    default List<Member> getElementsById( String id)
    {
        return getElementsById(MiscUtil.parseSnowflake(id));
    }

    /**
     * Creates an immutable list of all members matching the given username.
     * <br>This will check the name of the wrapped user.
     *
     * @param  name
     *         The name to check
     * @param  ignoreCase
     *         Whether to ignore case when comparing usernames
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is {@code null}
     *
     * @return Immutable list of members with the given username
     */
    
    List<Member> getElementsByUsername( String name, boolean ignoreCase);

    /**
     * Creates an immutable list of all members matching the given username.
     * <br>This will check the name of the wrapped user.
     *
     * @param  name
     *         The name to check
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is {@code null}
     *
     * @return Immutable list of members with the given username
     */
    
    default List<Member> getElementsByUsername( String name)
    {
        return getElementsByUsername(name, false);
    }

    /**
     * Creates an immutable list of all members matching the given nickname.
     * <br>This will check the nickname of the member.
     * If provided with {@code null} this will check for members
     * that have no nickname set.
     *
     * @param  name
     *         The nullable nickname to check
     * @param  ignoreCase
     *         Whether to ignore case when comparing nicknames
     *
     * @return Immutable list of members with the given nickname
     */
    
    List<Member> getElementsByNickname( String name, boolean ignoreCase);

    /**
     * Creates an immutable list of all members matching the given nickname.
     * <br>This will check the nickname of the member.
     * If provided with {@code null} this will check for members
     * that have no nickname set.
     *
     * @param  name
     *         The nullable nickname to check
     *
     * @return Immutable list of members with the given nickname
     */
    
    default List<Member> getElementsByNickname( String name)
    {
        return getElementsByNickname(name, false);
    }

    /**
     * Creates an immutable list of all members that hold all
     * of the provided roles.
     *
     * @param  roles
     *         Roles the members should have
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with {@code null}
     *
     * @return Immutable list of members with the given roles
     */
    
    List<Member> getElementsWithRoles( Role... roles);

    /**
     * Creates an immutable list of all members that hold all
     * of the provided roles.
     *
     * @param  roles
     *         Roles the members should have
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with {@code null}
     *
     * @return Immutable list of members with the given roles
     */
    
    List<Member> getElementsWithRoles( Collection<Role> roles);
}
