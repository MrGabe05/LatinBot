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

import net.latinplay.latinbot.jda.api.entities.Role;
import net.latinplay.latinbot.jda.api.events.role.RoleCreateEvent;
import net.latinplay.latinbot.jda.api.utils.data.DataObject;
import net.latinplay.latinbot.jda.internal.JDAImpl;
import net.latinplay.latinbot.jda.internal.entities.GuildImpl;

public class GuildRoleCreateHandler extends SocketHandler
{

    public GuildRoleCreateHandler(JDAImpl api)
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
            EventCache.LOG.debug("GUILD_ROLE_CREATE was received for a Guild that is not yet cached: {}", content);
            return null;
        }

        Role newRole = getJDA().getEntityBuilder().createRole(guild, content.getObject("role"), guild.getIdLong());
        getJDA().handleEvent(
            new RoleCreateEvent(
                getJDA(), responseNumber,
                newRole));
        return null;
    }
}
