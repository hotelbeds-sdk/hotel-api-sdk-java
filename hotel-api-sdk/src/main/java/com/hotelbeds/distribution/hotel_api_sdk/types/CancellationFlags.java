package com.hotelbeds.distribution.hotel_api_sdk.types;

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


/**
 * Copyright (c) Hotelbeds Technology S.L.U. All rights reserved.
 */
public enum CancellationFlags {
    /* CANCELLATION: Used for canceling the booking */
    CANCELLATION("C"),
    /* SIMULATION : Used for a booking cancellation sumulation. */
    SIMULATION("S");

    private String flag;

    CancellationFlags(final String flag) {
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public static CancellationFlags getCancellationFlag(final String value) {
        if (value != null && value.equalsIgnoreCase(CancellationFlags.SIMULATION.name())) {
            return CancellationFlags.SIMULATION;
        } else {
            return CancellationFlags.CANCELLATION;
        }
    }

}
