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

package net.latinplay.latinbot.jda.api.events.self;

import net.latinplay.latinbot.jda.api.JDA;

/**
 * Indicates that the verification state of the current user changed. (client-only)
 *
 * <p>Can be used to retrieve the old verification state.
 *
 * <p>Identifier: {@code verified}
 */
public class SelfUpdateVerifiedEvent extends GenericSelfUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "verified";

    public SelfUpdateVerifiedEvent( JDA api, long responseNumber, boolean wasVerified)
    {
        super(api, responseNumber, wasVerified, !wasVerified, IDENTIFIER);
    }

    /**
     * Whether the account was verified
     *
     * @return True, if this account was previously verified
     */
    public boolean wasVerified()
    {
        return getOldValue();
    }


    @Override
    public Boolean getOldValue()
    {
        return super.getOldValue();
    }


    @Override
    public Boolean getNewValue()
    {
        return super.getNewValue();
    }
}
