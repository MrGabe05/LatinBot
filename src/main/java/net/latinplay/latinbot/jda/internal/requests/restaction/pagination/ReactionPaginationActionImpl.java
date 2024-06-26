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

package net.latinplay.latinbot.jda.internal.requests.restaction.pagination;

import net.latinplay.latinbot.jda.api.entities.Message;
import net.latinplay.latinbot.jda.api.entities.MessageReaction;
import net.latinplay.latinbot.jda.api.entities.User;
import net.latinplay.latinbot.jda.api.exceptions.ParsingException;
import net.latinplay.latinbot.jda.api.requests.Request;
import net.latinplay.latinbot.jda.api.requests.Response;
import net.latinplay.latinbot.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.latinplay.latinbot.jda.api.utils.data.DataArray;
import net.latinplay.latinbot.jda.internal.entities.EntityBuilder;
import net.latinplay.latinbot.jda.internal.requests.Route;
import net.latinplay.latinbot.jda.internal.utils.EncodingUtil;

import java.util.LinkedList;
import java.util.List;

public class ReactionPaginationActionImpl
    extends PaginationActionImpl<User, ReactionPaginationAction>
    implements ReactionPaginationAction
{
    protected final MessageReaction reaction;

    /**
     * Creates a new PaginationAction instance
     *
     * @param reaction
     *        The target {@link MessageReaction MessageReaction}
     */
    public ReactionPaginationActionImpl(MessageReaction reaction)
    {
        super(reaction.getJDA(), Route.Messages.GET_REACTION_USERS.compile(reaction.getChannel().getId(), reaction.getMessageId(), getCode(reaction)), 1, 100, 100);
        this.reaction = reaction;
    }

    public ReactionPaginationActionImpl(Message message, String code)
    {
        super(message.getJDA(), Route.Messages.GET_REACTION_USERS.compile(message.getChannel().getId(), message.getId(), code), 1, 100, 100);
        this.reaction = null;
    }

    protected static String getCode(MessageReaction reaction)
    {
        MessageReaction.ReactionEmote emote = reaction.getReactionEmote();

        return emote.isEmote()
            ? emote.getName() + ":" + emote.getId()
            : EncodingUtil.encodeUTF8(emote.getName());
    }


    @Override
    public MessageReaction getReaction()
    {
        if (reaction == null)
            throw new IllegalStateException("Cannot get reaction for this action");
        return reaction;
    }

    @Override
    protected Route.CompiledRoute finalizeRoute()
    {
        Route.CompiledRoute route = super.finalizeRoute();

        String after = null;
        String limit = String.valueOf(getLimit());
        long last = this.lastKey;
        if (last != 0)
            after = Long.toUnsignedString(last);

        route = route.withQueryParams("limit", limit);

        if (after != null)
            route = route.withQueryParams("after", after);

        return route;
    }

    @Override
    protected void handleSuccess(Response response, Request<List<User>> request)
    {
        final EntityBuilder builder = api.getEntityBuilder();
        final DataArray array = response.getArray();
        final List<User> users = new LinkedList<>();
        for (int i = 0; i < array.length(); i++)
        {
            try
            {
                final User user = builder.createFakeUser(array.getObject(i), false);
                users.add(user);
                if (useCache)
                    cached.add(user);
                last = user;
                lastKey = last.getIdLong();
            }
            catch (ParsingException | NullPointerException e)
            {
                LOG.warn("Encountered exception in ReactionPagination", e);
            }
        }

        request.onSuccess(users);
    }

    @Override
    protected long getKey(User it)
    {
        return it.getIdLong();
    }
}
