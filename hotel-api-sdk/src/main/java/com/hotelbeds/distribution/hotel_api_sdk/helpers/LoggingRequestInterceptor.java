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


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.hotelbeds.distribution.hotel_api_sdk.types.HotelApiSDKException;
import com.hotelbeds.distribution.hotel_api_sdk.types.HotelbedsError;
import com.hotelbeds.distribution.hotel_api_sdk.types.RequestType;
import com.hotelbeds.hotelapimodel.auto.messages.GenericResponse;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import org.jooq.lambda.Unchecked;

import com.hotelbeds.distribution.hotel_api_sdk.HotelApiClient;
import com.hotelbeds.hotelapimodel.auto.util.AssignUtils;
import com.hotelbeds.hotelapimodel.auto.util.ObjectJoiner;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Data
@Slf4j
/**
 * An OkHttp interceptor that logs information about the requests and responses depending on the log level set.
 *
 * INFO logs just the most basic information about the request and response. DEBUG adds headers information TRACE shows the request, if present, and
 * response bodies, beautifying them as JSON objects if they match.
 *
 * Inspired by: https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/src/main/java/okhttp3/logging/HttpLoggingInterceptor.java
 */
public final class LoggingRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!log.isInfoEnabled()) {
            return chain.proceed(request);
        } else {
            final RequestBody requestBody = request.body();
            final boolean hasRequestBody = requestBody != null;
            final Connection connection = chain.connection();
            final Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
            final StringBuilder requestInformation = new StringBuilder("Request: ");
            requestInformation.append(ObjectJoiner.join(" ", protocol.toString().toUpperCase(), request.method(), request.url()));
            long requestBodySize = -1;
            if (hasRequestBody) {
                requestBodySize = requestBody.contentLength();
                requestInformation.append(", body:");
                requestInformation.append(requestBodySize);
                requestInformation.append(" bytes");
            }
            log.info(requestInformation.toString());

            if (log.isDebugEnabled()) {
                // If the request has a body, sometimes these headers are not present, so let's make them explicit
                if (hasRequestBody) {
                    if (requestBody.contentType() != null) {
                        logHeader(HotelApiClient.CONTENT_TYPE_HEADER, requestBody.contentType().toString());
                    }
                    if (requestBodySize != -1) {
                        logHeader(HotelApiClient.CONTENT_LENGTH_HEADER, Long.toString(requestBodySize));
                    }
                }
                // Log the other headers
                for (String header : request.headers().names()) {
                    if (!HotelApiClient.CONTENT_TYPE_HEADER.equalsIgnoreCase(header)
                        && !HotelApiClient.CONTENT_LENGTH_HEADER.equalsIgnoreCase(header)) {
                        for (String value : request.headers().values(header)) {
                            logHeader(header, value);
                        }
                    }
                }
                if (log.isTraceEnabled() && hasRequestBody) {
                    Supplier<Buffer> requestBufferSupplier = Unchecked.supplier(() -> {
                        Buffer buffer = new Buffer();
                        requestBody.writeTo(buffer);
                        return buffer;
                    });
                    logBody(requestBufferSupplier, requestBody.contentType(), request.headers());
                }
            }
            final long requestStart = System.nanoTime();
            final Response response = chain.proceed(request);
            final long totalRequestTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestStart);

            final ResponseBody responseBody = response.body();
            final long contentLength = responseBody.contentLength();

            log.info("Response: {}", ObjectJoiner.join(" ", response.code(), response.message()));
            if (contentLength >= 0) {
                log.info("  {}: {}", HotelApiClient.CONTENT_LENGTH_HEADER, contentLength);
            }
            log.info("  Request took {} ms", totalRequestTime);

            if (log.isDebugEnabled()) {
                for (String header : response.headers().names()) {
                    for (String value : response.headers().values(header)) {
                        logHeader(header, value);
                    }
                }
                if (log.isTraceEnabled() && HttpHeaders.hasBody(response)) {
                    MediaType contentType = responseBody.contentType();
                    Supplier<Buffer> responseBufferSupplier = Unchecked.supplier(() -> {
                        BufferedSource source = responseBody.source();
                        source.request(Long.MAX_VALUE);
                        return source.buffer().clone();
                    });
                    logBody(responseBufferSupplier, contentType, response.headers());
                }
            }
            return response;
        }
    }

    private void logBody(Supplier<Buffer> bufferSupplier, MediaType contentType, Headers headers) {
        if (bodyEncoded(headers)) {
            log.trace("  Body: encoded, not shown");
        } else {
            Buffer buffer = bufferSupplier.get();
            Charset charset = AssignUtils.UTF8;
            if (contentType != null) {
                try {
                    charset = contentType.charset(AssignUtils.UTF8);
                } catch (UnsupportedCharsetException e) {
                    log.error("  Body: Could not be decoded {}", e.getMessage());
                }
            }
            String body = buffer.readString(charset);
            String bodyContentType = headers.get(HotelApiClient.CONTENT_TYPE_HEADER);
            if (bodyContentType != null && bodyContentType.toLowerCase().startsWith(HotelApiClient.APPLICATION_JSON_HEADER)) {
                log.trace("  JSON Body: {}", writeJSON(body));

            } else if (body != "" && bodyContentType != null && bodyContentType.toLowerCase().startsWith(HotelApiClient.APPLICATION_XML_HEADER)) {
                try {
                    log.trace("  XML Body: {}", prettyPrint(body, true, 2));
                } catch (IOException e) {
                    log.error("  Body: Could not be prettyfied {}", e.getMessage());
                } catch (SAXException e) {
                    log.error("  Body: Could not be prettyfied {}", e.getMessage());
                } catch (ParserConfigurationException e) {
                    log.error("  Body: Could not be prettyfied {}", e.getMessage());
                }
            } else {
                log.trace("  Body: {}", body);
            }
        }
    }

    private void logHeader(final String headerName, final String headerValue) {
        log.debug("  Header: {}: \"{}\"", headerName, headerValue);
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get(HotelApiClient.CONTENT_ENCODING_HEADER);
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private static String prettyPrint(String xml, Boolean ommitXmlDeclaration, Integer indent) throws IOException, SAXException,
        ParserConfigurationException {

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xml)));

        OutputFormat format = new OutputFormat(doc);
        format.setIndenting(true);
        format.setIndent(indent);
        format.setOmitXMLDeclaration(ommitXmlDeclaration);
        format.setLineWidth(Integer.MAX_VALUE);
        Writer outxml = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(outxml, format);
        serializer.serialize(doc);

        return outxml.toString();
    }

    public static String writeJSON(final Object object) {
        ObjectMapper mapper = null;
        String result = null;
        mapper = new ObjectMapper();
        try {
            if (object instanceof String) {
                Object json = mapper.readValue((String) object, Object.class);
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            } else {
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            }
        } catch (final IOException e) {
            log.warn("Body is not a json object {}", e.getMessage());
        }
        return result;
    }

    public static String writeXML(final Object object) {
        String result = null;
        try {

            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            if (object instanceof String) {
                StringReader reader = new StringReader((String) object);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                Object xml = unmarshaller.unmarshal(reader);

                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(xml, stringWriter);
                result = stringWriter.toString();
            } else {
                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(object, stringWriter);
                result = stringWriter.toString();
            }
        } catch (final JAXBException e) {
            log.warn("Body is not a xml object", e);
        }
        return result;
    }

    public static String write(final Object object, RequestType reqType) {
        String result = null;

        if (reqType.equals(RequestType.JSON)) {
            result = writeJSON(object);
        }

        if (reqType.equals(RequestType.XML)) {
            result = writeXML(object);
        }

        return result;
    }

}
