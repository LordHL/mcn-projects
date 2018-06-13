package com.hiekn.boot.autoconfigure.base.util;

public final class CommonHelper {

    public static String parsePath(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

}
