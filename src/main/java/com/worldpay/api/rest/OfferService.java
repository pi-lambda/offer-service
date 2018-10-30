package com.worldpay.api.rest;

import com.worldpay.dao.HSQLDBOfferDAO;
import com.worldpay.dao.OfferDAO;
import com.worldpay.exception.ServerErrorException;
import com.worldpay.model.Offer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("offers")
public class OfferService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOffers() {
        OfferDAO dao = new HSQLDBOfferDAO();
        List<Offer> offers = dao.getAllOffers();
        return Response.status(Response.Status.OK).entity(offers.toArray(new Offer[0])).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOfferById(@PathParam("id") long id) {
        OfferDAO dao = new HSQLDBOfferDAO();
        Offer offer = dao.getOfferById(id);
        return Response.status(Response.Status.OK).entity(offer).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOffer(Offer offer) {
        OfferDAO dao = new HSQLDBOfferDAO();
        if (!isOfferValid(offer)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            dao.create(offer);
            try {
                URI location = new URI("/worldpay/offers/" + offer.getId());
                return Response.status(Response.Status.CREATED).location(location).build();
            } catch (URISyntaxException e) {
                throw new ServerErrorException("Unable to create new offer");
            }
        }
    }

    private boolean isOfferValid(Offer offer) {
        return offer!= null
                && offer.getDescription() != null
                && offer.getPrice() != null
                && offer.getCurrency() != null
                && offer.getValidityPeriodInDays() >= 0;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteOfferById(@PathParam("id") long id) {
        OfferDAO dao = new HSQLDBOfferDAO();
        dao.deleteOfferById(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
