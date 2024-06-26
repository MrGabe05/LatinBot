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

package net.latinplay.latinbot.jda.api.requests.restaction;

import net.latinplay.latinbot.jda.api.AccountType;
import net.latinplay.latinbot.jda.api.EmbedBuilder;
import net.latinplay.latinbot.jda.api.MessageBuilder;
import net.latinplay.latinbot.jda.api.Permission;
import net.latinplay.latinbot.jda.api.entities.Message;
import net.latinplay.latinbot.jda.api.entities.MessageChannel;
import net.latinplay.latinbot.jda.api.entities.MessageEmbed;
import net.latinplay.latinbot.jda.api.entities.SelfUser;
import net.latinplay.latinbot.jda.api.exceptions.InsufficientPermissionException;
import net.latinplay.latinbot.jda.api.requests.RestAction;
import net.latinplay.latinbot.jda.api.utils.AttachmentOption;
import net.latinplay.latinbot.jda.internal.utils.Checks;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Extension of a default {@link RestAction RestAction}
 * that allows setting message information before sending!
 *
 * <p>This is available as return type of all sendMessage/sendFile methods in {@link MessageChannel MessageChannel}
 * or by using {@link MessageBuilder#sendTo(MessageChannel) MessageBuilder.sendTo(MessageChannel)}
 *
 * <p>When updating a Message, unset fields will be ignored by default. To override existing fields with no value (remove content)
 * you can use {@link #override(boolean) override(true)}. Setting this to {@code true} will cause all fields to be considered
 * and will override the Message entirely causing unset values to be removed from that message.
 * <br>This can be used to remove existing embeds from a message:
 * <br>{@code message.editMessage("This message had an embed").override(true).queue()}
 *
 * <p><u>When this RestAction has been executed all provided files will be closed.
 * If you decide not to execute this action, you should call {@link #clearFiles()} to free resources.</u>
 * <br>Note that the garbage collector also frees opened file streams when it finalizes the stream object.
 *
 * <h1>Example</h1>
 * <pre><code>
 * {@literal @Override}
 *  public void onMessageReceived(MessageReceivedEvent event)
 *  {
 *      MessageChannel channel = event.getChannel();
 *      channel.sendMessage("This has an embed with an image!")
 *             .addFile(new File("dog.png"))
 *             .embed(new EmbedBuilder()
 *                 .setImage("attachment://dog.png")
 *                 .build())
 *             .queue(); // this actually sends the information to discord
 *  }
 * </code></pre>
 *
 * @since  3.4.0
 *
 * @see    Message#editMessage(Message)
 * @see    Message#editMessage(CharSequence)
 * @see    Message#editMessage(MessageEmbed)
 * @see    Message#editMessageFormat(String, Object...)
 * @see    MessageChannel#sendMessage(Message)
 * @see    MessageChannel#sendMessage(CharSequence)
 * @see    MessageChannel#sendMessage(MessageEmbed)
 * @see    MessageChannel#sendMessageFormat(String, Object...)
 * @see    MessageChannel#sendFile(File, AttachmentOption...)
 * @see    MessageChannel#sendFile(File, String, AttachmentOption...)
 * @see    MessageChannel#sendFile(InputStream, String, AttachmentOption...)
 * @see    MessageChannel#sendFile(byte[], String, AttachmentOption...)
 */
public interface MessageAction extends RestAction<Message>, Appendable
{
    
    @Override
    MessageAction setCheck(BooleanSupplier checks);

    /**
     * The target {@link MessageChannel} for this message
     *
     * @return The target channel
     */
    
    MessageChannel getChannel();

    /**
     * Whether this MessageAction has no values set.
     * <br>Trying to execute with {@code isEmpty() == true} will result in an {@link java.lang.IllegalStateException IllegalStateException}!
     *
     * <p><b>This does not check for files!</b>
     *
     * @return True, if no settings have been applied
     */
    boolean isEmpty();

    /**
     * Whether this MessageAction will be used to update an existing message.
     *
     * @return True, if this MessageAction targets an existing message
     */
    boolean isEdit();

    /**
     * Applies the sendable information of the provided {@link Message Message}
     * to this MessageAction settings.
     * <br>This will override all existing settings <b>if</b> new settings are available.
     *
     * <p><b>This does not copy files!</b>
     *
     * @param  message
     *         The nullable Message to apply settings from
     *
     * @throws java.lang.IllegalArgumentException
     *         If the message contains an {@link MessageEmbed MessageEmbed}
     *         that exceeds the sendable character limit,
     *         see {@link MessageEmbed#isSendable(AccountType) MessageEmbed.isSendable(AccountType)}
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    
    MessageAction apply(final Message message);

    /**
     * Enable/Disable Text-To-Speech for the resulting message.
     * <br>This is only relevant to MessageActions that are not {@code isEdit() == true}!
     *
     * @param  isTTS
     *         True, if this should cause a Text-To-Speech effect when sent to the channel
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    
    MessageAction tts(final boolean isTTS);

    /**
     * Resets this MessageAction to empty state
     * <br>{@link #isEmpty()} will result in {@code true} after this has been performed!
     *
     * <p>Convenience over using
     * {@code content(null).nonce(null).embed(null).tts(false).override(false).clearFiles()}
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    
    MessageAction reset();

    /**
     * Sets the validation nonce for the outgoing Message
     *
     * <p>For more information see {@link MessageBuilder#setNonce(String) MessageBuilder.setNonce(String)}
     * and {@link Message#getNonce() Message.getNonce()}
     *
     * @param  nonce
     *         The nonce that shall be used
     *
     * @return Updated MessageAction for chaining convenience
     *
     * @see    Message#getNonce()
     * @see    MessageBuilder#setNonce(String)
     * @see    <a href="https://en.wikipedia.org/wiki/Cryptographic_nonce" target="_blank">Cryptographic Nonce - Wikipedia</a>
     */
    
    
    MessageAction nonce(final String nonce);

    /**
     * Overrides existing content with the provided input
     * <br>The content of a Message may not exceed {@value Message#MAX_CONTENT_LENGTH}!
     *
     * @param  content
     *         Sets the specified content and overrides previous content
     *         or {@code null} to reset content
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided content exceeds the {@value Message#MAX_CONTENT_LENGTH} character limit
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    
    MessageAction content(final String content);

    /**
     * Sets the {@link MessageEmbed MessageEmbed}
     * that should be used for this Message.
     * Refer to {@link EmbedBuilder EmbedBuilder} for more information.
     *
     * @param  embed
     *         The {@link MessageEmbed MessageEmbed} that should
     *         be attached to this message, {@code null} to use no embed.
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided MessageEmbed is not sendable according to
     *         {@link MessageEmbed#isSendable(AccountType) MessageEmbed.isSendable(AccountType)}!
     *         If the provided MessageEmbed is an unknown implementation this operation will fail as we are unable to deserialize it.
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    
    MessageAction embed(final MessageEmbed embed);

    /**
     * {@inheritDoc}
     * @throws java.lang.IllegalArgumentException
     *         If the appended CharSequence is too big and will cause the content to
     *         exceed the {@value Message#MAX_CONTENT_LENGTH} character limit
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    @Override
    
    default MessageAction append( final CharSequence csq)
    {
        return append(csq, 0, csq.length());
    }

    /**
     * {@inheritDoc}
     * @throws java.lang.IllegalArgumentException
     *         If the appended CharSequence is too big and will cause the content to
     *         exceed the {@value Message#MAX_CONTENT_LENGTH} character limit
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    @Override
    
    MessageAction append(final CharSequence csq, final int start, final int end);

    /**
     * {@inheritDoc}
     * @throws java.lang.IllegalArgumentException
     *         If the appended CharSequence is too big and will cause the content to
     *         exceed the {@value Message#MAX_CONTENT_LENGTH} character limit
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    @Override
    
    MessageAction append(final char c);

    /**
     * Applies the result of {@link String#format(String, Object...) String.format(String, Object...)}
     * as content.
     *
     * <p>For more information of formatting review the {@link java.util.Formatter Formatter} documentation!
     *
     * @param  format
     *         The format String
     * @param  args
     *         The arguments that should be used for conversion
     *
     * @throws java.lang.IllegalArgumentException
     *         If the appended formatting is too big and will cause the content to
     *         exceed the {@value Message#MAX_CONTENT_LENGTH} character limit
     * @throws java.util.IllegalFormatException
     *         If a format string contains an illegal syntax,
     *         a format specifier that is incompatible with the given arguments,
     *         insufficient arguments given the format string, or other illegal conditions.
     *         For specification of all possible formatting errors,
     *         see the <a href="../util/Formatter.html#detail">Details</a>
     *         section of the formatter class specification.
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    
    default MessageAction appendFormat( final String format, final Object... args)
    {
        return append(String.format(format, args));
    }

    /**
     * Adds the provided {@link java.io.InputStream InputStream} as file data.
     * <br><u>The stream will be closed upon execution!</u>
     *
     * <p>To reset all files use {@link #clearFiles()}
     *
     * @param  data
     *         The InputStream that will be interpreted as file data
     * @param  name
     *         The file name that should be used to interpret the type of the given data
     *         using the file-name extension. This name is similar to what will be visible
     *         through {@link Message.Attachment#getFileName() Message.Attachment.getFileName()}
     * @param  options
     *         Possible options to apply to this attachment, such as marking it as spoiler image
     *
     * @throws java.lang.IllegalStateException
     *         If the file limit of {@value Message#MAX_FILE_AMOUNT} has been reached prior to calling this method,
     *         or if this MessageAction will perform an edit operation on an existing Message (see {@link #isEdit()})
     * @throws java.lang.IllegalArgumentException
     *         If the provided data is {@code null} or the provided name is blank or {@code null}
     * @throws InsufficientPermissionException
     *         If this is targeting a TextChannel and the currently logged in account does not have
     *         {@link Permission#MESSAGE_ATTACH_FILES Permission.MESSAGE_ATTACH_FILES}
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    
    MessageAction addFile( final InputStream data,  final String name,  AttachmentOption... options);

    /**
     * Adds the provided byte[] as file data.
     *
     * <p>To reset all files use {@link #clearFiles()}
     *
     * @param  data
     *         The byte[] that will be interpreted as file data
     * @param  name
     *         The file name that should be used to interpret the type of the given data
     *         using the file-name extension. This name is similar to what will be visible
     *         through {@link Message.Attachment#getFileName() Message.Attachment.getFileName()}
     * @param  options
     *         Possible options to apply to this attachment, such as marking it as spoiler image
     *
     * @throws java.lang.IllegalStateException
     *         If the file limit of {@value Message#MAX_FILE_AMOUNT} has been reached prior to calling this method,
     *         or if this MessageAction will perform an edit operation on an existing Message (see {@link #isEdit()})
     * @throws java.lang.IllegalArgumentException
     *         If the provided data is {@code null} or the provided name is blank or {@code null}
     *         or if the provided data exceeds the maximum file size of the currently logged in account
     * @throws InsufficientPermissionException
     *         If this is targeting a TextChannel and the currently logged in account does not have
     *         {@link Permission#MESSAGE_ATTACH_FILES Permission.MESSAGE_ATTACH_FILES}
     *
     * @return Updated MessageAction for chaining convenience
     *
     * @see    SelfUser#getAllowedFileSize() SelfUser.getAllowedFileSize()
     */
    
    
    default MessageAction addFile( final byte[] data,  final String name,  AttachmentOption... options)
    {
        Checks.notNull(data, "Data");
        final long maxSize = getJDA().getSelfUser().getAllowedFileSize();
        Checks.check(data.length <= maxSize, "File may not exceed the maximum file length of %d bytes!", maxSize);
        return addFile(new ByteArrayInputStream(data), name, options);
    }

    /**
     * Adds the provided {@link java.io.File File} as file data.
     * <br>Shortcut for {@link #addFile(java.io.File, String, AttachmentOption...) addFile(file, file.getName())} with the same side-effects.
     *
     * @param  file
     *         The File that will be interpreted as file data
     * @param  options
     *         Possible options to apply to this attachment, such as marking it as spoiler image
     *
     * @throws java.lang.IllegalStateException
     *         If the file limit of {@value Message#MAX_FILE_AMOUNT} has been reached prior to calling this method,
     *         or if this MessageAction will perform an edit operation on an existing Message (see {@link #isEdit()})
     * @throws java.lang.IllegalArgumentException
     *         If the provided file is {@code null} or if the provided File is bigger than the maximum file size of the currently logged in account
     * @throws InsufficientPermissionException
     *         If this is targeting a TextChannel and the currently logged in account does not have
     *         {@link Permission#MESSAGE_ATTACH_FILES Permission.MESSAGE_ATTACH_FILES}
     *
     * @return Updated MessageAction for chaining convenience
     *
     * @see    SelfUser#getAllowedFileSize() SelfUser.getAllowedFileSize()
     */
    
    
    default MessageAction addFile( final File file,  AttachmentOption... options)
    {
        Checks.notNull(file, "File");
        return addFile(file, file.getName(), options);
    }

    /**
     * Adds the provided {@link java.io.File File} as file data.
     *
     * <p>To reset all files use {@link #clearFiles()}
     * <br><u>This method opens a {@link java.io.FileInputStream FileInputStream} which will be closed by executing this action or using {@link #clearFiles()}!</u>
     *
     * @param  file
     *         The File that will be interpreted as file data
     * @param  name
     *         The file name that should be used to interpret the type of the given data
     *         using the file-name extension. This name is similar to what will be visible
     *         through {@link Message.Attachment#getFileName() Message.Attachment.getFileName()}
     * @param  options
     *         Possible options to apply to this attachment, such as marking it as spoiler image
     *
     * @throws java.lang.IllegalStateException
     *         If the file limit of {@value Message#MAX_FILE_AMOUNT} has been reached prior to calling this method,
     *         or if this MessageAction will perform an edit operation on an existing Message (see {@link #isEdit()})
     * @throws java.lang.IllegalArgumentException
     *         If the provided file is {@code null} or the provided name is blank or {@code null}
     *         or if the provided file is bigger than the maximum file size of the currently logged in account,
     *         or if the provided file does not exist/ is not readable
     * @throws InsufficientPermissionException
     *         If this is targeting a TextChannel and the currently logged in account does not have
     *         {@link Permission#MESSAGE_ATTACH_FILES Permission.MESSAGE_ATTACH_FILES}
     *
     * @return Updated MessageAction for chaining convenience
     *
     * @see    SelfUser#getAllowedFileSize() SelfUser.getAllowedFileSize()
     */
    
    
    MessageAction addFile( final File file,  final String name,  AttachmentOption... options);

    /**
     * Clears all previously added files
     * <br>And closes {@code FileInputStreams} generated by {@link #addFile(File, String, AttachmentOption...)}.
     * <br>To close all stream (including ones given by {@link #addFile(InputStream, String, AttachmentOption...)}) use {@link #clearFiles(Consumer)}.
     *
     * @return Updated MessageAction for chaining convenience
     *
     * @see    #clearFiles(BiConsumer)
     */
    
    
    MessageAction clearFiles();

    /**
     * Clears all previously added files
     *
     * @param  finalizer
     *         BiConsumer useful to <b>close</b> remaining resources,
     *         the consumer will receive the name as a string parameter and the resource as {@code InputStream}.
     *
     * @return Updated MessageAction for chaining convenience
     *
     * @see    java.io.Closeable
     */
    
    
    MessageAction clearFiles( BiConsumer<String, InputStream> finalizer);

    /**
     * Clears all previously added files
     * <br>The {@link #clearFiles(BiConsumer)} version provides the resource name for more selective operations.
     *
     * @param  finalizer
     *         Consumer useful to <b>close</b> remaining resources,
     *         the consumer will receive only the resource in the form of an {@code InputStream}
     *
     * @return Updated MessageAction for chaining convenience
     *
     * @see    java.io.Closeable
     */
    
    
    MessageAction clearFiles( Consumer<InputStream> finalizer);

    /**
     * Whether all fields should be considered when editing a message
     *
     * @param  bool
     *         True, to override all fields even if they are not set
     *
     * @return Updated MessageAction for chaining convenience
     */
    
    
    MessageAction override(final boolean bool);
}
