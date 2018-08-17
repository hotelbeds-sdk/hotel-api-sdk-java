package com.hotelbeds.demo;

/*
 * #%L
 * HotelAPI SDK Demo
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.hotelbeds.distribution.hotel_api_sdk.helpers.*;
import com.hotelbeds.distribution.hotel_api_sdk.types.RequestType;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes;
import com.hotelbeds.hotelapimodel.auto.messages.*;
import com.hotelbeds.hotelapimodel.auto.model.Holder;
import com.hotelbeds.hotelapimodel.auto.model.Room;
import org.jooq.lambda.Unchecked;

import com.hotelbeds.distribution.hotel_api_sdk.HotelApiClient;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.AvailRoom.AvailRoomBuilder;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.Booking.BookingBuilder;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.ConfirmRoom.ConfirmRoomBuilder;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.RoomDetail.GuestType;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiSDKException;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.BookingListFilterStatus;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.BookingListFilterType;
import com.hotelbeds.hotelapimodel.auto.model.Hotel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HotelAPIClientDemo {
    public static void main(String[] args) throws HotelApiSDKException {
        try (HotelApiClient apiClient = new HotelApiClient("", "")) {
            apiClient.setReadTimeout(40000);
            apiClient.init();
            boolean doCheckStatus = true;
            boolean doAvailability = true;
            boolean isRandom = true;
            boolean doCheckRate = doAvailability && false;
            boolean doConfirmation = doAvailability && true;
            boolean doBookingList = false;
            boolean doBookingDetail = doBookingList && true;
            int bookingDetails = 1;
            //
            if (doCheckStatus) {
                log.info("Requesting status...");
                StatusRS statusRS = apiClient.status();
                log.debug("StatusRS: {}", LoggingRequestInterceptor.writeJSON(statusRS));
            }
            if (doAvailability) {
                log.info("Requesting availability...");
                Random random = new Random();
                int numDaysOffset = 90;
                int numDaysStay = 2;
                int numAdults = 1;
                int numChildren = 0;
                if (isRandom) {
                    numDaysOffset = 30 + random.nextInt(30);
                    numDaysStay = 1 + random.nextInt(4);
                    numAdults = 1 + random.nextInt(2);
                    numChildren = random.nextInt(2);
                }
                AvailRoomBuilder availRoom = AvailRoom.builder().adults(numAdults);
                if (numChildren > 0) {
                    availRoom.children(numChildren);
                    for (int count = 0; count < numChildren; count++) {
                        availRoom.childOf(4);
                    }
                }
                LocalDate checkIn = LocalDate.now().plusDays(numDaysOffset);
                LocalDate checkOut = LocalDate.now().plusDays(numDaysOffset + numDaysStay);
                log.info("Requesting availability from {} to {} for {} adults and {} children", new Object[] {
                    checkIn, checkOut, numAdults, numChildren});
                // @formatter:off
            AvailabilityRS availabilityRS =
                apiClient.availability(
                    Availability.builder()
                    .language("CAS")
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .addRoom(availRoom)
                    //.addRoom(AvailRoom.builder().adults(1).children(1).childOf(4))
                    //.withinThis(new Square("45.37680856570233", "-2.021484375", "38.548165423046584", "8.658203125"))
                    //.minCategory(2)
                    //.limitHotelsTo(10)
                    //.numberOfTrypReviewsHigherThan(2)
                    //.trypScoreHigherThan(new BigDecimal(2))
                    //.destination("PMI")//.zone(10)
                    //.payed(Availability.Pay.AT_HOTEL)
                    //.payed(Pay.THROUGH_WEB)
                    //.payed(Pay.INDIFFERENT);
                    //.matchingKeyword(34)
                    //.matchingKeyword(81)
                    //.matchingAllKeywords()
                    //DEFAULT .matchingAnyKeyword()
                    //.ofType(Accommodation.HOTEL)
                    //.ofType(Accommodation.APARTMENT)
                    //
                    .includeHotel(111637)
                    .includeHotel(2818)
                    .includeHotel(138465)
                    .includeHotel(164471)
                    // or
                    //.excludeHotel(187013)
                    //.excludeHotel(188330)
                    //
                    //.useGiataCodes(false)
                    //
                    //.limitHotelsTo(250)
                    //.limitRoomsPerHotelTo(5)
                    //.litRatesPerRoomTo(5)
                    //.ratesHigherThan(new BigDecimal(50))
                    //.ratesLowerThan(new BigDecimal(350))
                    //
                    //.hbScoreHigherThan(new BigDecimal(3))
                    //.hbScoreLowerThan(new BigDecimal(5))
                    //.numberOfHBReviewsHigherThan(50)
                    //
                    //.trypScoreHigherThan(new BigDecimal(1))
                    //.trypScoreLowerThan(new BigDecimal(4))
                    //.numberOfTrypReviewsHigherThan(75)
                    //
                    //.withinThis(new Circle("2.646633999999949", "39.57119", 20000))
                    //
                    //.withinThis(new Square("45.37680856570233", "-2.021484375", "38.548165423046584", "8.658203125"))
                    //
                    //.includeBoard("RO-E10")
                    //.includeBoard("BB-E10")
                    // or
                    //.excludeBoard("RO")
                    //
                    //.includeRoomCode("DBL.ST")
                    //.includeRoomCode("DBL.SU")
                    // or
                    //.includeRoomCodes(Arrays.asList(new String[] {
                    //            "DBL.ST", "DBL.SU"}))
                    // or
                    //.excludeRoomCode("TPL.ST")
                    //
                    //.addRoom(Room.builder().adults(2).children(1).detailed(GuestType.ADULT, 30, "Perico", "Palotes").childOf(4))
                    .build());
            // @formatter:on
                if (availabilityRS != null && availabilityRS.getHotels() != null && availabilityRS.getHotels().getHotels() != null) {
                    log.info("Availability answered with {} hotels!", availabilityRS.getHotels().getHotels().size());
                    log.debug("AvailabilityRS: {}", LoggingRequestInterceptor.writeJSON(availabilityRS));
                } else {
                    log.info("No availability!");
                }
                //
                ConfirmRoomBuilder confirmRoom = ConfirmRoom.builder();
                for (int count = 0; count < numAdults; count++) {
                    int adultAge = 20 + random.nextInt(20);
                    confirmRoom.detailed(GuestType.ADULT, adultAge, "PaxTestName-" + count, "PaxTestSurname", 1);
                }
                if (numChildren > 0) {
                    availRoom.children(numChildren);
                    for (int count = 0; count < numChildren; count++) {
                        confirmRoom.childOf(4);
                    }
                }
                //
                if (doCheckRate && availabilityRS != null) {
                    if (availabilityRS.getHotels() != null && availabilityRS.getHotels().getHotels() != null) {
                        Optional<Hotel> firstHotel = availabilityRS.getHotels().getHotels().stream().findFirst();
                        if (firstHotel.isPresent()) {
                            Hotel theHotel = firstHotel.get();
                            String rateKey = theHotel.getRooms().stream().findAny().get().getRates().stream().findAny().get().getRateKey();
                            // Wait a bit so the availability is stored in the system (it's stored asynchronously)
                            try {
                                log.info("Waiting to try a check rate");
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                log.error("Error waiting. Wot? ", e);
                            }
                            log.info("Checking reservation with rate {}", rateKey);
                            // @formatter:off
                        CheckRateRS bookingRS =
                            apiClient.check(BookingCheck.builder()
                                .addRoom(rateKey, confirmRoom)
                                .build());
                        // @formatter:on
                            if (bookingRS != null) {
                                log.debug("CheckRateRS: {}", LoggingRequestInterceptor.writeJSON(bookingRS));
                            }
                        } else {
                            log.info("No hotel available");
                        }
                    } else {
                        log.info("No hotel available");
                    }
                }
                if (doConfirmation && availabilityRS != null) {
                    if (availabilityRS.getHotels() != null && availabilityRS.getHotels().getHotels() != null) {
                        Optional<Hotel> firstHotel = availabilityRS.getHotels().getHotels().stream().findFirst();
                        if (firstHotel.isPresent()) {
                            Hotel theHotel = firstHotel.get();
                            String rateKey = theHotel.getRooms().stream().findAny().get().getRates().stream().findAny().get().getRateKey();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                log.error("Interrupted while waiting to confirm", e);
                            }
                            log.info("Confirming reservation with rate {}", rateKey);
                            BookingBuilder bookingBuilder =
                                Booking.builder().withHolder("Rosetta", "Pruebas").clientReference("SDK Test").remark("***SDK***TESTING")
                                    .addRoom(rateKey, confirmRoom).withVoucher("ENG", "xxxxx@xxxxxxx.com", "xxxxx@xxxxxxx.com", "Test", "Test");
                            //                            bookingBuilder.cardHolderName("AUTHORISED").cardNumber("4444333322221111").cardCVC("597").cardType("VI")
                            //                                    .expiryDate("0718").email("xxxxx@xxxxxx.com").phoneNumber("666666666");
                            Booking booking = bookingBuilder.build();
                            if (booking != null) {
                                log.debug("BookingRQ: {}", LoggingRequestInterceptor.writeJSON(booking.toBookingRQ()));
                            }
                            BookingRS bookingRS = apiClient.confirm(booking);
                            if (bookingRS != null) {
                                log.debug("BookingRS: {}", LoggingRequestInterceptor.writeJSON(bookingRS));
                            }

                            if (bookingRS.getBooking() != null) {
                                Holder holder = new Holder();
                                holder.setName("NewHolderName");
                                holder.setSurname("NewHolderSurname");
                                List<Room> rooms = bookingRS.getBooking().getHotel().getRooms();
                                rooms.get(0).getPaxes().get(0).setSurname("NewPaxSurname");
                                rooms.get(0).getPaxes().get(0).setName("NewPaxName");
                                BookingChange bookingChangeBuilder =
                                    BookingChange.builder().fromBookingRS().booking(bookingRS.getBooking()).mode(SimpleTypes.ChangeMode.UPDATE)
                                        .bookingId(bookingRS.getBooking().getReference()).clientReference("NewClientReference").holder(holder)
                                        .remark("NewRemark")
                                        //.checkin(LocalDate.now().plusDays(60))
                                        //.checkout(LocalDate.now().plusDays(62))
                                        .rooms(rooms).build();
                                //log.debug("bookingChangeBuilder: {}", LoggingRequestInterceptor.write(bookingChangeBuilder, RequestType.JSON));
                                BookingChangeRS bookingChangeRS =
                                    apiClient.change(bookingRS.getBooking().getReference(), bookingChangeBuilder.toBookingRQ(), RequestType.JSON);
                                log.debug("BookingChangeRS: {}", LoggingRequestInterceptor.write(bookingChangeRS, RequestType.JSON));
                            } else {
                                log.info("BookingChange failed");
                            }

                            if (bookingRS.getBooking() != null) {
                                log.info("Confirmation succedded. Canceling reservation with id {}", bookingRS.getBooking().getReference());
                                BookingCancellationRS bookingCancellationRS = apiClient.cancel(bookingRS.getBooking().getReference());
                                if (bookingCancellationRS != null) {
                                    log.debug("BookingCancellationRS: {}", LoggingRequestInterceptor.writeJSON(bookingCancellationRS));
                                }
                                //
                                log.info("Getting detail after cancelation of id {}", bookingRS.getBooking().getReference());
                                BookingDetailRS bookingDetailRS = apiClient.detail(bookingRS.getBooking().getReference());
                                if (bookingDetailRS != null) {
                                    log.debug("BookingDetailRS: {}", LoggingRequestInterceptor.writeJSON(bookingDetailRS));
                                }
                                log.info("Detail obtained!");
                            } else {
                                log.info("Confirmation failed");
                            }
                        } else {
                            log.info("No hotel available");
                        }
                    } else {
                        log.info("No hotel available");
                    }
                }
            }
            //
            if (doBookingList) {
                log.info("Requesting booking list...");
                BookingListRS bookingListRS =
                    apiClient.list(LocalDate.now().minusDays(7), LocalDate.now().minusDays(0), 1, 10, BookingListFilterStatus.ALL,
                        BookingListFilterType.CREATION);
                if (bookingListRS != null) {
                    log.info("BookingListRS: {}", LoggingRequestInterceptor.writeJSON(bookingListRS));
                }
                if (bookingListRS != null && doBookingDetail) {
                    bookingListRS.getBookings().getBookings().stream().limit(bookingDetails).forEach(Unchecked.consumer(booking -> {
                        BookingDetailRS bookingDetailRS = apiClient.detail(booking.getReference());
                        if (bookingDetailRS != null) {
                            log.info("BookingDetailRS: {}", LoggingRequestInterceptor.writeJSON(bookingDetailRS));
                        }
                    }));
                }
            }
        }
    }
}
