package com.lohika.rtb.dto;

/**
 * Bid DTO which bidders need to return.
 * @author spichkurov
 */
public class Bid  implements Comparable<Bid>{
    private final double bid;
    private final String adUrl;

    public Bid(double bid, String adUrl) {
        this.bid = bid;
        this.adUrl = adUrl;
    }

    public double getBid() {
        return bid;
    }

    public String getAdUrl() {
        return adUrl;
    }


    @Override
    public int compareTo(Bid o) {
        return Double.compare(this.bid, o.bid);
    }

    @Override
    public String toString() {
        return "Bid{" +
                "bid=" + bid +
                ", adUrl='" + adUrl + '\'' +
                '}';
    }
}
