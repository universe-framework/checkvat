package org.dma.services.vies;

import eu.europa.ec.taxud.vies.services.checkvat.CheckVatPortType;
import eu.europa.ec.taxud.vies.services.checkvat.CheckVatService;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.Holder;

/**
 * http://ec.europa.eu/taxation_customs/vies/faq.html<br>
 * http://i18napis.appspot.com/address/data/PT (replace PT for other country codes)<br>
 * http://stackoverflow.com/questions/578406/what-is-the-ultimate-postal-code-and-zip-regex
 */
public enum Country {

    AT("Austria", "AT-\\d{4}"),
    BE("Belgium", "\\d{4}"),
    BG("Bulgaria", "\\d{4}"),
    CY("Cyprus", "\\d{4}"),
    CZ("Czech Republic", "\\d{3} ?\\d{2}"),
    DE("Germany", "\\d{5}"),
    DK("Denmark", "\\d{4}"),
    EE("Estonia", "\\d{5}"),
    /**
     * NOT ISO 3166
     */
    EL("Greece", "\\d{3} ?\\d{2}"),
    ES("Spain", "\\d{5}"),
    FI("Finland", "\\d{5}"),
    FR("France", "\\d{2} ?\\d{3}"),
    HR("Croatia", "\\d{5}"),
    HU("Hungary", "\\d{4}"),
    IE("Ireland", "[\\dA-Z]{3} ?[\\dA-Z]{4}"),
    IT("Italy", "\\d{5}"),
    LT("Lithuania", "\\d{5}"),
    LU("Luxembourg", "\\d{4}"),
    LV("Latvia", "LV-\\d{4}"),
    MT("Malta", "[A-Z]{3} ?\\d{2,4}"),
    NL("Netherlands", "\\d{4} ?[A-Z]{2}"),
    PL("Poland", "\\d{2}-\\d{3}"),
    PT("Portugal", "\\d{4}-\\d{3}"),
    RO("Romania", "\\d{6}"),
    SE("Sweden", "\\d{3} ?\\d{2}"),
    SI("Slovenia", "\\d{4}"),
    SK("Slovakia", "\\d{3} ?\\d{2}"),
    GB("United Kingdom", "GIR ?0AA|((AB|AL|B|BA|BB|BD|BH|BL|BN|BR|BS|BT|BX|CA|CB|CF|CH|CM|CO|CR|CT|CV|CW|DA|DD|DE|DG|DH|DL|DN|DT|DY|E|EC|EH|EN|EX|FK|FY|G|GL|GY|GU|HA|HD|HG|HP|HR|HS|HU|HX|IG|IM|IP|IV|JE|KA|KT|KW|KY|L|LA|LD|LE|LL|LN|LS|LU|M|ME|MK|ML|N|NE|NG|NN|NP|NR|NW|OL|OX|PA|PE|PH|PL|PO|PR|RG|RH|RM|S|SA|SE|SG|SK|SL|SM|SN|SO|SP|SR|SS|ST|SW|SY|TA|TD|TF|TN|TQ|TR|TS|TW|UB|W|WA|WC|WD|WF|WN|WR|WS|WV|YO|ZE)(\\d[\\dA-Z]? ?\\d[ABD-HJLN-UW-Z]{2}))|BFPO ?\\d{1,4}");

    /**
     * Country name
     */
    public final String name;
    /**
     * ZIP CODE pattern
     */
    public final Pattern zipcode;

    /*
     * Static
     */
    /**
     *
     * @param countryCode A country code
     *
     * @return An Country instance for the required country. Returns NULL if not found
     */
    public static Country getInstance(String countryCode) {
        try {
            return valueOf(countryCode.toUpperCase());
        } catch (Exception e) {
        }
        /* try ISO countries */
        return ISO3166.get(countryCode);
    }

    /*
     * Constructor
     */
    private Country(String name, String regex) {
        this.name = name;
        this.zipcode = regex == null ? null : Pattern.compile(regex);
    }

    /*
     * Methods
     */
    public CheckVatResult query(String vatNumber) {

        try {
            CheckVatService service = new CheckVatService();

//            System.out.println("Please read disclaimer from service provider at:");
//            System.out.println(service.getWSDLDocumentLocation());
//            System.out.println("Querying VAT Information Exchange System (VIES) via web service...");
//            System.out.println("Country: " + this);
//            System.out.println("Vat Number: " + vatNumber);
            Holder<Boolean> valid = new Holder(true);
            Holder<String> company = new Holder(new String());
            Holder<String> address = new Holder(new String());

            CheckVatPortType servicePort = service.getCheckVatPort();
            servicePort.checkVat(
                    new Holder(name()),
                    new Holder(vatNumber),
                    new Holder(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())),
                    valid, company, address);

            if (valid.value) {
                return new CheckVatResult(valid.value, company.value, parse(address.value));

            } else {
                return null;
            }

        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CheckVatAddress parse(String address) {

        try {
            Matcher matcher = zipcode.matcher(address);
            matcher.find();

            String street = address.substring(0, matcher.start());
            String zip = address.substring(matcher.start(), matcher.end());
            String city = address.substring(matcher.end());

            return new CheckVatAddress(street, zip, city);

        } catch (IllegalStateException ex) {
            System.err.println("Country matcher missing match for input: " + address);
        }

        return new CheckVatAddress(address);

    }

    public boolean checkZipcode(String zipcode) {
        return this.zipcode.matcher(zipcode).matches();
    }

    public boolean checkDigit(String vatNumber) {
        switch (this) {
            default:
                return true;
            case PT:
                return CheckDigit.PT(vatNumber);
        }
    }

    /*
     * Aux
     */
    /**
     * https://joinup.ec.europa.eu/asset/core_location/issue/european-use-uk-and-el-cf-iso-3166-codes-gb-and-gr
     */
    private enum ISO3166 {

        GR(EL);

        /* Returns NULL if not found */
        public static Country get(String countryCode) {
            try {
                return valueOf(countryCode.toUpperCase()).country;
            } catch (Exception e) {
            }
            return null;
        }

        public final Country country;

        private ISO3166(Country country) {
            this.country = country;
        }
    }
}
