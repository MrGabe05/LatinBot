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

package net.latinplay.latinbot.jda.internal.requests.restaction.order;

import net.latinplay.latinbot.jda.api.entities.Category;
import net.latinplay.latinbot.jda.api.entities.GuildChannel;
import net.latinplay.latinbot.jda.api.requests.restaction.order.CategoryOrderAction;
import net.latinplay.latinbot.jda.internal.utils.Checks;

import java.util.Collection;
import java.util.stream.Collectors;

public class CategoryOrderActionImpl
    extends ChannelOrderActionImpl
    implements CategoryOrderAction
{
    protected final Category category;

    /**
     * Creates a new CategoryOrderAction for the specified {@link Category Category}
     *
     * @param  category
     *         The target {@link Category Category}
     *         which the new CategoryOrderAction will order channels from.
     * @param  bucket
     *         The sorting bucket
     */
    public CategoryOrderActionImpl(Category category, int bucket)
    {
        super(category.getGuild(), bucket, getChannelsOfType(category, bucket));
        this.category = category;
    }


    @Override
    public Category getCategory()
    {
        return category;
    }

    @Override
    protected void validateInput(GuildChannel entity)
    {
        Checks.notNull(entity, "Provided channel");
        Checks.check(getCategory().equals(entity.getParent()), "Provided channel's Category is not this Category!");
        Checks.check(orderList.contains(entity), "Provided channel is not in the list of orderable channels!");
    }


    private static Collection<GuildChannel> getChannelsOfType(Category category, int bucket)
    {
        Checks.notNull(category, "Category");
        return getChannelsOfType(category.getGuild(), bucket).stream()
             .filter(it -> category.equals(it.getParent()))
             .sorted()
             .collect(Collectors.toList());
    }
}
