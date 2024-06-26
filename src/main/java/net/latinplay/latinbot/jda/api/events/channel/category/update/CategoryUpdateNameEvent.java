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

package net.latinplay.latinbot.jda.api.events.channel.category.update;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Category;

/**
 * Indicates that the name of a {@link Category Category} was updated.
 *
 * <p>Can be used to retrieve the old name
 *
 * <p>Identifier: {@code name}
 */
public class CategoryUpdateNameEvent extends GenericCategoryUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public CategoryUpdateNameEvent( JDA api, long responseNumber,  Category category,  String oldName)
    {
        super(api, responseNumber, category, oldName, category.getName(), IDENTIFIER);
    }

    /**
     * The previous name for this {@link Category Category}
     *
     * @return The previous name
     */
    
    public String getOldName()
    {
        return getOldValue();
    }

    /**
     * The new name for this {@link Category Category}
     *
     * @return The new name
     */
    
    public String getNewName()
    {
        return getNewValue();
    }

    
    @Override
    public String getOldValue()
    {
        return super.getOldValue();
    }

    
    @Override
    public String getNewValue()
    {
        return super.getNewValue();
    }
}
