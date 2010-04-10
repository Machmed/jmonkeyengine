package com.jme3.asset;

import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * <code>ImplHandler</code> manages the asset loader and asset locator
 * implementations in a thread safe way. This allows implementations
 * which store local persistent data to operate with a multi-threaded system.
 * This is done by keeping an instance of each asset loader and asset
 * locator object in a thread local.
 */
public class ImplHandler {

    private static final Logger logger = Logger.getLogger(ImplHandler.class.getName());

    private final AssetManager owner;
    
    private final ArrayList<ImplThreadLocal> genericLocators =
                new ArrayList<ImplThreadLocal>();

    private final Hashtable<String, ImplThreadLocal> loaders =
                new Hashtable<String, ImplThreadLocal>();

    private final Hashtable<String, ImplThreadLocal> locators =
                new Hashtable<String, ImplThreadLocal>();

    public ImplHandler(AssetManager owner){
        this.owner = owner;
    }

    protected class ImplThreadLocal extends ThreadLocal {

        private final Class<?> type;
        private final String path;

        public ImplThreadLocal(Class<?> type){
            this.type = type;
            path = null;
        }

        public ImplThreadLocal(Class<?> type, String path){
            this.type = type;
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public Class<?> getTypeClass(){
            return type;
        }

        @Override
        protected Object initialValue(){
            try {
                return type.newInstance();
            } catch (InstantiationException ex) {
                logger.severe("Cannot create locator of type "+
                              type.getName()+", does the class"+
                              " have an empty and publically accessible"+
                              " constructor?");
                logger.throwing(type.getName(), "<init>", ex);
            } catch (IllegalAccessException ex) {
                logger.severe("Cannot create locator of type "+
                              type.getName()+", does the class"+
                              " have an empty and publically accessible"+
                              " constructor?");
                logger.throwing(type.getName(), "<init>", ex);
            }
            return null;
        }
    }

    /**
     * Attempts to locate the given resource name.
     * @param name The full name of the resource.
     * @return The AssetInfo containing resource information required for
     * access, or null if not found.
     */
    public AssetInfo tryLocate(AssetKey key){
        ImplThreadLocal extLocal = null;
        synchronized (locators){
            extLocal = locators.get(key.getExtension());
        }

        if (extLocal != null){
            AssetLocator locator = (AssetLocator) extLocal.get();
            if (extLocal.getPath() != null){
                locator.setRootPath(extLocal.getPath());
            }
            AssetInfo info = locator.locate(owner, key);
            if (info != null)
                return info;
        }
        synchronized (locators){
            if (genericLocators.size() == 0)
                return null;

            for (ImplThreadLocal local : genericLocators){
                AssetLocator locator = (AssetLocator) local.get();
                if (local.getPath() != null){
                    locator.setRootPath((String) local.getPath());
                }
                AssetInfo info = locator.locate(owner, key);
                if (info != null)
                    return info;
            }
        }
        return null;
    }

    public int getLocatorCount(){
        synchronized (locators){
            return locators.size() + genericLocators.size();
        }
    }

    /**
     * Returns the AssetLoader registered for the given extension
     * of the current thread.
     * @return AssetLoader registered with addLoader.
     */
    public AssetLoader aquireLoader(AssetKey key){
        synchronized (loaders){
            ImplThreadLocal local = loaders.get(key.getExtension());
            if (local != null){
                AssetLoader loader = (AssetLoader) local.get();
                return loader;
            }
            return null;
        }
    }

    public void addLoader(final Class<?> loaderType, String ... extensions){
        ImplThreadLocal local = new ImplThreadLocal(loaderType);
        for (String extension : extensions){
            extension = extension.toLowerCase();
            synchronized (loaders){
                loaders.put(extension, local);
            }
        }
    }

    public void addLocator(final Class<?> locatorType, String rootPath, String ... extensions){
        ImplThreadLocal local = new ImplThreadLocal(locatorType, rootPath);
        if (extensions.length == 1 && extensions[0].equals("*")){
            synchronized (locators){
                genericLocators.add(local);
            }
        }else{
            for (String extension : extensions){
                extension = extension.toLowerCase();
                synchronized (locators){
                    locators.put(extension, local);
                }
            }
        }
    }

    public void removeLocator(final Class<?> locatorType, String rootPath, String ... extensions){
        if (extensions.length == 1 && extensions[0].equals("*")){
            synchronized (locators){
                Iterator<ImplThreadLocal> it = genericLocators.iterator();
                while (it.hasNext()){
                    ImplThreadLocal locator = it.next();
                    if (locator.getPath().equals(rootPath) &&
                        locator.getTypeClass().equals(locatorType)){
                        it.remove();
                    }
                }
            }
        }else{
            for (String extension : extensions){
                extension = extension.toLowerCase();
                synchronized (locators){
                    Iterator<ImplThreadLocal> it = locators.values().iterator();
                    while (it.hasNext()){
                        ImplThreadLocal locator = it.next();
                        if (locator.getPath().equals(rootPath) &&
                            locator.getTypeClass().equals(locatorType)){
                            it.remove();
                        }
                    }
                }
            }
        }
    }

}
