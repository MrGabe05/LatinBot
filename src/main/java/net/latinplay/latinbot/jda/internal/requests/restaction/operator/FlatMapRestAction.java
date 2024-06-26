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

package net.latinplay.latinbot.jda.internal.requests.restaction.operator;

import net.latinplay.latinbot.jda.api.exceptions.RateLimitedException;
import net.latinplay.latinbot.jda.api.requests.RestAction;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FlatMapRestAction<I, O> extends RestActionOperator<I, O>
{
    private final Function<? super I, ? extends RestAction<O>> function;
    private final Predicate<? super I> condition;

    public FlatMapRestAction(RestAction<I> action, Predicate<? super I> condition,
                             Function<? super I, ? extends RestAction<O>> function)
    {
        super(action);
        this.function = function;
        this.condition = condition;
    }

    @Override
    public void queue( Consumer<? super O> success,  Consumer<? super Throwable> failure)
    {
        Consumer<? super Throwable> onFailure = contextWrap(failure);
        action.queue((result) -> {
            if (condition != null && !condition.test(result))
                return;
            RestAction<O> then = function.apply(result);
            if (then == null)
                doFailure(onFailure, new IllegalStateException("FlatMap operand is null"));
            else
                then.queue(success, onFailure);
        }, onFailure);
    }

    @Override
    public O complete(boolean shouldQueue) throws RateLimitedException
    {
        return function.apply(action.complete(shouldQueue)).complete(shouldQueue);
    }


    @Override
    public CompletableFuture<O> submit(boolean shouldQueue)
    {
        return action.submit(shouldQueue)
                .thenCompose((result) -> function.apply(result).submit(shouldQueue));
    }
}
