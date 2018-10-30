package com.worldpay.dao;

import com.worldpay.model.Offer;

import java.util.List;

public interface OfferDAO {

    Offer getOfferById(long id);
    List<Offer> getAllOffers();
    void create(Offer offer);
    void deleteOfferById(long id);

}
