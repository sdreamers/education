package com.sixing.base.utils;

import java.util.StringTokenizer;

public class RequestObjectValidator {

    public static final String IMG_URL_DEML = ",";

    /**
     * Only store 1 url link for preview image.
     * @param imgUrl
     * @return
     */
    public static String parseValidPreviewImgUrl(String imgUrl) {
        String newUrl = "";
        if (StringUtils.isBlank(imgUrl)) {
            return newUrl;
        }

        StringTokenizer st = new StringTokenizer(imgUrl, IMG_URL_DEML);
        while (st.hasMoreElements()) {
            String obj = (String) st.nextElement();
            if (StringUtils.isNotBlank(obj)) {
                newUrl = obj;
                break;
            }
        }

        return newUrl;
    }
}
