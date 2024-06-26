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
import net.latinplay.latinbot.jda.api.Permission;
import net.latinplay.latinbot.jda.api.Region;
import net.latinplay.latinbot.jda.api.entities.Guild;
import net.latinplay.latinbot.jda.api.entities.Icon;
import net.latinplay.latinbot.jda.api.entities.TextChannel;
import net.latinplay.latinbot.jda.api.entities.VoiceChannel;
import net.latinplay.latinbot.jda.api.exceptions.InsufficientPermissionException;
import net.latinplay.latinbot.jda.api.managers.GuildManager;
import net.latinplay.latinbot.jda.api.utils.data.DataObject;
import net.latinplay.latinbot.jda.internal.requests.Route;
import net.latinplay.latinbot.jda.internal.utils.Checks;
import net.latinplay.latinbot.jda.internal.utils.cache.SnowflakeReference;
import okhttp3.RequestBody;

public class GuildManagerImpl extends ManagerBase<GuildManager> implements GuildManager
{
    protected final SnowflakeReference<Guild> guild;

    protected String name;
    protected String region;
    protected Icon icon, splash, banner;
    protected String afkChannel, systemChannel;
    protected String description, vanityCode;
    protected int afkTimeout;
    protected int mfaLevel;
    protected int notificationLevel;
    protected int explicitContentLevel;
    protected int verificationLevel;

    public GuildManagerImpl(Guild guild)
    {
        super(guild.getJDA(), Route.Guilds.MODIFY_GUILD.compile(guild.getId()));
        JDA api = guild.getJDA();
        this.guild = new SnowflakeReference<>(guild, api::getGuildById);
        if (isPermissionChecksEnabled())
            checkPermissions();
    }


    @Override
    public Guild getGuild()
    {
        return guild.resolve();
    }


    @Override

    public GuildManagerImpl reset(long fields)
    {
        super.reset(fields);
        if ((fields & NAME) == NAME)
            this.name = null;
        if ((fields & REGION) == REGION)
            this.region = null;
        if ((fields & ICON) == ICON)
            this.icon = null;
        if ((fields & SPLASH) == SPLASH)
            this.splash = null;
        if ((fields & AFK_CHANNEL) == AFK_CHANNEL)
            this.afkChannel = null;
        if ((fields & SYSTEM_CHANNEL) == SYSTEM_CHANNEL)
            this.systemChannel = null;
        if ((fields & DESCRIPTION) == DESCRIPTION)
            this.description = null;
        if ((fields & BANNER) == BANNER)
            this.banner = null;
        return this;
    }


    @Override

    public GuildManagerImpl reset(long... fields)
    {
        super.reset(fields);
        return this;
    }


    @Override

    public GuildManagerImpl reset()
    {
        super.reset();
        this.name = null;
        this.region = null;
        this.icon = null;
        this.splash = null;
        this.vanityCode = null;
        this.description = null;
        this.banner = null;
        this.afkChannel = null;
        this.systemChannel = null;
        return this;
    }


    @Override

    public GuildManagerImpl setName( String name)
    {
        Checks.notNull(name, "Name");
        Checks.check(name.length() >= 2 && name.length() <= 100, "Name must be between 2-100 characters long");
        this.name = name;
        set |= NAME;
        return this;
    }


    @Override

    public GuildManagerImpl setRegion( Region region)
    {
        Checks.notNull(region, "Region");
        Checks.check(region != Region.UNKNOWN, "Region must not be UNKNOWN");
        Checks.check(!region.isVip() || getGuild().getFeatures().contains("VIP_REGIONS"), "Cannot set a VIP voice region on this guild");
        this.region = region.getKey();
        set |= REGION;
        return this;
    }


    @Override

    public GuildManagerImpl setIcon(Icon icon)
    {
        this.icon = icon;
        set |= ICON;
        return this;
    }


    @Override

    public GuildManagerImpl setSplash(Icon splash)
    {
        checkFeature("INVITE_SPLASH");
        this.splash = splash;
        set |= SPLASH;
        return this;
    }


    @Override

    public GuildManagerImpl setAfkChannel(VoiceChannel afkChannel)
    {
        Checks.check(afkChannel == null || afkChannel.getGuild().equals(getGuild()), "Channel must be from the same guild");
        this.afkChannel = afkChannel == null ? null : afkChannel.getId();
        set |= AFK_CHANNEL;
        return this;
    }


    @Override

    public GuildManagerImpl setSystemChannel(TextChannel systemChannel)
    {
        Checks.check(systemChannel == null || systemChannel.getGuild().equals(getGuild()), "Channel must be from the same guild");
        this.systemChannel = systemChannel == null ? null : systemChannel.getId();
        set |= SYSTEM_CHANNEL;
        return this;
    }


    @Override

    public GuildManagerImpl setAfkTimeout( Guild.Timeout timeout)
    {
        Checks.notNull(timeout, "Timeout");
        this.afkTimeout = timeout.getSeconds();
        set |= AFK_TIMEOUT;
        return this;
    }


    @Override

    public GuildManagerImpl setVerificationLevel( Guild.VerificationLevel level)
    {
        Checks.notNull(level, "Level");
        Checks.check(level != Guild.VerificationLevel.UNKNOWN, "Level must not be UNKNOWN");
        this.verificationLevel = level.getKey();
        set |= VERIFICATION_LEVEL;
        return this;
    }


    @Override

    public GuildManagerImpl setDefaultNotificationLevel( Guild.NotificationLevel level)
    {
        Checks.notNull(level, "Level");
        Checks.check(level != Guild.NotificationLevel.UNKNOWN, "Level must not be UNKNOWN");
        this.notificationLevel = level.getKey();
        set |= NOTIFICATION_LEVEL;
        return this;
    }


    @Override

    public GuildManagerImpl setRequiredMFALevel( Guild.MFALevel level)
    {
        Checks.notNull(level, "Level");
        Checks.check(level != Guild.MFALevel.UNKNOWN, "Level must not be UNKNOWN");
        this.mfaLevel = level.getKey();
        set |= MFA_LEVEL;
        return this;
    }


    @Override

    public GuildManagerImpl setExplicitContentLevel( Guild.ExplicitContentLevel level)
    {
        Checks.notNull(level, "Level");
        Checks.check(level != Guild.ExplicitContentLevel.UNKNOWN, "Level must not be UNKNOWN");
        this.explicitContentLevel = level.getKey();
        set |= EXPLICIT_CONTENT_LEVEL;
        return this;
    }


    @Override
    public GuildManager setBanner( Icon banner)
    {
        checkFeature("BANNER");
        this.banner = banner;
        set |= BANNER;
        return this;
    }


    @Override
    public GuildManager setVanityCode( String code)
    {
        checkFeature("VANITY_URL");
        this.vanityCode = code;
        set |= VANITY_URL;
        return this;
    }


    @Override
    public GuildManager setDescription( String description)
    {
        checkFeature("VERIFIED");
        this.description = description;
        set |= DESCRIPTION;
        return this;
    }

    @Override
    protected RequestBody finalizeData()
    {
        DataObject body = DataObject.empty().put("name", getGuild().getName());
        if (shouldUpdate(NAME))
            body.put("name", name);
        if (shouldUpdate(REGION))
            body.put("region", region);
        if (shouldUpdate(AFK_TIMEOUT))
            body.put("afk_timeout", afkTimeout);
        if (shouldUpdate(ICON))
            body.put("icon", icon == null ? null : icon.getEncoding());
        if (shouldUpdate(SPLASH))
            body.put("splash", splash == null ? null : splash.getEncoding());
        if (shouldUpdate(AFK_CHANNEL))
            body.put("afk_channel_id", afkChannel);
        if (shouldUpdate(SYSTEM_CHANNEL))
            body.put("system_channel_id", systemChannel);
        if (shouldUpdate(VERIFICATION_LEVEL))
            body.put("verification_level", verificationLevel);
        if (shouldUpdate(NOTIFICATION_LEVEL))
            body.put("default_message_notifications", notificationLevel);
        if (shouldUpdate(MFA_LEVEL))
            body.put("mfa_level", mfaLevel);
        if (shouldUpdate(EXPLICIT_CONTENT_LEVEL))
            body.put("explicit_content_filter", explicitContentLevel);
        if (shouldUpdate(BANNER))
            body.put("banner", banner == null ? null : banner.getEncoding());
        if (shouldUpdate(VANITY_URL))
            body.put("vanity_code", vanityCode);
        if (shouldUpdate(DESCRIPTION))
            body.put("description", description);

        reset(); //now that we've built our JSON object, reset the manager back to the non-modified state
        return getRequestBody(body);
    }

    @Override
    protected boolean checkPermissions()
    {
        if (!getGuild().getSelfMember().hasPermission(Permission.MANAGE_SERVER))
            throw new InsufficientPermissionException(getGuild(), Permission.MANAGE_SERVER);
        return super.checkPermissions();
    }

    private void checkFeature(String feature)
    {
        if (!getGuild().getFeatures().contains(feature))
            throw new IllegalStateException("This guild doesn't have the " + feature + " feature enabled");
    }
}
