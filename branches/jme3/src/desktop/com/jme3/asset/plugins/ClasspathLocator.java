package com.jme3.asset.plugins;

import com.jme3.asset.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

/**
 * The <code>ClasspathLocator</code> looks up an asset in the classpath.
 * @author Kirill Vainer
 */
public class ClasspathLocator implements AssetLocator {

    private static final Logger logger = Logger.getLogger(ClasspathLocator.class.getName());
    private String root = "/";

    private static class ClasspathAssetInfo extends AssetInfo {

        private URLConnection conn;

        public ClasspathAssetInfo(AssetManager manager, AssetKey key, URLConnection conn){
            super(manager, key);
            this.conn = conn;
        }

        @Override
        public InputStream openStream() {
            try{
                return conn.getInputStream();
            }catch (IOException ex){
                return null; // failure..
            }
        }
    }

    public ClasspathLocator(){
    }

    public void setRootPath(String rootPath) {
        this.root = rootPath;
        if (!root.endsWith("/"))
            root += "/";
    }
    
    public AssetInfo locate(AssetManager manager, AssetKey key) {
        URL url;
        String name = key.getName();
        if (name.startsWith(root)){
            url = ClasspathLocator.class.getResource(name);
        }else{
            url = ClasspathLocator.class.getResource(root + name);
        }
        if (url == null)
            return null;
        
        try{
            URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            return new ClasspathAssetInfo(manager, key, conn);
        }catch (IOException ex){
            return null;
        }
        
    }


}
