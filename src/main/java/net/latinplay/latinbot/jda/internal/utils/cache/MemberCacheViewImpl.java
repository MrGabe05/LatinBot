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

package net.latinplay.latinbot.jda.internal.utils.cache;

import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.Role;
import net.latinplay.latinbot.jda.api.utils.cache.MemberCacheView;
import net.latinplay.latinbot.jda.internal.utils.Checks;

import java.util.*;

public class MemberCacheViewImpl extends SnowflakeCacheViewImpl<Member> implements MemberCacheView
{
    public MemberCacheViewImpl()
    {
        super(Member.class, Member::getEffectiveName);
    }

    @Override
    public Member getElementById(long id)
    {
        return get(id);
    }

    
    @Override
    public List<Member> getElementsByUsername( String name, boolean ignoreCase)
    {
        Checks.notEmpty(name, "Name");
        if (isEmpty())
            return Collections.emptyList();
        List<Member> members = new ArrayList<>();
        forEach(member ->
        {
            final String nick = member.getUser().getName();
            if (equals(ignoreCase, nick, name))
                members.add(member);
        });
        return Collections.unmodifiableList(members);
    }

    
    @Override
    public List<Member> getElementsByNickname( String name, boolean ignoreCase)
    {
        if (isEmpty())
            return Collections.emptyList();
        List<Member> members = new ArrayList<>();
        forEach(member ->
        {
            final String nick = member.getNickname();
            if (nick == null)
            {
                if (name == null)
                    members.add(member);
                return;
            }

            if (equals(ignoreCase, nick, name))
                members.add(member);
        });
        return Collections.unmodifiableList(members);
    }

    
    @Override
    public List<Member> getElementsWithRoles( Role... roles)
    {
        Checks.notNull(roles, "Roles");
        return getElementsWithRoles(Arrays.asList(roles));
    }

    
    @Override
    public List<Member> getElementsWithRoles( Collection<Role> roles)
    {
        Checks.noneNull(roles, "Roles");
        if (isEmpty())
            return Collections.emptyList();
        List<Member> members = new ArrayList<>();
        forEach(member ->
        {
            if (member.getRoles().containsAll(roles))
                members.add(member);
        });
        return members;
    }
}
