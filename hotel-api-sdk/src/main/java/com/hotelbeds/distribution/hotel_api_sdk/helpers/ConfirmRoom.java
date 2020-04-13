package com.hotelbeds.distribution.hotel_api_sdk.helpers;

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


import java.util.List;

import com.hotelbeds.distribution.hotel_api_sdk.helpers.RoomDetail.GuestType;

import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ConfirmRoom {

    private final String ratekey;

    @Singular
    private List<RoomDetail> details;

    public void validate() {

    }

    public static class ConfirmRoomBuilder {
        public ConfirmRoomBuilder detailed(GuestType type, int age, String name, String surname, int roomId) {
            detail(new RoomDetail(type, age, name, surname, roomId));
            return this;
        }

        public ConfirmRoomBuilder adultOf(int age) {
            detail(new RoomDetail(GuestType.ADULT, age, null, null, 1));
            return this;
        }

        public ConfirmRoomBuilder childOf(int age) {
            detail(new RoomDetail(age));
            return this;
        }
    }
}
