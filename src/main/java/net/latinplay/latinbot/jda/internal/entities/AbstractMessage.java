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

package net.latinplay.latinbot.jda.internal.entities;

import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.entities.*;
import net.latinplay.latinbot.jda.api.requests.RestAction;
import net.latinplay.latinbot.jda.api.requests.restaction.AuditableRestAction;
import net.latinplay.latinbot.jda.api.requests.restaction.MessageAction;
import net.latinplay.latinbot.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.latinplay.latinbot.jda.internal.utils.Helpers;
import org.apache.commons.collections4.Bag;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.List;

public abstract class AbstractMessage implements Message
{
    protected static final String UNSUPPORTED = "This operation is not supported for Messages of this type!";

    protected final String content;
    protected final String nonce;
    protected final boolean isTTS;

    public AbstractMessage(String content, String nonce, boolean isTTS)
    {
        this.content = content;
        this.nonce = nonce;
        this.isTTS = isTTS;
    }


    @Override
    public String getContentRaw()
    {
        return content;
    }

    @Override
    public String getNonce()
    {
        return nonce;
    }

    @Override
    public boolean isTTS()
    {
        return isTTS;
    }

    protected abstract void unsupported();

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision)
    {
        boolean upper = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        boolean leftJustified = (flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY;

        String out = content;

        if (upper)
            out = out.toUpperCase(formatter.locale());

        appendFormat(formatter, width, precision, leftJustified, out);
    }

    protected void appendFormat(Formatter formatter, int width, int precision, boolean leftJustified, String out)
    {
        try
        {
            Appendable appendable = formatter.out();
            if (precision > -1 && out.length() > precision)
            {
                appendable.append(Helpers.truncate(out, precision - 3)).append("...");
                return;
            }

            if (leftJustified)
                appendable.append(Helpers.rightPad(out, width));
            else
                appendable.append(Helpers.leftPad(out, width));
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public Bag<User> getMentionedUsersBag()
    {
        unsupported();
        return null;
    }


    @Override
    public Bag<TextChannel> getMentionedChannelsBag()
    {
        unsupported();
        return null;
    }


    @Override
    public Bag<Role> getMentionedRolesBag()
    {
        unsupported();
        return null;
    }


    @Override
    public List<User> getMentionedUsers()
    {
        unsupported();
        return null;
    }


    @Override
    public List<TextChannel> getMentionedChannels()
    {
        unsupported();
        return null;
    }


    @Override
    public List<Role> getMentionedRoles()
    {
        unsupported();
        return null;
    }


    @Override
    public List<Member> getMentionedMembers( Guild guild)
    {
        unsupported();
        return null;
    }


    @Override
    public List<Member> getMentionedMembers()
    {
        unsupported();
        return null;
    }


    @Override
    public List<IMentionable> getMentions( MentionType... types)
    {
        unsupported();
        return null;
    }

    @Override
    public boolean isMentioned( IMentionable mentionable,  MentionType... types)
    {
        unsupported();
        return false;
    }

    @Override
    public boolean mentionsEveryone()
    {
        unsupported();
        return false;
    }

    @Override
    public boolean isEdited()
    {
        unsupported();
        return false;
    }

    @Override
    public OffsetDateTime getTimeEdited()
    {
        unsupported();
        return null;
    }


    @Override
    public User getAuthor()
    {
        unsupported();
        return null;
    }

    @Override
    public Member getMember()
    {
        unsupported();
        return null;
    }


    @Override
    public String getJumpUrl()
    {
        unsupported();
        return null;
    }


    @Override
    public String getContentDisplay()
    {
        unsupported();
        return null;
    }


    @Override
    public String getContentStripped()
    {
        unsupported();
        return null;
    }


    @Override
    public List<String> getInvites()
    {
        unsupported();
        return null;
    }

    @Override
    public boolean isFromType( ChannelType type)
    {
        unsupported();
        return false;
    }


    @Override
    public ChannelType getChannelType()
    {
        unsupported();
        return null;
    }

    @Override
    public boolean isWebhookMessage()
    {
        unsupported();
        return false;
    }


    @Override
    public MessageChannel getChannel()
    {
        unsupported();
        return null;
    }


    @Override
    public PrivateChannel getPrivateChannel()
    {
        unsupported();
        return null;
    }


    @Override
    public TextChannel getTextChannel()
    {
        unsupported();
        return null;
    }

    @Override
    public Category getCategory()
    {
        unsupported();
        return null;
    }


    @Override
    public Guild getGuild()
    {
        unsupported();
        return null;
    }


    @Override
    public List<Attachment> getAttachments()
    {
        unsupported();
        return null;
    }


    @Override
    public List<MessageEmbed> getEmbeds()
    {
        unsupported();
        return null;
    }


    @Override
    public List<Emote> getEmotes()
    {
        unsupported();
        return null;
    }


    @Override
    public Bag<Emote> getEmotesBag()
    {
        unsupported();
        return null;
    }


    @Override
    public List<MessageReaction> getReactions()
    {
        unsupported();
        return null;
    }


    @Override
    public MessageAction editMessage( CharSequence newContent)
    {
        unsupported();
        return null;
    }


    @Override
    public MessageAction editMessage( MessageEmbed newContent)
    {
        unsupported();
        return null;
    }


    @Override
    public MessageAction editMessageFormat( String format,  Object... args)
    {
        unsupported();
        return null;
    }


    @Override
    public MessageAction editMessage( Message newContent)
    {
        unsupported();
        return null;
    }


    @Override
    public AuditableRestAction<Void> delete()
    {
        unsupported();
        return null;
    }


    @Override
    public JDA getJDA()
    {
        unsupported();
        return null;
    }

    @Override
    public boolean isPinned()
    {
        unsupported();
        return false;
    }


    @Override
    public RestAction<Void> pin()
    {
        unsupported();
        return null;
    }


    @Override
    public RestAction<Void> unpin()
    {
        unsupported();
        return null;
    }


    @Override
    public RestAction<Void> addReaction( Emote emote)
    {
        unsupported();
        return null;
    }


    @Override
    public RestAction<Void> addReaction( String unicode)
    {
        unsupported();
        return null;
    }


    @Override
    public RestAction<Void> clearReactions()
    {
        unsupported();
        return null;
    }


    @Override
    public RestAction<Void> removeReaction( Emote emote)
    {
        unsupported();
        return null;
    }


    @Override
    public RestAction<Void> removeReaction( Emote emote,  User user)
    {
        unsupported();
        return null;
    }


    @Override
    public RestAction<Void> removeReaction( String unicode)
    {
        unsupported();
        return null;
    }


    @Override
    public RestAction<Void> removeReaction( String unicode,  User user)
    {
        unsupported();
        return null;
    }


    @Override
    public ReactionPaginationAction retrieveReactionUsers( Emote emote)
    {
        unsupported();
        return null;
    }


    @Override
    public ReactionPaginationAction retrieveReactionUsers( String unicode)
    {
        unsupported();
        return null;
    }

    @Override
    public MessageReaction.ReactionEmote getReactionByUnicode( String unicode)
    {
        unsupported();
        return null;
    }

    @Override
    public MessageReaction.ReactionEmote getReactionById( String id)
    {
        unsupported();
        return null;
    }

    @Override
    public MessageReaction.ReactionEmote getReactionById(long id)
    {
        unsupported();
        return null;
    }


    @Override
    public AuditableRestAction<Void> suppressEmbeds(boolean suppressed)
    {
        unsupported();
        return null;
    }

    @Override
    public boolean isSuppressedEmbeds()
    {
        unsupported();
        return false;
    }


    @Override
    public EnumSet<MessageFlag> getFlags()
    {
        unsupported();
        return null;
    }


    @Override
    public MessageType getType()
    {
        unsupported();
        return null;
    }
}
