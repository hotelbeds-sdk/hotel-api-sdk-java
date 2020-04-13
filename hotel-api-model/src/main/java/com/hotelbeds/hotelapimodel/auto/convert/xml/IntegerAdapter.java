package com.hotelbeds.hotelapimodel.auto.convert.xml;

/*
 * #%L
 * HotelAPI Model
 * %%
 * Copyright (C) 2015 - 2018 HOTELBEDS TECHNOLOGY, S.L.U.
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


import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;



/**
 * The Class IntegerAdapter.
 */
public class IntegerAdapter extends XmlAdapter<String, Integer> {

    @Override
    public Integer unmarshal(final String integer) throws Exception {
        Integer result = null;
        if (StringUtils.isNotBlank(integer)) {
            result = Integer.valueOf(integer);
        }
        return result;
    }

    @Override
    public String marshal(final Integer integer) throws Exception {
        String result = null;
        if (integer != null) {
            result = integer.toString();
        }
        return result;
    }

}
