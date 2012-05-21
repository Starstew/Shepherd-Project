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

import org.ecocean.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

/**
 * @author mmcbride
 */
@Path("/encounters.json")
public class EncountersResource {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
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
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public org.ecocean.data.Encounter postEncounter(org.ecocean.data.Encounter encounter,
                                 @Context HttpServletResponse servletResponse
  ) throws Exception {
    // TODO: spam check
    Encounter newEncounter = new Encounter(encounter.getDate().get(Calendar.DAY_OF_MONTH),
            encounter.getDate().get(Calendar.MONTH),
            encounter.getDate().get(Calendar.YEAR),
            encounter.getDate().get(Calendar.HOUR_OF_DAY),
            encounter.getDate().get(Calendar.MINUTE) + "",
            String.format("%.0f", encounter.getSize()),
            encounter.getLocation(),
            encounter.getSubmitter().name,
            encounter.getSubmitter().email,
            new ArrayList<SinglePhotoVideo>());
    if (encounter.getRelease_date() != null) {
      newEncounter.setReleaseDate(encounter.getRelease_date().getTime());
    }
    if (encounter.getBehavior() != null) {
      newEncounter.setBehavior(encounter.getBehavior());
    }
    if (encounter.getLife_stage() != null) {
      newEncounter.setLifeStage(encounter.getLife_stage());
    }
    newEncounter.setSex(encounter.getSex());
    newEncounter.setLivingStatus(encounter.getLiving_status());
    newEncounter.setGenus(encounter.getGenus());
    newEncounter.setSpecificEpithet(encounter.getSpecies());
    newEncounter.setDistinguishingScar(encounter.getScars());
    if ("feet".equalsIgnoreCase(encounter.getMeasure_units())) {
      newEncounter.setSize(encounter.getSize() / 3.3);
      newEncounter.setDepth(encounter.getDepth() / 3.3);
      newEncounter.setMaximumElevationInMeters(encounter.getElevation() / 3.3);
    } else {
      newEncounter.setSize(encounter.getSize());
      newEncounter.setDepth(encounter.getDepth());
      newEncounter.setMaximumElevationInMeters(encounter.getElevation());
    }
    if (encounter.getLat() != null && encounter.getLongitude() != null) {
      newEncounter.setGPSLatitude(String.format("%.2f&deg; %.2f' %.2f\" %s", encounter.getLat(), encounter.getGps_latitude_minutes(), encounter.getGps_latitude_seconds(), encounter.getLat_direction()));
      double degrees = encounter.getLat();
      degrees += encounter.getGps_latitude_minutes() / 60;
      degrees += encounter.getGps_latitude_seconds() / 3600;
      newEncounter.setDWCDecimalLatitude(degrees);
    }
    if (encounter.getLongitude() != null) {
      newEncounter.setGPSLongitude(String.format("%.2f&deg; %.2f' %.2f\" %s", encounter.getLongitude(), encounter.getGps_longitude_minutes(), encounter.getGps_longitude_seconds(), encounter.getLong_direction()));
      double degrees = encounter.getLongitude();
      degrees += encounter.getGps_longitude_minutes() / 60;
      degrees += encounter.getGps_longitude_seconds() / 3600;
      newEncounter.setDWCDecimalLongitude(degrees);
    }
    if (encounter.getSubmitter() != null) {
      newEncounter.setSubmitterAddress(encounter.getSubmitter().address);
      newEncounter.setSubmitterEmail(encounter.getSubmitter().email);
      newEncounter.setSubmitterName(encounter.getSubmitter().name);
      newEncounter.setSubmitterOrganization(encounter.getSubmitter().organization);
      newEncounter.setSubmitterPhone(encounter.getSubmitter().phone);
      newEncounter.setSubmitterProject(encounter.getSubmitter().project);
    }
    if (encounter.getPhotographer() != null) {
      newEncounter.setPhotographerAddress(encounter.getSubmitter().address);
      newEncounter.setPhotographerEmail(encounter.getSubmitter().email);
      newEncounter.setPhotographerName(encounter.getSubmitter().name);
      newEncounter.setPhotographerPhone(encounter.getSubmitter().phone);
    }
    //TODO: populate submitter id
    newEncounter.setSubmitterID("N/A");
    if (encounter.isInform_others()) {
      newEncounter.setInformOthers("true");
    }

    String guid = UUID.randomUUID().toString();
    newEncounter.setDWCGlobalUniqueIdentifier(guid);

    //populate DarwinCore dates
    DateTimeFormatter fmt = ISODateTimeFormat.date();
    String strOutputDateTime = fmt.print(encounter.getDate().getTime().getTime());
    newEncounter.setDWCDateLastModified(strOutputDateTime);
    newEncounter.setDWCDateAdded(strOutputDateTime);
    Shepherd myShepherd = new Shepherd();
    myShepherd.storeNewEncounter(newEncounter, guid);
    return org.ecocean.data.Encounter.fromEncounter(newEncounter);
  }
}
