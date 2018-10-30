package com.worldpay;

import com.worldpay.dao.HSQLDBConnectionFactory;
import com.worldpay.model.Offer;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class MVPTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() {
        server = Main.startServer();
        HSQLDBConnectionFactory.init();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws SQLException {
        Connection connection = HSQLDBConnectionFactory.getConnection();
        connection.setAutoCommit(false);
        Statement deleteOffersStatement = connection.createStatement();
        String sqlDelete = "DELETE FROM Offers";
        deleteOffersStatement.executeUpdate(sqlDelete);
        connection.commit();
        server.shutdownNow();
    }

    @Test
    public void cannotCreateOfferWithEmptyBody() {
        Response offerCreatedResponse = target.path("offers").request().post(Entity.json("{}"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), offerCreatedResponse.getStatus());
    }

    @Test
    public void cannotCreateOfferWithoutDescription() {
        Response offerCreatedResponse = target.path("offers").request().post(Entity.json("{ \"price\":20, " +
                "\"currency\":\"GBP\", \"validityPeriodInDays\":30 }"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), offerCreatedResponse.getStatus());
    }

    @Test
    public void cannotCreateOfferWithoutPrice() {
        Response offerCreatedResponse = target.path("offers").request().post(Entity.json("{ \"description\":" +
                "\"Shopper friendly description\", \"currency\":\"GBP\", \"validityPeriodInDays\":30 }"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), offerCreatedResponse.getStatus());
    }

    @Test
    public void cannotCreateOfferWithoutCurrency() {
        Response offerCreatedResponse = target.path("offers").request().post(Entity.json("{ \"description\":" +
                "\"Shopper friendly description\", \"price\":20, \"validityPeriodInDays\":30 }"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), offerCreatedResponse.getStatus());
    }

    @Test
    public void cannotCreateOfferWithNegativeValidityPeriod() {
        Response offerCreatedResponse = target.path("offers").request().post(Entity.json("{ \"description\":" +
                "\"Shopper friendly description\", \"price\":20, \"currency\":\"GBP\", \"validityPeriodInDays\":-7 }"));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), offerCreatedResponse.getStatus());
    }

    @Test
    public void canCreateOfferWithDescriptionPriceCurrencyAndValidityPeriod() {
        Response offerCreatedResponse = target.path("offers").request().post(Entity.json("{ \"description\":" +
                "\"Shopper friendly description\", \"price\":20, \"currency\":\"GBP\", \"validityPeriodInDays\":30 }"));
        assertEquals(Response.Status.CREATED.getStatusCode(), offerCreatedResponse.getStatus());
        String offerCreatedId = offerCreatedResponse.getLocation().getPath().split("/")[3];
        Response createdOfferResponse = target.path("offers/" + offerCreatedId).request().get();
        Offer createdOffer = createdOfferResponse.readEntity(Offer.class);
        assertEquals("Shopper friendly description", createdOffer.getDescription());
        assertEquals(new BigDecimal(20), createdOffer.getPrice());
        assertEquals("GBP", createdOffer.getCurrency());
        assertEquals(30, createdOffer.getValidityPeriodInDays());
    }

    @Test
    public void canCreateAndRetrieveMultipleOffers() {
        Response firstOfferCreatedResponse = target.path("offers").request().post(Entity.json("{ \"description\":" +
                "\"First shopper friendly description\", \"price\":20, \"currency\":\"GBP\", \"validityPeriodInDays\":30 }"));
        assertEquals(Response.Status.CREATED.getStatusCode(), firstOfferCreatedResponse.getStatus());
        Response secondOfferCreatedResponse = target.path("offers").request().post(Entity.json("{ \"description\":" +
                "\"Second shopper friendly description\", \"price\":30, \"currency\":\"EUR\", \"validityPeriodInDays\":7 }"));
        assertEquals(Response.Status.CREATED.getStatusCode(), secondOfferCreatedResponse.getStatus());
        Response createdOffersResponse = target.path("offers").request().get();
        Offer[] createdOffers = createdOffersResponse.readEntity(Offer[].class);
        assertEquals("First shopper friendly description", createdOffers[0].getDescription());
        assertEquals(new BigDecimal(20), createdOffers[0].getPrice());
        assertEquals("GBP", createdOffers[0].getCurrency());
        assertEquals(30, createdOffers[0].getValidityPeriodInDays());
        assertEquals("Second shopper friendly description", createdOffers[1].getDescription());
        assertEquals(new BigDecimal(30), createdOffers[1].getPrice());
        assertEquals("EUR", createdOffers[1].getCurrency());
        assertEquals(7, createdOffers[1].getValidityPeriodInDays());
    }

    @Test
    public void offerExpiresAfterTheValidityPeriod() {
        Response offerCreatedResponse = target.path("offers").request().post(Entity.json("{ \"description\":" +
                "\"Shopper friendly description\", \"price\":20, \"currency\":\"GBP\", \"validityPeriodInDays\":0 }"));
        assertEquals(Response.Status.CREATED.getStatusCode(), offerCreatedResponse.getStatus());
        String offerCreatedId = offerCreatedResponse.getLocation().getPath().split("/")[3];
        Response expiredOfferResponse = target.path("offers/" + offerCreatedId).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), expiredOfferResponse.getStatus());
    }

    @Test
    public void canCancelOfferIfExists() {
        Response offerCreatedResponse = target.path("offers").request().post(Entity.json("{ \"description\":" +
                "\"Shopper friendly description\", \"price\":20, \"currency\":\"GBP\", \"validityPeriodInDays\":30 }"));
        assertEquals(Response.Status.CREATED.getStatusCode(), offerCreatedResponse.getStatus());
        String offerCreatedId = offerCreatedResponse.getLocation().getPath().split("/")[3];
        Response deletedOfferResponse = target.path("offers/" + offerCreatedId).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletedOfferResponse.getStatus());
    }

    @Test
    public void cannotCancelOfferIfDoesNotExist() {
        Response offerCreatedResponse = target.path("offers").request().post(Entity.json("{ \"description\":" +
                "\"Shopper friendly description\", \"price\":20, \"currency\":\"GBP\", \"validityPeriodInDays\":30 }"));
        assertEquals(Response.Status.CREATED.getStatusCode(), offerCreatedResponse.getStatus());
        Response deletedOfferResponse = target.path("offers/2000").request().delete();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), deletedOfferResponse.getStatus());
    }

}
