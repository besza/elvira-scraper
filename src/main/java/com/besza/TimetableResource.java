package com.besza;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RequestScoped
@Path("mav")
@Produces(MediaType.APPLICATION_JSON)
public class TimetableResource {

    @Inject
    DbService dbService;

    @GET
    public List<TimetableBE> getAll() {
        return dbService.findAll();
    }

}
