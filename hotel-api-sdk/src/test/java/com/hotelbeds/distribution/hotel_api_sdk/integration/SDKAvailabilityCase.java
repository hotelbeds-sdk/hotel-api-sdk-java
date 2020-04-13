package com.hotelbeds.distribution.hotel_api_sdk.integration;

/*
 * #%L
 * hotel-api-sdk
 * %%
 * Copyright (C) 2015 HOTELBEDS, S.L.U.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hotelbeds.distribution.hotel_api_sdk.HotelApiClient;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.AvailRoom;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.Availability;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiSDKException;
import com.hotelbeds.hotelapimodel.auto.messages.AvailabilityRS;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SDKAvailabilityCase {

    private static final int DAYS_IN_THE_FUTURE = 37;
    private static final int AVAILABILITY_INTERVAL = 2;
    private static final int AVAILABILITY_ADULTS = 2;
    private static final int AVAILABILITY_CHILDREN_AGE = 4;
    private static final int DESTINATION_AVAILABILITY_LIMIT = 20;
    private static final List<String> TEST_DESTINATIONS = Arrays.asList("MAD", "PMI", "BCN");


    private static List<String> destinations = null;
    private static HotelApiClient apiClient;

    @BeforeClass
    public static void setupClient() {
        apiClient = new HotelApiClient();
        //
        destinations = new ArrayList<>();
        destinations.addAll(TEST_DESTINATIONS);
        Collections.shuffle(destinations);

        log.info("Preloaded {} destinations", destinations.size());
    }

    //    @Test
    //    public void testStatus() {
    //        log.info("Testing status...");
    //        StatusRS statusRS = apiClient.status();
    //        Assert.assertTrue("Status is not ok: " + statusRS.getStatus(), "OK".equals(statusRS.getStatus()));
    //        log.info("Status ok.");
    //        //log.info("Status ok: {}", LoggingRequestInterceptor.writeJSON(statusRS, true));
    //    }

    @Test
    public void testSimpleDispo() throws HotelApiSDKException {
        String destination = destinations.stream().findFirst().get();
        log.debug("Testing availability at {}", destination);
        // @formatter:off
        AvailabilityRS availabilityRS = apiClient.availability(
            Availability.builder()
            .checkIn(LocalDate.now().plusDays(DAYS_IN_THE_FUTURE))
            .checkOut(LocalDate.now().plusDays(DAYS_IN_THE_FUTURE+AVAILABILITY_INTERVAL))
            .destination(destination)
            .addRoom(AvailRoom.builder().adults(AVAILABILITY_ADULTS).children(1).childOf(AVAILABILITY_CHILDREN_AGE))
            .limitHotelsTo(DESTINATION_AVAILABILITY_LIMIT)
            .build()
            );
        // @formatter:on
        Assert.assertNotNull("Null availability at " + destination, availabilityRS);
        Assert.assertNotNull(destination + " availability: null hotels!", availabilityRS.getHotels());
        //log.info("AvailabilityRS: {}", LoggingRequestInterceptor.writeJSON(availabilityRS, true));
        log.info("Availability at {} returned {} hotels.", destination, availabilityRS.getHotels().getTotal());
    }
}
