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

package net.latinplay.latinbot.jda.internal.managers;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.OnlineStatus;
import net.latinplay.latinbot.jda.api.entities.Activity;
import net.latinplay.latinbot.jda.api.managers.Presence;
import net.latinplay.latinbot.jda.api.utils.data.DataObject;
import net.latinplay.latinbot.jda.internal.JDAImpl;
import net.latinplay.latinbot.jda.internal.requests.WebSocketCode;
import net.latinplay.latinbot.jda.internal.utils.Checks;

/**
 * The Presence associated with the provided JDA instance
 * <br><b>Note that this does not automatically handle the 5/60 second rate limit!</b>
 *
 * @since  3.0
 */
public class PresenceImpl implements Presence
{
    private final JDAImpl api;
    private boolean idle = false;
    private Activity activity = null;
    private OnlineStatus status = OnlineStatus.ONLINE;

    /**
     * Creates a new Presence representation for the provided JDAImpl instance
     *
     * @param jda
     *        The not-null JDAImpl instance to use
     */
    public PresenceImpl(JDAImpl jda)
    {
        this.api = jda;
    }


    /* -- Public Getters -- */


    
    @Override
    public JDA getJDA()
    {
        return api;
    }

    
    @Override
    public OnlineStatus getStatus()
    {
        return status;
    }

    @Override
    public Activity getActivity()
    {
        return activity;
    }

    @Override
    public boolean isIdle()
    {
        return idle;
    }


    /* -- Public Setters -- */


    @Override
    public void setStatus(OnlineStatus status)
    {
        setPresence(status, activity, idle);
    }

    @Override
    public void setActivity(Activity game)
    {
        setPresence(status, game);
    }

    @Override
    public void setIdle(boolean idle)
    {
        setPresence(status, idle);
    }

    @Override
    public void setPresence(OnlineStatus status, Activity activity, boolean idle)
    {
        DataObject gameObj = getGameJson(activity);

        Checks.check(status != OnlineStatus.UNKNOWN,
                "Cannot set the presence status to an unknown OnlineStatus!");
        if (status == OnlineStatus.OFFLINE || status == null)
            status = OnlineStatus.INVISIBLE;

        DataObject object = DataObject.empty();

        object.put("game", gameObj);
        object.put("afk", idle);
        object.put("status", status.getKey());
        object.put("since", System.currentTimeMillis());
        update(object);
        this.idle = idle;
        this.status = status;
        this.activity = gameObj == null ? null : activity;
    }

    @Override
    public void setPresence(OnlineStatus status, Activity activity)
    {
        setPresence(status, activity, idle);
    }

    @Override
    public void setPresence(OnlineStatus status, boolean idle)
    {
        setPresence(status, activity, idle);
    }

    @Override
    public void setPresence(Activity game, boolean idle)
    {
        setPresence(status, game, idle);
    }


    /* -- Impl Setters -- */


    public PresenceImpl setCacheStatus(OnlineStatus status)
    {
        if (status == null)
            throw new NullPointerException("Null OnlineStatus is not allowed.");
        if (status == OnlineStatus.OFFLINE)
            status = OnlineStatus.INVISIBLE;
        this.status = status;
        return this;
    }

    public PresenceImpl setCacheActivity(Activity game)
    {
        this.activity = game;
        return this;
    }

    public PresenceImpl setCacheIdle(boolean idle)
    {
        this.idle = idle;
        return this;
    }


    /* -- Internal Methods -- */


    public DataObject getFullPresence()
    {
        DataObject activity = getGameJson(this.activity);
        return DataObject.empty()
              .put("afk", idle)
              .put("since", System.currentTimeMillis())
              .put("game", activity)
              .put("status", getStatus().getKey());
    }

    private DataObject getGameJson(Activity activity)
    {
        if (activity == null || activity.getName() == null || activity.getType() == null)
            return null;
        DataObject gameObj = DataObject.empty();
        gameObj.put("name", activity.getName());
        gameObj.put("type", activity.getType().getKey());
        if (activity.getUrl() != null)
            gameObj.put("url", activity.getUrl());

        return gameObj;
    }


    /* -- Terminal -- */


    protected void update(DataObject data)
    {
        JDA.Status status = api.getStatus();
        if (status == JDA.Status.RECONNECT_QUEUED || status == JDA.Status.SHUTDOWN || status == JDA.Status.SHUTTING_DOWN)
            return;
        api.getClient().send(DataObject.empty()
            .put("d", data)
            .put("op", WebSocketCode.PRESENCE).toString());
    }

}
