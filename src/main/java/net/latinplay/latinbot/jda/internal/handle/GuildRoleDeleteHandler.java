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
package net.latinplay.latinbot.jda.internal.handle;

import net.latinplay.latinbot.jda.api.entities.Emote;
import net.latinplay.latinbot.jda.api.entities.Role;
import net.latinplay.latinbot.jda.api.events.role.RoleDeleteEvent;
import net.latinplay.latinbot.jda.api.utils.data.DataObject;
import net.latinplay.latinbot.jda.internal.JDAImpl;
import net.latinplay.latinbot.jda.internal.entities.EmoteImpl;
import net.latinplay.latinbot.jda.internal.entities.GuildImpl;
import net.latinplay.latinbot.jda.internal.entities.MemberImpl;
import net.latinplay.latinbot.jda.internal.requests.WebSocketClient;

public class GuildRoleDeleteHandler extends SocketHandler
{
    public GuildRoleDeleteHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long guildId = content.getLong("guild_id");
        if (getJDA().getGuildSetupController().isLocked(guildId))
            return guildId;

        GuildImpl guild = (GuildImpl) getJDA().getGuildById(guildId);
        if (guild == null)
        {
            getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("GUILD_ROLE_DELETE was received for a Guild that is not yet cached: {}", content);
            return null;
        }

        final long roleId = content.getLong("role_id");
        Role removedRole = guild.getRolesView().remove(roleId);
        if (removedRole == null)
        {
            //getJDA().getEventCache().cache(EventCache.Type.ROLE, roleId, () -> handle(responseNumber, allContent));
            WebSocketClient.LOG.debug("GUILD_ROLE_DELETE was received for a Role that is not yet cached: {}", content);
            return null;
        }

        //Now that the role is removed from the Guild, remove it from all users and emotes.
        guild.getMembersView().forEach(m ->
        {
            MemberImpl member = (MemberImpl) m;
            member.getRoleSet().remove(removedRole);
        });

        for (Emote emote : guild.getEmoteCache())
        {
            EmoteImpl impl = (EmoteImpl) emote;
            if (impl.canProvideRoles())
                impl.getRoleSet().remove(removedRole);
        }

        getJDA().handleEvent(
            new RoleDeleteEvent(
                getJDA(), responseNumber,
                removedRole));
        getJDA().getEventCache().clear(EventCache.Type.ROLE, roleId);
        return null;
    }
}
