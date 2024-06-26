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

package net.latinplay.latinbot.jda.internal.requests.restaction;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Invite;
import net.latinplay.latinbot.jda.api.requests.Request;
import net.latinplay.latinbot.jda.api.requests.Response;
import net.latinplay.latinbot.jda.api.requests.restaction.InviteAction;
import net.latinplay.latinbot.jda.api.utils.data.DataObject;
import net.latinplay.latinbot.jda.internal.requests.Route;
import net.latinplay.latinbot.jda.internal.utils.Checks;
import okhttp3.RequestBody;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class InviteActionImpl extends AuditableRestActionImpl<Invite> implements InviteAction
{
    private Integer maxAge = null;
    private Integer maxUses = null;
    private Boolean temporary = null;
    private Boolean unique = null;

    public InviteActionImpl(final JDA api, final String channelId)
    {
        super(api, Route.Invites.CREATE_INVITE.compile(channelId));
    }


    @Override
    public InviteActionImpl setCheck(BooleanSupplier checks)
    {
        return (InviteActionImpl) super.setCheck(checks);
    }


    @Override

    public InviteActionImpl setMaxAge(final Integer maxAge)
    {
        if (maxAge != null)
            Checks.notNegative(maxAge, "maxAge");

        this.maxAge = maxAge;
        return this;
    }


    @Override

    public InviteActionImpl setMaxAge(final Long maxAge,  final TimeUnit timeUnit)
    {
        if (maxAge == null)
            return this.setMaxAge(null);

        Checks.notNegative(maxAge, "maxAge");
        Checks.notNull(timeUnit, "timeUnit");

        return this.setMaxAge(Math.toIntExact(timeUnit.toSeconds(maxAge)));
    }


    @Override

    public InviteActionImpl setMaxUses(final Integer maxUses)
    {
        if (maxUses != null)
            Checks.notNegative(maxUses, "maxUses");

        this.maxUses = maxUses;
        return this;
    }


    @Override

    public InviteActionImpl setTemporary(final Boolean temporary)
    {
        this.temporary = temporary;
        return this;
    }


    @Override

    public InviteActionImpl setUnique(final Boolean unique)
    {
        this.unique = unique;
        return this;
    }

    @Override
    protected RequestBody finalizeData()
    {
        DataObject object = DataObject.empty();

        if (this.maxAge != null)
            object.put("max_age", this.maxAge);
        if (this.maxUses != null)
            object.put("max_uses", this.maxUses);
        if (this.temporary != null)
            object.put("temporary", this.temporary);
        if (this.unique != null)
            object.put("unique", this.unique);

        return getRequestBody(object);
    }

    @Override
    protected void handleSuccess(final Response response, final Request<Invite> request)
    {
        request.onSuccess(this.api.getEntityBuilder().createInvite(response.getObject()));
    }
}
