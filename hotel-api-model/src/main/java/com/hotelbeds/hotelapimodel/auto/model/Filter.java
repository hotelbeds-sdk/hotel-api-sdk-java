/**
 * Autogenerated code by SdkModelGenerator.
 * Do not edit. Any modification on this file will be removed automatically after project build
 *
 */
package com.hotelbeds.hotelapimodel.auto.model;

/*
 * #%L
 * HotelAPI Model
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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.HotelPackage;
import com.hotelbeds.hotelapimodel.auto.common.SimpleTypes.ShowDirectPayment;
import com.hotelbeds.hotelapimodel.auto.convert.json.RateSerializer;
import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;

@JsonInclude(Include.NON_NULL)
@ToString
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Filter {

    @Min(value = 1)
    private Integer maxHotels;
    @Min(value = 1)
    private Integer maxRooms;
    @Min(value = 0)
    @JsonSerialize(using = RateSerializer.class)
    private BigDecimal minRate;
    @Min(value = 0)
    @JsonSerialize(using = RateSerializer.class)
    private BigDecimal maxRate;
    @Min(value = 1)
    private Integer maxRatesPerRoom;
    private Boolean packaging;
    @Valid
    private ShowDirectPayment paymentType;
    @Valid
    private HotelPackage hotelPackage;
    @Min(value = 1)
    private Integer minCategory;
    @Min(value = 1)
    private Integer maxCategory;


}
