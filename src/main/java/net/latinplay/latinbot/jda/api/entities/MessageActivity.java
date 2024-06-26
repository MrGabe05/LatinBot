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
package net.latinplay.latinbot.jda.api.entities;

/**
 * Represents a {@link Message} activity.
 *
 * @see Message#getActivity()
 */
public class MessageActivity
{
    private final ActivityType type;
    private final String partyId;
    private final Application application;

    public MessageActivity(ActivityType type, String partyId, Application application)
    {
        this.type = type;
        this.partyId = partyId;
        this.application = application;
    }

    /**
     * The current {@link MessageActivity.ActivityType ActivityType}
     *
     * @return the type of the activity.
     */
    
    public ActivityType getType()
    {
        return type;
    }

    /**
     * The party id discord uses internally, it may be {@code null}.
     *
     * @return Possibly-null party id.
     */
    
    public String getPartyId()
    {
        return partyId;
    }

    /**
     * The {@link MessageActivity.Application Application} this {@link MessageActivity MessageActivity} may have.
     *
     * @return A possibly-null {@link MessageActivity.Application}.
     */
    
    public MessageActivity.Application getApplication()
    {
        return application;
    }

    /**
     * Represents the {@link MessageActivity.Application Application} of a
     * {@link MessageActivity MessageActivity} if it's set.
     *
     * @see <a href="https://discordapp.com/api/v7/games" target="_blank">https://discordapp.com/api/v7/games</a>
     */
    public static class Application implements ISnowflake
    {
        private final String name;
        private final String description;
        private final String iconId;
        private final String coverId;
        private final long id;

        public Application(String name, String description, String iconId, String coverId, long id)
        {
            this.name = name;
            this.description = description;
            this.iconId = iconId;
            this.coverId = coverId;
            this.id = id;
        }

        /**
         * The name of this {@link MessageActivity.Application}.
         *
         * @return the applications name.
         */
        
        public String getName()
        {
            return name;
        }

        /**
         * A short description of this {@link MessageActivity.Application}.
         *
         * @return the applications description.
         */
        
        public String getDescription()
        {
            return description;
        }

        /**
         * The icon id of this {@link MessageActivity.Application}.
         *
         * @return the applications icon id.
         */
        
        public String getIconId()
        {
            return iconId;
        }

        /**
         * The url of the icon image for this application.
         *
         * @return the url of the icon
         */
        
        public String getIconUrl()
        {
            return iconId == null ? null : "https://cdn.discordapp.com/application/" + getId() + "/" + iconId + ".png";
        }

        /**
         * The cover aka splash id of this {@link MessageActivity.Application}.
         *
         * @return the applications cover image/id.
         */
        
        public String getCoverId()
        {
            return coverId;
        }

        /**
         * The url of the cover image for this application.
         *
         * @return the url of the cover/splash
         */
        
        public String getCoverUrl()
        {
            return coverId == null ? null : "https://cdn.discordapp.com/application/" + getId() + "/" + coverId + ".png";
        }

        @Override
        public long getIdLong()
        {
            return id;
        }
    }

    /**
     * An enum representing {@link MessageActivity MessageActivity} types.
     */
    public enum ActivityType
    {
        /**
         * The {@link MessageActivity MessageActivity} type used for inviting people to join a game.
         */
        JOIN(1),
        /**
         * The {@link MessageActivity MessageActivity} type used for inviting people to spectate a game.
         */
        SPECTATE(2),
        /**
         * The {@link MessageActivity MessageActivity} type used for inviting people to listen (Spotify) together.
         */
        LISTENING(3),
        /**
         * The {@link MessageActivity MessageActivity} type used for requesting to join a game.
         */
        JOIN_REQUEST(5),
        /**
         * Represents any unknown or unsupported {@link MessageActivity MessageActivity} types.
         */
        UNKNOWN(-1);

        private final int id;

        ActivityType(int id)
        {
            this.id = id;
        }

        /**
         * The id of this {@link MessageActivity.ActivityType ActivityType}.
         *
         * @return the id of the type.
         */
        public int getId()
        {
            return id;
        }

        
        public static ActivityType fromId(int id)
        {
            for (ActivityType activityType : values())
            {
                if (activityType.id == id)
                    return activityType;
            }
            return UNKNOWN;
        }
    }
}
