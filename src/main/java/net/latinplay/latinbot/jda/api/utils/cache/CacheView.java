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

package net.latinplay.latinbot.jda.api.utils.cache;

import net.latinplay.latinbot.jda.api.entities.ISnowflake;
import net.latinplay.latinbot.jda.api.entities.Member;
import net.latinplay.latinbot.jda.api.utils.ClosableIterator;
import net.latinplay.latinbot.jda.internal.utils.Checks;
import net.latinplay.latinbot.jda.internal.utils.cache.AbstractCacheView;
import net.latinplay.latinbot.jda.internal.utils.cache.ShardCacheViewImpl;
import net.latinplay.latinbot.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;
import net.latinplay.latinbot.jda.internal.utils.cache.UnifiedCacheViewImpl;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Read-only view on internal JDA cache of items.
 * <br>This can be useful to check information such as size without creating
 * an immutable snapshot first.
 *
 * <h2>Memory Efficient Usage</h2>
 * The {@link #forEach(Consumer)} method can be used to avoid creating a snapshot
 * of the backing data store, it is implemented by first acquiring a read-lock and then iterating the code.
 * The enhanced-for-loop uses the {@link #iterator()} which has to first create a snapshot to avoid
 * concurrent modifications. Alternatively the {@link #lockedIterator()} can be used to acquire an iterator
 * which holds a read-lock on the data store and thus prohibits concurrent modifications, for more details
 * read the documentation of {@link ClosableIterator}. Streams from {@link #stream()}/{@link #parallelStream()}
 * both use {@link #iterator()} with a snapshot of the backing data store to avoid concurrent modifications.
 * <br>Using {@link #getElementsByName(String)} is more efficient than {@link #asList()} as it uses {@link #forEach(Consumer)}
 * for pattern matching and thus does not need to create a snapshot of the entire data store like {@link #asList()} does.
 * <br>Both {@link #size()} and {@link #isEmpty()} are atomic operations.
 *
 * <p>Note that making a copy is a requirement if a specific order is desired. If using {@link #lockedIterator()}
 * the order is not guaranteed as it directly iterates the backing cache.
 * Using {@link #forEach(Consumer)} on a {@link SortedSnowflakeCacheView} will copy the cache in order to sort
 * it, use {@link SortedSnowflakeCacheView#forEachUnordered(Consumer)} to avoid this overhead.
 * The backing cache is stored using an un-ordered hash map.
 *
 * @param  <T>
 *         The cache type
 */
public interface CacheView<T> extends Iterable<T>
{
    /**
     * Creates an immutable snapshot of the current cache state.
     * <br>This will copy all elements contained in this cache into a list.
     * <br>This will be sorted for a {@link SortedSnowflakeCacheViewImpl SortedSnowflakeCacheView}.
     *
     * @return Immutable list of cached elements
     */
    
    List<T> asList();

    /**
     * Creates an immutable snapshot of the current cache state.
     * <br>This will copy all elements contained in this cache into a set.
     *
     * @return Immutable set of cached elements
     */
    
    Set<T> asSet();

    /**
     * Returns an iterator with direct access to the underlying data store.
     * This iterator does not support removing elements.
     * <br>After usage this iterator should be closed to allow modifications by the library internals.
     *
     * <p><b>Note: Order is not preserved in this iterator to be more efficient,
     * if order is desired use {@link #iterator()} instead!</b>
     *
     * @return {@link ClosableIterator} holding a read-lock on the data structure.
     *
     * @since  4.0.0
     */
    
    ClosableIterator<T> lockedIterator();

    /**
     * Behavior similar to {@link #forEach(Consumer)} but does not preserve order.
     * <br>This will not copy the data store as sorting is not needed.
     *
     * @param  action
     *         The action to perform
     *
     * @throws NullPointerException
     *         If provided with null
     *
     * @since  4.0.0
     */
    default void forEachUnordered( final Consumer<? super T> action)
    {
        forEach(action);
    }

    /**
     * Creates an unordered sequenced stream of the elements in this cache.
     * <br>This does not copy the backing cache prior to consumption unlike {@link #stream()}.
     *
     * <p>The stream will be closed once this method returns and cannot be used anymore.
     *
     * <h2>Example</h2>
     * <code>
     * {@literal CacheView<User>} view = jda.getUserCache();<br>
     * long shortNames = view.applyStream(stream {@literal ->} stream.filter(it {@literal ->} it.getName().length() {@literal <} 4).count());<br>
     * System.out.println(shortNames + " users with less than 4 characters in their name");
     * </code>
     *
     * @param  action
     *         The action to perform on the stream
     * @param  <R>
     *         The return type after performing the specified action
     *
     * @throws IllegalArgumentException
     *         If the action is null
     *
     * @return The resulting value after the action was performed
     *
     * @since  4.0.0
     *
     * @see    #acceptStream(Consumer)
     */
    
    default <R> R applyStream( Function<? super Stream<T>, ? extends R> action)
    {
        Checks.notNull(action, "Action");
        try (ClosableIterator<T> it = lockedIterator())
        {
            Spliterator<T> spliterator = Spliterators.spliterator(it, size(), Spliterator.IMMUTABLE | Spliterator.NONNULL);
            Stream<T> stream = StreamSupport.stream(spliterator, false);
            return action.apply(stream);
        }
    }

    /**
     * Creates an unordered sequenced stream of the elements in this cache.
     * <br>This does not copy the backing cache prior to consumption unlike {@link #stream()}.
     *
     * <p>The stream will be closed once this method returns and cannot be used anymore.
     *
     * <h2>Example</h2>
     * <code>
     * {@literal CacheView<TextChannel>} view = guild.getTextChannelCache();<br>
     * view.acceptStream(stream {@literal ->} stream.filter(it {@literal ->} it.isNSFW()).forEach(it {@literal ->} it.sendMessage("lewd").queue()));
     * </code>
     *
     * @param  action
     *         The action to perform on the stream
     *
     * @throws IllegalArgumentException
     *         If the action is null
     *
     * @since  4.0.0
     *
     * @see    #applyStream(Function)
     */
    default void acceptStream( Consumer<? super Stream<T>> action)
    {
        Checks.notNull(action, "Action");
        try (ClosableIterator<T> it = lockedIterator())
        {
            Spliterator<T> spliterator = Spliterators.spliterator(it, size(), Spliterator.IMMUTABLE | Spliterator.NONNULL);
            Stream<T> stream = StreamSupport.stream(spliterator, false);
            action.accept(stream);
        }
    }

    /**
     * The current size of this cache
     * <br>This is a {@code long} as it may be a projected view of multiple caches
     * (See {@link CacheView#all(java.util.function.Supplier)})
     *
     * <p>This is more efficient than creating a list or set snapshot first as it checks the size
     * of the internal cache directly.
     *
     * @return The current size of this cache
     */
    long size();

    /**
     * Whether the cache is empty
     *
     * <p>This is more efficient than creating a list or set snapshot first as it checks the size
     * of the internal cache directly.
     * <br>On a projected cache view this will simply look through all projected views and return false
     * the moment it finds one that is not empty.
     *
     * @return True, if this cache is currently empty
     */
    boolean isEmpty();

    /**
     * Creates an immutable list of all elements matching the given name.
     * <br>For a {@link MemberCacheView MemberCacheView} this will
     * check the {@link Member#getEffectiveName() Effective Name} of the cached members.
     *
     * @param  name
     *         The name to check
     * @param  ignoreCase
     *         Whether to ignore case when comparing names
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is {@code null}
     *
     * @return Immutable list of elements with the given name
     */
    
    List<T> getElementsByName( String name, boolean ignoreCase);

    /**
     * Creates an immutable list of all elements matching the given name.
     * <br>For a {@link MemberCacheView MemberCacheView} this will
     * check the {@link Member#getEffectiveName() Effective Name} of the cached members.
     *
     * @param  name
     *         The name to check
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is {@code null}
     *
     * @return Immutable list of elements with the given name
     */
    
    default List<T> getElementsByName( String name)
    {
        return getElementsByName(name, false);
    }

    /**
     * Creates a {@link java.util.stream.Stream Stream} of all cached elements.
     * <br>This will be sorted for a {@link SortedSnowflakeCacheViewImpl SortedSnowflakeCacheView}.
     *
     * @return Stream of elements
     */
    
    Stream<T> stream();

    /**
     * Creates a parallel {@link java.util.stream.Stream Stream} of all cached elements.
     * <br>This will be sorted for a {@link SortedSnowflakeCacheViewImpl SortedSnowflakeCacheView}.
     *
     * @return Parallel Stream of elements
     */
    
    Stream<T> parallelStream();

    /**
     * Collects all cached entities into a single Collection using the provided
     * {@link java.util.stream.Collector Collector}.
     * Shortcut for {@code stream().collect(collector)}.
     *
     * @param  collector
     *         The collector used to collect the elements
     *
     * @param  <R>
     *         The output type
     * @param  <A>
     *         The accumulator type
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided collector is {@code null}
     *
     * @return Resulting collections
     */
    
    default <R, A> R collect( Collector<? super T, A, R> collector)
    {
        return stream().collect(collector);
    }

    /**
     * Creates a combined {@link CacheView CacheView}
     * for all provided CacheView implementations. This allows to combine cache of multiple
     * JDA sessions or Guilds.
     *
     * @param  cacheViews
     *         Collection of {@link CacheView CacheView} implementations
     *
     * @param  <E>
     *         The target type of the projection
     *
     * @return Combined CacheView spanning over all provided implementation instances
     */
    
    static <E> CacheView<E> all( Collection<? extends CacheView<E>> cacheViews)
    {
        Checks.noneNull(cacheViews, "Collection");
        return new UnifiedCacheViewImpl<>(cacheViews::stream);
    }

    /**
     * Creates a combined {@link CacheView CacheView}
     * for all provided CacheView implementations. This allows to combine cache of multiple
     * JDA sessions or Guilds.
     *
     * @param  generator
     *         Stream generator of {@link CacheView CacheView} implementations
     *
     * @param  <E>
     *         The target type of the projection
     *
     * @return Combined CacheView spanning over all provided implementation instances
     */
    
    static <E> CacheView<E> all( Supplier<? extends Stream<? extends CacheView<E>>> generator)
    {
        Checks.notNull(generator, "Generator");
        return new UnifiedCacheViewImpl<>(generator);
    }

    /**
     * Creates a combined {@link ShardCacheView ShardCacheView}
     * for all provided ShardCacheView implementations.
     *
     * @param  cacheViews
     *         Collection of {@link ShardCacheView ShardCacheView} implementations
     *
     * @return Combined ShardCacheView spanning over all provided implementation instances
     */
    
    static ShardCacheView allShards( Collection<ShardCacheView> cacheViews)
    {
        Checks.noneNull(cacheViews, "Collection");
        return new ShardCacheViewImpl.UnifiedShardCacheViewImpl(cacheViews::stream);
    }

    /**
     * Creates a combined {@link ShardCacheView ShardCacheView}
     * for all provided ShardCacheView implementations.
     *
     * @param  generator
     *         Stream generator of {@link ShardCacheView ShardCacheView} implementations
     *
     * @return Combined ShardCacheView spanning over all provided implementation instances
     */
    
    static ShardCacheView allShards( Supplier<? extends Stream<? extends ShardCacheView>> generator)
    {
        Checks.notNull(generator, "Generator");
        return new ShardCacheViewImpl.UnifiedShardCacheViewImpl(generator);
    }

    /**
     * Creates a combined {@link SnowflakeCacheView SnowflakeCacheView}
     * for all provided SnowflakeCacheView implementations.
     * <br>This allows to combine cache of multiple JDA sessions or Guilds.
     *
     * @param  cacheViews
     *         Collection of {@link SnowflakeCacheView SnowflakeCacheView} implementations
     *
     * @param  <E>
     *         The target type of the chain
     *
     * @return Combined SnowflakeCacheView spanning over all provided implementation instances
     */
    
    static <E extends ISnowflake> SnowflakeCacheView<E> allSnowflakes( Collection<? extends SnowflakeCacheView<E>> cacheViews)
    {
        Checks.noneNull(cacheViews, "Collection");
        return new UnifiedCacheViewImpl.UnifiedSnowflakeCacheView<>(cacheViews::stream);
    }

    /**
     * Creates a combined {@link SnowflakeCacheView SnowflakeCacheView}
     * for all provided SnowflakeCacheView implementations.
     * <br>This allows to combine cache of multiple JDA sessions or Guilds.
     *
     * @param  generator
     *         Stream generator of {@link SnowflakeCacheView SnowflakeCacheView} implementations
     *
     * @param  <E>
     *         The target type of the chain
     *
     * @return Combined SnowflakeCacheView spanning over all provided implementation instances
     */
    
    static <E extends ISnowflake> SnowflakeCacheView<E> allSnowflakes( Supplier<? extends Stream<? extends SnowflakeCacheView<E>>> generator)
    {
        Checks.notNull(generator, "Generator");
        return new UnifiedCacheViewImpl.UnifiedSnowflakeCacheView<>(generator);
    }

    /**
     * Creates a combined {@link UnifiedMemberCacheView UnifiedMemberCacheView}
     * for all provided MemberCacheView implementations.
     * <br>This allows to combine cache of multiple JDA sessions or Guilds.
     *
     * @param  cacheViews
     *         Collection of {@link MemberCacheView MemberCacheView} instances
     *
     * @return Combined MemberCacheView spanning over all provided instances
     */
    
    static UnifiedMemberCacheView allMembers( Collection<? extends MemberCacheView> cacheViews)
    {
        Checks.noneNull(cacheViews, "Collection");
        return new UnifiedCacheViewImpl.UnifiedMemberCacheViewImpl(cacheViews::stream);
    }

    /**
     * Creates a combined {@link UnifiedMemberCacheView UnifiedMemberCacheView}
     * for all provided MemberCacheView implementations.
     * <br>This allows to combine cache of multiple JDA sessions or Guilds.
     *
     * @param  generator
     *         Stream generator of {@link MemberCacheView MemberCacheView} instances
     *
     * @return Combined MemberCacheView spanning over all provided instances
     */
    
    static UnifiedMemberCacheView allMembers( Supplier<? extends Stream<? extends MemberCacheView>> generator)
    {
        Checks.notNull(generator, "Generator");
        return new UnifiedCacheViewImpl.UnifiedMemberCacheViewImpl(generator);
    }

    class SimpleCacheView<T> extends AbstractCacheView<T>
    {
        public SimpleCacheView( Class<T> type,  Function<T, String> nameMapper)
        {
            super(type, nameMapper);
        }
    }
}
