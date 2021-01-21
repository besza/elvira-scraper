package com.besza;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
public class TimetableResource {

    @Inject
    DbService dbService;

    @Inject
    TimetableMapper mapper;

    @GET
    public List<TimetableDTO> getAll(@QueryParam("from") String origin,
                                    @QueryParam("to") String destination) {
        if (origin != null && destination != null) {
            return mapper.map(dbService.findByOriginAndDestination(origin, destination));
        } else {
            return mapper.map(dbService.findAll());
        }
    }

    @GET
    @Path("/routes")
    public Map<String, Set<String>> getRoutes() {
        Map<String, Set<String>> routes = new HashMap<>();
        dbService.findAll().forEach(
                timetableBE -> routes.computeIfAbsent(timetableBE.origin, key -> new HashSet<>()).add(timetableBE.destination));
        return routes;
    }
}
