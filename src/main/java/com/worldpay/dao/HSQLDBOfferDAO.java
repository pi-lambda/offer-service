package com.worldpay.dao;

import com.worldpay.exception.OfferNotFoundException;
import com.worldpay.exception.ServerErrorException;
import com.worldpay.model.Offer;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HSQLDBOfferDAO implements OfferDAO {

    private static final String ID = "id";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String CURRENCY = "currency";
    private static final String CREATED = "created";
    private static final String VALIDITY_PERIOD = "validity_period";
    private static final String VALID_UNTIL = "valid_until";

    @Override
    public Offer getOfferById(long id) {
        List<Offer> offers = new ArrayList<>();
        Connection connection = HSQLDBConnectionFactory.getConnection();
        Statement offerByIdStatement = null;
        ResultSet offerByIdResultSet = null;
        try {
            connection.setAutoCommit(false);
            offerByIdStatement = connection.createStatement();
            String sql = "SELECT id, description, price, currency, created, validity_period, valid_until " +
                    "FROM Offers WHERE id = " + id + " AND '" + Timestamp.valueOf(LocalDateTime.now())
                    + "' <= valid_until";
            offerByIdResultSet = offerByIdStatement.executeQuery(sql);
            if (offerByIdResultSet.next()) {
                offers.add(new Offer(offerByIdResultSet.getLong(ID),
                        offerByIdResultSet.getString(DESCRIPTION),
                        offerByIdResultSet.getBigDecimal(PRICE),
                        offerByIdResultSet.getString(CURRENCY),
                        offerByIdResultSet.getTimestamp(CREATED).toLocalDateTime(),
                        offerByIdResultSet.getInt(VALIDITY_PERIOD),
                        offerByIdResultSet.getTimestamp(VALID_UNTIL).toLocalDateTime()));
            } else {
                throw new OfferNotFoundException("Offer not found");
            }
            connection.commit();
        } catch (SQLException e) {
            throw new ServerErrorException("Unable to retrieve offer information");
        } finally {
            DbUtils.closeQuietly(offerByIdResultSet);
            DbUtils.closeQuietly(offerByIdStatement);
            DbUtils.closeQuietly(connection);
        }
        return offers.get(0);
    }

    @Override
    public List<Offer> getAllOffers() {
        List<Offer> offers = new ArrayList<>();
        Connection connection = HSQLDBConnectionFactory.getConnection();
        Statement allOffersStatement = null;
        ResultSet allOffersResultSet = null;
        try {
            connection.setAutoCommit(false);
            allOffersStatement = connection.createStatement();
            String sql = "SELECT id, description, price, currency, created, validity_period, valid_until FROM Offers" +
                    " WHERE '" + Timestamp.valueOf(LocalDateTime.now()) + "' <= valid_until";
            allOffersResultSet = allOffersStatement.executeQuery(sql);
            while(allOffersResultSet.next()){
                offers.add(new Offer(allOffersResultSet.getLong(ID),
                        allOffersResultSet.getString(DESCRIPTION),
                        allOffersResultSet.getBigDecimal(PRICE),
                        allOffersResultSet.getString(CURRENCY),
                        allOffersResultSet.getTimestamp(CREATED).toLocalDateTime(),
                        allOffersResultSet.getInt(VALIDITY_PERIOD),
                        allOffersResultSet.getTimestamp(VALID_UNTIL).toLocalDateTime()

                ));
            }
            connection.commit();
        } catch (SQLException e) {
            throw new ServerErrorException("Unable to retrieve offer information");
        } finally {
            DbUtils.closeQuietly(allOffersResultSet);
            DbUtils.closeQuietly(allOffersStatement);
            DbUtils.closeQuietly(connection);
        }
        return offers;
    }

    @Override
    public void create(Offer offer) {
        Connection connection = HSQLDBConnectionFactory.getConnection();
        Statement createOffersStatement = null;
        try {
            connection.setAutoCommit(false);
            createOffersStatement = connection.createStatement();
            LocalDateTime now = LocalDateTime.now();
            String sql = "INSERT INTO Offers (id, description, price, currency, created, validity_period, valid_until)\n" +
                    " VALUES ("+ offer.getId()
                    + ", '"+ offer.getDescription()
                    + "', " + offer.getPrice()
                    + ", '" + offer.getCurrency()
                    + "', '" + Timestamp.valueOf(now)
                    + "', " + offer.getValidityPeriodInDays()
                    + ", '" + Timestamp.valueOf(now.plusDays(offer.getValidityPeriodInDays()))
                    +"'); ";
            createOffersStatement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new ServerErrorException("Unable to create new offer");
        } finally {
            DbUtils.closeQuietly(createOffersStatement);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public void deleteOfferById(long id) {
        Connection connection = HSQLDBConnectionFactory.getConnection();
        Statement deleteOffersStatement = null;
        ResultSet offerByIdResultSet = null;
        try {
            connection.setAutoCommit(false);
            deleteOffersStatement = connection.createStatement();
            String sqlSelect = "SELECT id FROM Offers WHERE id = " + id;
            offerByIdResultSet = deleteOffersStatement.executeQuery(sqlSelect);
            if (offerByIdResultSet.next()) {
                String sqlDelete = "DELETE FROM Offers WHERE id = " + id;
                deleteOffersStatement.executeUpdate(sqlDelete);
            } else {
                throw new OfferNotFoundException("Unable to delete offer with id " + id + ". Offer not found");
            }
            connection.commit();
        } catch (SQLException e) {
            throw new ServerErrorException("Unable to delete offer with id " + id);
        } finally {
            DbUtils.closeQuietly(deleteOffersStatement);
            DbUtils.closeQuietly(connection);
        }
    }
}
