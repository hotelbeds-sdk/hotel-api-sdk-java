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


import java.util.HashMap;
import java.util.Properties;

import javax.validation.constraints.NotNull;

import com.hotelbeds.hotelapimodel.auto.messages.BookingVoucherRQ;
import com.hotelbeds.hotelapimodel.auto.model.RequestModifiers;
import com.hotelbeds.hotelapimodel.auto.model.VoucherEmail;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class Voucher {

    @NotNull
    private String bookingId;
    private String language;
    private VoucherEmail voucherEmail;

    Properties properties;

    public void validate() {

    }

    public BookingVoucherRQ toBookingVoucherRQ() {
        validate();
        BookingVoucherRQ bookingVoucherRq = new BookingVoucherRQ();
        bookingVoucherRq.setBookingId(getBookingId());
        bookingVoucherRq.setVoucher(new com.hotelbeds.hotelapimodel.auto.model.Voucher());
        bookingVoucherRq.getVoucher().setLanguage(getLanguage());
        if (getVoucherEmail() != null) {
            bookingVoucherRq.getVoucher().setEmail(getVoucherEmail());
        }
        if (properties != null) {
            RequestModifiers requestModifiers = new RequestModifiers();
            requestModifiers.setModifiers(new HashMap<>());
            for (String name : properties.stringPropertyNames()) {
                requestModifiers.getModifiers().put(name, properties.getProperty(name));
            }
            bookingVoucherRq.setModifiers(requestModifiers);
        }
        return bookingVoucherRq;
    }

    public static class VoucherBuilder {

        public VoucherBuilder withVoucherEmail(String to, String from, String title, String body) {
            VoucherEmail voucherEmail = new VoucherEmail();
            voucherEmail.setTo(to);
            voucherEmail.setFrom(from);
            voucherEmail.setTitle(title);
            voucherEmail.setBody(body);
            voucherEmail(voucherEmail);
            return this;
        }

        public VoucherBuilder withProperty(String name, String value) {
            if (properties == null) {
                properties = new Properties();
            }
            properties.setProperty(name, value);
            return this;
        }


    }
}
