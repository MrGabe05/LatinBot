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
package net.latinplay.latinbot.jda.api.exceptions;

import net.latinplay.latinbot.jda.api.Permission;

/**
 * Indicates that the currently logged in account does not meet the specified {@link Permission Permission}
 * from {@link #getPermission()}
 */
public class PermissionException extends RuntimeException
{
    private final Permission permission;

    /**
     * Creates a new PermissionException instance
     *
     * @param reason
     *        The reason for this Exception
     */
    public PermissionException(String reason)
    {
        this(Permission.UNKNOWN, reason);
    }

    /**
     * Creates a new PermissionException instance
     *
     * @param permission
     *        The required {@link Permission Permission}
     */
    protected PermissionException(Permission permission)
    {
        this(permission, "Cannot perform action due to a lack of Permission. Missing permission: " + permission.toString());
    }

    /**
     * Creates a new PermissionException
     *
     * @param permission
     *        The required {@link Permission Permission}
     * @param reason
     *        The reason for this Exception
     */
    protected PermissionException(Permission permission, String reason)
    {
        super(reason);
        this.permission = permission;
    }

    /**
     * The {@link Permission Permission} that is required for the operation
     *
     * <p><b>If this is a {@link HierarchyException HierarchyException}
     * this will always be {@link Permission#UNKNOWN Permission.UNKNOWN}!</b>
     *
     * @return The required {@link Permission Permission}
     */
    public Permission getPermission()
    {
        return permission;
    }
}
