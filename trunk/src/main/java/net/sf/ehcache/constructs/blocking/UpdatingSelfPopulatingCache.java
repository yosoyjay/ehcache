/**
 *  Copyright 2003-2006 Greg Luck
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache.constructs.blocking;


import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.concurrent.Mutex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;


/**
 * A {@link net.sf.ehcache.Cache} backed cache that creates entries on demand.
 * <p/>
 * Clients of the cache simply call it without needing knowledge of whether
 * the entry exists in the cache, or whether it needs updating before use.
 * <p/>
 * <p/>
 * Thread safety depends on the factory being used. The UpdatingCacheEntryFactory should be made
 * thread safe. In addition users of returned values should not modify their contents.
 *
 * @author Greg Luck
 * @version $Id$
 * @see SelfPopulatingCollectionCache
 */
public class UpdatingSelfPopulatingCache extends SelfPopulatingCache {
    private static final Log LOG = LogFactory.getLog(UpdatingSelfPopulatingCache.class.getName());

    /**
     * Creates a SelfPopulatingCache.
     */
    public UpdatingSelfPopulatingCache(Ehcache cache, final UpdatingCacheEntryFactory factory)
            throws CacheException {
        super(cache, factory);
    }

    /**
     * Looks up an object.
     * <p/>
     * If null, it creates it. If not null, it updates it. For performance this method should only be
     * used with {@link UpdatingCacheEntryFactory}'s
     * <p/>
     * It is expected that
     * gets, which update as part of the get, might take considerable time. Access to the cache cannot be blocked
     * while that is happening. This method is therefore not synchronized. Mutexes are used for thread safety based on key
     *
     * @param key
     * @return a value
     * @throws net.sf.ehcache.CacheException
     */
    public Element get(final Serializable key) throws LockTimeoutException {
        String oldThreadName = Thread.currentThread().getName();
        setThreadName("get", key);


        try {


            Ehcache backingCache = getCache();
            Element element = backingCache.get(key);

            if (element == null) {
                element = super.get(key);
            } else {
                Mutex lock = getLockForKey(key);
                try {
                    lock.acquire();
                    update(key);
                } finally {
                    lock.release();
                }
            }
            return element;
        } catch (final Throwable throwable) {
            // Could not fetch - Ditch the entry from the cache and rethrow
            setThreadName("put", key);
            put(new Element(key, null));
            throw new LockTimeoutException("Could not fetch object for cache entry \"" + key + "\".", throwable);
        } finally {
            Thread.currentThread().setName(oldThreadName);
        }
    }

    private void update(final Serializable key) {
        try {
            Ehcache backingCache = getCache();
            final Element element = backingCache.getQuiet(key);

            if (element == null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(getName() + ": entry with key " + key + " has been removed - skipping it");
                }
            }

            refreshElement(element, backingCache);
        } catch (final Exception e) {
            // Collect the exception and keep going.
            // Throw the exception once all the entries have been refreshed
            // If the refresh fails, keep the old element. It will simply become staler.
            LOG.warn(getName() + "Could not refresh element " + key, e);
        }
    }

    /**
     * This method should not be used. Because elements are always updated before they are
     * returned, it makes no sense to refresh this cache.
     */
    public void refresh() throws CacheException {
        throw new CacheException("UpdatingSelfPopulatingCache objects should not be refreshed.");
    }

}
