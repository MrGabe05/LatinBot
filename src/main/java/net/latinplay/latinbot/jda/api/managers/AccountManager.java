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

package net.latinplay.latinbot.jda.api.managers;

import net.latinplay.latinbot.jda.api.AccountType;
import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.Icon;
import net.latinplay.latinbot.jda.api.entities.SelfUser;
import net.latinplay.latinbot.jda.api.exceptions.AccountTypeException;

/**
 * Manager providing functionality to update one or more fields for the logged in account.
 *
 * <p><b>Example</b>
 * <pre>{@code
 * manager.setName("Minn")
 *        .setAvatar(null)
 *        .queue();
 * manager.reset(AccountManager.NAME | AccountManager.AVATAR)
 *        .setName("DV8FromTheWorld")
 *        .setAvatar(icon)
 *        .queue();
 * }</pre>
 *
 * @see JDA#getSelfUser() JDA.getSelfUser()
 * @see SelfUser#getManager()
 */
public interface AccountManager extends Manager<AccountManager>
{
    /** Used to reset the name field */
    long NAME = 0x1;
    /** Used to reset the avatar field */
    long AVATAR = 0x2;
    /** Used to reset the email field */
    long EMAIL = 0x4;
    /** Used to reset the password field */
    long PASSWORD = 0x8;

    /**
     * The {@link SelfUser SelfUser} that will be
     * modified by this AccountManager.
     * <br>This represents the currently logged in account.
     *
     * @return The corresponding SelfUser
     */
    
    SelfUser getSelfUser();

    /**
     * Resets the fields specified by the provided bit-flag pattern.
     * You can specify a combination by using a bitwise OR concat of the flag constants.
     * <br>Example: {@code manager.reset(AccountManager.NAME | AccountManager.AVATAR);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #NAME}</li>
     *     <li>{@link #AVATAR}</li>
     *     <li>{@link #EMAIL}</li>
     *     <li>{@link #PASSWORD}</li>
     * </ul>
     *
     * @param  fields
     *         Integer value containing the flags to reset.
     *
     * @return AccountManager for chaining convenience
     */
    
    @Override
    
    AccountManager reset(long fields);

    /**
     * Resets the fields specified by the provided bit-flag patterns.
     * You can specify a combination by using a bitwise OR concat of the flag constants.
     * <br>Example: {@code manager.reset(AccountManager.NAME, AccountManager.AVATAR);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #NAME}</li>
     *     <li>{@link #AVATAR}</li>
     *     <li>{@link #EMAIL}</li>
     *     <li>{@link #PASSWORD}</li>
     * </ul>
     *
     * @param  fields
     *         Integer values containing the flags to reset.
     *
     * @return AccountManager for chaining convenience
     */
    
    @Override
    
    AccountManager reset(long... fields);

    /**
     * Sets the username for the currently logged in account
     *
     * <p><b>Client-Accounts ({@link AccountType#CLIENT AccountType.CLIENT}) require the
     * current password to be updated. See {@link #setName(String, String)}</b>
     *
     * @param  name
     *         The new username
     *
     * @throws IllegalArgumentException
     *         If the provided name is:
     *         <ul>
     *             <li>Equal to {@code null}</li>
     *             <li>Less than {@code 2} or more than {@code 32} characters in length</li>
     *         </ul>
     *
     * @return AccountManager for chaining convenience
     */
    
    
    default AccountManager setName( String name)
    {
        return setName(name, null);
    }

    /**
     * Sets the username for the currently logged in account
     *
     * @param  name
     *         The new username
     * @param  currentPassword
     *         The current password for the represented account,
     *         this is only required for {@link AccountType#CLIENT AccountType.CLIENT}
     *
     * @throws IllegalArgumentException
     *         If this is action is performed on an account with the type {@link AccountType#CLIENT CLIENT}
     *         and the provided password is {@code null} or empty
     *         <br>If the provided name is:
     *         <ul>
     *             <li>Equal to {@code null}</li>
     *             <li>Less than 2 or more than 32 characters in length</li>
     *         </ul>
     *
     * @return AccountManager for chaining convenience
     */
    
    
    AccountManager setName( String name,  String currentPassword);

    /**
     * Sets the avatar for the currently logged in account
     *
     * <p><b>Client-Accounts ({@link AccountType#CLIENT AccountType.CLIENT}) require the
     * current password to be updated. See {@link #setAvatar(Icon, String) #setAvatar(Icon, String)}</b>
     *
     * @param  avatar
     *         An {@link Icon Icon} instance representing
     *         the new Avatar for the current account, {@code null} to reset the avatar to the default avatar.
     *
     * @return AccountManager for chaining convenience
     */
    
    
    default AccountManager setAvatar( Icon avatar)
    {
        return setAvatar(avatar, null);
    }

    /**
     * Sets the avatar for the currently logged in account
     *
     * @param  avatar
     *         An {@link Icon Icon} instance representing
     *         the new Avatar for the current account, {@code null} to reset the avatar to the default avatar.
     * @param  currentPassword
     *         The current password for the represented account,
     *         this is only required for {@link AccountType#CLIENT AccountType.CLIENT}
     *
     * @throws IllegalArgumentException
     *         If the provided {@code currentPassword} is {@code null} or empty and the currently
     *         logged in account is from {@link AccountType#CLIENT AccountType.CLIENT}
     *
     * @return AccountManager for chaining convenience
     */
    
    
    AccountManager setAvatar( Icon avatar,  String currentPassword);

    /**
     * Sets the email for the currently logged in client account.
     *
     * @param  email
     *         The new email
     * @param  currentPassword
     *         The <b>valid</b> current password for the represented account
     *
     * @throws AccountTypeException
     *         If the currently logged in account is not from {@link AccountType#CLIENT}
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided {@code currentPassword} or the provided {@code email} is {@code null} or empty
     *             <li>If the provided {@code email} is not valid.</li>
     *         </ul>
     *
     * @return AccountManager for chaining convenience
     */
    
    
    AccountManager setEmail( String email,  String currentPassword);

    /**
     * Sets the password for the currently logged in client account.
     * <br>If the new password is equal to the current password this does nothing.
     *
     * @param  newPassword
     *         The new password for the currently logged in account
     * @param  currentPassword
     *         The <b>valid</b> current password for the represented account
     *
     * @throws AccountTypeException
     *         If the currently logged in account is not from {@link AccountType#CLIENT}
     * @throws IllegalArgumentException
     *         If any of the provided passwords are {@code null} or empty
     *
     * @return AccountManager for chaining convenience
     */
    
    
    AccountManager setPassword( String newPassword,  String currentPassword);
}
