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

package net.latinplay.latinbot.jda.api.requests;

import net.latinplay.latinbot.jda.api.exceptions.ParsingException;
import net.latinplay.latinbot.jda.api.utils.IOFunction;
import net.latinplay.latinbot.jda.api.utils.data.DataArray;
import net.latinplay.latinbot.jda.api.utils.data.DataObject;
import net.latinplay.latinbot.jda.internal.utils.IOUtil;

import java.io.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Response implements Closeable
{
    public static final int ERROR_CODE = -1;
    public static final String ERROR_MESSAGE = "ERROR";
    public static final IOFunction<BufferedReader, DataObject> JSON_SERIALIZE_OBJECT = DataObject::fromJson;
    public static final IOFunction<BufferedReader, DataArray> JSON_SERIALIZE_ARRAY = DataArray::fromJson;

    public final int code;
    public final String message;
    public final long retryAfter;
    private final InputStream body;
    private final okhttp3.Response rawResponse;
    private final Set<String> cfRays;
    private String fallbackString;
    private Object object;
    private boolean attemptedParsing = false;
    private Exception exception;

    public Response( final okhttp3.Response response,  final Exception exception,  final Set<String> cfRays)
    {
        this(response, response != null ? response.code() : ERROR_CODE, ERROR_MESSAGE, -1, cfRays);
        this.exception = exception;
    }

    public Response( final okhttp3.Response response, final int code,  final String message, final long retryAfter,  final Set<String> cfRays)
    {
        this.rawResponse = response;
        this.code = code;
        this.message = message;
        this.exception = null;
        this.retryAfter = retryAfter;
        this.cfRays = cfRays;

        if (response == null)
        {
            this.body = null;
        }
        else // weird compatibility issue, thinks some final isn't initialized if we return pre-maturely
        try
        {
            this.body = IOUtil.getBody(response);
        }
        catch (final Exception e)
        {
            throw new IllegalStateException("An error occurred while parsing the response for a RestAction", e);
        }
    }

    public Response(final long retryAfter,  final Set<String> cfRays)
    {
        this(null, 429, "TOO MANY REQUESTS", retryAfter, cfRays);
    }

    public Response( final okhttp3.Response response, final long retryAfter,  final Set<String> cfRays)
    {
        this(response, response.code(), response.message(), retryAfter, cfRays);
    }

    
    public DataArray getArray()
    {
        return get(DataArray.class, JSON_SERIALIZE_ARRAY);
    }

    
    public Optional<DataArray> optArray()
    {
        return parseBody(true, DataArray.class, JSON_SERIALIZE_ARRAY);
    }

    
    public DataObject getObject()
    {
        return get(DataObject.class, JSON_SERIALIZE_OBJECT);
    }

    
    public Optional<DataObject> optObject()
    {
        return parseBody(true, DataObject.class, JSON_SERIALIZE_OBJECT);
    }

    
    public String getString()
    {
        return parseBody(String.class, this::readString)
            .orElseGet(() -> fallbackString == null ? "N/A" : fallbackString);
    }

    
    public <T> T get(Class<T> clazz, IOFunction<BufferedReader, T> parser)
    {
        return parseBody(clazz, parser).orElseThrow(IllegalStateException::new);
    }

    
    public okhttp3.Response getRawResponse()
    {
        return this.rawResponse;
    }

    
    public Set<String> getCFRays()
    {
        return cfRays;
    }

    
    public Exception getException()
    {
        return exception;
    }

    public boolean isError()
    {
        return this.code == Response.ERROR_CODE;
    }

    public boolean isOk()
    {
        return this.code > 199 && this.code < 300;
    }

    public boolean isRateLimit()
    {
        return this.code == 429;
    }

    @Override
    public String toString()
    {
        return this.exception == null
                ? "HTTPResponse[" + this.code + (this.object == null ? "" : ", " + this.object.toString()) + ']'
                : "HTTPException[" + this.exception.getMessage() + ']';
    }

    @Override
    public void close()
    {
        if (rawResponse != null)
            rawResponse.close();
    }

    private String readString(BufferedReader reader)
    {
        return reader.lines().collect(Collectors.joining("\n"));
    }

    private <T> Optional<T> parseBody(Class<T> clazz, IOFunction<BufferedReader, T> parser)
    {
        return parseBody(false, clazz, parser);
    }

    @SuppressWarnings("ConstantConditions")
    private <T> Optional<T> parseBody(boolean opt, Class<T> clazz, IOFunction<BufferedReader, T> parser)
    {
        if (attemptedParsing)
        {
            if (object != null && clazz.isAssignableFrom(object.getClass()))
                return Optional.of(clazz.cast(object));
            return Optional.empty();
        }

        attemptedParsing = true;
        if (body == null || rawResponse == null || rawResponse.body().contentLength() == 0)
            return Optional.empty();

        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(body));
            reader.mark(1024);
            T t = parser.apply(reader);
            this.object = t;
            return Optional.ofNullable(t);
        }
        catch (final Exception e)
        {
            try
            {
                reader.reset();
                this.fallbackString = readString(reader);
                reader.close();
            }
            catch (NullPointerException | IOException ignored) {}
            if (opt && e instanceof ParsingException)
                return Optional.empty();
            else
                throw new IllegalStateException("An error occurred while parsing the response for a RestAction", e);
        }
    }
}
