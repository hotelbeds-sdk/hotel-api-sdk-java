package com.hotelbeds.demo.simple;

/*
 * #%L
 * HotelAPI SDK Demo
 * %%
 * Copyright (C) 2015 - 2016 HOTELBEDS TECHNOLOGY, S.L.U.
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

import com.hotelbeds.distribution.hotel_api_sdk.HotelApiClient;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.LoggingRequestInterceptor;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiSDKException;
import com.hotelbeds.hotelcontentapi.auto.messages.Accommodation;
import com.hotelbeds.hotelcontentapi.auto.messages.Board;
import com.hotelbeds.hotelcontentapi.auto.messages.Category;
import com.hotelbeds.hotelcontentapi.auto.messages.Chain;
import com.hotelbeds.hotelcontentapi.auto.messages.Country;
import com.hotelbeds.hotelcontentapi.auto.messages.Currency;
import com.hotelbeds.hotelcontentapi.auto.messages.Destination;
import com.hotelbeds.hotelcontentapi.auto.messages.Facility;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityGroup;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityType;
import com.hotelbeds.hotelcontentapi.auto.messages.GroupCategory;
import com.hotelbeds.hotelcontentapi.auto.messages.Hotel;
import com.hotelbeds.hotelcontentapi.auto.messages.ImageType;
import com.hotelbeds.hotelcontentapi.auto.messages.Issue;
import com.hotelbeds.hotelcontentapi.auto.messages.Language;
import com.hotelbeds.hotelcontentapi.auto.messages.Promotion;
import com.hotelbeds.hotelcontentapi.auto.messages.RateCommentDetailsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.RateComments;
import com.hotelbeds.hotelcontentapi.auto.messages.Room;
import com.hotelbeds.hotelcontentapi.auto.messages.Segment;
import com.hotelbeds.hotelcontentapi.auto.messages.Terminal;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HotelContentSample {

    public static void main(String[] args) {
        // Perform the request and browse the results
        try (final HotelApiClient apiClient = new HotelApiClient()) {
            apiClient.setReadTimeout(20000);
            apiClient.init();


            for (Hotel hotel : apiClient.getAllHotels("ENG", false)) {
                log.debug("Hotel: {} {}", new Object[] {
                    hotel.getCode(), hotel.getName() != null ? hotel.getName().getContent() : ""});
            }
            log.info("Hotels Done!!");

            Hotel hotel = apiClient.getHotels(Arrays.asList(1234), "ENG", false).get(0);
            log.debug("hotel: {}", hotel);
            log.info("Hotel Done!!");

            for (Destination destination : apiClient.getAllDestinations("ENG", false)) {
                log.debug("Destination: {} {}", new Object[] {
                    destination.getCode(), destination.getName() != null ? destination.getName().getContent() : ""});
            }
            log.info("Destinations Done!!");

            apiClient.destinationsStream("ENG", false).parallel().forEach(destination -> {
                log.debug("Destination: {} {}", new Object[] {
                    destination.getCode(), destination.getName() != null ? destination.getName().getContent() : ""});
            });
            log.info("Destinations ENG Done!!");


            for (Country country : apiClient.getAllCountries("ENG", false)) {
                log.debug("Country: {}({}) {}", new Object[] {
                    country.getCode(), country.getIsoCode(), country.getDescription() != null ? country.getDescription().getContent() : ""});
            }
            log.info("Countries Done!!");

            RateCommentDetailsRS rateCommentDetailsRS = apiClient.getRateCommentDetail("1|88466|0");
            log.debug("rateCommentDetailsRS: {}", LoggingRequestInterceptor.writeJSON(rateCommentDetailsRS));
            log.info("RateCommentDetails Done!!");

            for (Board element : apiClient.getAllBoards("ENG", false)) {
                log.debug("Board: {}/{} - {}", new Object[] {
                    element.getCode(), element.getMultiLingualCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }

            log.info("Boards Done!!");

            for (Chain element : apiClient.getAllChains("ENG", false)) {
                log.debug("Chain: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("Chains Done!!");

            for (Accommodation element : apiClient.getAllAccommodations("ENG", false)) {
                log.debug("Accommodation: {} - {}", new Object[] {
                    element.getCode(), element.getTypeDescription() != null ? element.getTypeDescription() : ""});
            }
            log.info("Accommodations Done!!");

            for (Category element : apiClient.getAllCategories("ENG", false)) {
                log.debug("Category: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("Categories Done!!");

            for (RateComments element : apiClient.getAllRateComments("ENG", false)) {
                log.debug("RateComment: {} {}-{}", new Object[] {
                    element.getHotel(), element.getIncoming(), element.getCode()});
            }
            log.info("RateComments Done!!");
            //
            for (Currency element : apiClient.getAllCurrencies("ENG", false)) {
                log.debug("Currency: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("Currencies Done!!");
            //
            for (Facility element : apiClient.getAllFacilities("ENG", false)) {
                log.debug("Facility: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("Facilities Done!!");
            //
            for (FacilityGroup element : apiClient.getAllFacilityGroups("ENG", false)) {
                log.debug("FacilityGroup: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("FacilityGroups Done!!");
            //            //
            for (FacilityType element : apiClient.getAllFacilityTypes("ENG", false)) {
                log.debug("FacilityType: {}", new Object[] {element});
            }
            log.info("FacilityTypes Done!!");
            //            //
            for (Issue element : apiClient.getAllIssues("ENG", false)) {
                log.debug("Issue: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("Issues Done!!");
            //
            for (Language element : apiClient.getAllLanguages("ENG", false)) {
                log.debug("Language: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("Languages Done!!");
            //
            for (Promotion element : apiClient.getAllPromotions("ENG", false)) {
                log.debug("Promotion: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("Promotions Done!!");
            //
            for (Room element : apiClient.getAllRooms("ENG", false)) {
                log.debug("Room: {} - {}", new Object[] {
                    element.getCode(), element.getDescription()});
            }
            log.info("Rooms Done!!");
            //
            for (Segment element : apiClient.getAllSegments("ENG", false)) {
                log.debug("Segment: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("Segments Done!!");
            //
            for (Terminal element : apiClient.getAllTerminals("ENG", false)) {
                log.debug("Terminal: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("Terminals Done!!");
            //
            for (ImageType element : apiClient.getAllImageTypes("ENG", false)) {
                log.debug("ImageType: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("ImageTypes Done!!");
            //
            for (GroupCategory element : apiClient.getAllGroupCategories("ENG", false)) {
                log.debug("GroupCategory: {} - {}", new Object[] {
                    element.getCode(), element.getDescription() != null ? element.getDescription().getContent() : ""});
            }
            log.info("GroupCategories Done!!");

        } catch (HotelApiSDKException e) {
            log.error("Error requesting content", e);
        }
    }

}
