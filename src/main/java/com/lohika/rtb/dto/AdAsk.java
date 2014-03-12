package com.lohika.rtb.dto;

/**
 * Request data holder for ask operation.
 * @author spichkurov
 */
public class AdAsk {

    private final String adId;

    public AdAsk(String adId) {
        this.adId = adId;
    }

    public String getAdId() {
        return adId;
    }


    @Override
    public String toString() {
        return "AdAsk{" +
                "adId='" + adId + '\'' +
                '}';
    }
}
