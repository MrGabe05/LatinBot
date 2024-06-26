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

package net.latinplay.latinbot.jda.internal.utils.config;

import net.latinplay.latinbot.jda.api.utils.cache.CacheFlag;
import net.latinplay.latinbot.jda.internal.utils.config.flags.ConfigFlag;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetaConfig
{
    private static final MetaConfig defaultConfig = new MetaConfig(2048, null, EnumSet.allOf(CacheFlag.class), ConfigFlag.getDefault());
    private final ConcurrentMap<String, String> mdcContextMap;
    private final EnumSet<CacheFlag> cacheFlags;
    private final boolean enableMDC;
    private final boolean useShutdownHook;
    private final boolean guildSubscriptions;
    private final int maxBufferSize;

    public MetaConfig(
            int maxBufferSize,
             ConcurrentMap<String, String> mdcContextMap,
             EnumSet<CacheFlag> cacheFlags, EnumSet<ConfigFlag> flags)
    {
        this.maxBufferSize = maxBufferSize;
        this.cacheFlags = cacheFlags == null ? EnumSet.allOf(CacheFlag.class) : cacheFlags;
        this.enableMDC = flags.contains(ConfigFlag.MDC_CONTEXT);
        if (enableMDC)
            this.mdcContextMap = mdcContextMap == null ? new ConcurrentHashMap<>() : null;
        else
            this.mdcContextMap = null;
        this.useShutdownHook = flags.contains(ConfigFlag.SHUTDOWN_HOOK);
        this.guildSubscriptions = flags.contains(ConfigFlag.GUILD_SUBSCRIPTIONS);
    }

    
    public ConcurrentMap<String, String> getMdcContextMap()
    {
        return mdcContextMap;
    }

    
    public EnumSet<CacheFlag> getCacheFlags()
    {
        return cacheFlags;
    }

    public boolean isEnableMDC()
    {
        return enableMDC;
    }

    public boolean isUseShutdownHook()
    {
        return useShutdownHook;
    }

    public boolean isGuildSubscriptions()
    {
        return guildSubscriptions;
    }

    public int getMaxBufferSize()
    {
        return maxBufferSize;
    }

    
    public static MetaConfig getDefault()
    {
        return defaultConfig;
    }
}
