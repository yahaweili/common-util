package com.ynding.cloud.common.util.img;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Base64;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 图片上传七牛云工具类
 *
 * @author ynding
 * @version 2019/01/20
 */
@Slf4j
public class QiniuCloudUtil {

    /**
     * 设置需要操作的账号的AK和SK,进入七牛云个人中心-密钥管理可以看到
     */
    private static final String ACCESS_KEY = "ijGfA8Xs7yTlxvUGH7x_EE2EJZIWiULw4y_DheIX";
    private static final String SECRET_KEY = "bPzxo7baBdyNL2B_15fVExdtmeUjNQFCDq8Zdfu_";

    /**
     * 要上传的空间名称
     */
    private static final String BUCKET_NAME = "ynding";

    /**
     * 密钥
     */
    private static final Auth AUTH = Auth.create(ACCESS_KEY, SECRET_KEY);

    /**
     * 你的图片上传路径,进入内容管理，复制外链默认域名
     */
    private static final String DOMAIN = "http://plm6otg7d.bkt.clouddn.com/";

    /**
     * 样式
     */
    private static final String STYLE = "自定义的图片样式";

    public String getUpToken() {
        return AUTH.uploadToken(BUCKET_NAME, null, 3600, new StringMap().put("insertOnly", 1));
    }

    /**
     * 普通上传
     *
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return
     * @throws IOException 异常
     */
    public String upload(String filePath, String fileName) throws IOException {
        // 创建上传对象
        UploadManager uploadManager = new UploadManager();
        try {
            // 调用put方法上传
            String token = AUTH.uploadToken(BUCKET_NAME);
            if (StringUtils.isEmpty(token)) {
                log.info("未获取到token，请重试！");
                return null;
            }
            Response res = uploadManager.put(filePath, fileName, token);
            // 打印返回的信息
            log.info(res.bodyString());
            if (res.isOK()) {
                Ret ret = res.jsonToObject(Ret.class);
                //如果不需要对图片进行样式处理，则使用以下方式即可
                return DOMAIN + ret.key;
                //return DOMAIN + ret.key + "?" + style;
            }
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            log.error(r.toString());
            try {
                // 响应的文本信息
                log.error(r.bodyString());
            } catch (QiniuException e1) {
                // ignore
                log.error(e1.getMessage());
            }
        }
        return null;
    }

    /**
     * base64方式上传
     *
     * @param base64 base64
     * @param key    key
     * @return
     * @throws Exception 异常
     */
    public String put64image(byte[] base64, String key) throws Exception {
        String file64 = Base64.encodeToString(base64, 0);
        Integer l = base64.length;
        String url = "http://upload.qiniu.com/putb64/" + l + "/key/" + UrlSafeBase64.encodeToString(key);
        //非华东空间需要根据注意事项 1 修改上传域名
        RequestBody rb = RequestBody.create(null, file64);
        Request request = new Request.Builder().
                url(url).
                addHeader("Content-Type", "application/octet-stream")
                .addHeader("Authorization", "UpToken " + getUpToken())
                .post(rb).build();
        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response = client.newCall(request).execute();
        System.out.println(response);
        //如果不需要添加图片样式，使用以下方式
        return DOMAIN + key;
        //return DOMAIN + key + "?" + style;
    }


    /**
     * 普通删除(暂未使用以下方法，未测试)
     *
     * @param key key
     * @throws IOException 异常
     */
    public void delete(String key) throws IOException {
        // 实例化一个BucketManager对象
        BucketManager bucketManager = new BucketManager(AUTH);
        // 此处的33是去掉：http://ongsua0j7.bkt.clouddn.com/,剩下的key就是图片在七牛云的名称
        key = key.substring(33);
        try {
            // 调用delete方法移动文件
            bucketManager.delete(BUCKET_NAME, key);
        } catch (QiniuException e) {
            // 捕获异常信息
            Response r = e.response;
            System.out.println(r.toString());
        }
    }

    class Ret {
        public long fSize;
        public String key;
        public String hash;
        public int width;
        public int height;
    }
}
