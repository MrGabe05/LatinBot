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
package net.latinplay.latinbot.jda.api;

import net.latinplay.latinbot.jda.api.entities.EmbedType;
import net.latinplay.latinbot.jda.api.entities.MessageChannel;
import net.latinplay.latinbot.jda.api.entities.MessageEmbed;
import net.latinplay.latinbot.jda.api.entities.Role;
import net.latinplay.latinbot.jda.api.utils.AttachmentOption;
import net.latinplay.latinbot.jda.internal.entities.EntityBuilder;
import net.latinplay.latinbot.jda.internal.utils.Checks;
import net.latinplay.latinbot.jda.internal.utils.Helpers;

import java.awt.*;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Builder system used to build {@link MessageEmbed MessageEmbeds}.
 *
 * <br>A visual breakdown of an Embed and how it relates to this class is available at
 * <a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/01-Overview.png" target="_blank">Embed Overview</a>.
 *
 * @since  3.0
 * @author John A. Grosh
 */
public class EmbedBuilder
{
    public final static String ZERO_WIDTH_SPACE = "\u200E";
    public final static Pattern URL_PATTERN = Pattern.compile("\\s*(https?|attachment)://\\S+\\s*", Pattern.CASE_INSENSITIVE);

    private final List<MessageEmbed.Field> fields = new LinkedList<>();
    private final StringBuilder description = new StringBuilder();
    private int color = Role.DEFAULT_COLOR_RAW;
    private String url, title;
    private OffsetDateTime timestamp;
    private MessageEmbed.Thumbnail thumbnail;
    private MessageEmbed.AuthorInfo author;
    private MessageEmbed.Footer footer;
    private MessageEmbed.ImageInfo image;

    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * <br>Every part of an embed can be removed or cleared by providing {@code null} to the setter method.
     */
    public EmbedBuilder() { }

    public EmbedBuilder( EmbedBuilder builder)
    {
        if (builder != null)
        {
            setDescription(builder.description.toString());
            this.fields.addAll(builder.fields);
            this.url = builder.url;
            this.title = builder.title;
            this.timestamp = builder.timestamp;
            this.color = builder.color;
            this.thumbnail = builder.thumbnail;
            this.author = builder.author;
            this.footer = builder.footer;
            this.image = builder.image;
        }
    }
    
    /**
     * Creates an EmbedBuilder using fields in an existing embed.
     *
     * @param  embed
     *         the existing embed
     */
    public EmbedBuilder( MessageEmbed embed)
    {
        if(embed != null)
        {
            setDescription(embed.getDescription());
            this.url = embed.getUrl();
            this.title = embed.getTitle();
            this.timestamp = embed.getTimestamp();
            this.color = embed.getColorRaw();
            this.thumbnail = embed.getThumbnail();
            this.author = embed.getAuthor();
            this.footer = embed.getFooter();
            this.image = embed.getImage();
            if (embed.getFields() != null)
                fields.addAll(embed.getFields());
        }
    }

    /**
     * Returns a {@link MessageEmbed MessageEmbed}
     * that has been checked as being valid for sending.
     *
     * @throws java.lang.IllegalStateException
     *         If the embed is empty. Can be checked with {@link #isEmpty()}.
     *
     * @return the built, sendable {@link MessageEmbed}
     */
    
    public MessageEmbed build()
    {
        if (isEmpty())
            throw new IllegalStateException("Cannot build an empty embed!");
        if (description.length() > MessageEmbed.TEXT_MAX_LENGTH)
            throw new IllegalStateException(String.format("Description is longer than %d! Please limit your input!", MessageEmbed.TEXT_MAX_LENGTH));
        if (length() > MessageEmbed.EMBED_MAX_LENGTH_BOT)
            throw new IllegalStateException("Cannot build an embed with more than " + MessageEmbed.EMBED_MAX_LENGTH_BOT + " characters!");
        final String description = this.description.length() < 1 ? null : this.description.toString();

        return EntityBuilder.createMessageEmbed(url, title, description, EmbedType.RICH, timestamp,
                color, thumbnail, null, author, null, footer, image, new LinkedList<>(fields));
    }

    /**
     * Resets this builder to default state.
     * <br>All parts will be either empty or null after this method has returned.
     *
     * @return The current EmbedBuilder with default values
     */
    
    public EmbedBuilder clear()
    {
        description.setLength(0);
        fields.clear();
        url = null;
        title = null;
        timestamp = null;
        color = Role.DEFAULT_COLOR_RAW;
        thumbnail = null;
        author = null;
        footer = null;
        image = null;
        return this;
    }

    /**
     * Checks if the given embed is empty. Empty embeds will throw an exception if built
     *
     * @return true if the embed is empty and cannot be built
     */
    public boolean isEmpty()
    {
        return title == null
            && timestamp == null
            && thumbnail == null
            && author == null
            && footer == null
            && image == null
            && color == Role.DEFAULT_COLOR_RAW
            && description.length() == 0
            && fields.isEmpty();
    }

    /**
     * The overall length of the current EmbedBuilder in displayed characters.
     * <br>Represents the {@link MessageEmbed#getLength() MessageEmbed.getLength()} value.
     *
     * @return length of the current builder state
     */
    public int length()
    {
        int length = description.length();
        synchronized (fields)
        {
            length = fields.stream().map(f -> f.getName().length() + f.getValue().length()).reduce(length, Integer::sum);
        }
        if (title != null)
            length += title.length();
        if (author != null)
            length += author.getName().length();
        if (footer != null)
            length += footer.getText().length();
        return length;
    }

    /**
     * Checks whether the constructed {@link MessageEmbed MessageEmbed}
     * is within the limits for the specified {@link AccountType AccountType}
     * <ul>
     *     <li>Bot: {@value MessageEmbed#EMBED_MAX_LENGTH_BOT}</li>
     *     <li>Client: {@value MessageEmbed#EMBED_MAX_LENGTH_CLIENT}</li>
     * </ul>
     *
     * @param  type
     *         The {@link AccountType AccountType} to validate
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with {@code null}
     *
     * @return True, if the {@link #length() length} is less or equal to the specific limit
     */
    public boolean isValidLength( AccountType type)
    {
        Checks.notNull(type, "AccountType");
        final int length = length();
        switch (type)
        {
            case BOT:
                return length <= MessageEmbed.EMBED_MAX_LENGTH_BOT;
            case CLIENT:
            default:
                return length <= MessageEmbed.EMBED_MAX_LENGTH_CLIENT;
        }
    }

    /**
     * Sets the Title of the embed.
     * <br>Overload for {@link #setTitle(String, String)} without URL parameter.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/04-setTitle.png">Example</a></b>
     *
     * @param  title
     *         the title of the embed
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If the provided {@code title} is an empty String.</li>
     *             <li>If the length of {@code title} is greater than {@link MessageEmbed#TITLE_MAX_LENGTH}.</li>
     *         </ul>
     *
     * @return the builder after the title has been set
     */
    
    public EmbedBuilder setTitle( String title)
    {
        return setTitle(title, null);
    }
    
    /**
     * Sets the Title of the embed.
     * <br>You can provide {@code null} as url if no url should be used.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/04-setTitle.png">Example</a></b>
     *
     * @param  title
     *         the title of the embed
     * @param  url
     *         Makes the title into a hyperlink pointed at this url.
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If the provided {@code title} is an empty String.</li>
     *             <li>If the length of {@code title} is greater than {@link MessageEmbed#TITLE_MAX_LENGTH}.</li>
     *             <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
     *             <li>If the provided {@code url} is not a properly formatted http or https url.</li>
     *         </ul>
     *
     * @return the builder after the title has been set
     */
    
    public EmbedBuilder setTitle( String title,  String url)
    {
        if (title == null)
        {
            this.title = null;
            this.url = null;
        }
        else
        {
            Checks.notEmpty(title, "Title");
            Checks.check(title.length() <= MessageEmbed.TITLE_MAX_LENGTH, "Title cannot be longer than %d characters.", MessageEmbed.TITLE_MAX_LENGTH);
            if (Helpers.isBlank(url))
                url = null;
            urlCheck(url);

            this.title = title;
            this.url = url;
        }
        return this;
    }

    /**
     * The {@link java.lang.StringBuilder StringBuilder} used to
     * build the description for the embed.
     * <br>Note: To reset the description use {@link #setDescription(CharSequence) setDescription(null)}
     *
     * @return StringBuilder with current description context
     */
    
    public StringBuilder getDescriptionBuilder()
    {
        return description;
    }

    /**
     * Sets the Description of the embed. This is where the main chunk of text for an embed is typically placed.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/05-setDescription.png">Example</a></b>
     *
     * @param  description
     *         the description of the embed, {@code null} to reset
     *
     * @throws java.lang.IllegalArgumentException
     *         If the length of {@code description} is greater than {@link MessageEmbed#TEXT_MAX_LENGTH}
     *
     * @return the builder after the description has been set
     */
    
    public final EmbedBuilder setDescription( CharSequence description)
    {
        this.description.setLength(0);
        if (description != null && description.length() >= 1)
            appendDescription(description);
        return this;
    }

    /**
     * Appends to the description of the embed. This is where the main chunk of text for an embed is typically placed.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/05-setDescription.png">Example</a></b>
     *
     * @param  description
     *         the string to append to the description of the embed
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If the provided {@code description} String is null</li>
     *             <li>If the length of {@code description} is greater than {@link MessageEmbed#TEXT_MAX_LENGTH}.</li>
     *         </ul>
     *
     * @return the builder after the description has been set
     */
    
    public EmbedBuilder appendDescription( CharSequence description)
    {
        Checks.notNull(description, "description");
        Checks.check(this.description.length() + description.length() <= MessageEmbed.TEXT_MAX_LENGTH,
                "Description cannot be longer than %d characters.", MessageEmbed.TEXT_MAX_LENGTH);
        this.description.append(description);
        return this;
    }

    /**
     * Sets the Timestamp of the embed.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/13-setTimestamp.png">Example</a></b>
     *
     * <p><b>Hint:</b> You can get the current time using {@link java.time.Instant#now() Instant.now()} or convert time from a
     * millisecond representation by using {@link java.time.Instant#ofEpochMilli(long) Instant.ofEpochMilli(long)};
     *
     * @param  temporal
     *         the temporal accessor of the timestamp
     *
     * @return the builder after the timestamp has been set
     */
    
    public EmbedBuilder setTimestamp( TemporalAccessor temporal)
    {
        if (temporal == null)
        {
            this.timestamp = null;
        }
        else if (temporal instanceof OffsetDateTime)
        {
            this.timestamp = (OffsetDateTime) temporal;
        }
        else
        {
            ZoneOffset offset;
            try
            {
                offset = ZoneOffset.from(temporal);
            }
            catch (DateTimeException ignore)
            {
                offset = ZoneOffset.UTC;
            }
            try
            {
                LocalDateTime ldt = LocalDateTime.from(temporal);
                this.timestamp = OffsetDateTime.of(ldt, offset);
            }
            catch (DateTimeException ignore)
            {
                try
                {
                    Instant instant = Instant.from(temporal);
                    this.timestamp = OffsetDateTime.ofInstant(instant, offset);
                }
                catch (DateTimeException ex)
                {
                    throw new DateTimeException("Unable to obtain OffsetDateTime from TemporalAccessor: " +
                            temporal + " of type " + temporal.getClass().getName(), ex);
                }
            }
        }
        return this; 
    }
    
    /**
     * Sets the Color of the embed.
     *
     * <a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/02-setColor.png" target="_blank">Example</a>
     *
     * @param  color
     *         The {@link java.awt.Color Color} of the embed
     *         or {@code null} to use no color
     *
     * @return the builder after the color has been set
     *
     * @see    #setColor(int)
     */
    
    public EmbedBuilder setColor( Color color)
    {
        this.color = color == null ? Role.DEFAULT_COLOR_RAW : color.getRGB();
        return this;
    }

    /**
     * Sets the raw RGB color value for the embed.
     *
     * <a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/02-setColor.png" target="_blank">Example</a>
     *
     * @param  color
     *         The raw rgb value, or {@link Role#DEFAULT_COLOR_RAW} to use no color
     *
     * @return the builder after the color has been set
     *
     * @see    #setColor(java.awt.Color)
     */
    
    public EmbedBuilder setColor(int color)
    {
        this.color = color;
        return this;
    }
    
    /**
     * Sets the Thumbnail of the embed.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/06-setThumbnail.png">Example</a></b>
     *
     * <p><b>Uploading images with Embeds</b>
     * <br>When uploading an <u>image</u>
     * (using {@link MessageChannel#sendFile(java.io.File, AttachmentOption...) MessageChannel.sendFile(...)})
     * you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
     *
     * <p><u>Example</u>
     * <pre><code>
     * MessageChannel channel; // = reference of a MessageChannel
     * EmbedBuilder embed = new EmbedBuilder();
     * InputStream file = new URL("https://http.cat/500").openStream();
     * embed.setThumbnail("attachment://cat.png") // we specify this in sendFile as "cat.png"
     *      .setDescription("This is a cute cat :3");
     * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
     * </code></pre>
     *
     * @param  url
     *         the url of the thumbnail of the embed
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
     *             <li>If the provided {@code url} is not a properly formatted http or https url.</li>
     *         </ul>
     *
     * @return the builder after the thumbnail has been set
     */
    
    public EmbedBuilder setThumbnail( String url)
    {
        if (url == null)
        {
            this.thumbnail = null;
        }
        else
        {
            urlCheck(url);
            this.thumbnail = new MessageEmbed.Thumbnail(url, null, 0, 0);
        }
        return this;
    }

    /**
     * Sets the Image of the embed.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/11-setImage.png">Example</a></b>
     *
     * <p><b>Uploading images with Embeds</b>
     * <br>When uploading an <u>image</u>
     * (using {@link MessageChannel#sendFile(java.io.File, AttachmentOption...) MessageChannel.sendFile(...)})
     * you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
     *
     * <p><u>Example</u>
     * <pre><code>
     * MessageChannel channel; // = reference of a MessageChannel
     * EmbedBuilder embed = new EmbedBuilder();
     * InputStream file = new URL("https://http.cat/500").openStream();
     * embed.setImage("attachment://cat.png") // we specify this in sendFile as "cat.png"
     *      .setDescription("This is a cute cat :3");
     * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
     * </code></pre>
     *
     * @param  url
     *         the url of the image of the embed
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
     *             <li>If the provided {@code url} is not a properly formatted http or https url.</li>
     *         </ul>
     *
     * @return the builder after the image has been set
     *
     * @see    MessageChannel#sendFile(java.io.File, String, AttachmentOption...) MessageChannel.sendFile(...)
     */
    
    public EmbedBuilder setImage( String url)
    {
        if (url == null)
        {
            this.image = null;
        }
        else
        {
            urlCheck(url);
            this.image = new MessageEmbed.ImageInfo(url, null, 0, 0);
        }
        return this;
    }
    
    /**
     * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
     * image beside it along with the author's name being made clickable by way of providing a url.
     * This convenience method just sets the name.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/03-setAuthor.png">Example</a></b>
     *
     * @param  name
     *         the name of the author of the embed. If this is not set, the author will not appear in the embed
     *
     * @return the builder after the author has been set
     */
    
    public EmbedBuilder setAuthor( String name)
    {
        return setAuthor(name, null, null);
    }

    /**
     * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
     * image beside it along with the author's name being made clickable by way of providing a url.
     * This convenience method just sets the name and the url.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/03-setAuthor.png">Example</a></b>
     *
     * @param  name
     *         the name of the author of the embed. If this is not set, the author will not appear in the embed
     * @param  url
     *         the url of the author of the embed
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
     *             <li>If the provided {@code url} is not a properly formatted http or https url.</li>
     *         </ul>
     *
     * @return the builder after the author has been set
     */
    
    public EmbedBuilder setAuthor( String name,  String url)
    {
        return setAuthor(name, url, null);
    }

    /**
     * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
     * image beside it along with the author's name being made clickable by way of providing a url.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/03-setAuthor.png">Example</a></b>
     *
     * <p><b>Uploading images with Embeds</b>
     * <br>When uploading an <u>image</u>
     * (using {@link MessageChannel#sendFile(java.io.File, AttachmentOption...) MessageChannel.sendFile(...)})
     * you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
     *
     * <p><u>Example</u>
     * <pre><code>
     * MessageChannel channel; // = reference of a MessageChannel
     * EmbedBuilder embed = new EmbedBuilder();
     * InputStream file = new URL("https://http.cat/500").openStream();
     * embed.setAuthor("Minn", null, "attachment://cat.png") // we specify this in sendFile as "cat.png"
     *      .setDescription("This is a cute cat :3");
     * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
     * </code></pre>
     *
     * @param  name
     *         the name of the author of the embed. If this is not set, the author will not appear in the embed
     * @param  url
     *         the url of the author of the embed
     * @param  iconUrl
     *         the url of the icon for the author
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If the length of {@code url} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
     *             <li>If the provided {@code url} is not a properly formatted http or https url.</li>
     *             <li>If the length of {@code iconUrl} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
     *             <li>If the provided {@code iconUrl} is not a properly formatted http or https url.</li>
     *         </ul>
     *
     * @return the builder after the author has been set
     */
    
    public EmbedBuilder setAuthor( String name,  String url,  String iconUrl)
    {
        //We only check if the name is null because its presence is what determines if the
        // the author will appear in the embed.
        if (name == null)
        {
            this.author = null;
        }
        else
        {
            urlCheck(url);
            urlCheck(iconUrl);
            this.author = new MessageEmbed.AuthorInfo(name, url, iconUrl, null);
        }
        return this;
    }

    /**
     * Sets the Footer of the embed without icon.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/12-setFooter.png">Example</a></b>
     *
     * @param  text
     *         the text of the footer of the embed. If this is not set or set to null, the footer will not appear in the embed.
     *
     * @throws java.lang.IllegalArgumentException
     *         If the length of {@code text} is longer than {@link MessageEmbed#TEXT_MAX_LENGTH}.
     *
     * @return the builder after the footer has been set
     */
    
    public EmbedBuilder setFooter( String text)
    {
        return setFooter(text, null);
    }

    /**
     * Sets the Footer of the embed.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/12-setFooter.png">Example</a></b>
     *
     * <p><b>Uploading images with Embeds</b>
     * <br>When uploading an <u>image</u>
     * (using {@link MessageChannel#sendFile(java.io.File, AttachmentOption...) MessageChannel.sendFile(...)})
     * you can reference said image using the specified filename as URI {@code attachment://filename.ext}.
     *
     * <p><u>Example</u>
     * <pre><code>
     * MessageChannel channel; // = reference of a MessageChannel
     * EmbedBuilder embed = new EmbedBuilder();
     * InputStream file = new URL("https://http.cat/500").openStream();
     * embed.setFooter("Cool footer!", "attachment://cat.png") // we specify this in sendFile as "cat.png"
     *      .setDescription("This is a cute cat :3");
     * channel.sendFile(file, "cat.png").embed(embed.build()).queue();
     * </code></pre>
     *
     * @param  text
     *         the text of the footer of the embed. If this is not set, the footer will not appear in the embed.
     * @param  iconUrl
     *         the url of the icon for the footer
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If the length of {@code text} is longer than {@link MessageEmbed#TEXT_MAX_LENGTH}.</li>
     *             <li>If the length of {@code iconUrl} is longer than {@link MessageEmbed#URL_MAX_LENGTH}.</li>
     *             <li>If the provided {@code iconUrl} is not a properly formatted http or https url.</li>
     *         </ul>
     *
     * @return the builder after the footer has been set
     */
    
    public EmbedBuilder setFooter( String text,  String iconUrl)
    {
        //We only check if the text is null because its presence is what determines if the
        // footer will appear in the embed.
        if (text == null)
        {
            this.footer = null;
        }
        else
        {
            Checks.check(text.length() <= MessageEmbed.TEXT_MAX_LENGTH, "Text cannot be longer than %d characters.", MessageEmbed.TEXT_MAX_LENGTH);
            urlCheck(iconUrl);
            this.footer = new MessageEmbed.Footer(text, iconUrl, null);
        }
        return this;
    }

    /**
     * Copies the provided Field into a new Field for this builder.
     * <br>For additional documentation, see {@link #addField(String, String, boolean)}
     * 
     * @param  field
     *         the field object to add
     *
     * @return the builder after the field has been added
     */
    
    public EmbedBuilder addField( MessageEmbed.Field field)
    {
        return field == null ? this : addField(field.getName(), field.getValue(), field.isInline());
    }
    
    /**
     * Adds a Field to the embed.
     *
     * <p>Note: If a blank string is provided to either {@code name} or {@code value}, the blank string is replaced
     * with {@link EmbedBuilder#ZERO_WIDTH_SPACE}.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/07-addField.png">Example of Inline</a></b>
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/08-addField.png">Example if Non-inline</a></b>
     *
     * @param  name
     *         the name of the Field, displayed in bold above the {@code value}.
     * @param  value
     *         the contents of the field.
     * @param  inline
     *         whether or not this field should display inline.
     *
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If only {@code name} or {@code value} is set. Both must be set.</li>
     *             <li>If the length of {@code name} is greater than {@link MessageEmbed#TITLE_MAX_LENGTH}.</li>
     *             <li>If the length of {@code value} is greater than {@link MessageEmbed#VALUE_MAX_LENGTH}.</li>
     *         </ul>
     *
     * @return the builder after the field has been added
     */
    
    public EmbedBuilder addField( String name,  String value, boolean inline)
    {
        if (name == null && value == null)
            return this;
        this.fields.add(new MessageEmbed.Field(name, value, inline));
        return this;
    }
    
    /**
     * Adds a blank (empty) Field to the embed.
     *
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/07-addField.png">Example of Inline</a></b>
     * <p><b><a href="https://raw.githubusercontent.com/DV8FromTheWorld/JDA/assets/assets/docs/embeds/08-addField.png">Example if Non-inline</a></b>
     *
     * @param  inline
     *         whether or not this field should display inline
     *
     * @return the builder after the field has been added
     */
    
    public EmbedBuilder addBlankField(boolean inline)
    {
        this.fields.add(new MessageEmbed.Field(ZERO_WIDTH_SPACE, ZERO_WIDTH_SPACE, inline));
        return this;
    }

    /**
     * Clears all fields from the embed, such as those created with the
     * {@link EmbedBuilder#EmbedBuilder(MessageEmbed) EmbedBuilder(MessageEmbed)}
     * constructor or via the
     * {@link EmbedBuilder#addField(MessageEmbed.Field) addField} methods.
     *
     * @return the builder after the field has been added
     */
    
    public EmbedBuilder clearFields()
    {
        this.fields.clear();
        return this;
    }
    
    /**
     * <b>Modifiable</b> list of {@link MessageEmbed MessageEmbed} Fields that the builder will
     * use for {@link #build()}.
     * <br>You can add/remove Fields and restructure this {@link java.util.List List} and it will then be applied in the
     * built MessageEmbed. These fields will be available again through {@link MessageEmbed#getFields() MessageEmbed.getFields()}.
     *
     * @return Mutable List of {@link MessageEmbed.Field Fields}
     */
    
    public List<MessageEmbed.Field> getFields()
    {
        return fields;
    }

    private void urlCheck( String url)
    {
        if (url != null)
        {
            Checks.check(url.length() <= MessageEmbed.URL_MAX_LENGTH, "URL cannot be longer than %d characters.", MessageEmbed.URL_MAX_LENGTH);
            Checks.check(URL_PATTERN.matcher(url).matches(), "URL must be a valid http(s) or attachment url.");
        }
    }
}
