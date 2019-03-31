package org.dma.services.vies;

/**
 * http://zylla.wipos.p.lodz.pl/ut/translation.html
 */
public class CheckDigit {

    static private String[] ES_CODE = {"T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E"};

    public static boolean PT(String vatNumber) {
        final int max = 9;
        //check if is numeric and has 9 numbers
        if (vatNumber == null || !vatNumber.matches("\\d{9}")) {
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

    /*
     * https://comunidadhorizontal.com/utiles/validar-cif-nif-nie/
     * http://www.interior.gob.es/web/servicios-al-ciudadano/dni/calculo-del-digito-de-control-del-nif-nie
     * https://generadordni.es/#dni
     */
    public static boolean ES(String vatNumber) {
        if (vatNumber == null || vatNumber.isEmpty() || !vatNumber.matches("(\\d|[xXyYzZ])\\d+{7}[abcdefghjklmnpqrstvwxyzABCDEFGHJKLMNPQRSTVWXYZ]")) {
            return false;
        }

        if (vatNumber.matches("[xXyYzZ]\\d+{7}[abcdefghjklmnpqrstABCDEFGHJKLMNPQRST]")) {
            vatNumber = vatNumber.replaceFirst("x|X", "0");
            vatNumber = vatNumber.replaceFirst("y|Y", "1");
            vatNumber = vatNumber.replaceFirst("z|Z", "2");
        }

        Integer number = Integer.valueOf(vatNumber.substring(0, vatNumber.length() - 1));
        Integer mod = number % 23;
        return ES_CODE[mod].equalsIgnoreCase("" + vatNumber.charAt(vatNumber.length() - 1));
    }
}
