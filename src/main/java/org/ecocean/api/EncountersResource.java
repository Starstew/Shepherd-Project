/*
 * The Shepherd Project - A Mark-Recapture Framework
 * Copyright (C) 2011 Jason Holmberg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.ecocean.api;

import org.ecocean.Encounter;
import org.ecocean.ShepherdPMF;
import org.ecocean.SinglePhotoVideo;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author mmcbride
 */
@Path("/encounters.json")
public class EncountersResource {
  @GET
  @Produces("application/json")
  public Collection<Encounter> getEncounters() throws Exception {
    PersistenceManager pm = ShepherdPMF.getPMF().getPersistenceManager();
    Extent<Encounter> encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass);
    Object o = acceptedEncounters.execute();
    if (o instanceof Collection) {
      return (Collection<Encounter>) o;
    } else {
      throw new Exception("got a non-encounter collection from query layer");
    }
  }

  @POST
  @Produces("application/json")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Encounter postEncounter(@FormParam("day") int day,
                                 @FormParam("month") int month,
                                 @FormParam("year") int year,
                                 @FormParam("hour") int hour,
                                 @FormParam("minutes") String minutes,
                                 @FormParam("size_guess") String size_guess,
                                 @FormParam("location") String location,
                                 @FormParam("submitter_name") String submitterName,
                                 @FormParam("submitter_email") String submitterEmail,
                                 @Context HttpServletResponse servletResponse
  ) throws Exception {
    return new Encounter(day, month, year, hour, minutes, size_guess, location, submitterName, submitterEmail, new ArrayList<SinglePhotoVideo>());
  }
}
