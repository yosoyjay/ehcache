/**
 *  Copyright Terracotta, Inc.
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

package net.sf.ehcache.statisticsV2;

import net.sf.ehcache.CacheOperationOutcomes.GetOutcome;
import net.sf.ehcache.CacheOperationOutcomes.PutOutcome;
import net.sf.ehcache.CacheOperationOutcomes.RemoveOutcome;
import net.sf.ehcache.transaction.xa.XaCommitOutcome;
import net.sf.ehcache.transaction.xa.XaRecoveryOutcome;
import net.sf.ehcache.transaction.xa.XaRollbackOutcome;

public class CoreStatisticsPlaceholder implements CoreStatistics {

    @Override
    public CountOperation<GetOutcome> get() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<PutOutcome> put() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<RemoveOutcome> remove() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<net.sf.ehcache.store.StoreOperationOutcomes.GetOutcome> localHeapGet() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<net.sf.ehcache.store.StoreOperationOutcomes.PutOutcome> localHeapPut() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<net.sf.ehcache.store.StoreOperationOutcomes.RemoveOutcome> localHeapRemove() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<net.sf.ehcache.store.StoreOperationOutcomes.GetOutcome> localOffHeapGet() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<net.sf.ehcache.store.StoreOperationOutcomes.PutOutcome> localOffHeapPut() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<net.sf.ehcache.store.StoreOperationOutcomes.RemoveOutcome> localOffHeapRemove() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<net.sf.ehcache.store.StoreOperationOutcomes.GetOutcome> diskGet() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<net.sf.ehcache.store.StoreOperationOutcomes.PutOutcome> diskPut() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<net.sf.ehcache.store.StoreOperationOutcomes.RemoveOutcome> diskRemove() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<XaCommitOutcome> xaCommit() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<XaRecoveryOutcome> xaRecovery() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public CountOperation<XaRollbackOutcome> xaRollback() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getEvictionCount() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getExpiredCount() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getCacheMissCountExpired() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getEvictedCount() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLocalHeapSizeInBytes() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long calculateInMemorySize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getMemoryStoreSize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDiskStoreSize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long calculateOffHeapSize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getOffHeapStoreSize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getObjectCount() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getMemoryStoreObjectCount() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getDiskStoreObjectCount() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLocalHeapSize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getWriterQueueSize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getOffHeapStoreObjectCount() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalHeapSizeString() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getWriterQueueLength() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLocalDiskSize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLocalOffHeapSize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLocalDiskSizeInBytes() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLocalOffHeapSizeInBytes() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public long getSize() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }


}
