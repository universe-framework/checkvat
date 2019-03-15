package eu.lpinto.universe.api.services;

import eu.lpinto.universe.controllers.exceptions.PreConditionException;
import java.util.List;
import javax.ejb.Asynchronous;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.dma.services.vies.Country;

/**
 *
 * @author Luis Pinto <code>- luis.pinto@petuniversal.com</code>
 */
@Path("checkvat")
public class CeckVatService extends AbstractService {

    @GET
    @Asynchronous
    @Produces(value = MediaType.APPLICATION_JSON)
    public final void find(@Suspended final AsyncResponse asyncResponse,
                           final @Context UriInfo uriInfo,
                           final @HeaderParam(value = "userID") Long userID) throws PreConditionException {
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

        List<String> countries = queryParameters.get("country");
        if (countries == null || countries.isEmpty()) {
            asyncResponse.resume(badRequest("Cannot validate vat number without country"));
            return;
        }
        if (countries.size() > 1) {
            asyncResponse.resume(badRequest("Can define only one country"));
            return;
        }

        String country = countries.get(0);
        if (country == null || country.isEmpty()) {
            asyncResponse.resume(badRequest("Cannot validate vat number for undefined country"));
            return;
        }

        List<String> vats = queryParameters.get("vat");
        if (vats == null || vats.isEmpty()) {
            asyncResponse.resume(badRequest("Cannot validate vat number without vat"));
            return;
        }
        if (vats.size() > 1) {
            asyncResponse.resume(badRequest("Can define only one vat"));
            return;
        }

        String vat = vats.get(0);
        if (vat == null || vat.isEmpty()) {
            asyncResponse.resume(badRequest("Cannot validate vat number for undefined vat"));
            return;
        }

        Country countryService = Country.getInstance(country);
        if (countryService != null) {
            if (countryService.checkDigit(vat)) {
                asyncResponse.resume(ok(countryService.query(vat)));
            } else {
                asyncResponse.resume(badRequest("Invalid Vat Number"));
            }
        } else {
            asyncResponse.resume(badRequest("Unsupported country"));
        }
    }
}
