package com.poseidon.service;

import com.amazonaws.*;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poseidon.config.AwsConfigurationInfo;
import com.poseidon.config.Constants;
import com.poseidon.exceptions.IdNotFoundException;
import com.poseidon.handlers.AwsResponse;
import com.poseidon.handlers.ElasticSearchClientHandler;
import com.poseidon.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.*;


@Named
public class ElasticSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchService.class);

    private static final AWSCredentials AWS_CREDENTIALS = new DefaultAWSCredentialsProviderChain().getCredentials();

    @Inject
    private AwsConfigurationInfo configurationInfo;

    /**
     * Sign the request to AWS ElasticSearch using the AWS4Signer
     *
     * @param request The Request
     */
    private void signRequest(Request request) {
        final String region = configurationInfo.getRegion();
        final String serviceName = configurationInfo.getServiceName();

        final AWS4Signer aws4Signer = new AWS4Signer();
        aws4Signer.setRegionName(region);
        aws4Signer.setServiceName(serviceName);
        aws4Signer.sign(request, AWS_CREDENTIALS);
    }

    /**
     *  Build the full URL, create request headers, and build Request object prior to signing the Request to send
     *  to AWS ElasticSearch
     *
     * @param url The URL
     * @param json The request body
     * @param parameters The request parameters
     * @param httpMethodName The HTTPMethodName
     * @return The Request
     */
    private Request generateSignedRequest(final String url,
                                          final String json,
                                          final Map<String, List<String>> parameters,
                                          final HttpMethodName httpMethodName) {

        final String endpoint = configurationInfo.getEndpoint() + "/" + url;
        final Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");

        final Request request = new DefaultRequest(configurationInfo.getServiceName());
        request.setHeaders(headers);

        // JSON is used for Creating and Updating objects in ElasticSearch
        if (json != null) {
            request.setContent(new ByteArrayInputStream(json.getBytes()));
        }
        // Parameters are used for queries
        if (parameters != null) {
            request.setParameters(parameters);
        }
        request.setEndpoint(URI.create(endpoint));
        request.setHttpMethod(httpMethodName);

        signRequest(request);

        return request;
    }

    /**
     * Submit the Request to AWS, and return the response
     *
     * @param request The Request
     * @return AwsResponse
     */
    private AwsResponse executeRequest(Request request) {
        try {
            final ClientConfiguration configuration = new ClientConfiguration();
            final ExecutionContext context = new ExecutionContext(true);
            final ElasticSearchClientHandler client = new ElasticSearchClientHandler(configuration);

            return client.execute(context, request);
        } catch (Exception e) {
            LOGGER.error("Error executing ElasticSearch Request.", e);
        }
        return null;
    }


    /**
     * Build an ElasticSearch 'should' statement.
     *
     * @param field The field to search in
     * @param value The value to search for
     * @param array The JSONArray to append the the query to
     */
    private void buildElasticSearchShouldStatement(final String field, final Collection value,
                                                   final JSONArray array) {
        if (value.size() > 1) {
            final JSONObject bool = new JSONObject();
            final JSONObject should = new JSONObject();
            final JSONArray match = new JSONArray();
            for (Object objectValue : value) {
                buildElasticSearchMatchStatement(field, objectValue, match);
            }
            should.put("should", match);
            bool.put("bool", should);
            array.put(bool);
        } else {
            buildElasticSearchMatchStatement(field, value.iterator().next(), array);
        }
    }

    /**
     * Build an ElasticSearch 'match' statement. This is equivalent to a SQL 'equals' statement.
     *
     * @param field The field to search in
     * @param value The value to search for
     * @param array The JSONArray to append the query to
     */
    private void buildElasticSearchMatchStatement(final String field, final Object value, final JSONArray array) {
        final JSONObject matchItem = new JSONObject();
        final JSONObject matchTerms = new JSONObject();
        matchTerms.put(field, value);
        matchItem.put("match", matchTerms);
        array.put(matchItem);
    }

    /**
     * Build a fuzzy search clause
     *
     * @param field The field to search in
     * @param value The partial value to search for
     * @param searchTerm The JSONObject
     */
    private void buildElasticSearchFuzzyStatement(final String field, final Object value, final JSONObject searchTerm) {
        final JSONObject fuzzyBlock = new JSONObject();
        fuzzyBlock.put("value", value);
        fuzzyBlock.put("boost", 1.0);
        fuzzyBlock.put("fuzziness", 50);
        fuzzyBlock.put("prefix_length", 0);
        fuzzyBlock.put("max_expansions", 100);
        searchTerm.put(field, fuzzyBlock);
    }

    /**
     * Build request to /_stats API in ElasticSearch
     *
     * @param index The Index
     * @return Response
     */
    public String getIndexStatistics(final String index) {
        final String url = index + Constants.STATS_API;

        final Request request = generateSignedRequest(url, null, null, HttpMethodName.GET);

        final AwsResponse response = executeRequest(request);

        return response != null ? response.getBody() : "";
    }

}
