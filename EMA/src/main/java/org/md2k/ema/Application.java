package org.md2k.ema;

/**
 * Created by monowar on 3/10/16.
 */
public class Application {
    String id;
    String name;
    String file_name;
    String package_name;
    long timeout;

    public String getPackage_name() {
        return package_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getTimeout() {
        return timeout;
    }
}
