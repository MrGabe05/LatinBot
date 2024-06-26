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

package net.latinplay.latinbot.jda.internal.utils.config.sharding;

import net.latinplay.latinbot.jda.api.OnlineStatus;
import net.latinplay.latinbot.jda.api.entities.Activity;

import java.util.function.IntFunction;

public class PresenceProviderConfig
{
    private IntFunction<? extends Activity> activityProvider;
    private IntFunction<OnlineStatus> statusProvider;
    private IntFunction<Boolean> idleProvider;


    public IntFunction<? extends Activity> getActivityProvider()
    {
        return activityProvider;
    }

    public void setActivityProvider( IntFunction<? extends Activity> activityProvider)
    {
        this.activityProvider = activityProvider;
    }


    public IntFunction<OnlineStatus> getStatusProvider()
    {
        return statusProvider;
    }

    public void setStatusProvider( IntFunction<OnlineStatus> statusProvider)
    {
        this.statusProvider = statusProvider;
    }


    public IntFunction<Boolean> getIdleProvider()
    {
        return idleProvider;
    }

    public void setIdleProvider( IntFunction<Boolean> idleProvider)
    {
        this.idleProvider = idleProvider;
    }


    public static PresenceProviderConfig getDefault()
    {
        return new PresenceProviderConfig();
    }
}
