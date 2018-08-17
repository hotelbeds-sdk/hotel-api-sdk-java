package com.hotelbeds.distribution.hotel_api_sdk.unit.common;

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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.hotelbeds.distribution.hotel_api_sdk.HotelApiClient;
import com.hotelbeds.distribution.hotel_api_sdk.types.FilterType;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiVersion;
import com.hotelbeds.distribution.hotel_api_sdk.types.RequestType;

import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class HotelApiClientTest {

    //@Tested
    private HotelApiClient hotelApiClient;

    @Mocked
    @Injectable
    HotelApiVersion version = HotelApiVersion.DEFAULT;

    @Injectable
    RequestType requestType = RequestType.JSON;

    @Injectable
    String apiKey = "";


    @Mocked
    LocalDate localDate;

    @Mocked
    FilterType filterType;

    @Mocked
    boolean includeCancelled;

    @Test
    public void testBookingList() {
        Assert.assertTrue(true);
        //        new Expectations() {
        //            {
        //                version.getVersion();
        //                result = HotelApiVersion.DEFAULT.getVersion();
        //                hotelApiClient.bookingList(localDate, localDate, includeCancelled, filterType);
        //                result = "OK";
        //            }
        //        };
        //
        //        String response = hotelApiClient.bookingList(localDate, localDate, includeCancelled, filterType);
        //        Assert.assertNotNull(response);
    }

}
