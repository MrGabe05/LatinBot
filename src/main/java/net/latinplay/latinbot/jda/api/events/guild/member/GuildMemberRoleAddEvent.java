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
package net.latinplay.latinbot.jda.api.events.guild.member;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.JDABuilder;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.Role;

import java.util.Collections;
import java.util.List;

/**
 * Indicates that one or more {@link Role Roles} were assigned to a {@link Member Member}.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve affected member and guild. Provides a list of added roles.
 */
public class GuildMemberRoleAddEvent extends GenericGuildMemberEvent
{
    private final List<Role> addedRoles;

    public GuildMemberRoleAddEvent( JDA api, long responseNumber,  Member member,  List<Role> addedRoles)
    {
        super(api, responseNumber, member);
        this.addedRoles = Collections.unmodifiableList(addedRoles);
    }

    /**
     * The list of roles that were added
     *
     * @return The list of roles that were added
     */

    public List<Role> getRoles()
    {
        return addedRoles;
    }
}
