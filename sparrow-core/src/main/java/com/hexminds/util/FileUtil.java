package com.hexminds.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class FileUtil {
    public static String getBaseDir() {
        URL url;
        String path;
        File file;

        url = ClassLoader.getSystemResource("");
        if (url != null) {
            path = url.getPath();
            if (path != null && path.length() != 0)
                return path;
        }

        url = FileUtil.class.getProtectionDomain().getCodeSource().getLocation();
        if (url == null)
            return null;
        path = url.getPath();
        if (path == null || path.length() == 0)
            return null;
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        if (path == null || path.length() == 0)
            return null;
        file = new File(path);
        if (file.isFile() && path.endsWith(".jar"))
            path = path.substring(0, path.lastIndexOf(File.separatorChar));
        file = new File(path);
        path = file.getAbsolutePath();
        return path;
    }

    public boolean createDir(String dir) {
        if (dir == null || dir.length() == 0)
            return false;
        return true;
    }
}
