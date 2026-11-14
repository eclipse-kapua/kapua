/*******************************************************************************
 * Copyright (c) 2017, 2022 Red Hat Inc and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.setting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertyConverter;
import org.eclipse.kapua.commons.cache.CacheConfig;
import org.eclipse.kapua.commons.cache.ExpiryPolicy;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;

/**
 * An abstract base class which does not make any assumptions on where the configuration comes from
 *
 * @param <K>
 *         The settings key type
 */
public class AbstractBaseKapuaSetting<K extends SettingKey> {

    private boolean systemPropertyHotSwap;

    /**
     * Create an abstract configuration from a provided map
     * <p>
     * This is useful for testing when the configuration has to be provided
     * </p>
     *
     * @param map
     *         the map of values
     * @return the configuration, may be {@code null} if the "map" parameter was null
     */
    public static <K extends SettingKey> AbstractBaseKapuaSetting<K> fromMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        return new AbstractBaseKapuaSetting<>(new DataConfiguration(new MapConfiguration(map)));
    }

    protected DataConfiguration config;

    public AbstractBaseKapuaSetting(final DataConfiguration dataConfiguration) {
        init(dataConfiguration);
    }

    protected void init(final DataConfiguration dataConfiguration) {
        this.config = dataConfiguration;
        systemPropertyHotSwap = config.getBoolean(SystemSettingKey.SETTINGS_HOTSWAP.key(), false);
    }

    /**
     * Get the property for the key
     *
     * @param cls
     * @param key
     * @return
     * @deprecated since 1.0. Use typed get instead.
     */
    @Deprecated
    public <T> T get(Class<T> cls, K key) {
        return config.get(cls, key.key());
    }

    /**
     * Get the property for the key 'key'. Returns a default value if no property for that key is found.
     *
     * @param cls
     * @param key
     * @param defaultValue
     * @return
     * @deprecated since 1.0. Use typed get instead.
     */
    @Deprecated
    public <T> T get(Class<T> cls, K key, T defaultValue) {
        return config.get(cls, key.key(), defaultValue);
    }

    /**
     * Get the property list values for the key 'key'
     *
     * @param cls
     * @param key
     * @return
     */
    public <T> List<T> getList(Class<T> cls, K key) {
        return config.getList(cls, key.key());
    }

    /**
     * Get properties map with key matching the provided prefix and regex
     *
     * @param valueType
     * @param prefixKey
     * @param regex
     * @return
     */
    public <V> Map<String, V> getMap(Class<V> valueType, K prefixKey, String regex) {
        Map<String, V> map = new HashMap<>();
        Configuration subsetConfig = config.subset(prefixKey.key());
        DataConfiguration subsetDataConfig = new DataConfiguration(subsetConfig);
        for (Iterator<String> it = subsetConfig.getKeys(); it.hasNext(); ) {
            String key = it.next();
            if (Pattern.matches(regex, key)) {
                map.put(key, subsetDataConfig.get(valueType, key));
            }
        }
        return map;
    }

    /**
     * Get properties map with key matching the provided prefix
     *
     * @param valueType
     * @param prefixKey
     * @return
     */
    public <V> Map<String, V> getMap(Class<V> valueType, K prefixKey) {
        Map<String, V> map = new HashMap<>();
        Configuration subsetConfig = config.subset(prefixKey.key());
        DataConfiguration subsetDataConfig = new DataConfiguration(subsetConfig);
        for (Iterator<String> it = subsetConfig.getKeys(); it.hasNext(); ) {
            String key = it.next();
            map.put(key, subsetDataConfig.get(valueType, key));
        }
        return map;
    }

    /**
     * Get an integer property
     *
     * @param key
     * @return
     */
    public int getInt(K key) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toInteger(sysProp);
            }
        }
        return config.getInt(key.key());
    }

    /**
     * Get an integer property
     *
     * @param key
     * @return
     */
    public Optional<Integer> getInteger(K key) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                try {
                    return Optional.ofNullable(PropertyConverter.toInteger(sysProp));
                } catch (Exception ex) {
                    return Optional.empty();
                }
            }
        }
        try {
            return Optional.ofNullable(config.getInteger(key.key(), null));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /**
     * Get an integer property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInt(K key, int defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toInteger(sysProp);
            }
        }
        return config.getInt(key.key(), defaultValue);
    }

    /**
     * Get an integer property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInt(K key, Integer defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toInteger(sysProp);
            }
        }
        return config.getInt(key.key(), defaultValue);
    }

    /**
     * Get a boolean property
     *
     * @param key
     * @return
     */
    public boolean getBoolean(K key) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toBoolean(sysProp);
            }
        }
        return config.getBoolean(key.key());
    }

    /**
     * Get a boolean property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(K key, boolean defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toBoolean(sysProp);
            }
        }
        return config.getBoolean(key.key(), defaultValue);
    }

    /**
     * Get a boolean property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(K key, Boolean defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toBoolean(sysProp);
            }
        }
        return config.getBoolean(key.key(), defaultValue);
    }

    /**
     * Get a String property
     *
     * @param key
     * @return
     */
    public String getString(K key) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return sysProp;
            }
        }
        return config.getString(key.key());
    }

    /**
     * Get a String property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(K key, String defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return sysProp;
            }
        }
        return config.getString(key.key(), defaultValue);
    }

    /**
     * Get a long property
     *
     * @param key
     * @return
     */
    public long getLong(K key) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toLong(sysProp);
            }
        }
        return config.getLong(key.key());
    }

    /**
     * Get a long property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLong(K key, long defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toLong(sysProp);
            }
        }
        return config.getLong(key.key(), defaultValue);
    }

    /**
     * Get a long property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLong(K key, Long defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toLong(sysProp);
            }
        }
        return config.getLong(key.key(), defaultValue);
    }

    /**
     * Get a float property
     *
     * @param key
     * @return
     */
    public float getFloat(K key) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toFloat(sysProp);
            }
        }
        return config.getFloat(key.key());
    }

    /**
     * Get a float property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getFloat(K key, float defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toFloat(sysProp);
            }
        }
        return config.getFloat(key.key(), defaultValue);
    }

    /**
     * Get a float property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getFloat(K key, Float defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toFloat(sysProp);
            }
        }
        return config.getFloat(key.key(), defaultValue);
    }

    /**
     * Get a double property
     *
     * @param key
     * @return
     */
    public double getDouble(K key) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toDouble(sysProp);
            }
        }
        return config.getDouble(key.key());
    }

    /**
     * Get a double property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public double getDouble(K key, double defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toFloat(sysProp);
            }
        }
        return config.getDouble(key.key(), defaultValue);
    }

    /**
     * Get a double property with a default value (if property is not found)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public double getDouble(K key, Double defaultValue) {
        if (systemPropertyHotSwap) {
            String sysProp = System.getProperty(key.key());
            if (sysProp != null) {
                return PropertyConverter.toDouble(sysProp);
            }
        }
        return config.getDouble(key.key(), defaultValue);
    }

    public boolean isSystemPropertyHotSwap() {
        return systemPropertyHotSwap;
    }

    public void setSystemPropertyHotSwap(boolean systemPropertyHotSwap) {
        this.systemPropertyHotSwap = systemPropertyHotSwap;
    }

    /**
     * Gets the {@link CacheConfig} of a {@link org.eclipse.kapua.commons.cache.LocalCache} from the given keys.
     * <p>
     * A maximum size or an expiration time less than or equal to 0 results in a cache that never retains any entry.
     *
     * @param maxSizeKey           The maximum size key.
     * @param defaultMaxSize       The maximum size to use if the key is not set.
     * @param expireAfterKey       The expiration time (in seconds) key.
     * @param defaultExpireAfter   The expiration time (in seconds) to use if the key is not set.
     * @param expireStrategyKey    The {@link ExpiryPolicy} key. {@link ExpiryPolicy#MODIFIED} is used if the key is not set or its value is not valid.
     * @return The {@link CacheConfig}.
     * @since 2.1.0
     */
    public CacheConfig getCacheConfig(K maxSizeKey, int defaultMaxSize, K expireAfterKey, int defaultExpireAfter, K expireStrategyKey) {
        int maxSize = Math.max(0, getInteger(maxSizeKey).orElse(defaultMaxSize));
        int expireAfter = Math.max(0, getInteger(expireAfterKey).orElse(defaultExpireAfter));
        ExpiryPolicy expiryPolicy = Optional.ofNullable(getString(expireStrategyKey))
                .flatMap(v -> Arrays.stream(ExpiryPolicy.values())
                        .filter(e -> e.name().equalsIgnoreCase(v.trim()))
                        .findFirst())
                .orElse(ExpiryPolicy.MODIFIED);

        return new CacheConfig(maxSize, expireAfter, expiryPolicy);
    }
}
