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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.latinplay.latinbot.jda.internal.utils.Checks;

import java.util.regex.Pattern;

/**
 * Implements and algorithm that can strip or replace markdown in any supplied string.
 *
 * @see #sanitize(String, MarkdownSanitizer.SanitizationStrategy)
 *
 * @since  4.0.0
 */
public class MarkdownSanitizer
{
    /** Normal characters that are not special for markdown, ignoring this has no effect */
    public static final int NORMAL      = 0;
    /** Bold region such as "**Hello**" */
    public static final int BOLD        = 1 << 0;
    /** Italics region for underline such as "_Hello_" */
    public static final int ITALICS_U   = 1 << 1;
    /** Italics region for asterisks such as "*Hello*" */
    public static final int ITALICS_A   = 1 << 2;
    /** Monospace region such as "`Hello`" */
    public static final int MONO        = 1 << 3;
    /** Monospace region such as "``Hello``" */
    public static final int MONO_TWO    = 1 << 4;
    /** Codeblock region such as "```Hello```" */
    public static final int BLOCK       = 1 << 5;
    /** Spoiler region such as "||Hello||" */
    public static final int SPOILER     = 1 << 6;
    /** Underline region such as "__Hello__" */
    public static final int UNDERLINE   = 1 << 7;
    /** Strikethrough region such as "~~Hello~~" */
    public static final int STRIKE      = 1 << 8;
    /** Quote region such as {@code "> text here"} */
    public static final int QUOTE       = 1 << 9;
    /** Quote block region such as {@code ">>> text here"} */
    public static final int QUOTE_BLOCK = 1 << 10;

    private static final int ESCAPED_BOLD        = Integer.MIN_VALUE | BOLD;
    private static final int ESCAPED_ITALICS_U   = Integer.MIN_VALUE | ITALICS_U;
    private static final int ESCAPED_ITALICS_A   = Integer.MIN_VALUE | ITALICS_A;
    private static final int ESCAPED_MONO        = Integer.MIN_VALUE | MONO;
    private static final int ESCAPED_MONO_TWO    = Integer.MIN_VALUE | MONO_TWO;
    private static final int ESCAPED_BLOCK       = Integer.MIN_VALUE | BLOCK;
    private static final int ESCAPED_SPOILER     = Integer.MIN_VALUE | SPOILER;
    private static final int ESCAPED_UNDERLINE   = Integer.MIN_VALUE | UNDERLINE;
    private static final int ESCAPED_STRIKE      = Integer.MIN_VALUE | STRIKE;
    private static final int ESCAPED_QUOTE       = Integer.MIN_VALUE | QUOTE;
    private static final int ESCAPED_QUOTE_BLOCK = Integer.MIN_VALUE | QUOTE_BLOCK;

    private static final Pattern codeLanguage = Pattern.compile("^\\w+\n.*", Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern quote = Pattern.compile("> +\\S.*", Pattern.DOTALL | Pattern.MULTILINE);
    private static final Pattern quoteBlock = Pattern.compile(">>>\\s+\\S.*", Pattern.DOTALL | Pattern.MULTILINE);

    private static final TIntObjectMap<String> tokens;

    static
    {
        tokens = new TIntObjectHashMap<>();
        tokens.put(NORMAL, "");
        tokens.put(BOLD, "**");
        tokens.put(ITALICS_U, "_");
        tokens.put(ITALICS_A, "*");
        tokens.put(BOLD | ITALICS_A, "***");
        tokens.put(MONO, "`");
        tokens.put(MONO_TWO, "``");
        tokens.put(BLOCK, "```");
        tokens.put(SPOILER, "||");
        tokens.put(UNDERLINE, "__");
        tokens.put(STRIKE, "~~");
    }

    private int ignored;
    private SanitizationStrategy strategy;

    public MarkdownSanitizer()
    {
        this.ignored = NORMAL;
        this.strategy = SanitizationStrategy.REMOVE;
    }

    public MarkdownSanitizer(int ignored,  SanitizationStrategy strategy)
    {
        this.ignored = ignored;
        this.strategy = strategy == null ? SanitizationStrategy.REMOVE : strategy;
    }

    /**
     * Sanitize string with default settings.
     * <br>Same as {@code sanitize(sequence, SanitizationStrategy.REMOVE)}
     *
     * @param  sequence
     *         The string to sanitize
     *
     * @return The sanitized string
     */

    public static String sanitize( String sequence)
    {
        return sanitize(sequence, SanitizationStrategy.REMOVE);
    }

    /**
     * Sanitize string without ignoring anything.
     *
     * @param  sequence
     *         The string to sanitize
     * @param  strategy
     *         The {@link MarkdownSanitizer.SanitizationStrategy} to apply
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with null
     *
     * @return The sanitized string
     *
     * @see    MarkdownSanitizer#MarkdownSanitizer()
     * @see    #withIgnored(int)
     */

    public static String sanitize( String sequence,  SanitizationStrategy strategy)
    {
        Checks.notNull(sequence, "String");
        Checks.notNull(strategy, "Strategy");
        return new MarkdownSanitizer().withStrategy(strategy).compute(sequence);
    }

    /**
     * Escapes every markdown formatting found in the provided string.
     *
     * @param  sequence
     *         The string to sanitize
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with null
     *
     * @return The string with escaped markdown
     *
     * @see    #escape(String, int)
     */

    public static String escape( String sequence)
    {
        return escape(sequence, NORMAL);
    }

    /**
     * Escapes every markdown formatting found in the provided string.
     * <br>Example: {@code escape("**Hello** ~~World~~!", MarkdownSanitizer.BOLD | MarkdownSanitizer.STRIKE)}
     *
     * @param  sequence
     *         The string to sanitize
     * @param  ignored
     *         Formats to ignore
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with null
     *
     * @return The string with escaped markdown
     */

    public static String escape( String sequence, int ignored)
    {
        return new MarkdownSanitizer()
                .withIgnored(ignored)
                .withStrategy(SanitizationStrategy.ESCAPE)
                .compute(sequence);
    }

    /**
     * Switches the used {@link MarkdownSanitizer.SanitizationStrategy}.
     *
     * @param  strategy
     *         The new strategy
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with null
     *
     * @return The current sanitizer instance with the new strategy
     */

    public MarkdownSanitizer withStrategy( SanitizationStrategy strategy)
    {
        Checks.notNull(strategy, "Strategy");
        this.strategy = strategy;
        return this;
    }

    /**
     * Specific regions to ignore.
     * <br>Example: {@code new MarkdownSanitizer().withIgnored(MarkdownSanitizer.BOLD | MarkdownSanitizer.UNDERLINE).compute("Hello __world__!")}
     *
     * @param  ignored
     *         The regions to ignore
     *
     * @return The current sanitizer instance with the new ignored regions
     */

    public MarkdownSanitizer withIgnored(int ignored)
    {
        this.ignored |= ignored;
        return this;
    }

    private int getRegion(int index,  String sequence)
    {
        if (sequence.length() - index >= 3)
        {
            String threeChars = sequence.substring(index, index + 3);
            switch (threeChars)
            {
                case "```":
                    return doesEscape(index, sequence) ? ESCAPED_BLOCK : BLOCK;
                case "***":
                    return doesEscape(index, sequence) ? ESCAPED_BOLD | ITALICS_A : BOLD | ITALICS_A;
            }
        }
        if (sequence.length() - index >= 2)
        {
            String twoChars = sequence.substring(index, index + 2);
            switch (twoChars)
            {
                case "**":
                    return doesEscape(index, sequence) ? ESCAPED_BOLD : BOLD;
                case "__":
                    return doesEscape(index, sequence) ? ESCAPED_UNDERLINE : UNDERLINE;
                case "~~":
                    return doesEscape(index, sequence) ? ESCAPED_STRIKE : STRIKE;
                case "``":
                    return doesEscape(index, sequence) ? ESCAPED_MONO_TWO : MONO_TWO;
                case "||":
                    return doesEscape(index, sequence) ? ESCAPED_SPOILER : SPOILER;
            }
        }
        char current = sequence.charAt(index);
        switch (current)
        {
            case '*':
                return doesEscape(index, sequence) ? ESCAPED_ITALICS_A : ITALICS_A;
            case '_':
                return doesEscape(index, sequence) ? ESCAPED_ITALICS_U : ITALICS_U;
            case '`':
                return doesEscape(index, sequence) ? ESCAPED_MONO : MONO;
        }
        return NORMAL;
    }

    private boolean hasCollision(int index,  String sequence, char c)
    {
        if (index < 0)
            return false;
        return index < sequence.length() - 1 && sequence.charAt(index + 1) == c;
    }

    private int findEndIndex(int afterIndex, int region,  String sequence)
    {
        if (isEscape(region))
            return -1;
        int lastMatch = afterIndex + getDelta(region) + 1;
        while (lastMatch != -1)
        {
            switch (region)
            {
                case BOLD | ITALICS_A:
                    lastMatch = sequence.indexOf("***", lastMatch);
                    break;
                case BOLD:
                    lastMatch = sequence.indexOf("**", lastMatch);
                    if (lastMatch != -1 && hasCollision(lastMatch + 1, sequence, '*')) // did we find a bold italics tag?
                    {
                        lastMatch += 3;
                        continue;
                    }
                    break;
                case ITALICS_A:
                    lastMatch = sequence.indexOf('*', lastMatch);
                    if (lastMatch != -1 && hasCollision(lastMatch, sequence, '*')) // did we find a bold tag?
                    {
                        if (hasCollision(lastMatch + 1, sequence, '*'))
                            lastMatch += 3;
                        else
                            lastMatch += 2;
                        continue;
                    }
                    break;
                case UNDERLINE:
                    lastMatch = sequence.indexOf("__", lastMatch);
                    break;
                case ITALICS_U:
                    lastMatch = sequence.indexOf('_', lastMatch);
                    if (lastMatch != -1 && hasCollision(lastMatch, sequence, '_')) // did we find an underline tag?
                    {
                        lastMatch += 2;
                        continue;
                    }
                    break;
                case SPOILER:
                    lastMatch = sequence.indexOf("||", lastMatch);
                    break;
                case BLOCK:
                    lastMatch = sequence.indexOf("```", lastMatch);
                    break;
                case MONO_TWO:
                    lastMatch = sequence.indexOf("``", lastMatch);
                    if (lastMatch != -1 && hasCollision(lastMatch + 1, sequence, '`')) // did we find a codeblock?
                    {
                        lastMatch += 3;
                        continue;
                    }
                    break;
                case MONO:
                    lastMatch = sequence.indexOf('`', lastMatch);
                    if (lastMatch != -1 && hasCollision(lastMatch, sequence, '`')) // did we find a codeblock?
                    {
                        if (hasCollision(lastMatch + 1, sequence, '`'))
                            lastMatch += 3;
                        else
                            lastMatch += 2;
                        continue;
                    }
                    break;
                case STRIKE:
                    lastMatch = sequence.indexOf("~~", lastMatch);
                    break;
                default:
                    return -1;
            }
            if (lastMatch == -1 || !doesEscape(lastMatch, sequence))
                return lastMatch;
            lastMatch++;
        }
        return -1;
    }


    private String handleRegion(int start, int end,  String sequence, int region)
    {
        String resolved = sequence.substring(start, end);
        switch (region)
        {
            case BLOCK:
            case MONO:
            case MONO_TWO:
                return resolved;
            default:
                return new MarkdownSanitizer(ignored, strategy).compute(resolved);
        }
    }

    private int getDelta(int region)
    {
        switch (region)
        {
            case ESCAPED_BLOCK:
            case ESCAPED_BOLD | ITALICS_A:
            case BLOCK:
            case BOLD | ITALICS_A:
                return 3;
            case ESCAPED_MONO_TWO:
            case ESCAPED_BOLD:
            case ESCAPED_UNDERLINE:
            case ESCAPED_SPOILER:
            case ESCAPED_STRIKE:
            case MONO_TWO:
            case BOLD:
            case UNDERLINE:
            case SPOILER:
            case STRIKE:
                return 2;
            case ESCAPED_ITALICS_A:
            case ESCAPED_ITALICS_U:
            case ESCAPED_MONO:
            case ESCAPED_QUOTE:
            case ITALICS_A:
            case ITALICS_U:
            case MONO:
                return 1;
            default:
                return 0;
        }
    }

    private void applyStrategy(int region,  String seq,  StringBuilder builder)
    {
        if (strategy == SanitizationStrategy.REMOVE)
        {
            if (codeLanguage.matcher(seq).matches())
                builder.append(seq.substring(seq.indexOf("\n") + 1));
            else
                builder.append(seq);
            return;
        }
        String token = tokens.get(region);
        if (token == null)
            throw new IllegalStateException("Found illegal region for strategy ESCAPE '" + region + "' with no known format token!");
        if (region == UNDERLINE)
            token = "_\\_"; // UNDERLINE needs special handling because the client thinks its ITALICS_U if you only escape once
        else if (region == BOLD)
            token = "*\\*"; // BOLD needs special handling because the client thinks its ITALICS_A if you only escape once
        else if (region == (BOLD | ITALICS_A))
            token = "*\\*\\*"; // BOLD | ITALICS_A needs special handling because the client thinks its BOLD if you only escape once
        builder.append("\\").append(token)
               .append(seq)
               .append("\\").append(token);
    }

    private boolean doesEscape(int index,  String seq)
    {
        int backslashes = 0;
        for (int i = index - 1; i > -1; i--)
        {
            if (seq.charAt(i) != '\\')
                break;
            backslashes++;
        }
        return backslashes % 2 != 0;
    }

    private boolean isEscape(int region)
    {
        return (Integer.MIN_VALUE & region) != 0;
    }

    private boolean isIgnored(int nextRegion)
    {
        return (nextRegion & ignored) == nextRegion;
    }

    /**
     * Computes the provided input.
     * <br>Uses the specified {@link MarkdownSanitizer.SanitizationStrategy} and
     * ignores any regions specified with {@link #withIgnored(int)}.
     *
     * @param  sequence
     *         The string to compute
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided string is null
     *
     * @return The resulting string after applying the computation
     */

    public String compute( String sequence)
    {
        Checks.notNull(sequence, "Input");
        StringBuilder builder = new StringBuilder();
        String end = handleQuote(sequence, false);
        if (end != null) return end;

        for (int i = 0; i < sequence.length();)
        {
            int nextRegion = getRegion(i, sequence);
            if (nextRegion == NORMAL)
            {
                if (sequence.charAt(i) == '\n' && i + 1 < sequence.length())
                {
                    String result = handleQuote(sequence.substring(i + 1), true);
                    if (result != null)
                        return builder.append(result).toString();
                }

                builder.append(sequence.charAt(i++));
                continue;
            }

            int endRegion = findEndIndex(i, nextRegion, sequence);
            if (isIgnored(nextRegion) || endRegion == -1)
            {
                int delta = getDelta(nextRegion);
                for (int j = 0; j < delta; j++)
                    builder.append(sequence.charAt(i++));
                continue;
            }
            int delta = getDelta(nextRegion);
            applyStrategy(nextRegion, handleRegion(i + delta, endRegion, sequence, nextRegion), builder);
            i = endRegion + delta;
        }
        return builder.toString();
    }

    private String handleQuote( String sequence, boolean newline)
    {
        // Special handling for quote
        if (!isIgnored(QUOTE) && quote.matcher(sequence).matches())
        {
            int end = sequence.indexOf('\n');
            if (end < 0)
                end = sequence.length();
            StringBuilder builder = new StringBuilder(compute(sequence.substring(2, end)));
            if (strategy == SanitizationStrategy.ESCAPE)
                builder.insert(0, "\\> ");
            if (newline)
                builder.insert(0, '\n');
            if (end < sequence.length())
                builder.append(compute(sequence.substring(end)));
            return builder.toString();

        }
        else if (!isIgnored(QUOTE_BLOCK) && quoteBlock.matcher(sequence).matches())
        {
            if (strategy == SanitizationStrategy.ESCAPE)
                return compute("\\".concat(sequence));
            return compute(sequence.substring(4));
        }
        return null;
    }

    public enum SanitizationStrategy
    {
        /**
         * Remove any format tokens that are not escaped or within a special region.
         * <br>{@code "**Hello** World!" -> "Hello World!"}
         */
        REMOVE,

        /**
         * Escape any format tokens that are not escaped or within a special region.
         * <br>{@code "**Hello** World!" -> "\**Hello\** World!"}
         */
        ESCAPE,
    }
}
