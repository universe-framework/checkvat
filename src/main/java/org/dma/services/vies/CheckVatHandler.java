///** *****************************************************************************
// * 2008-2016 Public Domain
// * Contributors
// * Marco Lopes (marcolopes@netc.pt)
// ****************************************************************************** */
//package org.dma.services.vies;
//
///**
// * VAT Information Exchange System
// * <p>
// * http://ec.europa.eu/taxation_customs/vies
// */
//public class CheckVatHandler {
//
//    private final Country country;
//
//    /*
//     * Contructors
//     */
//    /** Country can be VIES or ISO
//     *
//     * @param countryCode A code representing the company registration country
//     */
//    public CheckVatHandler(String countryCode) {
//        this(Country.get(countryCode));
//    }
//
//    public CheckVatHandler(Country country) {
//        if (country == null) {
//            throw new IllegalArgumentException("Cannot instantiate for null country");
//        }
//        this.country = country;
//    }
//
//    /*
//     * Methods
//     */
//    public CheckVatResult query(String vatNumber) {
//        return country.query(vatNumber);
//    }
//}
