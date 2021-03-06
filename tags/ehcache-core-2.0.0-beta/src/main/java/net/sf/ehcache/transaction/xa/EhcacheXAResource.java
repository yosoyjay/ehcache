/**
 *  Copyright 2003-2009 Terracotta, Inc.
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

package net.sf.ehcache.transaction.xa;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.xa.XAResource;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.TransactionContext;

/**
 * @author Nabib El-Rahman
 */
public interface EhcacheXAResource extends XAResource {

    /**
     * Getter to the name of the cache wrapped by this XAResource
     * @return {@link net.sf.ehcache.Ehcache#getName} value
     */
    String getCacheName();

    /**
     * Getter to the store wrapped by this XAResource
     * @return the real underlying store (not the Transactional wrapper)
     */
    Store getStore();

    /**
     *
     * @return
     * @throws SystemException
     * @throws RollbackException
     */
    TransactionContext getOrCreateTransactionContext() throws SystemException, RollbackException;

    /**
     * Fall through methods to the underlying cache that will hit potential "guards" or "guarding read-only store"
     * @param key
     * @return
     */
    Element get(Object key);

    /**
     * Fall through methods to the underlying cache that will hit potential "guards" or "guarding read-only store"
     * @param key
     * @return
     */
    Element getQuiet(Object key);

}