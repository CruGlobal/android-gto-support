package org.ccci.gto.android.common.jsonapi.model;

import android.support.annotation.Nullable;

public class JsonApiError {
    public static final String JSON_ERROR_STATUS = "status";
    public static final String JSON_ERROR_DETAIL = "detail";

    private String mDetail;
    private Integer mStatus;

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(@Nullable final String detail) {
        mDetail = detail;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public void setStatus(@Nullable final Integer status) {
        mStatus = status;
    }
}
