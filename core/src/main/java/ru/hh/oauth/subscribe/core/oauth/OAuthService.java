package ru.hh.oauth.subscribe.core.oauth;

import com.ning.http.client.AsyncHttpClient;
import java.util.concurrent.Future;

import ru.hh.oauth.subscribe.core.exceptions.OAuthException;
import ru.hh.oauth.subscribe.core.model.AbstractRequest;
import ru.hh.oauth.subscribe.core.model.OAuthAsyncRequestCallback;
import ru.hh.oauth.subscribe.core.model.OAuthConfig;
import ru.hh.oauth.subscribe.core.model.OAuthConfigAsync;
import ru.hh.oauth.subscribe.core.model.Token;
import ru.hh.oauth.subscribe.core.model.Verifier;
import ru.hh.oauth.subscribe.core.model.ForceTypeOfHttpRequest;
import ru.hh.oauth.subscribe.core.model.SubScribeConfig;

/**
 * The main SubScribe object.
 *
 * A facade responsible for the retrieval of request and access tokens and for the signing of HTTP requests.
 *
 * @author Pablo Fernandez
 */
public abstract class OAuthService {

    private final OAuthConfig config;
    private AsyncHttpClient asyncHttpClient;

    public OAuthService(OAuthConfig config) {
        this.config = config;
        final ForceTypeOfHttpRequest forceTypeOfHttpRequest = SubScribeConfig.getForceTypeOfHttpRequests();
        if (config instanceof OAuthConfigAsync) {
            if (ForceTypeOfHttpRequest.FORCE_SYNC_ONLY_HTTP_REQUESTS == forceTypeOfHttpRequest) {
                throw new OAuthException("Cannot use async operations, only sync");
            }
            if (ForceTypeOfHttpRequest.PREFER_SYNC_ONLY_HTTP_REQUESTS == forceTypeOfHttpRequest) {
                config.log("Cannot use async operations, only sync");
            }
            asyncHttpClient = new AsyncHttpClient(((OAuthConfigAsync) config).getAsyncHttpClientConfig());
        } else {
            if (ForceTypeOfHttpRequest.FORCE_ASYNC_ONLY_HTTP_REQUESTS == forceTypeOfHttpRequest) {
                throw new OAuthException("Cannot use sync operations, only async");
            }
            if (ForceTypeOfHttpRequest.PREFER_ASYNC_ONLY_HTTP_REQUESTS == forceTypeOfHttpRequest) {
                config.log("Cannot use sync operations, only async");
            }
        }
    }

    public AsyncHttpClient getAsyncHttpClient() {
        return asyncHttpClient;
    }

    public void closeAsyncClient() {
        asyncHttpClient.close();
    }

    public OAuthConfig getConfig() {
        return config;
    }

    /**
     * Retrieve the request token.
     *
     * @return request token
     */
    public abstract Token getRequestToken();

    public abstract Token getAccessToken(Token requestToken, Verifier verifier);

    /**
     * Signs am OAuth request
     *
     * @param accessToken access token (obtained previously)
     * @param request request to sign
     */
    public abstract void signRequest(Token accessToken, AbstractRequest request);

    /**
     * Start the request to retrieve the access token. The optionally provided callback will be called with the Token when it is available.
     *
     * @param requestToken request token (obtained previously or null)
     * @param verifier verifier code
     * @param callback optional callback
     * @return Future
     */
    public abstract Future<Token> getAccessTokenAsync(Token requestToken, Verifier verifier, OAuthAsyncRequestCallback<Token> callback);

    /**
     * Returns the OAuth version of the service.
     *
     * @return oauth version as string
     */
    public abstract String getVersion();

    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     *
     * @param requestToken the request token you need to authorize
     * @return the URL where you should redirect your users
     */
    public abstract String getAuthorizationUrl(Token requestToken);

}