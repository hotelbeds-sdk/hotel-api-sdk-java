package com.hotelbeds.distribution.hotel_api_sdk.types;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import com.hotelbeds.hotelapimodel.auto.util.AssignUtils;
import com.hotelbeds.hotelcontentapi.auto.convert.json.DateSerializer;
import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentRequest;
import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentResponse;
import com.hotelbeds.hotelcontentapi.auto.messages.Accommodation;
import com.hotelbeds.hotelcontentapi.auto.messages.AccommodationsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.AccommodationsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Board;
import com.hotelbeds.hotelcontentapi.auto.messages.BoardsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.BoardsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.CategoriesRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.CategoriesRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Category;
import com.hotelbeds.hotelcontentapi.auto.messages.Chain;
import com.hotelbeds.hotelcontentapi.auto.messages.ChainsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.ChainsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.CountriesRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.CountriesRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Country;
import com.hotelbeds.hotelcontentapi.auto.messages.CurrenciesRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.CurrenciesRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Currency;
import com.hotelbeds.hotelcontentapi.auto.messages.Destination;
import com.hotelbeds.hotelcontentapi.auto.messages.DestinationsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.DestinationsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilitiesRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilitiesRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Facility;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityGroup;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityGroupsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityGroupsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityType;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityTypesRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.FacilityTypologiesRS;
import com.hotelbeds.hotelcontentapi.auto.messages.GroupCategoriesRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.GroupCategoriesRS;
import com.hotelbeds.hotelcontentapi.auto.messages.GroupCategory;
import com.hotelbeds.hotelcontentapi.auto.messages.Hotel;
import com.hotelbeds.hotelcontentapi.auto.messages.HotelDetailsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.HotelDetailsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.HotelsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.HotelsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.ImageType;
import com.hotelbeds.hotelcontentapi.auto.messages.ImageTypesRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.ImageTypesRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Issue;
import com.hotelbeds.hotelcontentapi.auto.messages.IssuesRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.IssuesRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Language;
import com.hotelbeds.hotelcontentapi.auto.messages.LanguagesRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.LanguagesRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Promotion;
import com.hotelbeds.hotelcontentapi.auto.messages.PromotionsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.PromotionsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.RateCommentDetailsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.RateCommentDetailsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.RateComments;
import com.hotelbeds.hotelcontentapi.auto.messages.RateCommentsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.RateCommentsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Room;
import com.hotelbeds.hotelcontentapi.auto.messages.RoomsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.RoomsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Segment;
import com.hotelbeds.hotelcontentapi.auto.messages.SegmentsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.SegmentsRS;
import com.hotelbeds.hotelcontentapi.auto.messages.Terminal;
import com.hotelbeds.hotelcontentapi.auto.messages.TerminalsRQ;
import com.hotelbeds.hotelcontentapi.auto.messages.TerminalsRS;

import lombok.Data;

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


public enum ContentType {
    // @formatter:off
    RATECOMMENT_DETAIL("ratecommentdetails", HotelContentPaths.RATECOMMENT_DETAIL_URL, RateCommentDetailsRQ.class, RateCommentDetailsRS.class, null),
    //
    HOTEL_DETAIL("hotels", HotelContentPaths.HOTEL_DETAIL_URL, HotelDetailsRQ.class, HotelDetailsRS.class, null),
    //
    HOTEL("hotels", HotelContentPaths.HOTELS_URL, HotelsRQ.class, HotelsRS.class, FunctionHolder.HOTELS_EXTRACTOR, "codes", "countryCode","destinationCode"),
    //
    DESTINATION("destinations", HotelContentPaths.DESTINATIONS_URL, DestinationsRQ.class, DestinationsRS.class, FunctionHolder.DESTINATIONS_EXTRACTOR, "codes","countryCodes"),
    COUNTRY("countries", HotelContentPaths.COUNTRIES_URL, CountriesRQ.class, CountriesRS.class, FunctionHolder.COUNTRIES_EXTRACTOR, "codes"),
    //
    BOARD("boards", HotelContentPaths.TYPES_URL, BoardsRQ.class, BoardsRS.class, FunctionHolder.BOARD_EXTRACTOR),
    CHAIN("chains", HotelContentPaths.TYPES_URL, ChainsRQ.class, ChainsRS.class, FunctionHolder.CHAIN_EXTRACTOR),
    ACCOMODATION("accommodations", HotelContentPaths.TYPES_URL, AccommodationsRQ.class, AccommodationsRS.class, FunctionHolder.ACCOMMODATION_EXTRACTOR),
    CATEGORY("categories", HotelContentPaths.TYPES_URL, CategoriesRQ.class, CategoriesRS.class, FunctionHolder.CATEGORY_EXTRACTOR),
    RATECOMMENT("ratecomments", HotelContentPaths.TYPES_URL, RateCommentsRQ.class, RateCommentsRS.class, FunctionHolder.RATECOMMENT_EXTRACTOR),
    CURRENCY("currencies", HotelContentPaths.TYPES_URL, CurrenciesRQ.class, CurrenciesRS.class, FunctionHolder.CURRENCY_EXTRACTOR),
    FACILITY("facilities", HotelContentPaths.TYPES_URL, FacilitiesRQ.class, FacilitiesRS.class, FunctionHolder.FACILITY_EXTRACTOR),
    FACILITY_GROUP("facilitygroups", HotelContentPaths.TYPES_URL, FacilityGroupsRQ.class, FacilityGroupsRS.class, FunctionHolder.FACILITY_GROUP_EXTRACTOR),
    FACILITY_TYPE("facilitytypologies", HotelContentPaths.TYPES_URL, FacilityTypesRQ.class, FacilityTypologiesRS.class, FunctionHolder.FACILITY_TYPE_EXTRACTOR),
    ISSUE("issues", HotelContentPaths.TYPES_URL, IssuesRQ.class, IssuesRS.class, FunctionHolder.ISSUE_EXTRACTOR),
    LANGUAGE("languages", HotelContentPaths.TYPES_URL, LanguagesRQ.class, LanguagesRS.class, FunctionHolder.LANGUAGE_EXTRACTOR),
    PROMOTION("promotions", HotelContentPaths.TYPES_URL, PromotionsRQ.class, PromotionsRS.class, FunctionHolder.PROMOTION_EXTRACTOR),
    ROOM("rooms", HotelContentPaths.TYPES_URL, RoomsRQ.class, RoomsRS.class, FunctionHolder.ROOM_EXTRACTOR),
    SEGMENT("segments", HotelContentPaths.TYPES_URL, SegmentsRQ.class, SegmentsRS.class, FunctionHolder.SEGMENT_EXTRACTOR),
    TERMINAL("terminals", HotelContentPaths.TYPES_URL, TerminalsRQ.class, TerminalsRS.class, FunctionHolder.TERMINAL_EXTRACTOR),
    IMAGE_TYPE("imagetypes", HotelContentPaths.TYPES_URL, ImageTypesRQ.class, ImageTypesRS.class, FunctionHolder.IMAGE_TYPE_EXTRACTOR),
    GROUP_CATEGORY("groupcategories", HotelContentPaths.TYPES_URL, GroupCategoriesRQ.class, GroupCategoriesRS.class, FunctionHolder.GROUP_CATEGORY_EXTRACTOR),
    // @formatter:on
    ;

    private final String urlTag;
    private final HotelContentPaths path;
    private final Class<? extends AbstractGenericContentRequest> requestClass;
    private final Class<? extends AbstractGenericContentResponse> responseClass;
    private final Function<AbstractGenericContentResponse, Collection<?>> resultsFunction;
    private final String[] defaultParameters;

    private ContentType(String urlTag, HotelContentPaths path, final Class<? extends AbstractGenericContentRequest> requestClass,
        final Class<? extends AbstractGenericContentResponse> responseClass,
        final Function<AbstractGenericContentResponse, Collection<?>> resultsFunction, final String... defaultParameters) {
        this.urlTag = urlTag;
        this.path = path;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
        this.resultsFunction = resultsFunction;
        this.defaultParameters = defaultParameters;
    }

    public String getUrlTag() {
        return urlTag;
    }

    public Class<? extends AbstractGenericContentRequest> getRequestClass() {
        return requestClass;
    }

    public Class<? extends AbstractGenericContentResponse> getResponseClass() {
        return responseClass;
    }

    public HotelContentPaths getPath() {
        return path;
    }

    public Function<AbstractGenericContentResponse, Collection<?>> getResultsFunction() {
        return resultsFunction;
    }

    public String[] getDefaultParameters() {
        return defaultParameters;
    }

    public void addCommonParameters(AbstractGenericContentRequest request, final Map<String, String> params) {
        if (getDefaultParameters() != null) {
            for (String param : getDefaultParameters()) {
                params.put(param, "");
            }
        }
        params.put("type", getUrlTag());
        params.put("language", request.getLanguage() != null ? request.getLanguage() : "ENG");
        params.put("from", request.getFrom() != null ? request.getFrom().toString() : "1");
        params.put("to", request.getTo() != null ? request.getTo().toString() : "100");
        params.put("fields", request.getFields() != null ? String.join(",", request.getFields()) : null);
        params.put("useSecondaryLanguage", Boolean.toString(request.isUseSecondaryLanguage()));
        params.put("lastUpdateTime", request.getLastUpdateTime() != null ? DateSerializer.REST_FORMATTER.format(request.getLastUpdateTime()) : null);
    }


    @Data
    private static class Extractor<T extends AbstractGenericContentResponse, Y> implements Function<AbstractGenericContentResponse, Collection<?>> {

        private final Class<T> responseClass;
        private final Function<T, Collection<Y>> resultsFunction;

        @Override
        public Collection<Y> apply(AbstractGenericContentResponse abstractGenericContentResponse) {
            Collection<Y> result = Collections.emptyList();
            if (abstractGenericContentResponse != null && responseClass.isInstance(abstractGenericContentResponse)) {
                @SuppressWarnings("unchecked")
                T response = (T) abstractGenericContentResponse;
                Collection<Y> responseResults = resultsFunction.apply(response);
                if (AssignUtils.isNotEmpty(responseResults)) {
                    result = responseResults;
                }
            }
            return result;
        }
    }

    private static class FunctionHolder {
        //
        private static final Function<HotelsRS, Collection<Hotel>> HOTELS_RS_EXTRACTOR = response -> {
            return response.getHotels();
        };
        private static final Extractor<HotelsRS, Hotel> HOTELS_EXTRACTOR = new Extractor<HotelsRS, Hotel>(HotelsRS.class, HOTELS_RS_EXTRACTOR);
        //
        private static final Function<CountriesRS, Collection<Country>> COUNTRIES_RS_EXTRACTOR = response -> {
            return response.getCountries();
        };
        private static final Extractor<CountriesRS, Country> COUNTRIES_EXTRACTOR = new Extractor<CountriesRS, Country>(CountriesRS.class,
            COUNTRIES_RS_EXTRACTOR);
        //
        private static final Function<DestinationsRS, Collection<Destination>> DESTINATIONS_RS_EXTRACTOR = response -> {
            return response.getDestinations();
        };
        private static final Extractor<DestinationsRS, Destination> DESTINATIONS_EXTRACTOR = new Extractor<DestinationsRS, Destination>(
            DestinationsRS.class, DESTINATIONS_RS_EXTRACTOR);
        //
        private static final Function<BoardsRS, Collection<Board>> BOARD_RS_EXTRACTOR = response -> {
            return response.getBoards();
        };
        private static final Extractor<BoardsRS, Board> BOARD_EXTRACTOR = new Extractor<BoardsRS, Board>(BoardsRS.class, BOARD_RS_EXTRACTOR);
        //
        private static final Function<ChainsRS, Collection<Chain>> CHAIN_RS_EXTRACTOR = response -> {
            return response.getChains();
        };
        private static final Extractor<ChainsRS, Chain> CHAIN_EXTRACTOR = new Extractor<ChainsRS, Chain>(ChainsRS.class, CHAIN_RS_EXTRACTOR);
        //
        private static final Function<AccommodationsRS, Collection<Accommodation>> ACCOMMODATION_RS_EXTRACTOR = response -> {
            return response.getAccommodations();
        };
        private static final Extractor<AccommodationsRS, Accommodation> ACCOMMODATION_EXTRACTOR = new Extractor<AccommodationsRS, Accommodation>(
            AccommodationsRS.class, ACCOMMODATION_RS_EXTRACTOR);
        //
        private static final Function<CategoriesRS, Collection<Category>> CATEGORY_RS_EXTRACTOR = response -> {
            return response.getCategories();
        };
        private static final Extractor<CategoriesRS, Category> CATEGORY_EXTRACTOR = new Extractor<CategoriesRS, Category>(CategoriesRS.class,
            CATEGORY_RS_EXTRACTOR);
        //
        private static final Function<RateCommentsRS, Collection<RateComments>> RATECOMMENT_RS_EXTRACTOR = response -> {
            return response.getRateComments();
        };
        private static final Extractor<RateCommentsRS, RateComments> RATECOMMENT_EXTRACTOR = new Extractor<RateCommentsRS, RateComments>(
            RateCommentsRS.class, RATECOMMENT_RS_EXTRACTOR);

        //
        private static final Function<CurrenciesRS, Collection<Currency>> CURRENCY_RS_EXTRACTOR = response -> {
            return response.getCurrencies();
        };
        private static final Extractor<CurrenciesRS, Currency> CURRENCY_EXTRACTOR = new Extractor<CurrenciesRS, Currency>(CurrenciesRS.class,
            CURRENCY_RS_EXTRACTOR);
        //
        private static final Function<FacilitiesRS, Collection<Facility>> FACILITY_RS_EXTRACTOR = response -> {
            return response.getFacilities();
        };
        private static final Extractor<FacilitiesRS, Facility> FACILITY_EXTRACTOR = new Extractor<FacilitiesRS, Facility>(FacilitiesRS.class,
            FACILITY_RS_EXTRACTOR);
        //
        private static final Function<FacilityGroupsRS, Collection<FacilityGroup>> FACILITY_GROUP_RS_EXTRACTOR = response -> {
            return response.getFacilityGroups();
        };
        private static final Extractor<FacilityGroupsRS, FacilityGroup> FACILITY_GROUP_EXTRACTOR = new Extractor<FacilityGroupsRS, FacilityGroup>(
            FacilityGroupsRS.class, FACILITY_GROUP_RS_EXTRACTOR);

        //
        private static final Function<FacilityTypologiesRS, Collection<FacilityType>> FACILITY_TYPE_RS_EXTRACTOR = response -> {
            return response.getFacilityTypologies();
        };
        private static final Extractor<FacilityTypologiesRS, FacilityType> FACILITY_TYPE_EXTRACTOR =
            new Extractor<FacilityTypologiesRS, FacilityType>(FacilityTypologiesRS.class, FACILITY_TYPE_RS_EXTRACTOR);

        //
        private static final Function<IssuesRS, Collection<Issue>> ISSUE_RS_EXTRACTOR = response -> {
            return response.getIssues();
        };
        private static final Extractor<IssuesRS, Issue> ISSUE_EXTRACTOR = new Extractor<IssuesRS, Issue>(IssuesRS.class, ISSUE_RS_EXTRACTOR);

        //
        private static final Function<LanguagesRS, Collection<Language>> LANGUAGE_RS_EXTRACTOR = response -> {
            return response.getLanguages();
        };
        private static final Extractor<LanguagesRS, Language> LANGUAGE_EXTRACTOR = new Extractor<LanguagesRS, Language>(LanguagesRS.class,
            LANGUAGE_RS_EXTRACTOR);
        //
        private static final Function<PromotionsRS, Collection<Promotion>> PROMOTION_RS_EXTRACTOR = response -> {
            return response.getPromotions();
        };
        private static final Extractor<PromotionsRS, Promotion> PROMOTION_EXTRACTOR = new Extractor<PromotionsRS, Promotion>(PromotionsRS.class,
            PROMOTION_RS_EXTRACTOR);
        //
        private static final Function<RoomsRS, Collection<Room>> ROOM_RS_EXTRACTOR = response -> {
            return response.getRooms();
        };
        private static final Extractor<RoomsRS, Room> ROOM_EXTRACTOR = new Extractor<RoomsRS, Room>(RoomsRS.class, ROOM_RS_EXTRACTOR);

        //
        private static final Function<SegmentsRS, Collection<Segment>> SEGMENT_RS_EXTRACTOR = response -> {
            return response.getSegments();
        };
        private static final Extractor<SegmentsRS, Segment> SEGMENT_EXTRACTOR = new Extractor<SegmentsRS, Segment>(SegmentsRS.class,
            SEGMENT_RS_EXTRACTOR);
        //
        private static final Function<TerminalsRS, Collection<Terminal>> TERMINAL_RS_EXTRACTOR = response -> {
            return response.getTerminals();
        };
        private static final Extractor<TerminalsRS, Terminal> TERMINAL_EXTRACTOR = new Extractor<TerminalsRS, Terminal>(TerminalsRS.class,
            TERMINAL_RS_EXTRACTOR);
        //
        private static final Function<ImageTypesRS, Collection<ImageType>> IMAGE_TYPE_RS_EXTRACTOR = response -> {
            return response.getImageTypes();
        };
        private static final Extractor<ImageTypesRS, ImageType> IMAGE_TYPE_EXTRACTOR = new Extractor<ImageTypesRS, ImageType>(ImageTypesRS.class,
            IMAGE_TYPE_RS_EXTRACTOR);
        //
        private static final Function<GroupCategoriesRS, Collection<GroupCategory>> GROUP_CATEGORY_RS_EXTRACTOR = response -> {
            return response.getGroupCategories();
        };
        private static final Extractor<GroupCategoriesRS, GroupCategory> GROUP_CATEGORY_EXTRACTOR = new Extractor<GroupCategoriesRS, GroupCategory>(
            GroupCategoriesRS.class, GROUP_CATEGORY_RS_EXTRACTOR);

        //
    }
}
