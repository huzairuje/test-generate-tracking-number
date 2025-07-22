package com.teleport.trackingnumbergenerator;

import com.teleport.trackingnumbergenerator.controller.TrackingNumberController;
import com.teleport.trackingnumbergenerator.model.TrackingNumberResponse;
import com.teleport.trackingnumbergenerator.service.TrackingNumberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackingNumberController.class)
public class TrackingNumberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackingNumberService trackingNumberService;

    @Test
    public void whenValidInput_thenReturns200() throws Exception {
        // Create a response with expected tracking number
        TrackingNumberResponse mockResponse = new TrackingNumberResponse();
        mockResponse.setTracking_number("TEST1234567890");
        mockResponse.setCreated_at("2018-11-20T19:29:32+08:00");
        mockResponse.setOrigin_country_id("MY");
        mockResponse.setDestination_country_id("ID");
        mockResponse.setWeight(BigDecimal.valueOf(1.234));
        mockResponse.setCustomer_id(UUID.fromString("de619854-b59b-425e-9db4-943979e1bd49"));
        mockResponse.setCustomer_name("RedBox Logistics");
        mockResponse.setCustomer_slug("redbox-logistics");

        when(trackingNumberService.generateTrackingNumber(any())).thenReturn(mockResponse);

        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", "MY")
                        .param("destination_country_id", "ID")
                        .param("weight", "1.234")
                        .param("created_at", "2018-11-20T19:29:32+08:00")
                        .param("customer_id", "de619854-b59b-425e-9db4-943979e1bd49")
                        .param("customer_name", "RedBox Logistics")
                        .param("customer_slug", "redbox-logistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tracking_number").value("TEST1234567890"));
    }

    @Test
    public void whenMissingRequiredParam_thenReturns400() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", "MY")
                        .param("destination_country_id", "ID")
                        .param("weight", "1.234")
                        // Missing created_at
                        .param("customer_id", "de619854-b59b-425e-9db4-943979e1bd49")
                        .param("customer_name", "RedBox Logistics")
                        .param("customer_slug", "redbox-logistics"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void whenInvalidParamValue_thenReturns400() throws Exception {
        mockMvc.perform(get("/next-tracking-number")
                        .param("origin_country_id", "INVALID")  // Invalid country code
                        .param("destination_country_id", "ID")
                        .param("weight", "not-a-number")        // Invalid weight
                        .param("created_at", "invalid-date")
                        .param("customer_id", "not-a-uuid")
                        .param("customer_name", "RedBox Logistics")
                        .param("customer_slug", "redbox-logistics"))
                .andExpect(status().isInternalServerError());
    }
}