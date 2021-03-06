/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */
package org.terracotta.modules.ehcache.store;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.toolkit.cache.ToolkitCache;
import org.terracotta.toolkit.config.Configuration;
import org.terracotta.toolkit.events.ToolkitNotificationEvent;
import org.terracotta.toolkit.events.ToolkitNotificationListener;
import org.terracotta.toolkit.events.ToolkitNotifier;
import org.terracotta.toolkit.internal.cache.ToolkitCacheInternal;
import org.terracotta.toolkit.store.ToolkitConfigFields;

import java.io.Serializable;

public class CacheConfigChangeBridge implements CacheConfigurationListener, ToolkitNotificationListener {

  private static final Logger            LOG = LoggerFactory.getLogger(CacheConfigChangeBridge.class);

  private final ToolkitNotifier          notifier;
  private final ToolkitCache             backend;
  private final CacheConfiguration       cacheConfiguration;
  private final String                   fullyQualifiedEhcacheName;

  public CacheConfigChangeBridge(String fullyQualifiedEhcacheName, ToolkitCacheInternal backend,
                                 ToolkitNotifier<CacheConfigChangeNotificationMsg> notifier,
                                 CacheConfiguration cacheConfiguration) {
    this.cacheConfiguration = cacheConfiguration;
    this.fullyQualifiedEhcacheName = fullyQualifiedEhcacheName;
    this.backend = backend;
    this.notifier = notifier;
  }

  public void connectConfigs() {
    // Match up the global config values to the local cache configuration first.
    initializeFromCluster();

    cacheConfiguration.addConfigurationListener(this);
    notifier.addNotificationListener(this);
  }

  public void disconnectConfigs() {
    cacheConfiguration.removeConfigurationListener(this);
    notifier.removeNotificationListener(this);
  }

  private void initializeFromCluster() {
    Configuration clusterConfig = backend.getConfiguration();
    if (clusterConfig.hasField(ToolkitConfigFields.MAX_TOTAL_COUNT_FIELD_NAME)) {
      cacheConfiguration.internalSetMaxEntriesInCache(
          mapTotalCountToMaxEntriesInCache(clusterConfig.getInt(ToolkitConfigFields.MAX_TOTAL_COUNT_FIELD_NAME)));
    }

    if (clusterConfig.hasField(ToolkitConfigFields.MAX_TTL_SECONDS_FIELD_NAME) ||
        clusterConfig.hasField(ToolkitConfigFields.MAX_TTI_SECONDS_FIELD_NAME)) {

      int tti = clusterConfig.hasField(ToolkitConfigFields.MAX_TTI_SECONDS_FIELD_NAME) ?
          clusterConfig.getInt(ToolkitConfigFields.MAX_TTI_SECONDS_FIELD_NAME) : 0;
      int ttl = clusterConfig.hasField(ToolkitConfigFields.MAX_TTL_SECONDS_FIELD_NAME) ?
          clusterConfig.getInt(ToolkitConfigFields.MAX_TTL_SECONDS_FIELD_NAME) : 0;
      if (tti != 0 || ttl != 0) {
        cacheConfiguration.internalSetEternal(false);
        cacheConfiguration.internalSetTimeToIdle(tti);
        cacheConfiguration.internalSetTimeToLive(ttl);
      } else {
        // We do not want to override the eternal flag since we can't make the assumption that having 0 TTI/TTL means
        // the cache is eternal. The user could very well have set the TTI/TTL to 0 with the intent to set it to something
        // non-zero later.
        cacheConfiguration.internalSetTimeToIdle(0);
        cacheConfiguration.internalSetTimeToLive(0);
      }
    }

    if (clusterConfig.hasField(ToolkitConfigFields.MAX_COUNT_LOCAL_HEAP_FIELD_NAME)) {
      cacheConfiguration.internalSetMemCapacity(clusterConfig.getInt(ToolkitConfigFields.MAX_COUNT_LOCAL_HEAP_FIELD_NAME));
    }
    if (clusterConfig.hasField(ToolkitConfigFields.MAX_BYTES_LOCAL_HEAP_FIELD_NAME)) {
      cacheConfiguration.internalSetMemCapacityInBytes(clusterConfig.getLong(ToolkitConfigFields.MAX_BYTES_LOCAL_HEAP_FIELD_NAME));
    }
    if (clusterConfig.hasField(ToolkitConfigFields.OFFHEAP_ENABLED_FIELD_NAME)) {
      cacheConfiguration.internalSetOverflowToOffheap(clusterConfig.getBoolean(ToolkitConfigFields.OFFHEAP_ENABLED_FIELD_NAME));
    }
    if (clusterConfig.hasField(ToolkitConfigFields.MAX_BYTES_LOCAL_OFFHEAP_FIELD_NAME)) {
      cacheConfiguration.internalSetMaxBytesLocalOffheap(clusterConfig.getLong(ToolkitConfigFields.MAX_BYTES_LOCAL_OFFHEAP_FIELD_NAME));
    }
  }

  private void change(DynamicConfigType type, Serializable newValue, boolean notifyRemote) {
    backend.setConfigField(type.getToolkitConfigName(), newValue);
    if (notifyRemote) {
    notifier.notifyListeners(new CacheConfigChangeNotificationMsg(fullyQualifiedEhcacheName, type
        .getToolkitConfigName(), newValue));
    }
  }

  @Override
  public void timeToIdleChanged(long oldTimeToIdle, long newTimeToIdle) {
    change(DynamicConfigType.MAX_TTI_SECONDS, (int) newTimeToIdle, true);
  }

  @Override
  public void timeToLiveChanged(long oldTimeToLive, long newTimeToLive) {
    change(DynamicConfigType.MAX_TTL_SECONDS, (int) newTimeToLive, true);
  }

  @Override
  public void diskCapacityChanged(int oldCapacity, int newCapacity) {
    // change(DynamicConfigType.MAX_TOTAL_COUNT, newCapacity, true);
    throw new IllegalStateException("Disk capacity should not change for terracotta clustered caches");
  }

  @Override
  public void maxEntriesInCacheChanged(long oldCapacity, long newCapacity) {
    if (newCapacity > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Values greater than Integer.MAX_VALUE are not currently supported.");
    } else {
      change(DynamicConfigType.MAX_TOTAL_COUNT, mapMaxEntriesInCacheToTotalCount((int) newCapacity), true);
    }
  }

  @Override
  public void memoryCapacityChanged(int oldCapacity, int newCapacity) {
    change(DynamicConfigType.MAX_COUNT_LOCAL_HEAP, newCapacity, false);
  }

  @Override
  public void maxBytesLocalHeapChanged(long oldValue, long newValue) {
    change(DynamicConfigType.MAX_BYTES_LOCAL_HEAP, newValue, false);
  }

  @Override
  public void maxBytesLocalDiskChanged(long oldValue, long newValue) {
    // not supported
  }

  @Override
  public void loggingChanged(boolean oldValue, boolean newValue) {
    // TODO: should this be supported?
  }

  @Override
  public void registered(CacheConfiguration config) {
    // noop
  }

  @Override
  public void deregistered(CacheConfiguration config) {
    // noop
  }

  @Override
  public void onNotification(ToolkitNotificationEvent event) {
    if (shouldProcessNotification(event)) {
      processConfigChangeNotification((CacheConfigChangeNotificationMsg) event.getMessage());
    } else {
      LOG.warn("Ignoring uninterested notification - " + event);
    }
  }

  private void processConfigChangeNotification(CacheConfigChangeNotificationMsg notification) {
    try {
      DynamicConfigType type = DynamicConfigType.getTypeFromToolkitConfigName(notification.getToolkitConfigName());
      Object newValue = notification.getNewValue();
      switch (type) {
        case MAX_TTI_SECONDS: {
          cacheConfiguration.internalSetTimeToIdle(getLong(newValue));
          break;
        }
        case MAX_TTL_SECONDS: {
          cacheConfiguration.internalSetTimeToLive(getLong(newValue));
          break;
        }
        case MAX_TOTAL_COUNT: {
          cacheConfiguration.internalSetMaxEntriesInCache(mapTotalCountToMaxEntriesInCache(getInt(newValue)));
          break;
        }
        case MAX_COUNT_LOCAL_HEAP: {
          cacheConfiguration.internalSetMemCapacity(getInt(newValue));
          break;
        }
        case MAX_BYTES_LOCAL_HEAP: {
          cacheConfiguration.internalSetMemCapacityInBytes(getLong(newValue));
          break;
        }
      }
    } catch (IllegalArgumentException e) {
      LOG.warn("Notification will be ignored. Caught IllegalArgumentException while processing notification: "
               + notification + ", exception: " + e.getMessage());
    }
  }

  private boolean shouldProcessNotification(ToolkitNotificationEvent event) {
    return event.getMessage() instanceof CacheConfigChangeNotificationMsg
           && ((CacheConfigChangeNotificationMsg) event.getMessage()).getFullyQualifiedEhcacheName()
               .equals(fullyQualifiedEhcacheName);
  }

  private static enum DynamicConfigType {
    MAX_TOTAL_COUNT(ToolkitConfigFields.MAX_TOTAL_COUNT_FIELD_NAME), MAX_COUNT_LOCAL_HEAP(
        ToolkitConfigFields.MAX_COUNT_LOCAL_HEAP_FIELD_NAME), MAX_BYTES_LOCAL_HEAP(
        ToolkitConfigFields.MAX_BYTES_LOCAL_HEAP_FIELD_NAME), MAX_TTI_SECONDS(
        ToolkitConfigFields.MAX_TTI_SECONDS_FIELD_NAME), MAX_TTL_SECONDS(ToolkitConfigFields.MAX_TTL_SECONDS_FIELD_NAME);

    private final String toolkitConfigName;

    private DynamicConfigType(String toolkitConfigName) {
      this.toolkitConfigName = toolkitConfigName;
    }

    public String getToolkitConfigName() {
      return toolkitConfigName;
    }

    public static DynamicConfigType getTypeFromToolkitConfigName(final String toolkitName) {
      for (DynamicConfigType type : DynamicConfigType.values()) {
        if (type.getToolkitConfigName().equals(toolkitName)) { return type; }
      }
      throw new IllegalArgumentException("Unknown toolkit config name - " + toolkitName);
    }
  }

  private static long getLong(Object newValue) {
    if (newValue instanceof Integer) {
      return ((Integer) newValue).intValue();
    } else if (newValue instanceof Long) {
      return ((Long) newValue).longValue();
    } else {
      throw new IllegalArgumentException("Expected long value but got: " + newValue);
    }
  }

  private static int getInt(Object newValue) {
    if (newValue instanceof Integer) {
      return ((Integer) newValue).intValue();
    } else {
      throw new IllegalArgumentException("Expected int value but got: " + newValue);
    }
  }

  public static int mapMaxEntriesInCacheToTotalCount(int maxEntriesInCache) {
    if (maxEntriesInCache == 0) {
      return -1;
    }
    return maxEntriesInCache;
  }

  private static int mapTotalCountToMaxEntriesInCache(int totalCount) {
    if (totalCount == -1) {
      return 0;
    }
    return totalCount;
  }
}
