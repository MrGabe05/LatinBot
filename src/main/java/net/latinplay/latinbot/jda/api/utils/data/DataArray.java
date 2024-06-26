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
import com.fasterxml.jackson.databind.type.CollectionType;
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

/**
 * Represents a list of values used in communication with the Discord API.
 *
 * <p>Throws {@link java.lang.IndexOutOfBoundsException}
 * if provided with index out of bounds.
 *
 * <p>This class is not Thread-Safe
 */
public class DataArray implements Iterable<Object>
{
    private static final Logger log = LoggerFactory.getLogger(DataObject.class);
    private static final ObjectMapper mapper;
    private static final SimpleModule module;
    private static final CollectionType listType;

    static
    {
        mapper = new ObjectMapper();
        module = new SimpleModule();
        module.addAbstractTypeMapping(Map.class, HashMap.class);
        module.addAbstractTypeMapping(List.class, ArrayList.class);
        mapper.registerModule(module);
        listType = mapper.getTypeFactory().constructRawCollectionType(ArrayList.class);
    }

    protected final List<Object> data;

    protected DataArray(List<Object> data)
    {
        this.data = data;
    }

    /**
     * Creates a new empty DataArray, ready to be populated with values.
     *
     * @return An empty DataArray instance
     *
     * @see    #add(Object)
     */
    
    public static DataArray empty()
    {
        return new DataArray(new ArrayList<>());
    }

    /**
     * Creates a new DataArray and populates it with the contents
     * of the provided collection.
     *
     * @param  col
     *         The {@link java.util.Collection}
     *
     * @return A new DataArray populated with the contents of the collection
     */
    
    public static DataArray fromCollection( Collection<?> col)
    {
        return empty().addAll(col);
    }

    /**
     * Parses a JSON Array into a DataArray instance.
     *
     * @param  json
     *         The correctly formatted JSON Array
     *
     * @throws ParsingException
     *         If the provided JSON is incorrectly formatted
     *
     * @return A new DataArray instance for the provided array
     */
    
    public static DataArray fromJson( String json)
    {
        try
        {
            return new DataArray(mapper.readValue(json, listType));
        }
        catch (IOException e)
        {
            throw new ParsingException(e);
        }
    }

    /**
     * Parses a JSON Array into a DataArray instance.
     *
     * @param  json
     *         The correctly formatted JSON Array
     *
     * @throws ParsingException
     *         If the provided JSON is incorrectly formatted or an I/O error occurred
     *
     * @return A new DataArray instance for the provided array
     */
    
    public static DataArray fromJson( InputStream json)
    {
        try
        {
            return new DataArray(mapper.readValue(json, listType));
        }
        catch (IOException e)
        {
            throw new ParsingException(e);
        }
    }

    /**
     * Parses a JSON Array into a DataArray instance.
     *
     * @param  json
     *         The correctly formatted JSON Array
     *
     * @throws ParsingException
     *         If the provided JSON is incorrectly formatted or an I/O error occurred
     *
     * @return A new DataArray instance for the provided array
     */
    
    public static DataArray fromJson( Reader json)
    {
        try
        {
            return new DataArray(mapper.readValue(json, listType));
        }
        catch (IOException e)
        {
            throw new ParsingException(e);
        }
    }

    /**
     * Whether the value at the specified index is null.
     *
     * @param  index
     *         The index to check
     *
     * @return True, if the value at the index is null
     */
    public boolean isNull(int index)
    {
        return data.get(index) == null;
    }

    /**
     * Whether the value at the specified index is of the specified type.
     *
     * @param  index
     *         The index to check
     * @param  type
     *         The type to check
     *
     * @return True, if the type check is successful
     *
     * @see    DataType#isType(Object) DataType.isType(Object)
     */
    public boolean isType(int index,  DataType type)
    {
        return type.isType(data.get(index));
    }

    /**
     * The length of the array.
     *
     * @return The length of the array
     */
    public int length()
    {
        return data.size();
    }

    /**
     * Whether this array is empty
     *
     * @return True, if this array is empty
     */
    public boolean isEmpty()
    {
        return data.isEmpty();
    }

    /**
     * Resolves the value at the specified index to a DataObject
     *
     * @param  index
     *         The index to resolve
     *
     * @throws ParsingException
     *         If the value is of the wrong type or missing
     *
     * @return The resolved DataObject
     */
    
    @SuppressWarnings("unchecked")
    public DataObject getObject(int index)
    {
        Map<String, Object> child = null;
        try
        {
            child = (Map<String, Object>) get(Map.class, index);
        }
        catch (ClassCastException ex)
        {
            log.error("Unable to extract child data", ex);
        }
        if (child == null)
            throw valueError(index, "DataObject");
        return new DataObject(child);
    }

    /**
     * Resolves the value at the specified index to a DataArray
     *
     * @param  index
     *         The index to resolve
     *
     * @throws ParsingException
     *         If the value is of the wrong type or null
     *
     * @return The resolved DataArray
     */
    
    @SuppressWarnings("unchecked")
    public DataArray getArray(int index)
    {
        List<Object> child = null;
        try
        {
            child = (List<Object>) get(List.class, index);
        }
        catch (ClassCastException ex)
        {
            log.error("Unable to extract child data", ex);
        }
        if (child == null)
            throw valueError(index, "DataArray");
        return new DataArray(child);
    }

    /**
     * Resolves the value at the specified index to a String.
     *
     * @param  index
     *         The index to resolve
     *
     * @throws ParsingException
     *         If the value is of the wrong type or null
     *
     * @return The resolved String
     */
    
    public String getString(int index)
    {
        String value = get(String.class, index, UnaryOperator.identity(), String::valueOf);
        if (value == null)
            throw valueError(index, "String");
        return value;
    }

    /**
     * Resolves the value at the specified index to a String.
     *
     * @param  index
     *         The index to resolve
     * @param  defaultValue
     *         Alternative value to use when the value associated with the index is null
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The resolved String
     */
    @Contract("_, !null -> !null")
    public String getString(int index,  String defaultValue)
    {
        String value = get(String.class, index, UnaryOperator.identity(), String::valueOf);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves the value at the specified index to a boolean.
     *
     * @param  index
     *         The index to resolve
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return True, if the value is present and set to true. Otherwise false.
     */
    public boolean getBoolean(int index)
    {
        return getBoolean(index, false);
    }

    /**
     * Resolves the value at the specified index to a boolean.
     *
     * @param  index
     *         The index to resolve
     * @param  defaultValue
     *         Alternative value to use when the value associated with the index is null
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return True, if the value is present and set to true. False, if it is set to false. Otherwise defaultValue.
     */
    public boolean getBoolean(int index, boolean defaultValue)
    {
        Boolean value = get(Boolean.class, index, Boolean::parseBoolean, null);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves the value at the specified index to an int.
     *
     * @param  index
     *         The index to resolve
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The resolved int value
     */
    public int getInt(int index)
    {
        Integer value = get(Integer.class, index, Integer::parseInt, Number::intValue);
        if (value == null)
            throw valueError(index, "int");
        return value;
    }

    /**
     * Resolves the value at the specified index to an int.
     *
     * @param  index
     *         The index to resolve
     * @param  defaultValue
     *         Alternative value to use when the value associated with the index is null
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The resolved int value
     */
    public int getInt(int index, int defaultValue)
    {
        Integer value = get(Integer.class, index, Integer::parseInt, Number::intValue);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves the value at the specified index to an unsigned int.
     *
     * @param  index
     *         The index to resolve
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The resolved unsigned int value
     */
    public int getUnsignedInt(int index)
    {
        Integer value = get(Integer.class, index, Integer::parseUnsignedInt, Number::intValue);
        if (value == null)
            throw valueError(index, "unsigned int");
        return value;
    }

    /**
     * Resolves the value at the specified index to an unsigned int.
     *
     * @param  index
     *         The index to resolve
     * @param  defaultValue
     *         Alternative value to use when the value associated with the index is null
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The resolved unsigned int value
     */
    public int getUnsignedInt(int index, int defaultValue)
    {
        Integer value = get(Integer.class, index, Integer::parseUnsignedInt, Number::intValue);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves the value at the specified index to a long.
     *
     * @param  index
     *         The index to resolve
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The resolved long value
     */
    public long getLong(int index)
    {
        Long value = get(Long.class, index, Long::parseLong, Number::longValue);
        if (value == null)
            throw valueError(index, "long");
        return value;
    }

    /**
     * Resolves the value at the specified index to a long.
     *
     * @param  index
     *         The index to resolve
     * @param  defaultValue
     *         Alternative value to use when the value associated with the index is null
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The resolved long value
     */
    public long getLong(int index, long defaultValue)
    {
        Long value = get(Long.class, index, Long::parseLong, Number::longValue);
        return value == null ? defaultValue : value;
    }

    /**
     * Resolves the value at the specified index to an unsigned long.
     *
     * @param  index
     *         The index to resolve
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The resolved unsigned long value
     */
    public long getUnsignedLong(int index)
    {
        Long value = get(Long.class, index, Long::parseUnsignedLong, Number::longValue);
        if (value == null)
            throw valueError(index, "unsigned long");
        return value;
    }

    /**
     * Resolves the value at the specified index to an unsigned long.
     *
     * @param  index
     *         The index to resolve
     * @param  defaultValue
     *         Alternative value to use when the value associated with the index is null
     *
     * @throws ParsingException
     *         If the value is of the wrong type
     *
     * @return The resolved unsigned long value
     */
    public long getUnsignedLong(int index, long defaultValue)
    {
        Long value = get(Long.class, index, Long::parseUnsignedLong, Number::longValue);
        return value == null ? defaultValue : value;
    }

    /**
     * Appends the provided value to the end of the array.
     *
     * @param  value
     *         The value to append
     *
     * @return A DataArray with the value inserted at the end
     */
    
    public DataArray add( Object value)
    {
        if (value instanceof SerializableData)
            data.add(((SerializableData) value).toData().data);
        else if (value instanceof DataArray)
            data.add(((DataArray) value).data);
        else
            data.add(value);
        return this;
    }

    /**
     * Appends the provided values to the end of the array.
     *
     * @param  values
     *         The values to append
     *
     * @return A DataArray with the values inserted at the end
     */
    
    public DataArray addAll( Collection<?> values)
    {
        values.forEach(this::add);
        return this;
    }

    /**
     * Appends the provided values to the end of the array.
     *
     * @param  array
     *         The values to append
     *
     * @return A DataArray with the values inserted at the end
     */
    
    public DataArray addAll( DataArray array)
    {
        return addAll(array.data);
    }

    /**
     * Inserts the specified value at the provided index.
     *
     * @param  index
     *         The target index
     * @param  value
     *         The value to insert
     *
     * @return A DataArray with the value inserted at the specified index
     */
    
    public DataArray insert(int index,  Object value)
    {
        if (value instanceof SerializableData)
            data.add(index, ((SerializableData) value).toData().data);
        else if (value instanceof DataArray)
            data.add(index, ((DataArray) value).data);
        else
            data.add(index, value);
        return this;
    }

    /**
     * Removes the value at the specified index.
     *
     * @param  index
     *         The target index to remove
     *
     * @return A DataArray with the value removed
     */
    
    public DataArray remove(int index)
    {
        data.remove(index);
        return this;
    }

    /**
     * Removes the specified value.
     *
     * @param  value
     *         The value to remove
     *
     * @return A DataArray with the value removed
     */
    
    public DataArray remove( Object value)
    {
        data.remove(value);
        return this;
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
            throw new IllegalStateException(e);
        }
    }

    /**
     * Converts this DataArray to a {@link java.util.List}.
     *
     * @return The resulting list
     */
    
    public List<Object> toList()
    {
        return data;
    }

    private ParsingException valueError(int index, String expectedType)
    {
        return new ParsingException("Unable to resolve value at " + index + " to type " + expectedType + ": " + data.get(index));
    }

    
    private <T> T get( Class<T> type, int index)
    {
        return get(type, index, null, null);
    }

    
    private <T> T get( Class<T> type, int index,  Function<String, T> stringMapper,  Function<Number, T> numberMapper)
    {
        Object value = data.get(index);
        if (value == null)
            return null;
        if (type.isAssignableFrom(value.getClass()))
            return type.cast(value);
        // attempt type coercion
        if (stringMapper != null && value instanceof String)
            return stringMapper.apply((String) value);
        else if (numberMapper != null && value instanceof Number)
            return numberMapper.apply((Number) value);

        throw new ParsingException(String.format("Cannot parse value for index %d into type %s: %s instance of %s",
                                                      index, type.getSimpleName(), value, value.getClass().getSimpleName()));
    }

    
    @Override
    public Iterator<Object> iterator()
    {
        return data.iterator();
    }
}
