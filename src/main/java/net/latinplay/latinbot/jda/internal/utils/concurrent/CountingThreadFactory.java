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

package net.latinplay.latinbot.jda.internal.utils.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class CountingThreadFactory implements ThreadFactory
{
    private final Supplier<String> identifier;
    private final AtomicLong count = new AtomicLong(1);

    public CountingThreadFactory( Supplier<String> identifier,  String specifier)
    {
        this.identifier = () -> identifier.get() + " " + specifier;
    }


    @Override
    public Thread newThread( Runnable r)
    {
        final Thread thread = new Thread(r, identifier.get() + "-Worker " + count.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }
}
