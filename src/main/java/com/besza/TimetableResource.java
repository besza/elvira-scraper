package com.besza;

import org.jboss.resteasy.annotations.GZIP;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequestScoped
@Path("mav")
@Produces(MediaType.APPLICATION_JSON)
@GZIP
public class TimetableResource {

    @Inject
    DbService dbService;

    @Inject
    TimetableMapper mapper;

    @GET
    public List<TimetableDTO> getAll(@QueryParam("from") String origin,
                                    @QueryParam("to") String destination) {
        if (origin != null && destination != null) {
            return mapper.mapSimple(dbService.findByOriginAndDestination(origin, destination));
        } else {
            throw new BadRequestException("Missing query params: from, to");
        }
    }

    @GET
    @Path("routes")
    public Map<String, Set<String>> getRoutes() {
        Map<String, Set<String>> routes = new HashMap<>();
        dbService.findRecent().forEach(
                timetableBE -> routes.computeIfAbsent(timetableBE.origin, key -> new HashSet<>()).add(timetableBE.destination));
        return routes;
    }
}
