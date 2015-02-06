package org.ccci.gto.android.common.api;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.util.UriUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractApi<R extends AbstractApi.Request> {
    private static final int DEFAULT_ATTEMPTS = 3;

    @NonNull
    private final Uri mBaseUri;

    protected AbstractApi(@NonNull final String baseUri) {
        this(Uri.parse(baseUri.endsWith("/") ? baseUri : baseUri + "/"));
    }

    protected AbstractApi(@NonNull final Uri baseUri) {
        mBaseUri = baseUri;
    }

    @NonNull
    protected final HttpURLConnection sendRequest(@NonNull final R request) throws ApiException {
        return this.sendRequest(request, DEFAULT_ATTEMPTS);
    }

    @NonNull
    protected final HttpURLConnection sendRequest(@NonNull final R request, final int attempts)
            throws ApiException {
        Uri.Builder uri = null;
        try {
            // build the request uri
            uri = this.buildRequestUri(request, mBaseUri.buildUpon());

            // build base request object
            final HttpURLConnection conn = (HttpURLConnection) new URL(uri.build().toString()).openConnection();
            conn.setRequestMethod(request.method.toString());
            if (request.accept != null) {
                conn.addRequestProperty("Accept", request.accept.type);
            }
            conn.setInstanceFollowRedirects(request.followRedirects);

            // no need to explicitly execute, accessing the response triggers the execute
            conn.getResponseCode();

            // return the connection for method specific handling
            return conn;
        } catch (final MalformedURLException e) {
            throw new RuntimeException("invalid Request URL: " + uri.build().toString(), e);
        } catch (final IOException e) {
            throw new ApiSocketException(e);
        }
    }

    @NonNull
    protected Uri.Builder buildRequestUri(@NonNull final R request, @NonNull final Uri.Builder uri)
            throws ApiException {
        // build the request uri
        uri.appendEncodedPath(request.path);
        if (request.params.size() > 0) {
            if (request.replaceParams) {
                final List<String> keys = new ArrayList<>();
                for (final Request.Parameter param : request.params) {
                    keys.add(param.name);
                }
                UriUtils.removeQueryParams(uri, keys.toArray(new String[keys.size()]));
            }
            for (final Request.Parameter param : request.params) {
                uri.appendQueryParameter(param.name, param.value);
            }
        }
        return uri;
    }

    protected static class Request {
        public enum MediaType {
            APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"), APPLICATION_JSON("application/json"),
            APPLICATION_XML("application/xml"), TEXT_PLAIN("text/plain");

            private final String type;

            private MediaType(final String type) {
                this.type = type;
            }
        }

        public enum Method {GET, POST, PUT, DELETE}

        public static final class Parameter {
            private final String name;
            private final String value;

            public Parameter(@NonNull final String name, @NonNull final String value) {
                this.name = name;
                this.value = value;
            }
        }

        @NonNull
        public Method method = Method.GET;

        // uri attributes
        @NonNull
        final String path;
        public final Collection<Parameter> params = new ArrayList<>();
        public boolean replaceParams = false;

        // miscellaneous attributes
        @Nullable
        public MediaType accept = null;
        public boolean followRedirects = false;

        public Request(@NonNull final String path) {
            this.path = path;
        }
    }
}
