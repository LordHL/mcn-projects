package com.hiekn.boot.autoconfigure.qiniu;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

public class QiNiu {

    private QiNiuProperties properties;

    public QiNiu(QiNiuProperties properties) {
        this.properties = properties;
    }

    public void upload(final byte[] data, final String fileName) throws QiniuException {
        UploadManager uploadManager = new UploadManager();
        Auth auth = Auth.create(properties.getAk(), properties.getSk());
        String token = auth.uploadToken(properties.getBucket());
        uploadManager.put(data, fileName, token);
    }
}
