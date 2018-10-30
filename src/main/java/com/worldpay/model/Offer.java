package com.worldpay.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@XmlRootElement
public class Offer {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1000);

    @XmlElement
    private final long id;
    @XmlElement
    private String description;
    @XmlElement
    private BigDecimal price;
    @XmlElement
    private String currency;
    @XmlElement
    private LocalDateTime created;
    @XmlElement
    private int validityPeriodInDays;
    @XmlElement
    private LocalDateTime validUntil;

    public Offer() {
        this.id = ID_GENERATOR.getAndIncrement();
    }

    public Offer(long id, String description, BigDecimal price, String currency, LocalDateTime created,
                 int validityPeriodInDays, LocalDateTime validUntil) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.created = created;
        this.validityPeriodInDays = validityPeriodInDays;
        this.validUntil = validUntil;
    }

    public static AtomicLong getIdGenerator() {
        return ID_GENERATOR;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public int getValidityPeriodInDays() {
        return validityPeriodInDays;
    }

    public void setValidityPeriodInDays(int validityPeriodInDays) {
        this.validityPeriodInDays = validityPeriodInDays;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offer offer = (Offer) o;
        return id == offer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
