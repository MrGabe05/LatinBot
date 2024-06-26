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

package net.latinplay.latinbot.jda.api.utils.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.MapType;
import net.latinplay.latinbot.jda.api.exceptions.ParsingException;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class DataObject implements SerializableData
{
    private static final Logger log = LoggerFactory.getLogger(DataObject.class);
    private static final ObjectMapper mapper;
    private static final SimpleModule module;
    private static final MapType mapType;

    static
    {
        mapper = new ObjectMapper();
        module = new SimpleModule();
        module.addAbstractTypeMapping(Map.class, HashMap.class);
        module.addAbstractTypeMapping(List.class, ArrayList.class);
        mapper.registerModule(module);
        mapType = mapper.getTypeFactory().constructRawMapType(HashMap.class);
    }

    protected final Map<String, Object> data;

    protected DataObject( Map<String, Object> data)
    {
        this.data = data;
    }

    /**
     * Creates a new empty DataObject, ready to be populated with values.
     *
     * @return An empty DataObject instance
     *
     * @see    #put(String, Object)
     */
    
    public static DataObject empty()
    {
        return new DataObject(new HashMap<>());
    }

    /**
     * Parses a JSON payload into a DataObject instance.
     *
     * @param  json
     *         The correctly formatted JSON payload to parse
     *
     * @throws ParsingException
     *         If the provided json is incorrectly formatted
     *
     * @return A DataObject instance for the provided payload
     */
    
    public static DataObject fromJson( String json)
    {
        try
        {
            Map<String, Object> map = mapper.readValue(json, mapType);
            return new DataObject(map);
        }
        catch (IOException ex)
        {
            throw new ParsingException(ex);
        }
    }

    /**
     * Parses a JSON payload into a DataObject instance.
     *
     * @param  stream
     *         The correctly formatted JSON payload to parse
     *
     * @throws ParsingException
     *         If the provided json is incorrectly formatted or an I/O error occurred
     *
     * @return A DataObject instance for the provided payload
     */
    
    public static DataObject fromJson( InputStream stream)
    {
        try
        {
            Map<String, Object> map = mapper.readValue(stream, mapType);
            return new DataObject(map);
        }
        catch (IOException ex)
        {
            throw new ParsingException(ex);
        }
    }

    /**
     * Parses a JSON payload into a DataObject instance.
     *
     * @param  stream
     *         The correctly formatted JSON payload to parse
     *
     * @throws ParsingException
     *         If the provided json is incorrectly formatted or an I/O error occurred
     *
     * @return A DataObject instance for the provided payload
     */
    
    public static DataObject fromJson( Reader stream)
    {
        try
        {
            Map<String, Object> map = mapper.readValue(stream, mapType);
            return new DataObject(map);
        }
        catch (IOException ex)
        {
            throw new ParsingException(ex);
        }
    }

    /**
     * Whether the specified key is present.
     *
     * @param  key
     *         The key to check
     *
     * @return True, if the specified key is present
     */
    public boolean hasKey( String key)
    {
        return data.containsKey(key);
    }

    /**
     * Whether the specified key is missing or null
     *
     * @param  key
     *         The key to check
     *
     * @return True, if the specified key is null or missing
     */
    public boolean isNull( String key)
    {
        return data.get(key) == null;
    }

    /**
     * Whether the specified key is of the specified type.
     *
     * @param  key
     *         The key to check
     * @param  type
     *         The type to check
     *
     * @return True, if the type check is successful
     *
     * @see    DataType#isType(Object) DataType.isType(Object)
     */
    public boolean isType( String key,  DataType type)
    {
        return type.isType(data.get(key));
    }

    /**
     * Resolves a DataObject to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the type is incorrect or no value is present for the specified key
     *
     * @return The resolved instance of DataObject for the key
     */
    
    public DataObject getObject( String key)
    {
        return optObject(key).orElseThrow(() -> valueError(key, "DataObject"));
    }

    /**
     * Resolves a DataObject to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the type is incorrect
     *
     * @return The resolved instance of DataObject for the key, wrapped in {@link java.util.Optional}
     */
    
    @SuppressWarnings("unchecked")
    public Optional<DataObject> optObject( String key)
    {
        Map<String, Object> child = null;
        try
        {
            child = (Map<String, Object>) get(Map.class, key);
        }
        catch (ClassCastException ex)
        {
            log.error("Unable to extract child data", ex);
        }
        return child == null ? Optional.empty() : Optional.of(new DataObject(child));
    }

    /**
     * Resolves a DataArray to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the type is incorrect or no value is present for the specified key
     *
     * @return The resolved instance of DataArray for the key
     */
    
    public DataArray getArray( String key)
    {
        return optArray(key).orElseThrow(() -> valueError(key, "DataArray"));
    }

    /**
     * Resolves a DataArray to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the type is incorrect
     *
     * @return The resolved instance of DataArray for the key, wrapped in {@link java.util.Optional}
     */
    
    @SuppressWarnings("unchecked")
    public Optional<DataArray> optArray( String key)
    {
        List<Object> child = null;
        try
        {
            child = (List<Object>) get(List.class, key);
        }
        catch (ClassCastException ex)
        {
            log.error("Unable to extract child data", ex);
        }
        return child == null ? Optional.empty() : Optional.of(new DataArray(child));
    }

    /**
     * Resolves any type to the provided key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @return {@link java.util.Optional} with a possible value
     */
    
    public Optional<Object> opt( String key)
    {
        return Optional.ofNullable(data.get(key));
    }

    /**
     * Resolves any type to the provided key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the value is missing or null
     *
     * @return The value of any type
     *
     * @see    #opt(String)
     */
    
    public Object get( String key)
    {
        Object value = data.get(key);
        if (value == null)
            throw valueError(key, "any");
        return value;
    }

    /**
     * Resolves a {@link java.lang.String} to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the value is missing or null
     *
     * @return The String value
     */
    
    public String getString( String key)
    {
        String value = getString(key, null);
        if (value == null)
            throw valueError(key, "String");
        return value;
    }

    /**
     * Resolves a {@link java.lang.String} to a key.
     *
     * @param  key
     *         The key to check for a value
     * @param  defaultValue
     *         Alternative value to use when no value or null value is associated with the key
     *
     * @return The String value, or null if provided with null defaultValue
     */
    @Contract("_, !null -> !null")
    public String getString( String key,  String defaultValue)
    {
        String value = get(String.class, key, UnaryOperator.identity(), String::valueOf);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves a {@link java.lang.Boolean} to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return True, if the value is present and set to true. False if the value is missing or set to false.
     */
    public boolean getBoolean( String key)
    {
        return getBoolean(key, false);
    }

    /**
     * Resolves a {@link java.lang.Boolean} to a key.
     *
     * @param  key
     *         The key to check for a value
     * @param  defaultValue
     *         Alternative value to use when no value or null value is associated with the key
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return True, if the value is present and set to true. False if the value is set to false. defaultValue if it is missing.
     */
    public boolean getBoolean( String key, boolean defaultValue)
    {
        Boolean value = get(Boolean.class, key, Boolean::parseBoolean, null);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves a long to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the value is missing, null, or of the wrong type
     *
     * @return The long value for the key
     */
    public long getLong( String key)
    {
        Long value = get(Long.class, key, Long::parseLong, Number::longValue);
        if (value == null)
            throw valueError(key, "long");
        return value;
    }

    /**
     * Resolves a long to a key.
     *
     * @param  key
     *         The key to check for a value
     * @param  defaultValue
     *         Alternative value to use when no value or null value is associated with the key
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The long value for the key
     */
    public long getLong( String key, long defaultValue)
    {
        Long value = get(Long.class, key, Long::parseLong, Number::longValue);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves an unsigned long to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the value is missing, null, or of the wrong type
     *
     * @return The unsigned long value for the key
     */
    public long getUnsignedLong( String key)
    {
        Long value = get(Long.class, key, Long::parseUnsignedLong, Number::longValue);
        if (value == null)
            throw valueError(key, "unsigned long");
        return value;
    }

    /**
     * Resolves an unsigned long to a key.
     *
     * @param  key
     *         The key to check for a value
     * @param  defaultValue
     *         Alternative value to use when no value or null value is associated with the key
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The unsigned long value for the key
     */
    public long getUnsignedLong( String key, long defaultValue)
    {
        Long value = get(Long.class, key, Long::parseUnsignedLong, Number::longValue);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves an int to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the value is missing, null, or of the wrong type
     *
     * @return The int value for the key
     */
    public int getInt( String key)
    {
        Integer value = get(Integer.class, key, Integer::parseInt, Number::intValue);
        if (value == null)
            throw valueError(key, "int");
        return value;
    }

    /**
     * Resolves an int to a key.
     *
     * @param  key
     *         The key to check for a value
     * @param  defaultValue
     *         Alternative value to use when no value or null value is associated with the key
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The int value for the key
     */
    public int getInt( String key, int defaultValue)
    {
        Integer value = get(Integer.class, key, Integer::parseInt, Number::intValue);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves an unsigned int to a key.
     *
     * @param  key
     *         The key to check for a value
     *
     * @throws ParsingException
     *         If the value is missing, null, or of the wrong type
     *
     * @return The unsigned int value for the key
     */
    public int getUnsignedInt( String key)
    {
        Integer value = get(Integer.class, key, Integer::parseUnsignedInt, Number::intValue);
        if (value == null)
            throw valueError(key, "unsigned int");
        return value;
    }

    /**
     * Resolves an unsigned int to a key.
     *
     * @param  key
     *         The key to check for a value
     * @param  defaultValue
     *         Alternative value to use when no value or null value is associated with the key
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The unsigned int value for the key
     */
    public int getUnsignedInt( String key, int defaultValue)
    {
        Integer value = get(Integer.class, key, Integer::parseUnsignedInt, Number::intValue);
        return value == null ? defaultValue : value;
    }

    /**
     * Removes the value associated with the specified key.
     * If no value is associated with the key, this does nothing.
     *
     * @param  key
     *         The key to unlink
     *
     * @return A DataObject with the removed key
     */
    
    public DataObject remove( String key)
    {
        data.remove(key);
        return this;
    }

    /**
     * Upserts a null value for the provided key.
     *
     * @param  key
     *         The key to upsert
     *
     * @return A DataObject with the updated value
     */
    
    public DataObject putNull( String key)
    {
        data.put(key, null);
        return this;
    }

    /**
     * Upserts a new value for the provided key.
     *
     * @param  key
     *         The key to upsert
     * @param  value
     *         The new value
     *
     * @return A DataObject with the updated value
     */
    
    public DataObject put( String key,  Object value)
    {
        if (value instanceof SerializableData)
            data.put(key, ((SerializableData) value).toData().data);
        else if (value instanceof DataArray)
            data.put(key, ((DataArray) value).data);
        else
            data.put(key, value);
        return this;
    }

    /**
     * {@link java.util.Collection} of all values in this DataObject.
     *
     * @return {@link java.util.Collection} for all values
     */
    
    public Collection<Object> values()
    {
        return data.values();
    }

    /**
     * {@link java.util.Set} of all keys in this DataObject.
     *
     * @return {@link Set} of keys
     */
    
    public Set<String> keys()
    {
        return data.keySet();
    }

    @Override
    public String toString()
    {
        try
        {
            return mapper.writeValueAsString(data);
        }
        catch (JsonProcessingException e)
        {
            throw new ParsingException(e);
        }
    }

    /**
     * Converts this DataObject to a {@link java.util.Map}
     *
     * @return The resulting map
     */
    
    public Map<String, Object> toMap()
    {
        return data;
    }

    
    @Override
    public DataObject toData()
    {
        return this;
    }

    private ParsingException valueError(String key, String expectedType)
    {
        return new ParsingException("Unable to resolve value with key " + key + " to type " + expectedType + ": " + data.get(key));
    }

    
    private <T> T get( Class<T> type,  String key)
    {
        return get(type, key, null, null);
    }

    
    private <T> T get( Class<T> type,  String key,  Function<String, T> stringParse,  Function<Number, T> numberParse)
    {
        Object value = data.get(key);
        if (value == null)
            return null;
        if (type.isAssignableFrom(value.getClass()))
            return type.cast(value);
        // attempt type coercion
        if (value instanceof Number && numberParse != null)
            return numberParse.apply((Number) value);
        else if (value instanceof String && stringParse != null)
            return stringParse.apply((String) value);

        throw new ParsingException(String.format("Cannot parse value for %s into type %s: %s instance of %s",
                                                      key, type.getSimpleName(), value, value.getClass().getSimpleName()));
    }
}
