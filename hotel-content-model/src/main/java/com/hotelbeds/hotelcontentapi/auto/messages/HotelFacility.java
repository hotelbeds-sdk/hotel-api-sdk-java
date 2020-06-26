/**
 * Autogenerated code by SdkModelGenerator.
 * Do not edit. Any modification on this file will be removed automatically after project build
 *
 */
package com.hotelbeds.hotelcontentapi.auto.messages;

/*
 * #%L
 * Hotel Content Model
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


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hotelbeds.hotelcontentapi.auto.convert.json.DateDeserializer;
import com.hotelbeds.hotelcontentapi.auto.convert.json.DateSerializer;
import com.hotelbeds.hotelcontentapi.auto.convert.json.TimeDeserializer;
import com.hotelbeds.hotelcontentapi.auto.convert.json.TimeSerializer;
import com.hotelbeds.hotelcontentapi.auto.messages.Content;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@ToString
@NoArgsConstructor
@Data
public class HotelFacility {

    private Integer facilityCode;
    private Integer facilityGroupCode;
    private Content description;
    private Integer order;
    private Boolean indLogic;
    private Boolean indFee;
    private Boolean indYesOrNo;
    private String facilityName;
    private Integer number;
    private Integer distance;
    private Integer ageFrom;
    private Integer ageTo;
    @JsonProperty
    @JsonSerialize(using = DateSerializer.class)
    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate dateFrom;
    @JsonProperty
    @JsonSerialize(using = DateSerializer.class)
    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate dateTo;
    @JsonProperty
    @JsonSerialize(using = TimeSerializer.class)
    @JsonDeserialize(using = TimeDeserializer.class)
    private LocalTime timeFrom;
    @JsonProperty
    @JsonSerialize(using = TimeSerializer.class)
    @JsonDeserialize(using = TimeDeserializer.class)
    private LocalTime timeTo;
    private BigDecimal amount;
    private String currency;
    private String applicationType;


}
