package org.dma.services.vies;

/**
 * http://zylla.wipos.p.lodz.pl/ut/translation.html
 */
public class CheckDigit {

    public static boolean PT(String vatNumber) {
        final int max = 9;
        //check if is numeric and has 9 numbers
        if (!vatNumber.matches("[0-9]+") || vatNumber.length() != max) {
            return false;
        }
        int checkSum = 0;
        //calculate checkSum
        for (int i = 0; i < max - 1; i++) {
            checkSum += (vatNumber.charAt(i) - '0') * (max - i);
        }
        int checkDigit = 11 - (checkSum % 11);
        //if checkDigit is higher than TEN set it to zero
        if (checkDigit >= 10) {
            checkDigit = 0;
        }
        //compare checkDigit with the last number of NIF
        return checkDigit == vatNumber.charAt(max - 1) - '0';
    }

}
