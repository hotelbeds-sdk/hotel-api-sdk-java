package com.hotelbeds.distribution.hotel_api_sdk;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;

import com.hotelbeds.distribution.hotel_api_sdk.types.ContentType;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiSDKException;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelbedsError;
import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentRequest;
import com.hotelbeds.hotelcontentapi.auto.messages.AbstractGenericContentResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class ContentElementSpliterator<T> extends AbstractSpliterator<T> {

    private static final long PAGE_SIZE = 999L;
    private final HotelApiClient hotelApiClient;
    private final ContentType contentType;
    private long currentElement = 1;
    private long currentPageElement = 1;
    private long size;
    private final AbstractGenericContentRequest abstractGenericContentRequest;
    private final Queue<T> elements = new LinkedList<>();

    public ContentElementSpliterator(final HotelApiClient hotelApiClient, final ContentType contentType,
        final AbstractGenericContentRequest abstractGenericContentRequest, final long currentElement, final long size) {
        super(size, ORDERED | DISTINCT | SIZED | NONNULL | IMMUTABLE);
        this.hotelApiClient = hotelApiClient;
        this.contentType = contentType;
        this.abstractGenericContentRequest = abstractGenericContentRequest;
        this.currentElement = currentElement;
        this.currentPageElement = currentElement;
        this.size = size;
    }

    public ContentElementSpliterator(final HotelApiClient hotelApiClient, final ContentType contentType,
        final AbstractGenericContentRequest abstractGenericContentRequest) {
        this(hotelApiClient, contentType, abstractGenericContentRequest, 1, Long.MAX_VALUE);
        size = getElementBlock(currentElement);
    }

    @SuppressWarnings("unchecked")
    private long getElementBlock(final long from) {
        long total = 0;
        try {
            final Map<String, String> params = new HashMap<>();
            contentType.addCommonParameters(abstractGenericContentRequest, params);
            params.put("from", Long.toString(from));
            final long to = from + PAGE_SIZE;
            params.put("to", Long.toString(to));
            if (log.isDebugEnabled()) {
                log.debug("Retrieving {} to {} of type {}...", new Object[] {
                    from, to, contentType});
            }
            AbstractGenericContentResponse response = hotelApiClient.callRemoteContentAPI(abstractGenericContentRequest, params, contentType);
            if (contentType.getResultsFunction() != null) {
                elements.addAll((Collection<? extends T>) contentType.getResultsFunction().apply(response));
                total = response.getTotal();
                currentPageElement = to;
            } else {
                throw new HotelApiSDKException(new HotelbedsError("SDK Configuration error",
                    "Extracting results from a type that has no extractor configured: " + contentType.name()));
            }
        } catch (Exception e) {
            log.error("Error retrieving list of elements", e);
        }
        return total;
    }


    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (!elements.isEmpty()) {
            action.accept(elements.poll());
            currentElement++;
            return true;
        } else if (currentElement < size) {
            long newTotal = getElementBlock(currentPageElement + 1);
            if (!elements.isEmpty()) {
                action.accept(elements.poll());
                currentElement++;
                return true;
            } else {
                size = newTotal;
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public long estimateSize() {
        return this.size;
    }
}
