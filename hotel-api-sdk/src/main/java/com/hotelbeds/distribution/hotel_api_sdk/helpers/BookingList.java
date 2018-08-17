package com.hotelbeds.distribution.hotel_api_sdk.helpers;

/*
 * #%L
 * HotelAPI SDK
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


import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import javax.validation.constraints.NotNull;

import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.BookingListFilterStatus;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.BookingListFilterType;

import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class BookingList {
    @NotNull
    private BookingListFilterType filterType;
    @Singular
    private List<String> countries;
    @Singular
    private List<String> destinations;
    private String clientReference;
    @Singular
    private List<Integer> hotels;
    private BookingListFilterStatus status;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
    @NotNull
    private Integer from;
    @NotNull
    private Integer to;

    private Properties properties;

    public void validate() {
        // 0 < From < to
        // now < fromDate < toDate
        // usingDate mandatory or default?!?
    }

    public static class BookingListBuilder {

        public BookingListBuilder withProperty(String name, String value) {
            if (properties == null) {
                properties = new Properties();
            }
            properties.setProperty(name, value);
            return this;
        }
    }
}
