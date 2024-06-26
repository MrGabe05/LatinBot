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

package net.latinplay.latinbot.jda.api.utils;

import net.latinplay.latinbot.jda.api.requests.restaction.MessageAction;

/**
 * Options that can be applied to attachments in {@link MessageAction}.
 */
public enum AttachmentOption
{
    /** Marks an image attachment as a spoiler by prefixing the name with {@code SPOILER_} */
    SPOILER,
    NONE
}
