package com.astar.spring.library.utils;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * The type Rest utility.
 */


// TODO LOG THE REQUEST BODY
@Service
public class RESTUtility {
    private final static Logger LOGGER = LoggerFactory.getLogger(RESTUtility.class);

    private final WebClient.Builder clientBuilder;

    /**
     * Instantiates a new Rest utility.
     *
     * @param builder the builder
     */
    public RESTUtility(WebClient.Builder builder) {
        this.clientBuilder = builder;
    }

    private WebClient getClient(
            String baseUrl,
            Map<String, String> headers,
            Map<String, String> cookies,
            Map<String, String> uriVariables,
            HttpClient http
    ) {
        WebClient.Builder builder = clientBuilder.clone()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (headers != null) headers.forEach(builder::defaultHeader);
        if (cookies != null) cookies.forEach(builder::defaultCookie);
        if (uriVariables != null) builder.defaultUriVariables(uriVariables);
        if (http != null) builder.clientConnector(new ReactorClientHttpConnector(http));

        return builder.build();
    }

    /**
     * Get t.
     *
     * @param <T>                 the type parameter
     * @param baseUrl             the base url
     * @param uri                 the uri
     * @param headers             the headers
     * @param cookies             the cookies
     * @param uriVariables        the uri variables
     * @param queryParams         the query params
     * @param timeOutMilliseconds the time out milliseconds
     * @param responseObj         the response obj
     * @return the t
     */
    public <T> T get(
            String baseUrl,
            String uri,
            Map<String, String> headers,
            Map<String, String> cookies,
            Map<String, String> uriVariables,
            Map<String, String> queryParams,
            int timeOutMilliseconds,
            Class<T> responseObj
    ) {
        Objects.requireNonNull(baseUrl, "BaseUrl cannot be null");
        if (uri == null) uri = "";
        HttpClient http = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOutMilliseconds)
                .responseTimeout(Duration.ofMillis(timeOutMilliseconds))
                .doOnConnected(conn -> {
                    conn.addHandlerLast(
                                    new ReadTimeoutHandler(timeOutMilliseconds, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(timeOutMilliseconds,
                                                                    TimeUnit.MILLISECONDS));
                });
        WebClient client = this.getClient(baseUrl, headers, cookies, uriVariables, http);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);
        if (queryParams != null) {
            queryParams.forEach(uriBuilder::queryParam);
        }
        LOGGER.info("<1> Sending Request To : {}", (baseUrl + uriBuilder.toUriString()));
        return client
                .options()
                .uri(uriBuilder.toUriString())
                .retrieve()
                .bodyToMono(responseObj)
                .block(Duration.ofMillis(1000000));
    }

    /**
     * Post t.
     *
     * @param <T>                 the type parameter
     * @param baseUrl             the base url
     * @param uri                 the uri
     * @param headers             the headers
     * @param cookies             the cookies
     * @param uriVariables        the uri variables
     * @param queryParams         the query params
     * @param timeOutMilliseconds the time out milliseconds
     * @param requestBody         the request body
     * @param responseObj         the response obj
     * @return the t
     */
    public <T> T post(
            String baseUrl,
            String uri,
            Map<String, String> headers,
            Map<String, String> cookies,
            Map<String, String> uriVariables,
            Map<String, String> queryParams,
            int timeOutMilliseconds,
            Object requestBody,
            Class<T> responseObj
    ) {
        Objects.requireNonNull(baseUrl, "BaseUrl cannot be null");
        if (uri == null) uri = "";
        HttpClient http = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOutMilliseconds)
                .responseTimeout(Duration.ofMillis(timeOutMilliseconds))
                .doOnConnected(conn -> {
                    conn.addHandlerLast(
                                    new ReadTimeoutHandler(timeOutMilliseconds, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(timeOutMilliseconds,
                                                                    TimeUnit.MILLISECONDS));
                });
        WebClient client = this.getClient(baseUrl, headers, cookies, uriVariables, http);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);
        if (queryParams != null) {
            queryParams.forEach(uriBuilder::queryParam);
        }
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uriBuilder.toUriString());
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(requestBody);
        LOGGER.info("<2> Sending Request To : {}", (baseUrl + uriBuilder.toUriString()));
        return headersSpec
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .ifNoneMatch("*")
                .ifModifiedSince(ZonedDateTime.now())
                .retrieve()
                .bodyToMono(responseObj)
                .block(Duration.ofMillis(100000000));
    }


    /**
     * Put t.
     *
     * @param <T>                 the type parameter
     * @param baseUrl             the base url
     * @param uri                 the uri
     * @param headers             the headers
     * @param cookies             the cookies
     * @param uriVariables        the uri variables
     * @param queryParams         the query params
     * @param timeOutMilliseconds the time out milliseconds
     * @param requestBody         the request body
     * @param responseObj         the response obj
     * @return the t
     */
    public <T> T put(
            String baseUrl,
            String uri,
            Map<String, String> headers,
            Map<String, String> cookies,
            Map<String, String> uriVariables,
            Map<String, String> queryParams,
            int timeOutMilliseconds,
            Object requestBody,
            Class<T> responseObj
    ) {
        Objects.requireNonNull(baseUrl, "BaseUrl cannot be null");
        if (uri == null) uri = "";
        HttpClient http = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOutMilliseconds)
                .responseTimeout(Duration.ofMillis(timeOutMilliseconds))
                .doOnConnected(conn -> {
                    conn.addHandlerLast(
                                    new ReadTimeoutHandler(timeOutMilliseconds, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(timeOutMilliseconds,
                                                                    TimeUnit.MILLISECONDS));
                });
        WebClient client = this.getClient(baseUrl, headers, cookies, uriVariables, http);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);
        if (queryParams != null) {
            queryParams.forEach(uriBuilder::queryParam);
        }
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.PUT);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uriBuilder.toUriString());
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(requestBody);
        LOGGER.info("<3> Sending Request To : {}", (baseUrl + uriBuilder.toUriString()));
        return headersSpec
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .ifNoneMatch("*")
                .ifModifiedSince(ZonedDateTime.now())
                .retrieve()
                .bodyToMono(responseObj)
                .block(Duration.ofMillis(100000000));
    }

    /**
     * Patch t.
     *
     * @param <T>                 the type parameter
     * @param baseUrl             the base url
     * @param uri                 the uri
     * @param headers             the headers
     * @param cookies             the cookies
     * @param uriVariables        the uri variables
     * @param queryParams         the query params
     * @param timeOutMilliseconds the time out milliseconds
     * @param requestBody         the request body
     * @param responseObj         the response obj
     * @return the t
     */
    public <T> T patch(
            String baseUrl,
            String uri,
            Map<String, String> headers,
            Map<String, String> cookies,
            Map<String, String> uriVariables,
            Map<String, String> queryParams,
            int timeOutMilliseconds,
            Object requestBody,
            Class<T> responseObj
    ) {
        {
            Objects.requireNonNull(baseUrl, "BaseUrl cannot be null");
            if (uri == null) uri = "";
            HttpClient http = HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOutMilliseconds)
                    .responseTimeout(Duration.ofMillis(timeOutMilliseconds))
                    .doOnConnected(conn -> {
                        conn.addHandlerLast(
                                        new ReadTimeoutHandler(timeOutMilliseconds, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeOutMilliseconds,
                                                                        TimeUnit.MILLISECONDS));
                    });
            WebClient client = this.getClient(baseUrl, headers, cookies, uriVariables, http);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);
            if (queryParams != null) {
                queryParams.forEach(uriBuilder::queryParam);
            }
            WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.PATCH);
            WebClient.RequestBodySpec bodySpec = uriSpec.uri(uriBuilder.toUriString());
            WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(requestBody);
            LOGGER.info("<4> Sending Request To : {}", (baseUrl + uriBuilder.toUriString()));
            return headersSpec
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .ifNoneMatch("*")
                    .ifModifiedSince(ZonedDateTime.now())
                    .retrieve()
                    .bodyToMono(responseObj)
                    .block(Duration.ofMillis(100000000));
        }

    }

    /**
     * Delete t.
     *
     * @param <T>                 the type parameter
     * @param baseUrl             the base url
     * @param uri                 the uri
     * @param headers             the headers
     * @param cookies             the cookies
     * @param uriVariables        the uri variables
     * @param queryParams         the query params
     * @param timeOutMilliseconds the time out milliseconds
     * @param responseObj         the response obj
     * @return the t
     */
    public <T> T delete(
            String baseUrl,
            String uri,
            Map<String, String> headers,
            Map<String, String> cookies,
            Map<String, String> uriVariables,
            Map<String, String> queryParams,
            int timeOutMilliseconds,
            Class<T> responseObj
    ) {
        {
            Objects.requireNonNull(baseUrl, "BaseUrl cannot be null");
            if (uri == null) uri = "";
            HttpClient http = HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOutMilliseconds)
                    .responseTimeout(Duration.ofMillis(timeOutMilliseconds))
                    .doOnConnected(conn -> {
                        conn.addHandlerLast(
                                        new ReadTimeoutHandler(timeOutMilliseconds, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeOutMilliseconds,
                                                                        TimeUnit.MILLISECONDS));
                    });
            WebClient client = this.getClient(baseUrl, headers, cookies, uriVariables, http);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);
            if (queryParams != null) {
                queryParams.forEach(uriBuilder::queryParam);
            }
            LOGGER.info("<5> Sending Request To : {}", (baseUrl + uriBuilder.toUriString()));
            return client
                    .delete()
                    .uri(uriBuilder.toUriString())
                    .retrieve()
                    .bodyToMono(responseObj)
                    .block(Duration.ofMillis(1000000));
        }
    }

    /**
     * Options t.
     *
     * @param <T>                 the type parameter
     * @param baseUrl             the base url
     * @param uri                 the uri
     * @param headers             the headers
     * @param cookies             the cookies
     * @param uriVariables        the uri variables
     * @param queryParams         the query params
     * @param timeOutMilliseconds the time out milliseconds
     * @param requestBody         the request body
     * @param responseType        the response type
     * @return the t
     *
     */
//    TODO IMPLEMENT
    public <T> T options(
            String baseUrl,
            String uri,
            Map<String, String> headers,
            Map<String, String> cookies,
            Map<String, String> uriVariables,
            Map<String, String> queryParams,
            int timeOutMilliseconds,
            Object requestBody,
            Class<T> responseType
    ) {
        return null;
    }
}
