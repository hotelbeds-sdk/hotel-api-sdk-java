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


import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes;
import com.hotelbeds.hotelapimodel.auto.messages.BookingChangeRQ;
import com.hotelbeds.hotelapimodel.auto.model.*;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;


@Builder
@Value
@ToString
public class BookingChange {
    @NotNull
    private String bookingId;
    @NotNull
    private SimpleTypes.ChangeMode mode;
    @NotNull
    private com.hotelbeds.hotelapimodel.auto.model.Booking booking;
    @NotNull
    private String clientReference;
    @NotNull
    private Holder holder;
    @NotNull
    private String remark;
    @NotNull
    private LocalDate checkin;
    @NotNull
    private LocalDate checkout;
    @NotNull
    private List<Room> rooms;

    public BookingChangeRQ toBookingRQ() {
        BookingChangeRQ bookingChangeRQ = new BookingChangeRQ();
        bookingChangeRQ.setMode(this.mode);
        bookingChangeRQ.setBookingId(this.bookingId);
        bookingChangeRQ.setBooking(this.booking);
        if (this.clientReference != null) {
            bookingChangeRQ.getBooking().setClientReference(this.clientReference);
        }
        if (this.holder != null) {
            bookingChangeRQ.getBooking().setHolder(this.holder);
        }
        if (this.remark != null) {
            bookingChangeRQ.getBooking().setRemark(this.remark);
        }
        if (this.checkin != null) {
            bookingChangeRQ.getBooking().getHotel().setCheckIn(this.checkin);
        }
        if (this.checkout != null) {
            bookingChangeRQ.getBooking().getHotel().setCheckOut(this.checkout);
        }
        if (this.rooms != null) {
            bookingChangeRQ.getBooking().getHotel().setRooms(this.rooms);
        }
        return bookingChangeRQ;
    }

    public static class BookingChangeBuilder {

        public BookingChangeBuilder fromBookingRS() {
            return this;
        }
    }
}
