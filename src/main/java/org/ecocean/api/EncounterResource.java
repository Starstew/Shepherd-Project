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

import com.sun.jersey.api.NotFoundException;
import org.ecocean.Encounter;
import org.ecocean.ShepherdPMF;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.ws.rs.*;
import java.util.Collection;

/**
 * @author mmcbride
 */
@Path("/encounters/{catalogNumber}.json")
public class EncounterResource {
  @GET
  @Produces("application/json")
  public Encounter getEncounter(@PathParam("catalogNumber") String catalogNumber) throws Exception {
    PersistenceManager pm = ShepherdPMF.getPMF().getPersistenceManager();
    Extent<Encounter> encClass= pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass);
    acceptedEncounters.setFilter("catalogNumber == '" + catalogNumber + "'");
    Object o = acceptedEncounters.execute();
    if (o instanceof Collection) {
      Collection<Encounter> candidates = ((Collection<Encounter>)o);
      if (candidates.size() > 0) {
        return candidates.iterator().next();
      } else {
        throw new NotFoundException("no encounters matching catalog number " + catalogNumber);
      }
    } else {
      throw new Exception("got a non-encounter collection from query layer");
    }
  }
}
