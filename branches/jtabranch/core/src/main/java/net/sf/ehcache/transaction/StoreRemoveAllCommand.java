package net.sf.ehcache.transaction;

import net.sf.ehcache.store.Store;

/**
 * @author Alex Snaps
 */
public class StoreRemoveAllCommand implements StoreWriteCommand {
    public boolean execute(final Store store) {
        store.removeAll();
        return true;
    }

    public boolean isPut(Object key) {
        return false;
    }

    public boolean isRemove(Object key) {
        return true;
    }

    public String getCommandName() {
        return Command.REMOVE_ALL;
    }
}
