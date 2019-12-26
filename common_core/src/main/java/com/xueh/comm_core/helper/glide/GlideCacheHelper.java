package com.xueh.comm_core.helper.glide;

import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.signature.EmptySignature;

import java.io.File;
import java.io.IOException;

/**
 * 当前包名: com.sunlands.comm_core.helper.glide
 * 创 建 人: xueh
 * 创建日期: 2019/4/11 14:04
 * 备注：
 */
public class GlideCacheHelper {
    //4.0
    public static File getCacheFile(String id) {
        DataCacheKey dataCacheKey = new DataCacheKey(new GlideUrl(id), EmptySignature.obtain());
        SafeKeyGenerator safeKeyGenerator = new SafeKeyGenerator();
        String safeKey = safeKeyGenerator.getSafeKey(dataCacheKey);
        try {
            int cacheSize = 100 * 1000 * 1000;
            DiskLruCache diskLruCache = DiskLruCache.open(new File(Utils.getApp().getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR), 1, 1, cacheSize);
            DiskLruCache.Value value = diskLruCache.get(safeKey);
            if (value != null) {
                return value.getFile(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
