package com.hotelbeds.distribution.hotel_api_sdk;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotelbeds.distribution.hotel_api_sdk.helpers.*;
import com.hotelbeds.distribution.hotel_api_sdk.types.*;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelbedsError;
import com.hotelbeds.hotelapimodel.auto.messages.*;
import com.hotelbeds.hotelcontentapi.auto.convert.json.DateSerializer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.BookingListFilterStatus;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.BookingListFilterType;
import com.hotelbeds.hotelapimodel.auto.util.AssignUtils;
import com.hotelbeds.hotelapimodel.auto.util.ObjectJoiner;
import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentRequest;
import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentResponse;
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
import com.hotelbeds.hotelcontentapi.auto.messages.HotelDetailsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.HotelDetailsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.ImageType;
import com.hotelbeds.hotelcontentapi.auto.messages.Issue;
import com.hotelbeds.hotelcontentapi.auto.messages.Language;
import com.hotelbeds.hotelcontentapi.auto.messages.Promotion;
import com.hotelbeds.hotelcontentapi.auto.messages.RateCommentDetailsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.RateCommentDetailsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.RateComments;
import com.hotelbeds.hotelcontentapi.auto.messages.Room;
import com.hotelbeds.hotelcontentapi.auto.messages.Segment;
import com.hotelbeds.hotelcontentapi.auto.messages.Terminal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Copyright (c) Hotelbeds Technology S.L.U. All rights reserved.
 */
@Slf4j
@Data
public class HotelApiClient implements AutoCloseable {

    private static final String EXTRA_PARAM_PREFIX = "extra_";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String APPLICATION_JSON_HEADER = "application/json";
    public static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
    public static final String APPLICATION_XML_HEADER = "application/xml";
    public static final String APIKEY_PROPERTY_NAME = "hotelapi.apikey";
    public static final String SHAREDSECRET_PROPERTY_NAME = "hotelapi.sharedsecret";
    public static final String VERSION_PROPERTY_NAME = "hotelapi.version";
    public static final String VERSION_PAYMENT_PROPERTY_NAME = "hotelapi.payversion";
    public static final String SERVICE_PROPERTY_NAME = "hotelapi.service";
    public static final String HOTELAPI_PROPERTIES_FILE_NAME = "hotelapi.properties";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    public static final String DEFAULT_LANGUAGE = "ENG";
    private static final int REST_TEMPLATE_READ_TIME_OUT = 500000;

    private static final String HOTEL_API_URL_PROPERTY = "hotel-api.url";
    private static final String HOTEL_API_SECURE_URL_PROPERTY = "hotel-api-secure.url";
    private static final String HOTEL_CONTENT_URL_PROPERTY = "hotel-content.url";
    private static final String API_KEY_HEADER_NAME = "Api-Key";
    private static final String SIGNATURE_HEADER_NAME = "X-Signature";

    private final String apiKey;
    private final String sharedSecret;
    private final HotelApiVersion hotelApiVersion;
    private final HotelApiService hotelApiService;
    private String defaultLanguage;
    private boolean defaultUseSecondaryLanguage;
    private Properties properties = null;
    private OkHttpClient restTemplate = null;
    private boolean initialised = false;
    private int readTimeout = REST_TEMPLATE_READ_TIME_OUT;
    private int connectTimeout = REST_TEMPLATE_READ_TIME_OUT;
    private int connectionRequestTimeout = REST_TEMPLATE_READ_TIME_OUT;
    private ExecutorService executorService = null;
    private ObjectMapper mapper = null;
    private final String alternativeHotelApiPath;
    private final String alternativeHotelApiSecurePath;
    private final String alternativeHotelContentPath;

    public HotelApiClient() {
        this((String) null, null);
    }

    public HotelApiClient(HotelApiService service) {
        this(service, null, null);
    }

    public HotelApiClient(HotelApiVersion version, HotelApiService service) {
        this(version, service, null, null);
    }

    public HotelApiClient(String apiKey, String sharedSecret) {
        this(HotelApiVersion.DEFAULT, HotelApiService.TEST, apiKey, sharedSecret);
    }

    public HotelApiClient(HotelApiService service, String apiKey, String sharedSecret) {
        this(HotelApiVersion.DEFAULT, service, apiKey, sharedSecret);
    }

    public HotelApiClient(HotelApiVersion version, HotelApiService service, String apiKey, String sharedSecret) {
        this.apiKey = getHotelApiKey(apiKey);
        this.sharedSecret = getHotelApiSharedSecret(sharedSecret);
        hotelApiVersion = getHotelApiVersion(version);
        hotelApiService = getHotelApiService(service);
        if (StringUtils.isBlank(this.apiKey) || hotelApiVersion == null || hotelApiService == null || StringUtils.isBlank(this.sharedSecret)) {
            throw new IllegalArgumentException(
                "HotelApiClient cannot be created without specifying an API key, Shared Secret, the Hotel API version and the service you are connecting to.");
        }
        alternativeHotelApiPath = getHotelApiUrl();
        alternativeHotelApiSecurePath = getHotelApiSecureUrl();
        alternativeHotelContentPath = getHotelContentUrl();
        properties = new Properties();
    }

    public void init() {
        // @formatter:off
        restTemplate = new OkHttpClient.Builder()
        .writeTimeout(connectionRequestTimeout, TimeUnit.MILLISECONDS)
        .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
        .addInterceptor(new LoggingRequestInterceptor())
        .hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
              return true;
            }
          })
        .build();
        // @formatter:on
        initialised = true;
        executorService = Executors.newFixedThreadPool(8);
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        if (isInitialised()) {
            log.warn("HotelAPIClient is already initialised, new timeout will have no effect.");
        }
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        if (isInitialised()) {
            log.warn("HotelAPIClient is already initialised, new timeout will have no effect.");
        }
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        if (isInitialised()) {
            log.warn("HotelAPIClient is already initialised, new timeout will have no effect.");
        }
    }

    private String getHotelApiProperty(String propertyName) {
        if (properties == null) {
            try (InputStream hotelApiPropertiesIS = ClassLoader.getSystemResourceAsStream(HOTELAPI_PROPERTIES_FILE_NAME)) {
                properties = new Properties();
                if (hotelApiPropertiesIS != null) {
                    properties.load(hotelApiPropertiesIS);
                }
            } catch (IOException e) {
                log.error("Error loading properties (){}.", HOTELAPI_PROPERTIES_FILE_NAME, e);
            }
        }
        return properties.getProperty(propertyName);
    }

    private String getHotelApiUrl() {
        String result = null;
        String alternativeUrl = getValueFromProperties("Alternative Hotel Api Url", HOTEL_API_URL_PROPERTY);
        if (alternativeUrl != null) {
            result = alternativeUrl;
        }
        return result;
    }

    private String getHotelApiSecureUrl() {
        String result = null;
        String alternativeUrl = getValueFromProperties("Alternative Hotel Api Secure Url", HOTEL_API_SECURE_URL_PROPERTY);
        if (alternativeUrl != null) {
            result = alternativeUrl;
        }
        return result;
    }

    private String getHotelContentUrl() {
        String result = null;
        String alternativeUrl = getValueFromProperties("Alternative Hotel Content Url", HOTEL_CONTENT_URL_PROPERTY);
        if (alternativeUrl != null) {
            result = alternativeUrl;
        }
        return result;
    }

    private String getHotelApiKey(String providedDefault) {
        String result = providedDefault;
        String fromProperties = getValueFromProperties("Api Key", APIKEY_PROPERTY_NAME);
        if (fromProperties != null) {
            result = fromProperties;
        }
        return result;
    }

    private String getHotelApiSharedSecret(String providedDefault) {
        String result = providedDefault;
        String fromProperties = getValueFromProperties("Shared Secret", SHAREDSECRET_PROPERTY_NAME);
        if (fromProperties != null) {
            result = fromProperties.trim();
        }
        return result;
    }

    private HotelApiVersion getHotelApiVersion(HotelApiVersion providedDefault) {
        HotelApiVersion result = providedDefault;
        String fromProperties = getValueFromProperties("HotelAPI version", VERSION_PROPERTY_NAME);
        if (fromProperties != null) {
            try {
                result = HotelApiVersion.valueOf(fromProperties.trim());
            } catch (Exception e) {
                log.error("Incorrect value provided for HotelAPI version: {}, it has to be one of {}. Using {}", new Object[] {
                    fromProperties, HotelApiVersion.values(), providedDefault});
                result = providedDefault;
            }
        }
        return result;
    }

    private HotelApiService getHotelApiService(HotelApiService providedDefault) {
        HotelApiService result = providedDefault;
        String fromProperties = getValueFromProperties("HotelAPI service", SERVICE_PROPERTY_NAME);
        if (fromProperties != null) {
            try {
                result = HotelApiService.valueOf(fromProperties.trim());
            } catch (Exception e) {
                log.error("Incorrect value provided for HotelAPI service: {}, it has to be one of {}. Using {}", new Object[] {
                    fromProperties, HotelApiService.values(), providedDefault});
                result = providedDefault;
            }
        }
        return result;
    }

    private String getValueFromProperties(String name, String propertyName) {
        String apiKey = System.getProperty(propertyName);
        if (apiKey == null) {
            apiKey = getHotelApiProperty(propertyName);
            if (apiKey != null) {
                log.debug("{} loaded from properties file. {}", name, apiKey);
            } else {
                log.debug("No {} loaded from properties, value not specified.", name);
            }
        } else {
            apiKey = apiKey.trim();
            log.debug("{} loaded from system properties. {}", name, apiKey);
        }
        return apiKey;
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public AvailabilityRS availability(Availability availability) throws HotelApiSDKException {
        return doAvailability(availability.toAvailabilityRQ());
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public AvailabilityRS availability(Availability availability, RequestType reqType) throws HotelApiSDKException {
        return doAvailability(availability.toAvailabilityRQ(), reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public AvailabilityRS doAvailability(final AvailabilityRQ request) throws HotelApiSDKException {
        return (AvailabilityRS) callRemoteAPI(request, HotelApiPaths.AVAILABILITY);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public AvailabilityRS doAvailability(final AvailabilityRQ request, RequestType reqType) throws HotelApiSDKException {
        return (AvailabilityRS) callRemoteAPI(request, HotelApiPaths.AVAILABILITY, reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingChangeRS change(String bookingId, BookingChangeRQ request, RequestType reqType) throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        addPropertiesAsParams(properties, params);
        return (BookingChangeRS) callRemoteAPI(request, params, HotelApiPaths.BOOKING_CHANGE, reqType);
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public BookingListRS list(LocalDate start, LocalDate end, int from, int to, BookingListFilterStatus status, BookingListFilterType filterType)
        throws HotelApiSDKException {
        return list(start, end, from, to, status, filterType, null, null, null, null, null);
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public BookingListRS list(LocalDate start, LocalDate end, int from, int to, BookingListFilterStatus status, BookingListFilterType filterType,
        Properties properties, List<String> countries, List<String> destinations, String clientReference, List<Integer> hotels)
        throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("start", start.toString());
        params.put("end", end.toString());
        params.put("from", Integer.toString(from));
        params.put("to", Integer.toString(to));
        if (status != null) {
            params.put("status", status.name());
        }
        if (filterType != null) {
            params.put("filterType", filterType.name());
        }
        if (countries != null && !countries.isEmpty()) {
            params.put("country", String.join(",", countries));
        }
        if (destinations != null && !destinations.isEmpty()) {
            params.put("destination", String.join(",", destinations));
        }
        if (hotels != null && !hotels.isEmpty()) {
            params.put("hotel", hotels.stream().map(hotelCode -> hotelCode.toString()).collect(Collectors.joining(",")));
        }
        if (clientReference != null) {
            params.put("clientReference", clientReference);
        }
        addPropertiesAsParams(properties, params);
        return (BookingListRS) callRemoteAPI(params, HotelApiPaths.BOOKING_LIST, RequestType.JSON);
    }

    // TODO Fix so it does return an object of the proper type, else throw an error if failed
    // TODO Documentation pending
    public BookingListRS list(LocalDate start, LocalDate end, int from, int to, BookingListFilterStatus status, BookingListFilterType filterType,
        Properties properties, List<String> countries, List<String> destinations, String clientReference, List<Integer> hotels, RequestType reqType)
        throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("start", start.toString());
        params.put("end", end.toString());
        params.put("from", Integer.toString(from));
        params.put("to", Integer.toString(to));
        if (status != null) {
            params.put("status", status.name());
        }
        if (filterType != null) {
            params.put("filterType", filterType.name());
        }
        if (countries != null && !countries.isEmpty()) {
            params.put("country", String.join(",", countries));
        }
        if (destinations != null && !destinations.isEmpty()) {
            params.put("destination", String.join(",", destinations));
        }
        if (hotels != null && !hotels.isEmpty()) {
            params.put("hotel", hotels.stream().map(hotelCode -> hotelCode.toString()).collect(Collectors.joining(",")));
        }
        if (clientReference != null) {
            params.put("clientReference", clientReference);
        }
        addPropertiesAsParams(properties, params);
        return (BookingListRS) callRemoteAPI(params, HotelApiPaths.BOOKING_LIST, reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingListRS list(BookingList bookingList) throws HotelApiSDKException {
        return list(bookingList.getFromDate(), bookingList.getToDate(), bookingList.getFrom(), bookingList.getTo(), bookingList.getStatus(),
            bookingList.getFilterType(), bookingList.getProperties(), bookingList.getCountries(), bookingList.getDestinations(),
            bookingList.getClientReference(), bookingList.getHotels());
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingDetailRS detail(String bookingId, Properties properties) throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        addPropertiesAsParams(properties, params);
        return (BookingDetailRS) callRemoteAPI(params, HotelApiPaths.BOOKING_DETAIL, RequestType.JSON);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingDetailRS detail(String bookingId, Properties properties, RequestType reqType) throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        addPropertiesAsParams(properties, params);
        return (BookingDetailRS) callRemoteAPI(params, HotelApiPaths.BOOKING_DETAIL, reqType);
    }


    public void addPropertiesAsParams(Properties properties, final Map<String, String> params) {
        if (properties != null) {
            for (Object name : properties.keySet()) {
                String propertyName = (String) name;
                params.put(EXTRA_PARAM_PREFIX + propertyName, properties.getProperty(propertyName));
            }
        }
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingDetailRS detail(String bookingId) throws HotelApiSDKException {
        return detail(bookingId, null, RequestType.JSON);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingDetailRS detail(String bookingId, RequestType reqType) throws HotelApiSDKException {
        return detail(bookingId, null, reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingRS confirm(Booking booking) throws HotelApiSDKException {
        return doBookingConfirm(booking.toBookingRQ());
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingRS confirm(Booking booking, RequestType reqType) throws HotelApiSDKException {
        return doBookingConfirm(booking.toBookingRQ(), reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingRS doBookingConfirm(BookingRQ request) throws HotelApiSDKException {
        if (request.getPaymentData() != null && request.getPaymentData().getPaymentCard() != null) {
            final Map<String, String> params = new HashMap<>();
            params.put("path", hotelApiService.getHotelApiSecurePath(alternativeHotelApiSecurePath));
            return (BookingRS) callRemoteAPI(request, params, HotelApiPaths.BOOKING_CONFIRM);
        } else {
            return (BookingRS) callRemoteAPI(request, HotelApiPaths.BOOKING_CONFIRM);
        }
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingRS doBookingConfirm(BookingRQ request, RequestType reqType) throws HotelApiSDKException {
        if (request.getPaymentData() != null && request.getPaymentData().getPaymentCard() != null) {
            final Map<String, String> params = new HashMap<>();
            params.put("path", hotelApiService.getHotelApiSecurePath(alternativeHotelApiSecurePath));
            return (BookingRS) callRemoteAPI(request, params, HotelApiPaths.BOOKING_CONFIRM, reqType);
        } else {
            return (BookingRS) callRemoteAPI(request, HotelApiPaths.BOOKING_CONFIRM, reqType);
        }
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingVoucherRS voucher(Voucher voucher) throws HotelApiSDKException {
        return doBookingVoucher(voucher.getBookingId(), voucher.toBookingVoucherRQ(), RequestType.JSON);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingVoucherRS voucher(Voucher voucher, RequestType reqType) throws HotelApiSDKException {
        return doBookingVoucher(voucher.getBookingId(), voucher.toBookingVoucherRQ(), reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingVoucherRS doBookingVoucher(String bookingId, BookingVoucherRQ request, RequestType reqtType) throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        return (BookingVoucherRS) callRemoteAPI(request, params, HotelApiPaths.BOOKING_VOUCHER, reqtType);
    }

    public BookingCancellationRS cancel(String bookingId) throws HotelApiSDKException {
        return cancel(bookingId, false, RequestType.JSON);
    }

    public BookingCancellationRS cancel(String bookingId, RequestType reqType) throws HotelApiSDKException {
        return cancel(bookingId, false, reqType);
    }

    public BookingCancellationRS cancel(String bookingId, boolean isSimulation) throws HotelApiSDKException {
        return cancel(bookingId, isSimulation, null, RequestType.JSON); //FIXME buscar solucion elegante a este problema
    }

    public BookingCancellationRS cancel(String bookingId, boolean isSimulation, RequestType reqType) throws HotelApiSDKException {
        return cancel(bookingId, isSimulation, null, reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public BookingCancellationRS cancel(String bookingId, boolean isSimulation, Properties properties) throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        params.put("cancellationFlag", isSimulation ? CancellationFlags.SIMULATION.name() : CancellationFlags.CANCELLATION.name());
        addPropertiesAsParams(properties, params);
        return (BookingCancellationRS) callRemoteAPI(params, HotelApiPaths.BOOKING_CANCEL, RequestType.JSON);
    }

    public BookingCancellationRS cancel(String bookingId, boolean isSimulation, Properties properties, RequestType reqType)
        throws HotelApiSDKException {
        final Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);
        params.put("cancellationFlag", isSimulation ? CancellationFlags.SIMULATION.name() : CancellationFlags.CANCELLATION.name());
        addPropertiesAsParams(properties, params);
        return (BookingCancellationRS) callRemoteAPI(params, HotelApiPaths.BOOKING_CANCEL, reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public CheckRateRS check(BookingCheck bookingCheck) throws HotelApiSDKException {
        return doCheckRate(bookingCheck.toCheckRateRQ());
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public CheckRateRS check(BookingCheck bookingCheck, RequestType reqType) throws HotelApiSDKException {
        return doCheckRate(bookingCheck.toCheckRateRQ(), reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public CheckRateRS doCheckRate(CheckRateRQ request) throws HotelApiSDKException {
        return (CheckRateRS) callRemoteAPI(request, HotelApiPaths.CHECK_AVAIL);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public CheckRateRS doCheckRate(CheckRateRQ request, RequestType reqType) throws HotelApiSDKException {
        return (CheckRateRS) callRemoteAPI(request, HotelApiPaths.CHECK_AVAIL, reqType);
    }

    //TODO Fix so it does return an object of the proper type, else throw an error if failed
    //TODO Documentation pending
    public StatusRS status() throws HotelApiSDKException {
        return (StatusRS) callRemoteAPI(HotelApiPaths.STATUS);
    }

    public StatusRS status(RequestType reqType) throws HotelApiSDKException {
        return (StatusRS) callRemoteAPI(HotelApiPaths.STATUS, reqType);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////// HOTEL CONTENT
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    public RateCommentDetailsRS getRateCommentDetail(Integer contract, Integer incoming, Integer... rates) throws HotelApiSDKException {
        return getRateCommentDetail(LocalDate.now(), contract, incoming, rates);
    }

    public RateCommentDetailsRS getRateCommentDetail(LocalDate date, Integer contract, Integer incoming, Integer... rates)
        throws HotelApiSDKException {
        return getRateCommentDetail(date, ObjectJoiner.join("|", contract, incoming, ObjectJoiner.join(",", Arrays.asList(rates))));
    }

    public RateCommentDetailsRS getRateCommentDetail(String rateCommentId) throws HotelApiSDKException {
        return getRateCommentDetail(LocalDate.now(), rateCommentId);
    }

    public RateCommentDetailsRS getRateCommentDetail(LocalDate date, String rateCommentId) throws HotelApiSDKException {
        RateCommentDetailsRQ request = new RateCommentDetailsRQ();
        final Map<String, String> params = new HashMap<>();
        request.setDate(date);
        ContentType.RATECOMMENT_DETAIL.addCommonParameters(request, params);
        params.put("code", rateCommentId);
        // Validate, date cannot be null
        params.put("date", DateSerializer.REST_FORMATTER.format(request.getDate()));
        return (RateCommentDetailsRS) callRemoteContentAPI(request, params, ContentType.RATECOMMENT_DETAIL);
    }


    public Hotel getHotel(final int code, final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        HotelDetailsRQ request = new HotelDetailsRQ();
        request.setLanguage(language);
        request.setUseSecondaryLanguage(useSecondaryLanguage);
        request.setFields(new String[] {"all"});
        final Map<String, String> params = new HashMap<>();
        ContentType.HOTEL_DETAIL.addCommonParameters(request, params);
        params.put("code", Integer.toString(code));
        HotelDetailsRS hotelDetailRS = (HotelDetailsRS) callRemoteContentAPI(request, params, ContentType.HOTEL_DETAIL);
        if (hotelDetailRS.getHotel() != null) {
            return hotelDetailRS.getHotel();
        } else {
            throw new HotelApiSDKException(new HotelbedsError("Hotel not found", Integer.toString(code)));
        }
    }

    public List<Destination> getAllDestinations(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.DESTINATION);
    }

    public Stream<Destination> destinationsStream() throws HotelApiSDKException {
        checkDefaultValues();
        return destinationsStream(defaultLanguage, defaultUseSecondaryLanguage);
    }

    public Stream<Destination> destinationsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.DESTINATION);
    }

    public List<Country> getAllCountries(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.COUNTRY);
    }

    public Stream<Country> countriesStream() throws HotelApiSDKException {
        checkDefaultValues();
        return countriesStream(defaultLanguage, defaultUseSecondaryLanguage);
    }

    public Stream<Country> countriesStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.COUNTRY);
    }

    public List<Hotel> getAllHotels(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.HOTEL);
    }

    public Stream<Hotel> hotelsStream() throws HotelApiSDKException {
        checkDefaultValues();
        return hotelsStream(defaultLanguage, defaultUseSecondaryLanguage);
    }

    private void checkDefaultValues() {
        if (StringUtils.isBlank(defaultLanguage)) {
            throw new IllegalArgumentException("You must specify a language or set the default language");
        }
    }

    public Stream<Hotel> hotelsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.HOTEL);
    }

    public Stream<Hotel> hotelsStream(final String language, final boolean useSecondaryLanguage, String... fields) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.HOTEL, fields);
    }

    ////////////////
    // Gemeric Types
    ////////////////

    //    public BoardsRS getBoards(BoardsRQ request) throws HotelApiSDKException {
    //        final Map<String, String> params = new HashMap<>();
    //        addCommonParameters(request, ContentType.BOARD, params);
    //        return (BoardsRS) callRemoteContentAPI(request, params, ContentType.BOARD);
    //    }

    public List<Board> getAllBoards(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.BOARD);
    }

    public Stream<Board> boardsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.BOARD);
    }

    public List<Chain> getAllChains(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.CHAIN);
    }

    public Stream<Chain> chainsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.CHAIN);
    }

    public List<Accommodation> getAllAccommodations(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.ACCOMODATION);
    }

    public Stream<Accommodation> accommodationsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.ACCOMODATION);
    }

    public List<Category> getAllCategories(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.CATEGORY);
    }

    public Stream<Category> categoriesStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.CATEGORY);
    }

    public List<RateComments> getAllRateComments(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.RATECOMMENT);
    }

    public Stream<RateComments> rateCommentsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.RATECOMMENT);
    }

    public List<Currency> getAllCurrencies(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.CURRENCY);
    }

    public Stream<Currency> currenciesStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.CURRENCY);
    }

    public List<Facility> getAllFacilities(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.FACILITY);
    }

    public Stream<Facility> facilitiesStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.FACILITY);
    }

    public List<FacilityGroup> getAllFacilityGroups(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.FACILITY_GROUP);
    }

    public Stream<FacilityGroup> facilityGroupsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.FACILITY_GROUP);
    }

    public List<FacilityType> getAllFacilityTypes(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.FACILITY_TYPE);
    }

    public Stream<FacilityType> facilityTypesStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.FACILITY_TYPE);
    }

    public List<Issue> getAllIssues(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.ISSUE);
    }

    public Stream<Issue> issuesStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.ISSUE);
    }

    public List<Language> getAllLanguages(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.LANGUAGE);
    }

    public Stream<Language> languagesStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.LANGUAGE);
    }

    public List<Promotion> getAllPromotions(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.PROMOTION);
    }

    public Stream<Promotion> promotionsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.PROMOTION);
    }

    public List<Room> getAllRooms(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.ROOM);
    }

    public Stream<Room> roomsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.ROOM);
    }

    public List<Segment> getAllSegments(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.SEGMENT);
    }

    public Stream<Segment> segmentsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.SEGMENT);
    }

    public List<Terminal> getAllTerminals(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.TERMINAL);
    }

    public Stream<Terminal> terminalsStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.TERMINAL);
    }

    public List<ImageType> getAllImageTypes(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.IMAGE_TYPE);
    }

    public Stream<ImageType> imageTypesStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.IMAGE_TYPE);
    }

    public List<GroupCategory> getAllGroupCategories(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getAllElements(language, useSecondaryLanguage, ContentType.GROUP_CATEGORY);
    }

    public Stream<GroupCategory> groupCategoriesStream(final String language, final boolean useSecondaryLanguage) throws HotelApiSDKException {
        return getStreamOf(language, useSecondaryLanguage, ContentType.GROUP_CATEGORY);
    }

    @Data
    private class RemoteApiCallable implements Callable<AbstractGenericContentResponse> {
        private final ContentType type;
        private final AbstractGenericContentRequest abstractGenericContentRequest;
        private final Map<String, String> callableParams;

        @Override
        public AbstractGenericContentResponse call() throws Exception {
            return callRemoteContentAPI(abstractGenericContentRequest, callableParams, type);
        }
    }

    private <T> List<T> getAllElements(final String language, final boolean useSecondaryLanguage, ContentType type) throws HotelApiSDKException {
        try {
            return StreamSupport.stream(
                new ContentElementSpliterator<T>(this, type, generateDefaultFullRequest(language, useSecondaryLanguage, type)), false).collect(
                Collectors.toList());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new HotelApiSDKException(new HotelbedsError("SDK Configuration error", e.getCause().getMessage()));
        }
    }

    private <T> Stream<T> getStreamOf(final String language, final boolean useSecondaryLanguage, ContentType type) throws HotelApiSDKException {
        try {
            return StreamSupport.stream(
                new ContentElementSpliterator<T>(this, type, generateDefaultFullRequest(language, useSecondaryLanguage, type)), false);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new HotelApiSDKException(new HotelbedsError("SDK Configuration error", e.getCause().getMessage()));
        }
    }

    private <T> Stream<T> getStreamOf(final String language, final boolean useSecondaryLanguage, ContentType type, String... fields)
        throws HotelApiSDKException {
        try {
            return StreamSupport.stream(
                new ContentElementSpliterator<T>(this, type, generateDefaultFullRequest(language, useSecondaryLanguage, type, fields)), false);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new HotelApiSDKException(new HotelbedsError("SDK Configuration error", e.getCause().getMessage()));
        }
    }

    private AbstractGenericContentRequest generateDefaultFullRequest(final String language, final boolean useSecondaryLanguage, ContentType type)
        throws InstantiationException, IllegalAccessException {
        final AbstractGenericContentRequest abstractGenericContentRequest;
        final Map<String, String> params = new HashMap<>();
        abstractGenericContentRequest = type.getRequestClass().newInstance();
        abstractGenericContentRequest.setLanguage(language);
        abstractGenericContentRequest.setUseSecondaryLanguage(useSecondaryLanguage);
        abstractGenericContentRequest.setFields(new String[] {"all"});
        type.addCommonParameters(abstractGenericContentRequest, params);
        return abstractGenericContentRequest;
    }

    private AbstractGenericContentRequest generateDefaultFullRequest(final String language, final boolean useSecondaryLanguage, ContentType type,
        String... fields) throws InstantiationException, IllegalAccessException {
        AbstractGenericContentRequest abstractGenericContentRequest = generateDefaultFullRequest(language, useSecondaryLanguage, type);
        abstractGenericContentRequest.setFields(fields);
        return abstractGenericContentRequest;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////// INTERNALS
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    private GenericResponse callRemoteAPI(HotelApiPaths path) throws HotelApiSDKException {
        return callRemoteAPI(null, null, path);
    }

    private GenericResponse callRemoteAPI(final Map<String, String> params, HotelApiPaths path) throws HotelApiSDKException {
        return callRemoteAPI(null, params, path);
    }

    private GenericResponse callRemoteAPI(final AbstractGenericRequest request, HotelApiPaths path) throws HotelApiSDKException {
        return callRemoteAPI(request, null, path);
    }

    private GenericResponse callRemoteAPI(HotelApiPaths path, RequestType requestType) throws HotelApiSDKException {
        return callRemoteAPI((AbstractGenericRequest) null, null, path, requestType);
    }

    private GenericResponse callRemoteAPI(final Map<String, String> params, HotelApiPaths path, RequestType requestType) throws HotelApiSDKException {
        return callRemoteAPI(null, params, path, requestType);
    }

    private GenericResponse callRemoteAPI(final AbstractGenericRequest request, HotelApiPaths path, RequestType requestType)
        throws HotelApiSDKException {
        return callRemoteAPI(request, null, path, requestType);
    }

    private String obtainUrlFromPath(final Map<String, String> params, HotelApiPaths path) {
        final String url;
        if ((AllowedMethod.GET == path.getAllowedMethod() || AllowedMethod.DELETE == path.getAllowedMethod()) && !path.getAllowedParams().isEmpty()) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(path.getUrl(hotelApiService, hotelApiVersion, params, alternativeHotelApiPath)).newBuilder();
            for (String param : path.getAllowedParams()) {
                String value = params.get(param);
                if (value != null) {
                    urlBuilder.addQueryParameter(param, value);
                }
            }
            url = urlBuilder.build().toString();
        } else {
            url = path.getUrl(hotelApiService, hotelApiVersion, params, alternativeHotelApiPath);
        }
        return url;
    }

    private Request.Builder generateRequestBuilder(final AbstractGenericRequest abstractGenericRequest, final AllowedMethod allowedMethod,
        final String url) throws HotelApiSDKException {
        Request.Builder requestBuilder = new Request.Builder().headers(getHeaders(allowedMethod)).url(url);
        switch (allowedMethod) {
            case DELETE:
                requestBuilder.delete(transformToRequestBody(abstractGenericRequest));
                break;
            case PUT:
                requestBuilder.put(transformToRequestBody(abstractGenericRequest));
                break;
            case POST:
                requestBuilder.post(transformToRequestBody(abstractGenericRequest));
                break;
            default:
                break;
        }
        return requestBuilder;
    }

    private Request.Builder generateRequestBuilder(final AbstractGenericRequest abstractGenericRequest, final AllowedMethod allowedMethod,
        final String url, final RequestType reqType) throws HotelApiSDKException {
        Request.Builder requestBuilder = new Request.Builder().headers(getHeaders(allowedMethod, reqType)).url(url);

        RequestBody reqBody = null;
        if (reqType.equals(RequestType.JSON)) {
            reqBody = transformToJsonRequestBody(abstractGenericRequest);
        } else if (reqType.equals(RequestType.XML)) {
            reqBody = transformToXmlRequestBody(abstractGenericRequest);
        } else {
            throw new HotelApiSDKException(new HotelbedsError("Invalid request", reqType.toString() + " content type is not supported."));
        }
        switch (allowedMethod) {
            case DELETE:
                requestBuilder.delete(reqBody);
                break;
            case PUT:
                requestBuilder.put(reqBody);
                break;
            case POST:
                requestBuilder.post(reqBody);
                break;
            default:
                break;
        }
        return requestBuilder;
    }

    private GenericResponse callRemoteAPI(final AbstractGenericRequest abstractGenericRequest, final Map<String, String> params, HotelApiPaths path)
        throws HotelApiSDKException {
        if (isInitialised()) {
            final String url = obtainUrlFromPath(params, path);
            try {
                Request.Builder requestBuilder = generateRequestBuilder(abstractGenericRequest, path.getAllowedMethod(), url);

                Response response = restTemplate.newCall(requestBuilder.build()).execute();
                try (ResponseBody body = response.body()) {
                    BufferedSource source = body.source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer = source.buffer();
                    Charset charset = AssignUtils.UTF8;
                    if (body.contentType() != null) {
                        try {
                            charset = body.contentType().charset(AssignUtils.UTF8);
                        } catch (UnsupportedCharsetException e) {
                            log.error("Response body could not be decoded {}", e.getMessage());
                        }
                    }
                    String theContent = buffer.readString(charset);
                    if (response.headers().get(CONTENT_TYPE_HEADER).toLowerCase().startsWith(HotelApiClient.APPLICATION_JSON_HEADER)) {
                        GenericResponse genericResponse = transformToGenericResponse(theContent, path.getResponseClass());
                        if (genericResponse.getError() != null) {
                            throw new HotelApiSDKException(genericResponse.getError());
                        }
                        return genericResponse;
                    } else {
                        throw new HotelApiSDKException(new HotelbedsError("Invalid response", "Wrong content type"
                            + response.headers().get(CONTENT_TYPE_HEADER)));
                    }
                }
            } catch (HotelApiSDKException e) {
                throw e;
            } catch (IOException e) {
                if (e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
                    throw new HotelApiSDKException(new HotelbedsError("Timeout", e.getMessage()), e);
                } else {
                    throw new HotelApiSDKException(new HotelbedsError("Error accessing API", e.getMessage()), e);
                }
            } catch (Exception e) {
                throw new HotelApiSDKException(new HotelbedsError(e.getClass().getName(), e.getMessage()), e);
            }
        } else {
            throw new HotelApiSDKException(new HotelbedsError("HotelAPIClient not initialised",
                "You have to call init() first, to be able to use this object."));
        }
    }

    private GenericResponse callRemoteAPI(final AbstractGenericRequest abstractGenericRequest, final Map<String, String> params, HotelApiPaths path,
        RequestType reqType) throws HotelApiSDKException {
        if (isInitialised()) {
            final String url = obtainUrlFromPath(params, path);
            try {
                Request.Builder requestBuilder = generateRequestBuilder(abstractGenericRequest, path.getAllowedMethod(), url, reqType);

                Response response = restTemplate.newCall(requestBuilder.build()).execute();
                try (ResponseBody body = response.body()) {
                    BufferedSource source = body.source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer = source.buffer();
                    Charset charset = AssignUtils.UTF8;
                    if (body.contentType() != null) {
                        try {
                            charset = body.contentType().charset(AssignUtils.UTF8);
                        } catch (UnsupportedCharsetException e) {
                            log.error("Response body could not be decoded {}", e.getMessage());
                        }
                    }
                    String theContent = buffer.readString(charset);
                    if (response.headers().get(CONTENT_TYPE_HEADER).toLowerCase().startsWith(HotelApiClient.APPLICATION_JSON_HEADER)) {
                        GenericResponse genericResponse = transformJsonToGenericResponse(theContent, path.getResponseClass());
                        if (genericResponse.getError() != null) {
                            throw new HotelApiSDKException(genericResponse.getError());
                        }

                        return genericResponse;

                    } else if (response.headers().get(CONTENT_TYPE_HEADER).toLowerCase().startsWith(HotelApiClient.APPLICATION_XML_HEADER)) {
                        GenericResponse genericResponse = transformXmlToGenericResponse(theContent, path.getResponseClass());
                        if (genericResponse.getError() != null) {
                            throw new HotelApiSDKException(genericResponse.getError());
                        }

                        return genericResponse;
                    }

                    else

                    {
                        throw new HotelApiSDKException(new HotelbedsError("Invalid response", "Wrong content type"
                            + response.headers().get(CONTENT_TYPE_HEADER)));
                    }
                }
            } catch (HotelApiSDKException e) {
                throw e;
            } catch (IOException e) {
                if (e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
                    throw new HotelApiSDKException(new HotelbedsError("Timeout", e.getMessage()), e);
                } else {
                    throw new HotelApiSDKException(new HotelbedsError("Error accessing API", e.getMessage()), e);
                }
            } catch (Exception e) {
                throw new HotelApiSDKException(new HotelbedsError(e.getClass().getName(), e.getMessage()), e);
            }
        } else {
            throw new HotelApiSDKException(new HotelbedsError("HotelAPIClient not initialised",
                "You have to call init() first, to be able to use this object."));
        }
    }

    private GenericResponse transformToGenericResponse(String content, Class<? extends GenericResponse> responseClass) throws HotelApiSDKException {
        try {
            return mapper.readValue(content, responseClass);
        } catch (IOException e) {
            log.error("Error parsing JSON response: ", e);
            throw new HotelApiSDKException(new HotelbedsError("Error parsing JSON response", e.getMessage()));
        }
    }

    private GenericResponse transformJsonToGenericResponse(String content, Class<? extends GenericResponse> responseClass)
        throws HotelApiSDKException {
        try {
            return mapper.readValue(content, responseClass);
        } catch (IOException e) {
            log.error("Error parsing JSON response: ", e);
            throw new HotelApiSDKException(new HotelbedsError("Error parsing JSON response", e.getMessage()));
        }
    }

    private GenericResponse transformXmlToGenericResponse(String content, Class<? extends GenericResponse> responseClass) throws HotelApiSDKException {
        GenericResponse genericResponse = null;
        try {
            StringReader reader = new StringReader(content);
            JAXBContext context = JAXBContext.newInstance(responseClass);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            genericResponse = (GenericResponse) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            log.error("Error parsing XML response: ", e);
            throw new HotelApiSDKException(new HotelbedsError("Error parsing XML response", e.getMessage()));
        }
        return genericResponse;
    }

    private String obtainUrlFromContentPath(final Map<String, String> params, HotelContentPaths path) {
        final String url;
        if (!path.getAllowedParams().isEmpty()) {
            HttpUrl.Builder urlBuilder =
                HttpUrl.parse(path.getUrl(hotelApiService, hotelApiVersion, params, alternativeHotelContentPath)).newBuilder();
            for (String param : path.getAllowedParams()) {
                String value = params.get(param);
                if (value != null) {
                    urlBuilder.addQueryParameter(param, value);
                }
            }
            url = urlBuilder.build().toString();
        } else {
            url = path.getUrl(hotelApiService, hotelApiVersion, params, alternativeHotelContentPath);
        }
        return url;
    }

    AbstractGenericContentResponse callRemoteContentAPI(final AbstractGenericContentRequest abstractGenericContentResponse,
        final Map<String, String> params, ContentType type) throws HotelApiSDKException {
        HotelContentPaths path = type.getPath();
        if (isInitialised()) {
            final AllowedMethod allowedMethod = AllowedMethod.GET;
            final String url = obtainUrlFromContentPath(params, path);
            try {
                Request.Builder requestBuilder = new Request.Builder().headers(getHeaders(allowedMethod)).url(url);
                Response response = restTemplate.newCall(requestBuilder.build()).execute();
                try (ResponseBody body = response.body()) {
                    BufferedSource source = body.source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer = source.buffer();
                    Charset charset = AssignUtils.UTF8;
                    if (body.contentType() != null) {
                        try {
                            charset = body.contentType().charset(AssignUtils.UTF8);
                        } catch (UnsupportedCharsetException e) {
                            log.error("Response body could not be decoded {}", e.getMessage());
                        }
                    }
                    String theContent = buffer.readString(charset);
                    if (response.headers().get(CONTENT_TYPE_HEADER).toLowerCase().startsWith(HotelApiClient.APPLICATION_JSON_HEADER)) {
                        AbstractGenericContentResponse genericResponse = transformToGenericContentResponse(theContent, type.getResponseClass());
                        if (genericResponse.getError() != null) {
                            throw new HotelApiSDKException(genericResponse.getError());
                        }
                        return genericResponse;
                    } else {
                        throw new HotelApiSDKException(new HotelbedsError("Invalid response", "Wrong content type"
                            + response.headers().get(CONTENT_TYPE_HEADER)));
                    }
                }
            } catch (HotelApiSDKException e) {
                throw e;
            } catch (IOException e) {
                if (e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
                    throw new HotelApiSDKException(new HotelbedsError("Timeout", e.getCause().getMessage()));
                } else if (e.getCause() != null) {
                    throw new HotelApiSDKException(new HotelbedsError("Error accessing API", e.getCause().getMessage()));
                } else {
                    throw new HotelApiSDKException(new HotelbedsError("Error accessing API", e.getMessage()));
                }
            } catch (Exception e) {
                throw new HotelApiSDKException(new HotelbedsError(e.getClass().getName(), e.getMessage()), e);
            }
        } else {
            throw new HotelApiSDKException(new HotelbedsError("HotelAPIClient not initialised",
                "You have to call init() first, to be able to use this object."));
        }
    }

    private AbstractGenericContentResponse transformToGenericContentResponse(String content,
        Class<? extends AbstractGenericContentResponse> responseClass) throws HotelApiSDKException {
        try {
            return mapper.readValue(content, responseClass);
        } catch (IOException e) {
            log.error("Error parsing JSON response: ", e);
            throw new HotelApiSDKException(new HotelbedsError("Error parsing JSON response", e.getMessage()));
        }
    }

    private RequestBody transformToRequestBody(AbstractGenericRequest request) throws HotelApiSDKException {
        try {
            return RequestBody.create(JSON, mapper.writeValueAsString(request));
        } catch (IOException e) {
            log.error("Error parsing JSON response: ", e);
            throw new HotelApiSDKException(new HotelbedsError("Error parsing JSON response", e.getMessage()));
        }
    }

    private RequestBody transformToJsonRequestBody(AbstractGenericRequest request) throws HotelApiSDKException {
        try {
            return RequestBody.create(JSON, mapper.writeValueAsString(request));
        } catch (IOException e) {
            log.error("Error parsing JSON response: ", e);
            throw new HotelApiSDKException(new HotelbedsError("Error parsing JSON response", e.getMessage()));
        }
    }

    private RequestBody transformToXmlRequestBody(AbstractGenericRequest request) throws HotelApiSDKException {
        try {
            String xmlString = null;
            if (request != null) { //TODO quick fix for void body requests
                JAXBContext context = JAXBContext.newInstance(request.getClass());
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(request, stringWriter);
                xmlString = stringWriter.toString();
                return RequestBody.create(XML, xmlString);
            } else {
                return RequestBody.create(null, new byte[0]);
            }//for void requests
        } catch (JAXBException e) {
            log.error("Error parsing XML response: ", e);
            throw new HotelApiSDKException(new HotelbedsError("Error parsing XML response", e.getMessage()));
        }
    }

    private Headers getHeaders(AllowedMethod httpMethod) {
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add(API_KEY_HEADER_NAME, apiKey);
        headersBuilder.add("User-Agent", "hotel-api-sdk-java, " + getClass().getPackage().getImplementationVersion());
        // Hash the Api Key + Shared Secret + Current timestamp in seconds
        headersBuilder.add(SIGNATURE_HEADER_NAME, DigestUtils.sha256Hex(apiKey + sharedSecret + System.currentTimeMillis() / 1000));
        switch (httpMethod) {
            case POST:
            case PUT:
                headersBuilder.add("Content-Type", APPLICATION_JSON_HEADER);
            case GET:
            case DELETE:
                headersBuilder.add("Accept", APPLICATION_JSON_HEADER);
                break;
            default:
                break;
        }
        return headersBuilder.build();
    }

    private Headers getHeaders(AllowedMethod httpMethod, RequestType reqType) {
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add(API_KEY_HEADER_NAME, apiKey);
        headersBuilder.add("User-Agent", "hotel-api-sdk-java, " + getClass().getPackage().getImplementationVersion());
        // Hash the Api Key + Shared Secret + Current timestamp in seconds
        headersBuilder.add(SIGNATURE_HEADER_NAME, DigestUtils.sha256Hex(apiKey + sharedSecret + System.currentTimeMillis() / 1000));

        String contentType = null;
        if (reqType.equals(RequestType.JSON)) {
            contentType = APPLICATION_JSON_HEADER;
        } else if (reqType.equals(RequestType.XML)) {
            contentType = APPLICATION_XML_HEADER;
        } else {
            log.error("Error content type not supported");
        }

        switch (httpMethod) {
            case POST:
            case PUT:
            case GET:
            case DELETE:
                headersBuilder.add("Content-Type", contentType);
                headersBuilder.add("Accept", contentType);
                break;
            default:
                break;
        }
        return headersBuilder.build();
    }

    @Override
    public void close() {
        try {
            if (executorService != null) {
                executorService.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Error closing HotelAPI client resources", e);
        }
    }
}
