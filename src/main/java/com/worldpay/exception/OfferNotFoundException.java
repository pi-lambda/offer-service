package com.worldpay.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class OfferNotFoundException extends WebApplicationException {

    public OfferNotFoundException() {
        super(Response.status(Response.Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).build());
    }

    public OfferNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
