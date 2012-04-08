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
import org.ecocean.ShepherdPMF;
import org.ecocean.SinglePhotoVideo;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;

/**
 * @author mmcbride
 */
@Path("/photovideos/{id}.json")
public class PhotoVideoResource {
  @GET
  @Produces("application/json")
  public SinglePhotoVideo getSinglePhotoVideo(@PathParam("id") String id) throws Exception {
    PersistenceManager pm = ShepherdPMF.getPMF().getPersistenceManager();
    Extent<SinglePhotoVideo> encClass= pm.getExtent(SinglePhotoVideo.class, true);
    Query photos = pm.newQuery(encClass);
    photos.setFilter("dataCollectionEventID == '" + id + "'");
    Object o = photos.execute();
    if (o instanceof Collection) {
      Collection<SinglePhotoVideo> candidates = ((Collection<SinglePhotoVideo>)o);
      if (candidates.size() > 0) {
        return candidates.iterator().next();
      } else {
        throw new NotFoundException("no photos/videos matching id " + id);
      }
    } else {
      throw new Exception("got a non-photo/video collection from query layer");
    }
  }

}
