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

package net.latinplay.latinbot.jda.internal.utils.cache;

import net.latinplay.latinbot.jda.api.entities.ISnowflake;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.entities.Role;
import net.latinplay.latinbot.jda.api.utils.ClosableIterator;
import net.latinplay.latinbot.jda.api.utils.cache.CacheView;
import net.latinplay.latinbot.jda.api.utils.cache.MemberCacheView;
import net.latinplay.latinbot.jda.api.utils.cache.SnowflakeCacheView;
import net.latinplay.latinbot.jda.api.utils.cache.UnifiedMemberCacheView;
import net.latinplay.latinbot.jda.internal.utils.ChainedClosableIterator;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnifiedCacheViewImpl<T, E extends CacheView<T>> implements CacheView<T>
{
    protected final Supplier<? extends Stream<? extends E>> generator;

    public UnifiedCacheViewImpl(Supplier<? extends Stream<? extends E>> generator)
    {
        this.generator = generator;
    }

    @Override
    public long size()
    {
        return distinctStream().mapToLong(CacheView::size).sum();
    }

    @Override
    public boolean isEmpty()
    {
        return distinctStream().allMatch(CacheView::isEmpty);
    }

    @Override
    public void forEach(Consumer<? super T> action)
    {
        Objects.requireNonNull(action);
        try (ClosableIterator<T> it = lockedIterator())
        {
            while (it.hasNext())
                action.accept(it.next());
        }
    }


    @Override
    public List<T> asList()
    {
        List<T> list = new LinkedList<>();
        forEach(list::add);
        return Collections.unmodifiableList(list);
    }


    @Override
    public Set<T> asSet()
    {
        try (ChainedClosableIterator<T> it = lockedIterator())
        {
            //because the iterator needs to retain elements to avoid duplicates,
            // we can use the resulting HashSet as our return value!
            while (it.hasNext()) it.next();
            return Collections.unmodifiableSet(it.getItems());
        }
    }


    @Override
    public ChainedClosableIterator<T> lockedIterator()
    {
        Iterator<? extends E> gen = generator.get().iterator();
        return new ChainedClosableIterator<>(gen);
    }


    @Override
    public List<T> getElementsByName( String name, boolean ignoreCase)
    {
        return Collections.unmodifiableList(distinctStream()
                .flatMap(view -> view.getElementsByName(name, ignoreCase).stream())
                .distinct()
                .collect(Collectors.toList()));
    }


    @Override
    public Stream<T> stream()
    {
        return distinctStream().flatMap(CacheView::stream).distinct();
    }


    @Override
    public Stream<T> parallelStream()
    {
        return distinctStream().flatMap(CacheView::parallelStream).distinct();
    }


    @Override
    public Iterator<T> iterator()
    {
        return stream().iterator();
    }

    protected Stream<? extends E> distinctStream()
    {
        return generator.get().distinct();
    }

    public static class UnifiedSnowflakeCacheView<T extends ISnowflake>
        extends UnifiedCacheViewImpl<T, SnowflakeCacheView<T>> implements SnowflakeCacheView<T>
    {
        public UnifiedSnowflakeCacheView(Supplier<? extends Stream<? extends SnowflakeCacheView<T>>> generator)
        {
            super(generator);
        }

        @Override
        public T getElementById(long id)
        {
            return generator.get()
                .map(view -> view.getElementById(id))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
        }
    }

    public static class UnifiedMemberCacheViewImpl
        extends UnifiedCacheViewImpl<Member, MemberCacheView> implements UnifiedMemberCacheView
    {

        public UnifiedMemberCacheViewImpl(Supplier<? extends Stream<? extends MemberCacheView>> generator)
        {
            super(generator);
        }


        @Override
        public List<Member> getElementsById(long id)
        {
            return Collections.unmodifiableList(distinctStream()
                .map(view -> view.getElementById(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }


        @Override
        public List<Member> getElementsByUsername( String name, boolean ignoreCase)
        {
            return Collections.unmodifiableList(distinctStream()
                .flatMap(view -> view.getElementsByUsername(name, ignoreCase).stream())
                .collect(Collectors.toList()));
        }


        @Override
        public List<Member> getElementsByNickname( String name, boolean ignoreCase)
        {
            return Collections.unmodifiableList(distinctStream()
                .flatMap(view -> view.getElementsByNickname(name, ignoreCase).stream())
                .collect(Collectors.toList()));
        }


        @Override
        public List<Member> getElementsWithRoles( Role... roles)
        {
            return Collections.unmodifiableList(distinctStream()
                .flatMap(view -> view.getElementsWithRoles(roles).stream())
                .collect(Collectors.toList()));
        }


        @Override
        public List<Member> getElementsWithRoles( Collection<Role> roles)
        {
            return Collections.unmodifiableList(distinctStream()
                .flatMap(view -> view.getElementsWithRoles(roles).stream())
                .collect(Collectors.toList()));
        }
    }
}
