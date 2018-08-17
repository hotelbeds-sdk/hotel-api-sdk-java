package com.hotelbeds.distribution.hotel_api_sdk.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentResponse;
import com.hotelbeds.hotelcontentapi.auto.messages.HotelDetailsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.HotelsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.RateCommentDetailsRS;

/**
 * Copyright (c) Hotelbeds Technology S.L.U. All rights reserved.
 */
public enum HotelContentPaths {

    COUNTRIES_URL(ConstantHolder.BASIC_LOCATION_PATH, AbstractGenericContentResponse.class, Arrays.asList("codes", "from", "to", "lastUpdateTime")),
    DESTINATIONS_URL(ConstantHolder.BASIC_LOCATION_PATH, AbstractGenericContentResponse.class, Arrays.asList("countryCodes", "codes", "from", "to",
        "lastUpdateTime")),
    TYPES_URL(ConstantHolder.BASIC_TYPE_PATH, AbstractGenericContentResponse.class, Arrays.asList("from", "to", "lastUpdateTime")),
    RATECOMMENT_DETAIL_URL(ConstantHolder.BASIC_TYPE_PATH, RateCommentDetailsRS.class, Arrays.asList("date", "code")),
    HOTEL_DETAIL_URL(ConstantHolder.BASIC_HOTEL_PATH, HotelDetailsRS.class),
    HOTELS_URL(ConstantHolder.HOTELS_PATH, HotelsRS.class, Arrays.asList("countryCode", "destinationCode", "codes", "from", "to", "lastUpdateTime"));

    private final String urlTemplate;
    private final Class<? extends AbstractGenericContentResponse> responseClass;
    private final List<String> allowedParams;

    HotelContentPaths(final String urlTemplate, Class<? extends AbstractGenericContentResponse> responseClass) {
        this(urlTemplate, responseClass, Collections.emptyList());
    }

    HotelContentPaths(final String urlTemplate, Class<? extends AbstractGenericContentResponse> responseClass, List<String> allowedParams) {
        this.urlTemplate = urlTemplate;
        this.responseClass = responseClass;
        this.allowedParams = new ArrayList<>(allowedParams);
        this.allowedParams.add("fields");
        this.allowedParams.add("language");
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public List<String> getAllowedParams() {
        return allowedParams;
    }

    public Class<? extends AbstractGenericContentResponse> getResponseClass() {
        return responseClass;
    }

    public String getUrl(HotelApiService service, HotelApiVersion version, String alternativeHotelContentPath) {
        return getUrl(service, version, null);
    }

    public String getUrl(HotelApiService service, HotelApiVersion version, Map<String, String> params, String alternativeHotelContentPath) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("path", service.getHotelContentPath(alternativeHotelContentPath));
        params.put("version", version.getVersion());
        StrSubstitutor strSubstitutor = new StrSubstitutor(params);
        return strSubstitutor.replace(urlTemplate);
    }

    private static class ConstantHolder {
        private final static String BASIC_PATH = "${path}/${version}/";
        private final static String PAGINATION_PARAMETERS = "&from=${from}&to=${to}&lastUpdateTime=${lastUpdateTime}";
        private final static String BASIC_TYPE_PATH = BASIC_PATH + "types/${type}";
        private final static String BASIC_HOTEL_PATH = BASIC_PATH + "${type}/${code}";
        private final static String BASIC_LOCATION_PATH = BASIC_PATH + "locations/${type}";
        private final static String HOTELS_PATH = BASIC_PATH + "${type}";
    }

}
