package com.hotelbeds.distribution.hotel_api_sdk.helpers;

/*
 * #%L
 * hotel-api-sdk
 * %%
 * Copyright (C) 2015 - 2018 HOTELBEDS GROUP, S.L.U.
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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.hotelbeds.distribution.hotel_api_sdk.helpers.ConfirmRoom.ConfirmRoomBuilder;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.RoomDetail.GuestType;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.HotelbedsCustomerType;
import com.hotelbeds.hotelapimodel.auto.messages.BookingRQ;
import com.hotelbeds.hotelapimodel.auto.model.BookingRoom;
import com.hotelbeds.hotelapimodel.auto.model.Holder;
import com.hotelbeds.hotelapimodel.auto.model.Pax;
import com.hotelbeds.hotelapimodel.auto.model.PaymentCard;
import com.hotelbeds.hotelapimodel.auto.model.PaymentContactData;
import com.hotelbeds.hotelapimodel.auto.model.PaymentData;
import com.hotelbeds.hotelapimodel.auto.model.RequestModifiers;
import com.hotelbeds.hotelapimodel.auto.model.VoucherEmail;

import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class Booking {

    @NotNull
    private Holder holder;

    @NotNull
    @Size(min = 1, max = 20)
    private String clientReference;

    @Size(max = 2000)
    private String remark;

    private String cardType;
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cardCVC;
    private String email;
    private String phoneNumber;
    private Integer platform;
    private VoucherEmail voucherEmail;
    private String voucherLanguage;

    @Singular
    private List<ConfirmRoom> rooms;

    Properties properties;

    public void validate() {

    }

    @SuppressWarnings("unchecked")
    public BookingRQ toBookingRQ() {
        validate();
        BookingRQ bookingRQ = new BookingRQ();
        //
        bookingRQ.setPlatform(platform);
        //
        bookingRQ.setHolder(holder);
        //
        bookingRQ.setClientReference(clientReference);
        //
        bookingRQ.setRemark(remark);
        //
        if (StringUtils.isNoneBlank(cardType, cardNumber, cardHolderName, expiryDate, cardCVC) || StringUtils.isNoneBlank(email, phoneNumber)) {
            PaymentData paymentData = new PaymentData();
            if (StringUtils.isNoneBlank(cardType, cardNumber, cardHolderName, expiryDate, cardCVC)) {
                paymentData.setPaymentCard(new PaymentCard(cardType, cardNumber, cardHolderName, expiryDate, cardCVC));
            }
            if (StringUtils.isNoneBlank(email, phoneNumber)) {
                paymentData.setContactData(new PaymentContactData(email, phoneNumber));
            }
            bookingRQ.setPaymentData(paymentData);
        }
        //
        bookingRQ.setRooms(new ArrayList<>());
        for (ConfirmRoom room : rooms) {
            BookingRoom bookingRoom = new BookingRoom();
            bookingRoom.setRateKey(room.getRatekey());
            if (room.getDetails() != null && !room.getDetails().isEmpty()) {
                bookingRoom.setPaxes(new ArrayList<>());
                for (RoomDetail detail : room.getDetails()) {
                    Pax pax = new Pax();
                    pax.setType(detail.getType() == GuestType.ADULT ? HotelbedsCustomerType.AD : HotelbedsCustomerType.CH);
                    pax.setAge(detail.getAge());
                    pax.setName(detail.getName());
                    pax.setSurname(detail.getSurname());
                    pax.setRoomId(detail.getRoomId());
                    bookingRoom.getPaxes().add(pax);
                }
            }
            bookingRQ.getRooms().add(bookingRoom);
        }
        //
        if (getVoucherEmail() != null) {
            com.hotelbeds.hotelapimodel.auto.model.Voucher voucher = new com.hotelbeds.hotelapimodel.auto.model.Voucher();
            voucher.setEmail(getVoucherEmail());
            voucher.setLanguage(getVoucherLanguage());
            bookingRQ.setVoucher(voucher);
        }
        //
        if (properties != null) {
            RequestModifiers requestModifiers = new RequestModifiers();
            requestModifiers.setModifiers(new HashMap<>());
            for (String name : properties.stringPropertyNames()) {
                requestModifiers.getModifiers().put(name, properties.getProperty(name));
            }
            bookingRQ.setModifiers(requestModifiers);
        }
        return bookingRQ;
    }

    public static class BookingBuilder {

        public BookingBuilder withHolder(String name, String surname) {
            Holder holder = new Holder();
            holder.setName(name);
            holder.setSurname(surname);
            holder(holder);
            return this;
        }

        public BookingBuilder withProperty(String name, String value) {
            if (properties == null) {
                properties = new Properties();
            }
            properties.setProperty(name, value);
            return this;
        }


        public BookingBuilder withVoucher(String language, String to, String from, String title, String body) {
            voucherLanguage(language);
            VoucherEmail email = new VoucherEmail();
            email.setTo(to);
            email.setFrom(from);
            email.setTitle(title);
            email.setBody(body);
            voucherEmail(email);
            return this;
        }

        public BookingBuilder addRoom(String ratekey, ConfirmRoomBuilder roomBuilder) {
            room(roomBuilder.ratekey(ratekey).build());
            return this;
        }
    }
}
