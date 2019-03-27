package com.poseidon.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.poseidon.config.Constants;
import com.poseidon.exceptions.IdNotFoundException;
import com.poseidon.handlers.AwsResponse;
import com.poseidon.service.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

public class ElasticSearchResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchResource.class);

    @Inject
    private ElasticSearchService elasticSearchService;







    /**
     * Get statistics about an ElasticSearch Index
     *
     * @param index The targeted index
     * @return Response Entity
     */
    @GetMapping(value = "/statistics", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<String> indexStatistics(@RequestParam("index") final String index) {
        String response = elasticSearchService.getIndexStatistics(index);
        if (response != null) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching statistics for index");
        }
    }
}
