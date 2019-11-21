package commerce.cloud.trainings.webapi.occ.base;

import commerce.cloud.trainings.webapi.occ.ICommerceEndpointsUrls;
import commerce.cloud.trainings.webapi.occ.properties.CommerceValuesConfig;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;



/**
 * @author Nick Mandrik
 */


@Component
public class CommerceBaseRestUtil implements ICommerceEndpointsUrls, ICommerceBaseParams {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommerceBaseRestUtil.class);

    private static final Integer AMOUNT_POSSIBLE_REQUESTS = 5;

    private CommerceValuesConfig valuesConfiguration;
    private CloseableHttpClient httpClient;
    private String commerceAccessToken;

    @Autowired
    public CommerceBaseRestUtil(CommerceValuesConfig valuesConfiguration) {
        this.valuesConfiguration = valuesConfiguration;
        startHttpClient();
    }

    /**
     * Validate Commerce Cloud oauth token and execute {@link HttpRequestBase} request.
     */
    public CloseableHttpResponse validateTokenLogAndExecuteRequest(HttpRequestBase initialRequest) throws CommerceRestException {
        validateOauthToken();
        initialRequest.setHeader(AUTHORIZATION_HEADER_ARG,BEARER_SPACE_HEADER_VALUE + commerceAccessToken);
        String url = initialRequest.getURI().getPath();
        if(initialRequest.getURI().getQuery() != null) {
            url += initialRequest.getURI().getQuery();
        }
        LOGGER.info("Execute OCC request: " + url + " ...");
        CloseableHttpResponse httpResponse = executeRequest(initialRequest);

        String response = logResponse(httpResponse, initialRequest.getURI()).getValue();

        try (PrintWriter out = new PrintWriter("src/main/resources/targetFile.tmp")) {
            out.println(response);
        } catch (IOException e) {
            throw new CommerceRestException(e);
        }

        LOGGER.info("Response from OCC: " + url + ". Status code: " + httpResponse.getStatusLine().getStatusCode());
        /*refreshOauthToken();*/
        return httpResponse;
    }

    public CloseableHttpResponse validateTokenAndExecuteRequest(HttpRequestBase initialRequest) throws CommerceRestException {
        validateOauthToken();
        initialRequest.setHeader(AUTHORIZATION_HEADER_ARG,BEARER_SPACE_HEADER_VALUE + commerceAccessToken);
        String url = initialRequest.getURI().getPath();
        if(initialRequest.getURI().getQuery() != null) {
            url += initialRequest.getURI().getQuery();
        }
        LOGGER.info("Execute OCC request: " + url + " ...");
        CloseableHttpResponse httpResponse = executeRequest(initialRequest);

        LOGGER.info("Response from OCC: " + url + ". Status code: " + httpResponse.getStatusLine().getStatusCode());
        return httpResponse;
    }

    /**
     * Execute POST request with json media type
     * @param urlRequest url of the POST request
     * @param jsonParams json parameters in String representation
     * @throws CommerceRestException 1) url invalid or json parameters not correct.
     * 2) Cannot validate or refresh access token. 3) Cannot execute request
     */
    public CloseableHttpResponse executePostJsonRequest(String urlRequest, String jsonParams) throws CommerceRestException {
        URI uri;
        try {
            uri = new URI(urlRequest);
        } catch (URISyntaxException e) {
            throw new CommerceRestException("Invalid uri: " + urlRequest, e);
        }

        HttpPost uploadRequest = new HttpPost(uri);

        uploadRequest.setHeader(CONTENT_TYPE_HEADER_ARG, MediaType.APPLICATION_JSON_VALUE);

        StringEntity entity;
        try {
            entity = new StringEntity(jsonParams);
        } catch (UnsupportedEncodingException e) {
            throw new CommerceRestException("Cannot find encode format for " + uri.getPath(), e);
        }

        uploadRequest.setEntity(entity);

        return validateTokenLogAndExecuteRequest(uploadRequest);
    }

    /**
     * Call to login in Oracle Commerce Cloud.
     */
    private Map.Entry<CloseableHttpResponse, String> loginCall() throws CommerceRestException {
        /*final String url = valuesConfiguration.getAdminDomain() + POST_CCADMIN_LOGIN;

        HttpPost postRequest = new HttpPost(url);
        postRequest.setHeader(CONTENT_TYPE_HEADER_ARG, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        postRequest.setHeader(AUTHORIZATION_HEADER_ARG,BEARER_SPACE_HEADER_VALUE + valuesConfiguration.getApplicationID());

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(GRANT_TYPE_PARAM_ARG, CLIENT_CREDENTIALS_PARAM_VALUE));
        try {
            postRequest.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
           throw new CommerceRestException("Login error", e);
        }

        CloseableHttpResponse loginResponse = executeRequest(postRequest);
        return logResponse(loginResponse, postRequest.getURI());*/

        final String url = valuesConfiguration.getAdminDomain() + POST_CCADMIN_LOGIN;

        HttpPost postRequest = new HttpPost(url);
        postRequest.setHeader(CONTENT_TYPE_HEADER_ARG, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("username", "mike.reutski@vsgcommerce.com"));
        params.add(new BasicNameValuePair("password", "4F&a12ND11!"));
        try {
            postRequest.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            throw new CommerceRestException("Login error", e);
        }

        CloseableHttpResponse loginResponse = executeRequest(postRequest);
        return logResponse(loginResponse, postRequest.getURI());
    }

    /**
     * Call to verify oauth token in Oracle Commerce Cloud.
     * @param isRefreshable indicates whether is need to refresh the oauth token.
     *                      True - token will be refreshed, else - only check status of access
     */
    private Map.Entry<CloseableHttpResponse, String> verifyTokenCall(boolean isRefreshable) throws CommerceRestException {
        String url = valuesConfiguration.getAdminDomain();

        if(isRefreshable) {
            url += POST_CCADMIN_REFRESH;
        } else {
            url += POST_CCADMIN_VERIFY;
        }

        HttpPost postRequest = new HttpPost(url);
        postRequest.setHeader(AUTHORIZATION_HEADER_ARG, BEARER_SPACE_HEADER_VALUE + commerceAccessToken);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(GRANT_TYPE_PARAM_ARG, CLIENT_CREDENTIALS_PARAM_VALUE));
        try {
            postRequest.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            throw new CommerceRestException("Verify oauth token error", e);
        }

        CloseableHttpResponse loginResponse = executeRequest(postRequest);
        return logResponse(loginResponse, postRequest.getURI());
    }


    /**
     * Call to logout in Oracle Commerce Cloud.
     */
    private Map.Entry<CloseableHttpResponse, String> logoutCall() throws CommerceRestException {
        final String url = valuesConfiguration.getAdminDomain() + POST_CCADMIN_LOGOUT;

        HttpPost postRequest = new HttpPost(url);
        CloseableHttpResponse logoutResponse = executeRequest(postRequest);
        return logResponse(logoutResponse, postRequest.getURI());
    }


    /**
     * Validate Commerce Cloud token and access to the OCC Rest endpoints.
     */
    private void validateOauthToken() throws CommerceRestException {
        Map.Entry<CloseableHttpResponse, String> verifyOauthResponse = verifyTokenCall(false);
        try {
            String verifyResponseStr = verifyOauthResponse.getValue();
            JSONObject jsonVerifyResponse = new JSONObject(verifyResponseStr);
            boolean isVerifySuccess = false;
            if(jsonVerifyResponse.has(VERIFY_TOKEN_OUTPUT_PARAM_ARG)) {
                isVerifySuccess = jsonVerifyResponse.getBoolean(VERIFY_TOKEN_OUTPUT_PARAM_ARG);
            }

            if(!isVerifySuccess) {
                LOGGER.info("Oauth token wasn't verified. Attempt to login.");
                commerceAccessToken = null;
                for(int ind = 0; ind < AMOUNT_POSSIBLE_REQUESTS && commerceAccessToken == null; ind++) {
                    Map.Entry<CloseableHttpResponse, String> oauthResponse = loginCall();
                    String loginResponseStr = oauthResponse.getValue();
                    JSONObject jsonResponse = new JSONObject(loginResponseStr);
                    commerceAccessToken = jsonResponse.getString(ACCESS_TOKEN_OUTPUT_PARAM_ARG);
                    LOGGER.info("Created new oauth access token: " + commerceAccessToken);
                }
            }
        } catch (JSONException e) {
            throw new CommerceRestException("Validate response error", e);
        }
    }

    /**
     * Refresh Commerce Cloud token and access to the OCC Rest endpoints.
     */
    private void refreshOauthToken() throws CommerceRestException {
        Map.Entry<CloseableHttpResponse, String> refreshOauthResponse = verifyTokenCall(true);
        try {
            String refreshResponseStr = refreshOauthResponse.getValue();
            JSONObject jsonRefreshResponse = new JSONObject(refreshResponseStr);
            commerceAccessToken = jsonRefreshResponse.getString(ACCESS_TOKEN_OUTPUT_PARAM_ARG);
            LOGGER.info("New oauth access token: " + commerceAccessToken);
        } catch (JSONException e) {
            throw new CommerceRestException("Validate response error", e);
        }
    }

    /**
     * Log response from Commerce Cloud
     */
    private Map.Entry<CloseableHttpResponse, String> logResponse(CloseableHttpResponse response, URI uri)
            throws CommerceRestException {
        String bodyAsString;
        try {
            bodyAsString = EntityUtils.toString(response.getEntity());
            String logMessage = String.format("Action: %s. Status code: %s",
                    uri.getPath(),
                    response.getStatusLine().getStatusCode()
            );
            if(!Objects.equals(String.valueOf(response.getStatusLine().getStatusCode()), OK_STATUS_CODE)) {
                logMessage += ". Message: " + bodyAsString;
            }
            LOGGER.info(logMessage);
        } catch (IOException e) {
            throw new CommerceRestException("Log error.", e);
        }
        return new AbstractMap.SimpleEntry<>(response, bodyAsString);
    }

    /**
     * Base method to execute {@link HttpRequestBase} request.
     * @throws CommerceRestException wrapper of IOException throws by HTTP Client. See {@link CloseableHttpClient#execute(HttpUriRequest)}}
     * to get more information.
     */
    private CloseableHttpResponse executeRequest(HttpRequestBase request) throws CommerceRestException {
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            throw new CommerceRestException("Execute OCC request error.", e);
        }
        return response;
    }


    /**
     * Start HTTP Client to connect with Oracle Commerce Cloud.
     */
    private void startHttpClient() {
        httpClient = HttpClients.createDefault();
    }


    /**
     * Logout and close HTTP connection with Oracle Commerce Cloud.
     * @throws CommerceRestException wrapper of IOException throws by HTTP Client. See {@link CloseableHttpClient#close()}
     * to get more information.
     */
    public void closeHttpClient() throws CommerceRestException {
        logoutCall();
        try {
            httpClient.close();
        } catch (IOException e) {
            throw new CommerceRestException("Close http client error", e);
        }
    }



    /**
     * Execute query to OCC with oauth validation.
     * @param query part of URI equals REST request.
     * @param typeQuery type of the Rest request. One of: GET, POST, PUT, DELETE (not ENUM!)
     * @param additionalHeaders additional headers to the request.
     * @param inputData input params in JSON representation.
     * @throws CommerceRestException if invalid type of request, error to parse JSON representation of additional headers
     */
    public CloseableHttpResponse executeQuery(String query, String typeQuery,
                                              String additionalHeaders, String inputData) throws CommerceRestException {
        final String url = valuesConfiguration.getAdminDomain() + query;
        HttpRequestBase request;
        switch (typeQuery) {
            case GET_PARAM:
                request = new HttpGet(url);
                break;
            case POST_PARAM:
                request = new HttpPost(url);
                break;
            case PUT_PARAM:
                request = new HttpPut(url);
                break;
            case DELETE_PARAM:
                request = new HttpDelete(url);
                break;
            default:
                throw new CommerceRestException("Invalid type of request");
        }

        if(!(inputData == null || inputData.equals(""))) {
            switch (typeQuery) {
                case PUT_PARAM:
                case POST_PARAM:
                    request.setHeader(CONTENT_TYPE_HEADER_ARG, MediaType.APPLICATION_JSON_VALUE);
                    StringEntity params;
                    try {
                        params = new StringEntity(inputData);
                    } catch (UnsupportedEncodingException e) {
                        throw new CommerceRestException("Parse input data error");
                    }
                    if (typeQuery.equals(POST_PARAM)) {
                        ((HttpPost) request).setEntity(params);
                    } else {
                        ((HttpPut) request).setEntity(params);
                    }
                    break;
            }
        }

        if(!(additionalHeaders == null || additionalHeaders.equals(""))) {
            try {
                JSONObject jsonHeaders = new JSONObject(additionalHeaders);
                Iterator it = jsonHeaders.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = jsonHeaders.getString(key);
                    request.setHeader(key, value);
                }
            } catch (JSONException e) {
                throw new CommerceRestException("Parse additional headers error");
            }
        }


        return validateTokenLogAndExecuteRequest(request);
    }

    /* Getter and Setters */

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CommerceValuesConfig getValuesConfiguration() {
        return valuesConfiguration;
    }

    public void setValuesConfiguration(CommerceValuesConfig valuesConfiguration) {
        this.valuesConfiguration = valuesConfiguration;
    }

    public String getCommerceAccessToken() {
        return commerceAccessToken;
    }

    public void setCommerceAccessToken(String commerceAccessToken) {
        this.commerceAccessToken = commerceAccessToken;
    }
}
