package org.ccci.gto.android.common.jsonapi.model;

import androidx.annotation.Nullable;

import org.json.JSONObject;

public class JsonApiError {
    public static final String JSON_ERROR_STATUS = "status";
    public static final String JSON_ERROR_DETAIL = "detail";
    public static final String JSON_ERROR_SOURCE = "source";
    public static final String JSON_ERROR_META = "meta";

    @Nullable
    private Integer mStatus;
    @Nullable
    private String mDetail;

    @Nullable
    private Source mSource;

    @Nullable
    private JSONObject mRawMeta;

    @Nullable
    public String getDetail() {
        return mDetail;
    }

    public void setDetail(@Nullable final String detail) {
        mDetail = detail;
    }

    @Nullable
    public Integer getStatus() {
        return mStatus;
    }

    public void setStatus(@Nullable final Integer status) {
        mStatus = status;
    }

    @Nullable
    public Source getSource() {
        return mSource;
    }

    public void setSource(@Nullable final Source source) {
        mSource = source;
    }

    @Nullable
    public JSONObject getRawMeta() {
        return mRawMeta;
    }

    public void setRawMeta(@Nullable final JSONObject meta) {
        mRawMeta = meta;
    }

    public static class Source {
        public static final String JSON_ERROR_SOURCE_POINTER = "pointer";

        @Nullable
        private String mPointer;

        @Nullable
        public String getPointer() {
            return mPointer;
        }

        public void setPointer(@Nullable final String pointer) {
            mPointer = pointer;
        }
    }
}
