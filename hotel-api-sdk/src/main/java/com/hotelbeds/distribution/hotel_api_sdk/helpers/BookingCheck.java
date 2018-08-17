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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.hotelbeds.distribution.hotel_api_sdk.helpers.ConfirmRoom.ConfirmRoomBuilder;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.RoomDetail.GuestType;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.HotelbedsCustomerType;
import com.hotelbeds.hotelapimodel.auto.messages.CheckRateRQ;
import com.hotelbeds.hotelapimodel.auto.model.BookingRoom;
import com.hotelbeds.hotelapimodel.auto.model.Pax;
import com.hotelbeds.hotelapimodel.auto.model.RequestModifiers;

import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class BookingCheck {

    @Singular
    private List<ConfirmRoom> rooms;
    private Integer platform;

    Properties properties;

    public void validate() {

    }

    @SuppressWarnings("unchecked")
    public CheckRateRQ toCheckRateRQ() {
        validate();
        CheckRateRQ checkRateRQ = new CheckRateRQ();
        //
        checkRateRQ.setPlatform(platform);
        checkRateRQ.setRooms(new ArrayList<>());
        for (ConfirmRoom room : rooms) {
            BookingRoom bookingRoom = new BookingRoom();
            bookingRoom.setRateKey(room.getRatekey());
            bookingRoom.setPaxes(new ArrayList<>());
            for (RoomDetail detail : room.getDetails()) {
                Pax pax = new Pax();
                pax.setType(detail.getType() == GuestType.ADULT ? HotelbedsCustomerType.AD : HotelbedsCustomerType.CH);
                pax.setAge(detail.getAge());
                pax.setName(detail.getName());
                pax.setSurname(detail.getSurname());
                bookingRoom.getPaxes().add(pax);
            }
            checkRateRQ.getRooms().add(bookingRoom);
        }
        //
        if (properties != null) {
            RequestModifiers requestModifiers = new RequestModifiers();
            requestModifiers.setModifiers(new HashMap<>());
            for (String name : properties.stringPropertyNames()) {
                requestModifiers.getModifiers().put(name, properties.getProperty(name));
            }
            checkRateRQ.setModifiers(requestModifiers);
        }
        return checkRateRQ;
    }

    public static class BookingCheckBuilder {

        public BookingCheckBuilder withProperty(String name, String value) {
            if (properties == null) {
                properties = new Properties();
            }
            properties.setProperty(name, value);
            return this;
        }

        public BookingCheckBuilder addRoom(String ratekey, ConfirmRoomBuilder roomBuilder) {
            room(roomBuilder.ratekey(ratekey).build());
            return this;
        }
    }
}
