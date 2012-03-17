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

package org.ecocean;

import org.ecocean.grid.ScanTask;
import org.ecocean.grid.ScanWorkItem;
import org.ecocean.servlet.ServletUtilities;
import org.ecocean.genetics.*;

import javax.jdo.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <code>Shepherd</code> is the main	information	retrieval, processing, and persistence class in the Shepherd Project.
 * The <code>Shepherd</code>	class interacts directly with the database and	all	persistent objects stored within it, using the Java Data Objects (JDO) implementation from DataNucleus.org.
 * Any application seeking access to	mark-recapture data must invoke an	instance of	Shepherd first and use it to retrieve any data stored in the
 * database.
 * <p/>
 * While	a <code>Shepherd</code>	object is easily invoked with a	single constructor,	no data
 * can be retrieved until a new Transaction has been	started. Changes made using the Transaction must be committed (store changes) or rolled back (ignore changes).
 * Example:
 * <code>
 *<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
 *<%@ page contentType="text/html; charset=utf-8" language="java" import="org.ecocean.*,javax.jdo.*"%>
 *<html>
 *<head>
 *<title>My Sample JSP File Using a Shepherd Object</title>
 *</head>
 *<body>
 *<%
 *Shepherd myShepherd=new Shepherd();
 *%>
 *
 *<p>The number of encounters in the database is: <%=myShepherd.getNumEncounters()%></p>
 *<p>The number of marked individuals in the database is: <%=myShepherd.getNumMarkedIndividuals()%></p>
 *<%
 *myShepherd.rollbackDBTransaction();
 *myShepherd.closeDBTransaction();
 *%>
 *</body>
 *</html>
 *</code>
 * @author Jason Holmberg
 * @see MarkedIndividual, Encounter, Keyword
 */
public class Shepherd {

  /*
   * The JDO persistence manager used under the covers in this class.
   */
  private PersistenceManager pm;

  /*
   * The PersistenceMangerFactory used to generate a PersistenceManger instance for this Shepherd.
   */
  private PersistenceManagerFactory pmf;

  public static Vector matches = new Vector();

  /**
   * Constructor to create a new shepherd thread object
   */
  public Shepherd() {
    if (pm == null || pm.isClosed()) {
      pmf = ShepherdPMF.getPMF();
      try {
        pm = pmf.getPersistenceManager();
      } catch (JDOUserException e) {
        System.out.println("Hit an excpetion while trying to instantiate a PM. Not fatal I think.");
        e.printStackTrace();
      }
    }
  }

/*
 * Returns the JDO persistence manager used by Shepherd under the covers.
 * 
 */
  public PersistenceManager getPM() {
    return pm;
  }

  /*
   * Returns the JDO factory class that creates persistence managers.
   * 
   */
  public PersistenceManagerFactory getPMF() {
    return pmf;
  }


  /**
   * Stores a new, unassigned Encounter in the database for later retrieval and analysis.
   * Each new encounter is assigned a unique number which is also its unique retrievable ID in the database.
   * @param enc The new encounter to be added to the database.
   * @return An identifier that represents the unique number of this new encounter in the database.
   * @ see Encounter
   */
  public String storeNewEncounter(Encounter enc, String uniqueID) {
    enc.setEncounterNumber(uniqueID);
    beginDBTransaction();
    try {
      pm.makePersistent(enc);
      commitDBTransaction();
    } catch (Exception e) {
      rollbackDBTransaction();
      System.out.println("I failed to create a new encounter in Shepherd.storeNewEncounter().");
      System.out.println("     uniqueID:" + uniqueID);
      e.printStackTrace();
      return "fail";
    }
    return (uniqueID);
  }
  
  public boolean storeNewMarkedIndividual(MarkedIndividual indie) {
    
    beginDBTransaction();
    try {
      pm.makePersistent(indie);
      commitDBTransaction();
    } catch (Exception e) {
      rollbackDBTransaction();
      System.out.println("I failed to create a new MarkedIndividual in Shepherd.storeNewMarkedIndividual().");
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Persists a new adoption in the database.
   * @param ad The adoption to persist.
   * @param uniqueID The unique identifier (e.g., primary key for an RDBMS) to persist the adoption under.
   * @return A string of "fail on a failed persist" and the unique identifier on success.
   */
  public String storeNewAdoption(Adoption ad, String uniqueID) {
    beginDBTransaction();
    try {
      pm.makePersistent(ad);
      commitDBTransaction();
    } catch (Exception e) {
      rollbackDBTransaction();
      System.out.println("I failed to create a new adoption in shepherd.storeNewAdoption().");
      System.out.println("     uniqueID:" + uniqueID);
      e.printStackTrace();
      return "fail";
    }
    return (uniqueID);
  }

  /*
   * Persists a new Keyword in the database.
   */
  public String storeNewKeyword(Keyword kw) {
    beginDBTransaction();
    try {

      pm.makePersistent(kw);
      commitDBTransaction();
    } catch (Exception e) {
      rollbackDBTransaction();
      return "fail";
    }
    return "success";
  }

/*
 * Persists a spot pattern recognition ScanTask in the database.
 * 
 */
  public boolean storeNewTask(ScanTask task) {
    //beginDBTransaction();
    try {
      pm.makePersistent(task);
      return true;
    } catch (Exception e) {
      System.out.println("I failed to store the new task number: " + task.getUniqueNumber());
      return false;
    }

  }


  /**
   * Removes an encounter from the database. ALL DATA FOR THE ENCOUNTER WILL BE LOST!!!
   *
   * @param enc the encounter to delete the database
   * @see Encounter
   */
  public void throwAwayEncounter(Encounter enc) {
    String number = enc.getEncounterNumber();
    pm.deletePersistent(enc);
  }

  /*
   * Removes a TissueSample object from the database. It's data is permanently lost. 
   */
  public void throwAwayTissueSample(TissueSample genSample) {
    pm.deletePersistent(genSample);
  }
  
  /*
   * Removes an object of base class Genetic Analysis from the database. It's data is permanently lost. 
   */  
  public void throwAwayGeneticAnalysis(GeneticAnalysis analysis) {
    //String removedParameters = analysis.getHTMLString();
    pm.deletePersistent(analysis);
    //return removedParameters;
  }

  /*
   * Removes a MicrosatelliteMarkersAnalysis object from the database. It's data is permanently lost. 
   */
  public void throwAwayMicrosatelliteMarkersAnalysis(MicrosatelliteMarkersAnalysis analysis) {
    pm.deletePersistent(analysis);
  }

  /*
   * Removes an Adoption object from the database. It's data is permanently lost. 
   */  
  public void throwAwayAdoption(Adoption ad) {
    String number = ad.getID();
    pm.deletePersistent(ad);
  }

  /*
   * Removes a Keyword object from the database. It's data is permanently lost. 
   */
  public void throwAwayKeyword(Keyword word) {
    String indexname = word.getIndexname();
    pm.deletePersistent(word);
  }

  /*
   * Removes an array of spot patterns that were previously assigned to an Encounter. 
   */
  public void throwAwaySuperSpotArray(SuperSpot[] spots) {
    if (spots != null) {
      for (int i = 0; i < spots.length; i++) {
        pm.deletePersistent(spots[i]);
      }
    }
  }

  /**
   * Removes a marked individual from the database.
   * ALL DATA FOR THE INDIVIDUAL WILL BE LOST!!!
   *
   * @param MarkedIndividual to delete from the database
   * @see MarkedIndividual
   */
  public void throwAwayMarkedIndividual(MarkedIndividual bye_bye_sharky) {
    //String name=bye_bye_sharky.getName();
    pm.deletePersistent(bye_bye_sharky);
  }

  /*
   * Removes a ScanTask object from the database. It's data is permanently lost. 
   */
  public void throwAwayTask(ScanTask sTask) {
    String name = sTask.getUniqueNumber();

    //throw away the task
    pm.deletePersistent(sTask);
    pmf.getDataStoreCache().unpin(sTask);
    pmf.getDataStoreCache().evict(sTask);

  }

/**
 * Returns an Encounter object identified by its catalogNumber (of the primary key in the database).
 * @param num Unique identifier (Encounter.catalogNumber) of the encounter.
 * @return null if nothing matching this string was found. Return the encounter if it can be found.
 */
  public Encounter getEncounter(String num) {
    Encounter tempEnc = null;
    try {
      tempEnc = ((Encounter) (pm.getObjectById(pm.newObjectIdInstance(Encounter.class, num.trim()), true)));
    } catch (Exception nsoe) {
      return null;
    }
    return tempEnc;
  }

  /**
   * Returns a SinglePhotoVideo object from the database.
   * @param num The primary key/unique number of the object.
   * @return The object or null if it does not exist in the database.
   */
  public SinglePhotoVideo getSinglePhotoVideo(String num) {
    SinglePhotoVideo tempEnc = null;
    try {
      tempEnc = ((SinglePhotoVideo) (pm.getObjectById(pm.newObjectIdInstance(SinglePhotoVideo.class, num.trim()), true)));
    } catch (Exception nsoe) {
      return null;
    }
    return tempEnc;
  }

  /**
   * Returns a TissueSample object stored for an encounter.
   * @param sampleID The sample identifier for the tissue sample.
   * @param encounterNumber The encounter number under which the sample is stored.
   * @return The tissue sample or null if no matching object was found.
   */
  public TissueSample getTissueSample(String sampleID, String encounterNumber) {
    TissueSample tempEnc = null;
    try {
      String filter = "this.sampleID == \""+sampleID+"\" && this.correspondingEncounterNumber == \""+encounterNumber+"\"";
      Extent encClass = pm.getExtent(TissueSample.class, true);
      Query acceptedEncounters = pm.newQuery(encClass, filter);
      Collection c = (Collection) (acceptedEncounters.execute());
      Iterator it = c.iterator();
      while(it.hasNext()){
        return ((TissueSample)it.next());
      }
    }
    catch (Exception nsoe) {
      nsoe.printStackTrace();
      return null;
    }
    return null;
  }

  /**
   * Returns a haplotype analysis identified by its encounter number, tissue sample number, and analysis number.
   * @param sampleID The sample ID for the tissue sample on which this analysis was performed.
   * @param encounterNumber The Encounter.catalogNumber under which the tissue sample is stored.
   * @param analysisIDThe identifier of this haplotype analysis.
   * @return The analysis object or null if no match was found in the database.
   */
  public MitochondrialDNAAnalysis getMitochondrialDNAAnalysis(String sampleID, String encounterNumber, String analysisID) {
    try {
      MitochondrialDNAAnalysis mtDNA = (MitochondrialDNAAnalysis)getGeneticAnalysis(sampleID, encounterNumber, analysisID, "MitochondrialDNA");
      return mtDNA;
    }
    catch (Exception nsoe) {
      nsoe.printStackTrace();
      return null;
    }
  }

  /**
   * Returns the designated genetic sex analysis.
   * @param sampleID The sample ID for the tissue sample on which this analysis was performed.
   * @param encounterNumber The Encounter.catalogNumber under which the tissue sample is stored.
   * @param analysisIDThe identifier of this genetic sex analysis.
   * @return The analysis object or null if no match was found in the database.
   */
  public SexAnalysis getSexAnalysis(String sampleID, String encounterNumber, String analysisID) {
    try {
      SexAnalysis mtDNA = (SexAnalysis)getGeneticAnalysis(sampleID, encounterNumber, analysisID, "SexAnalysis");
      return mtDNA;
    }
    catch (Exception nsoe) {
      nsoe.printStackTrace();
      return null;
    }
  }

  /**
   * Returns the designated microsatellite marker analysis.
   * @param sampleID The sample ID for the tissue sample on which this analysis was performed.
   * @param encounterNumber The Encounter.catalogNumber under which the tissue sample is stored.
   * @param analysisID The identifier of this microsatellite marker analysis.
   * @return The analysis object or null if no match was found in the database.
   */
  public MicrosatelliteMarkersAnalysis getMicrosatelliteMarkersAnalysis(String sampleID, String encounterNumber, String analysisID) {
    try {
      MicrosatelliteMarkersAnalysis msDNA = (MicrosatelliteMarkersAnalysis)getGeneticAnalysis(sampleID, encounterNumber, analysisID, "MicrosatelliteMarkers");
      return msDNA;
    }
    catch (Exception nsoe) {
      nsoe.printStackTrace();
      return null;
    }
  }




/**
 * Returns the specified adoption object.
 * @param num The unique identifier of the Adoption.
 * @return The object or null if no match was found in the database. 
 */
  public Adoption getAdoption(String num) {
    Adoption tempEnc = null;
    try {
      tempEnc = ((Adoption) (pm.getObjectById(pm.newObjectIdInstance(Adoption.class, num.trim()), true)));
    } catch (Exception nsoe) {
      return null;
    }
    return tempEnc;
  }

  /**
   * Returns the specified DataCollectionEvent object.
   * @param clazz Class of the object.
   * @param num Unique identifier of the object.
   * @return The object or null if no match was found in the database.
   */
  public <T extends DataCollectionEvent> T findDataCollectionEvent(Class<T> clazz, String num) {
    T dataCollectionEvent = null;
    try {
      dataCollectionEvent = (T) pm.getObjectById((pm.newObjectIdInstance(clazz, num.trim())), true);
    } catch (Exception e) {
    }
    return dataCollectionEvent;
  }


  /**
   * Returns a detached, fully populated copy of an Encounter object.
   * @param num The unique number of the object to copy.
   * @return The object or null if it could not be found or copied without error.
   */
  public Encounter getEncounterDeepCopy(String num) {
    if (isEncounter(num)) {
      Encounter tempEnc = getEncounter(num.trim());
      Encounter transmitEnc = null;
      try {
        transmitEnc = (Encounter) pm.detachCopy(tempEnc);
      } catch (Exception e) {
      }
      return transmitEnc;
    } else {
      return null;
    }
  }
  
  /**
   * Returns a detached, fully populated copy of an Adoption object.
   * @param num The unique number of the object to copy.
   * @return The object or null if it could not be found or copied without error.
   */
  public Adoption getAdoptionDeepCopy(String num) {
    if (isAdoption(num)) {
      Adoption tempEnc = getAdoption(num.trim());
      Adoption transmitEnc = null;
      try {
        transmitEnc = (Adoption) pm.detachCopy(tempEnc);
      } catch (Exception e) {
      }
      return transmitEnc;
    } else {
      return null;
    }
  }

  /**
   * Returns a detached, fully populated copy of a Keyword object.
   * @param num The unique number of the object to copy.
   * @return The object or null if it could not be found or copied without error.
   */
  public Keyword getKeywordDeepCopy(String name) {
    if (isKeyword(name)) {
      Keyword tempWord = ((Keyword) (getKeyword(name.trim())));
      Keyword transmitWord = null;
      try {
        transmitWord = (Keyword) (pm.detachCopy(tempWord));
      } catch (Exception e) {
      }
      return transmitWord;
    } else {
      return null;
    }
  }

/**
 * Returns a persisted spot pattern recognition ScanTask object.
 * @param uniqueID The unique identifier of the task.
 * @return The object or null if not found in the database (or if an error occurred).
 */
  public ScanTask getScanTask(String uniqueID) {
    ScanTask tempTask = null;
    try {
      tempTask = ((ScanTask) (pm.getObjectById(pm.newObjectIdInstance(ScanTask.class, uniqueID.trim()), true)));
    } catch (Exception nsoe) {
      return null;
    }
    return tempTask;
  }

  /**
   * Returns a keyword either by matching its readableName or indexName attributes.
   * @param readableName The Keyword.readableName or Keyword.indexName attribute for the Keyword to match and return.
   * @return The object or null if not found in the database.
   */
  public Keyword getKeyword(String readableName) {

    Iterator keywords = getAllKeywords();
	while (keywords.hasNext()) {
      Keyword kw = (Keyword) keywords.next();
      if((kw.getReadableName().equals(readableName))||(kw.getIndexname().equals(readableName))){return kw;}
  	}
  return null;

  }

  /**
   * Returns a list of Keyword objects that two Encounter objects have in common.
   * @param encounterNumber1 The Encounter.catalogNumber attribute to match for the first Encounter object to compare.
   * @param encounterNumber2 The Encounter.catalogNumber attribute to match for the first Encounter object to compare.
   * @return An ArrayList of the Keyword.readableName attributes for Keywords that both Encounter object have in common.
   */
  public ArrayList<String> getKeywordsInCommon(String encounterNumber1, String encounterNumber2) {
    ArrayList<String> inCommon = new ArrayList<String>();
    Encounter enc1 = getEncounter(encounterNumber1);
    Encounter enc2 = getEncounter(encounterNumber2);

    Iterator keywords = getAllKeywords();
    while (keywords.hasNext()) {
      Keyword kw = (Keyword) keywords.next();
      if (enc1.hasKeyword(kw) && enc2.hasKeyword(kw)) {
        inCommon.add(kw.getReadableName());
      }


    }
    return inCommon;
  }

/**
 * Returns true if the designated String matches the Encounter.catalogNumber attribute of an Encounter object in the database.
 * @param num Encounter.catalogNumber attribute to match.
 * @return True if the object exists in the database. Otherwise, false.
 */
  public boolean isEncounter(String num) {
    try {
      Encounter tempEnc = ((org.ecocean.Encounter) (pm.getObjectById(pm.newObjectIdInstance(Encounter.class, num.trim()), true)));
    } catch (Exception nsoe) {
      nsoe.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Returns true if a matching TissueSample object can be found in the database.
   * @param num Encounter.catalogNumber attribute to match.
   * @param sampleID Sample identifier to match.
   * @return True if the object exists in the database. Otherwise, false.
   */
  public boolean isTissueSample(String sampleID, String encounterNumber) {
    TissueSample tempEnc = null;
    try {
      String filter = "this.sampleID == \""+sampleID+"\" && this.correspondingEncounterNumber == \""+encounterNumber+"\"";
      Extent encClass = pm.getExtent(TissueSample.class, true);
      Query acceptedEncounters = pm.newQuery(encClass, filter);
      Collection c = (Collection) (acceptedEncounters.execute());
      Iterator it = c.iterator();
      while(it.hasNext()){
        return true;
      }
    }
    catch (Exception nsoe) {
      nsoe.printStackTrace();
      return false;
    }
    return false;
  }

  /**
   * Returns true if a matching GeneticAnalysis object can be found in the database.
   * @param num Encounter.catalogNumber attribute to match.
   * @param sampleID Sample identifier to match.
   * @param analysisID The identifier of the analysis.
   * @return True if the object exists in the database. Otherwise, false.
   */
  public boolean isGeneticAnalysis(String sampleID, String encounterNumber, String analysisID, String type) {
    TissueSample tempEnc = null;
    try {
      String filter = "this.analysisType == \""+type+"\" && this.analysisID == \""+analysisID+"\" && this.sampleID == \""+sampleID+"\" && this.correspondingEncounterNumber == \""+encounterNumber+"\"";
      Extent encClass = pm.getExtent(GeneticAnalysis.class, true);
      Query acceptedEncounters = pm.newQuery(encClass, filter);
      Collection c = (Collection) (acceptedEncounters.execute());
      Iterator it = c.iterator();
      while(it.hasNext()){
        return true;
      }
    }
    catch (Exception nsoe) {
      nsoe.printStackTrace();
      return false;
    }
    return false;
  }

  /**
   * Returns the designated, uncast GeneticAnalysis object from the database.
   * @param sampleID The sample ID for the tissue sample on which this analysis was performed.
   * @param encounterNumber The Encounter.catalogNumber under which the tissue sample is stored.
   * @param analysisID The identifier of this genetic analysis.
   * @return The analysis object or null if no match was found in the database.
   */
  public GeneticAnalysis getGeneticAnalysis(String sampleID, String encounterNumber, String analysisID) {
    try {
      String filter = "this.analysisID == \""+analysisID+"\" && this.sampleID == \""+sampleID+"\" && this.correspondingEncounterNumber == \""+encounterNumber+"\"";
      Extent encClass = pm.getExtent(GeneticAnalysis.class, true);
      Query acceptedEncounters = pm.newQuery(encClass, filter);
      Collection c = (Collection) (acceptedEncounters.execute());
      Iterator it = c.iterator();
      while(it.hasNext()){
        return (GeneticAnalysis)it.next();
      }
    }
    catch (Exception nsoe) {
      nsoe.printStackTrace();
      return null;
    }
    return null;
  }
  
  /**
   * Returns the designated, uncast GeneticAnalysis object from the database.
   * @param sampleID The sample ID for the tissue sample on which this analysis was performed.
   * @param encounterNumber The Encounter.catalogNumber under which the tissue sample is stored.
   * @param analysisID The identifier of this genetic analysis.
   * @param type The type of the GeneticAnalysis to match.
   * @return The analysis object or null if no match was found in the database.
   */
  public GeneticAnalysis getGeneticAnalysis(String sampleID, String encounterNumber, String analysisID, String type) {
    try {
      String filter = "this.analysisType == \""+type+"\" && this.analysisID == \""+analysisID+"\" && this.sampleID == \""+sampleID+"\" && this.correspondingEncounterNumber == \""+encounterNumber+"\"";
      Extent encClass = pm.getExtent(GeneticAnalysis.class, true);
      Query acceptedEncounters = pm.newQuery(encClass, filter);
      Collection c = (Collection) (acceptedEncounters.execute());
      Iterator it = c.iterator();
      while(it.hasNext()){
        return (GeneticAnalysis)it.next();
      }
    }
    catch (Exception nsoe) {
      nsoe.printStackTrace();
      return null;
    }
    return null;
  }

  /**
   * Returns true if a matching Adoption object is found in the database.
   * @param num Unique identifier of the Adoption to match.
   * @return True if the adoption can be found in the database. Otherwise, false.
   */
  public boolean isAdoption(String num) {
    try {
      Adoption tempEnc = ((org.ecocean.Adoption) (pm.getObjectById(pm.newObjectIdInstance(Adoption.class, num.trim()), true)));
    } catch (Exception nsoe) {
      return false;
    }
    return true;
  }

  /**
   * Returns true if a matching Keyword object is found in the database. The match can be made if the String parameter matches the readableName or indexName of the Keyword.
   * @param keywordDescription Unique Keyword.readableName or Keyword.indexName to match.
   * @return True if the object can be found in the database. Otherwise, false.
   */
  public boolean isKeyword(String keywordDescription) {
    Iterator keywords = getAllKeywords();
	    while (keywords.hasNext()) {
      Keyword kw = (Keyword) keywords.next();
      if((kw.getReadableName().equals(keywordDescription))||(kw.getIndexname().equals(keywordDescription))){return true;}
 	}

    return false;
  }

  
  public boolean isSinglePhotoVideo(String indexname) {
    try {
      SinglePhotoVideo tempEnc = ((org.ecocean.SinglePhotoVideo) (pm.getObjectById(pm.newObjectIdInstance(SinglePhotoVideo.class, indexname.trim()), true)));
    } catch (Exception nsoe) {
      return false;
    }
    return true;
  }

  public boolean isScanTask(String uniqueID) {
    try {
      ScanTask tempEnc = ((org.ecocean.grid.ScanTask) (pm.getObjectById(pm.newObjectIdInstance(ScanTask.class, uniqueID.trim()), true)));
    } catch (Exception nsoe) {
      return false;
    }
    return true;
  }


  public boolean isMarkedIndividual(String name) {
    try {
      MarkedIndividual tempShark = ((org.ecocean.MarkedIndividual) (pm.getObjectById(pm.newObjectIdInstance(MarkedIndividual.class, name.trim()), true)));
    } catch (Exception nsoe) {
      return false;
    }
    return true;
  }


  /**
   * Adds a new shark to the database
   *
   * @param newShark the new shark to be added to the database
   * @see MarkedIndividual
   */
  public boolean addMarkedIndividual(MarkedIndividual newShark) {
    return storeNewMarkedIndividual(newShark);
  }


  /**
   * Retrieves any unassigned encounters that are stored in the database - but not yet analyzed - to see whether they represent new or already persistent sharks
   *
   * @return an Iterator of shark encounters that have yet to be assigned shark status or assigned to an existing shark in the database
   * @see encounter, java.util.Iterator
   */
  public Iterator getUnassignedEncounters() {
    String filter = "this.individualID == \"Unassigned\"";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query orphanedEncounters = pm.newQuery(encClass, filter);
    Collection c = (Collection) (orphanedEncounters.execute());
    return c.iterator();
  }

  public Iterator getUnassignedEncountersIncludingUnapproved() {
    String filter = "this.individualID == \"Unassigned\"";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query orphanedEncounters = pm.newQuery(encClass, filter);
    Collection c = (Collection) (orphanedEncounters.execute());
    return c.iterator();
  }

  public Iterator getUnassignedEncountersIncludingUnapproved(Query orphanedEncounters) {
    String filter = "this.individualID == \"Unassigned\" && this.state != \"unidentifiable\"";
    //Extent encClass=pm.getExtent(encounter.class, true);
    orphanedEncounters.setFilter(filter);
    Collection c = (Collection) (orphanedEncounters.execute());
    return c.iterator();
  }

  public Iterator getAllEncountersNoFilter() {
    Collection c;
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass);
    try {
      c = (Collection) (acceptedEncounters.execute());
      ArrayList list = new ArrayList(c);
      Iterator it = list.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllEncountersNoFilter. Returning a null collection because I didn't have a transaction to use.");
      npe.printStackTrace();
      return null;
    }
  }

  public Iterator getAllEncountersNoQuery() {
    try {
      Extent encClass = pm.getExtent(Encounter.class, true);
      Iterator it = encClass.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllEncountersNoQuery. Returning a null iterator.");
      npe.printStackTrace();
      return null;
    }
  }

  public Iterator getAllAdoptionsNoQuery() {
    try {
      Extent encClass = pm.getExtent(Adoption.class, true);
      Iterator it = encClass.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllAdoptionsNoQuery. Returning a null iterator.");
      npe.printStackTrace();
      return null;
    }
  }

  public Iterator getAllAdoptionsWithQuery(Query ads) {
    try {
      Collection c = (Collection) (ads.execute());
      Iterator it = c.iterator();
      return it;
    } catch (Exception npe) {
      npe.printStackTrace();
      return null;
    }
  }

  public Iterator getAllScanTasksNoQuery() {
    try {
      Extent taskClass = pm.getExtent(ScanTask.class, true);
      Iterator it = taskClass.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllScanTasksNoQuery. Returning a null iterator.");
      npe.printStackTrace();
      return null;
    }
  }

  public Iterator getAllScanWorkItemsNoQuery() {
    try {
      Extent taskClass = pm.getExtent(ScanWorkItem.class, true);
      Iterator it = taskClass.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllScanWorkItemsNoQuery. Returning a null iterator.");
      npe.printStackTrace();
      return null;
    }
  }


  /**
   * Retrieves any all encounters that are stored in the database
   *
   * @return an Iterator of all whale shark encounters stored in the database
   * @see encounter, java.util.Iterator
   */
  public Iterator getAllEncounters() {
    Collection c;
    //String filter = "!this.state == \"unidentifiable\" && this.state == \"approved\"";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass);
    try {
      c = (Collection) (acceptedEncounters.execute());
      ArrayList list = new ArrayList(c);
      Iterator it = list.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllEncounters. Returning a null collection because I didn't have a transaction to use.");
      npe.printStackTrace();
      return null;
    }
  }

  public Iterator getAllEncounters(Query acceptedEncounters) {
    Collection c;
    try {
      c = (Collection) (acceptedEncounters.execute());
      ArrayList list = new ArrayList(c);
      //Collections.reverse(list);
      Iterator it = list.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllEncounters(Query). Returning a null collection.");
      npe.printStackTrace();
      return null;
    }
  }
  
  public ArrayList<SinglePhotoVideo> getAllSinglePhotoVideo(Query acceptedEncounters) {
    Collection c;
    try {
      c = (Collection) (acceptedEncounters.execute());
      ArrayList<SinglePhotoVideo> list = new ArrayList<SinglePhotoVideo>(c);
      return list;
    } 
    catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllSinglePhotoVideo(Query). Returning a null collection.");
      npe.printStackTrace();
      return null;
    }
  }

  public Iterator getAllEncounters(Query acceptedEncounters, Map<String, Object> paramMap) {
    Collection c;
    try {
      c = (Collection) (acceptedEncounters.executeWithMap(paramMap));
      ArrayList list = new ArrayList(c);
      //Collections.reverse(list);
      Iterator it = list.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllEncounters(Query). Returning a null collection.");
      npe.printStackTrace();
      return null;
    }
  }


  public Iterator getAvailableScanWorkItems(int pageSize, long timeout) {
    Collection c;
    Extent encClass = getPM().getExtent(ScanWorkItem.class, true);
    Query query = getPM().newQuery(encClass);
    long timeDiff = System.currentTimeMillis() - timeout;
    query.setFilter("!this.done && this.startTime < " + timeDiff);
    query.setRange(0, pageSize);
    try {
      c = (Collection) (query.execute());
      ArrayList list = new ArrayList(c);
      Iterator it = list.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllEncounters(Query). Returning a null collection.");
      npe.printStackTrace();
      return null;
    }
  }

  public Iterator getAvailableScanWorkItems(int pageSize, String taskID, long timeout) {
    Collection c;
    Extent encClass = getPM().getExtent(ScanWorkItem.class, true);
    Query query = getPM().newQuery(encClass);
    long timeDiff = System.currentTimeMillis() - timeout;
    String filter = "!this.done && this.taskID == \"" + taskID + "\" && this.startTime < " + timeDiff;
    query.setFilter(filter);
    query.setRange(0, pageSize);
    try {
      c = (Collection) (query.execute());
      ArrayList list = new ArrayList(c);
      //Collections.reverse(list);
      Iterator it = list.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllEncounters(Query). Returning a null collection.");
      npe.printStackTrace();
      return null;
    }
  }

  public ArrayList getID4AvailableScanWorkItems(Query query, int pageSize, long timeout, boolean forceReturn) {
    Collection c;
    query.setResult("uniqueNum");
    long timeDiff = System.currentTimeMillis() - timeout;
    String filter = "";
    if (forceReturn) {
      filter = "this.done == false";
    } else {
      filter = "this.done == false && this.startTime < " + timeDiff;
      query.setRange(0, pageSize);
    }
    query.setFilter(filter);
    //query.setOrdering("createTime ascending");
    try {
      c = (Collection) (query.execute());
      ArrayList list = new ArrayList(c);
      return list;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getID4AvailableScanWorkItems. Returning a null collection.");
      npe.printStackTrace();
      return null;
    }
  }

  public ArrayList getPairs(Query query, int pageSize) {
    Collection c;
    query.setRange(0, pageSize);
    try {
      c = (Collection) (query.execute());
      ArrayList list = new ArrayList(c);
      return list;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllEncounters(Query). Returning a null collection.");
      npe.printStackTrace();
      return null;
    }
  }

  public ArrayList getID4AvailableScanWorkItems(String taskID, Query query, int pageSize, long timeout, boolean forceReturn) {
    Collection c;
    query.setResult("uniqueNum");
    long timeDiff = System.currentTimeMillis() - timeout;
    String filter = "";
    if (forceReturn) {
      filter = "this.done == false && this.taskID == \"" + taskID + "\"";
    } else {
      filter = "this.done == false && this.taskID == \"" + taskID + "\" && this.startTime < " + timeDiff;
      query.setRange(0, pageSize);
    }
    query.setFilter(filter);
    //query.setOrdering("createTime ascending");
    try {
      c = (Collection) (query.execute());
      ArrayList list = new ArrayList(c);
      return list;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getID4AvailableScanWorkItems). Returning a null collection.");
      npe.printStackTrace();
      return null;
    }
  }

  public ArrayList getAdopterEmailsForMarkedIndividual(String shark) {
    Collection c;
    Extent encClass = getPM().getExtent(Adoption.class, true);
    Query query = getPM().newQuery(encClass);
    query.setResult("adopterEmail");
    String filter = "this.individual == '" + shark + "'";
    query.setFilter(filter);
    try {
      c = (Collection) (query.execute());
      ArrayList list = new ArrayList(c);
      return list;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute shepherd.getAdopterEmailsForMarkedIndividual(). Returning a null collection.");
      npe.printStackTrace();
      return null;
    }
  }


  public Iterator getAllEncountersAndUnapproved() {
    Collection c;
    String filter = "this.state != \"unidentifiable\"";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    try {
      c = (Collection) (acceptedEncounters.execute());
      ArrayList list = new ArrayList(c);
      int wr = list.size();
      Iterator it = list.iterator();
      return it;
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute getAllEncounters. Returning a null collection because I didn't have a transaction to use.");
      npe.printStackTrace();
      return null;
    }

  }

  /**
   * Retrieves all encounters that are stored in the database in the order specified by the input String
   *
   * @return an Iterator of all valid whale shark encounters stored in the visual database, arranged by the input String
   * @see encounter, java.util.Iterator
   */
  public Iterator getAllEncounters(String order) {
    //String filter = "this.state != \"unidentifiable\" && this.state == \"approved\"";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass);
    acceptedEncounters.setOrdering(order);
    Collection c = (Collection) (acceptedEncounters.execute());
    Iterator it = c.iterator();
    return it;
  }

  public ArrayList getAllAdoptionsForMarkedIndividual(String ind) {
    String filter = "this.individual == '" + ind + "'";
    Extent encClass = pm.getExtent(Adoption.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    Collection c = (Collection) (acceptedEncounters.execute());
    return (new ArrayList(c));
  }

  public ArrayList getAllAdoptionsForEncounter(String shark) {
    String filter = "this.encounter == '" + shark + "'";
    Extent encClass = pm.getExtent(Adoption.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    Collection c = (Collection) (acceptedEncounters.execute());
    return (new ArrayList(c));
  }

  public Iterator getAllEncounters(Query acceptedEncounters, String order) {
    acceptedEncounters.setOrdering(order);
    Collection c = (Collection) (acceptedEncounters.execute());
    ArrayList list = new ArrayList(c);
    //Collections.reverse(list);
    Iterator it = list.iterator();
    return it;

  }

  /**
   * Retrieves a filtered list of encounters that are stored in the database in the order specified by the input String
   *
   * @return a filtered Iterator of whale shark encounters stored in the visual database, arranged by the input String
   * @see encounter, java.util.Iterator
   */
  public Iterator getAllEncounters(String order, String filter2use) {
    //String filter = filter2use + " && this.approved == true";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter2use);
    acceptedEncounters.setOrdering(order);
    Collection c = (Collection) (acceptedEncounters.execute());
    Iterator it = c.iterator();
    return it;
  }

  public ArrayList<TissueSample> getAllTissueSamplesForEncounter(String encNum) {
    String filter = "correspondingEncounterNumber == \""+encNum+"\"";
    Extent encClass = pm.getExtent(TissueSample.class, true);
    Query samples = pm.newQuery(encClass, filter);
    Collection c = (Collection) (samples.execute());
    return (new ArrayList<TissueSample>(c));
  }

  public ArrayList<SinglePhotoVideo> getAllSinglePhotoVideosForEncounter(String encNum) {
    String filter = "correspondingEncounterNumber == \""+encNum+"\"";
    Extent encClass = pm.getExtent(SinglePhotoVideo.class, true);
    Query samples = pm.newQuery(encClass, filter);
    Collection c = (Collection) (samples.execute());
    return (new ArrayList<SinglePhotoVideo>(c));
  }

  public Iterator getAllEncountersNoFilter(String order, String filter2use) {
    String filter = filter2use;
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    acceptedEncounters.setOrdering(order);
    Collection c = (Collection) (acceptedEncounters.execute());
    Iterator it = c.iterator();
    return it;

  }


  public Query getAllEncountersNoFilterReturnQuery(String order, String filter2use) {
    String filter = filter2use;
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    return acceptedEncounters;

  }

  /**
   * Retrieves all encounters that are stored in the database but which have been rejected for the visual database
   *
   * @return an Iterator of all whale shark encounters stored in the database that are unacceptable for the visual ID library
   * @see encounter, java.util.Iterator
   */
  public Iterator getAllUnidentifiableEncounters(Query rejectedEncounters) {
    rejectedEncounters.setFilter("this.state == \"unidentifiable\"");
    Collection c = (Collection) (rejectedEncounters.execute());
    ArrayList list = new ArrayList(c);

    //Collections.reverse(list);
    Iterator it = list.iterator();
    return it;
  }

  /**
   * Retrieves all new encounters that are stored in the database but which have been approved for public viewing in the visual database
   *
   * @return an Iterator of all whale shark encounters stored in the database that are unacceptable for the visual ID library
   * @see encounter, java.util.Iterator
   */
  public Iterator getUnapprovedEncounters(Query acceptedEncounters) {
    Collection c = (Collection) (acceptedEncounters.execute());
    ArrayList list = new ArrayList(c);
    Iterator it = list.iterator();
    return it;

  }

  public Iterator getUnapprovedEncounters(Query unapprovedEncounters, String order) {
    unapprovedEncounters.setOrdering(order);
    Collection c = (Collection) (unapprovedEncounters.execute());
    Iterator it = c.iterator();
    return it;
  }

  //Returns encounters submitted by the specified user
  public Iterator getUserEncounters(Query userEncounters, String user) {
    Collection c = (Collection) (userEncounters.execute());
    ArrayList list = new ArrayList(c);

    Iterator it = list.iterator();
    return it;
  }


  public Iterator getSortedUserEncounters(Query userEncounters, String order2) {
    userEncounters.setOrdering(order2);
    Collection c = (Collection) (userEncounters.execute());
    Iterator it = c.iterator();
    return it;
  }

  /**
   * Retrieves all encounters that are stored in the database but which have been rejected for the visual database in the order identified by the input String
   *
   * @return an Iterator of all whale shark encounters stored in the database that are unacceptable for the visual ID library in the String order
   * @see encounter, java.util.Iterator
   */
  public Iterator getAllUnidentifiableEncounters(Query unacceptedEncounters, String order) {
    unacceptedEncounters.setOrdering(order);
    Collection c = (Collection) (unacceptedEncounters.execute());
    ArrayList list = new ArrayList(c);

    //Collections.reverse(list);
    Iterator it = list.iterator();
    return it;
  }


  public MarkedIndividual getMarkedIndividual(String name) {
    MarkedIndividual tempShark = null;
    try {
      tempShark = ((org.ecocean.MarkedIndividual) (pm.getObjectById(pm.newObjectIdInstance(MarkedIndividual.class, name.trim()), true)));
    } catch (Exception nsoe) {
      nsoe.printStackTrace();
      return null;
    }
    return tempShark;
  }


  /**
   * Returns all of the names of the sharks that can be used for training purporses - i.e. have more than one encounter - in a Vector format
   *
   * @return a Vector of shark names that have more than one encounter with spots associated with them
   * @see encounter, shark, java.util.Vector
   */
  public Vector getPossibleTrainingIndividuals() {
    Iterator allSharks = getAllMarkedIndividuals();
    MarkedIndividual tempShark;
    Vector possibleTrainingSharkNames = new Vector();
    while (allSharks.hasNext()) {
      tempShark = (MarkedIndividual) (allSharks.next());
      if (tempShark.getNumberTrainableEncounters() >= 2) {
        possibleTrainingSharkNames.add(tempShark);
        //System.out.println(tempShark.getName()+" has more than one encounter.");
      }
      ;

    }
    return possibleTrainingSharkNames;

  }

  public Vector getRightPossibleTrainingIndividuals() {
    Iterator allSharks = getAllMarkedIndividuals();
    MarkedIndividual tempShark;
    Vector possibleTrainingSharkNames = new Vector();
    while (allSharks.hasNext()) {
      tempShark = (MarkedIndividual) (allSharks.next());
      if (tempShark.getNumberRightTrainableEncounters() >= 2) {
        possibleTrainingSharkNames.add(tempShark);
        //System.out.println(tempShark.getName()+" has more than one encounter.");
      }
      ;
    }
    return possibleTrainingSharkNames;

  }


  /**
   * Retrieves an Iterator of all the sharks in the database
   *
   * @return an Iterator containing all of the shark objects that have been stored in the database
   * @see shark, java.util.Iterator
   */
  public Iterator getAllMarkedIndividuals() {
    Extent allSharks = null;
    try {
      allSharks = pm.getExtent(MarkedIndividual.class, true);
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
    }
    Extent encClass = pm.getExtent(MarkedIndividual.class, true);
    Query sharks = pm.newQuery(encClass);
    Collection c = (Collection) (sharks.execute());
    ArrayList list = new ArrayList(c);
    Iterator it = list.iterator();
    return it;
  }

  public ArrayList<MarkedIndividual> getAllMarkedIndividualsFromLocationID(String locCode) {
    Extent allSharks = null;
    try {
      allSharks = pm.getExtent(MarkedIndividual.class, true);
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
    }
    Extent encClass = pm.getExtent(MarkedIndividual.class, true);
    Query sharks = pm.newQuery(encClass);
    Collection c = (Collection) (sharks.execute());
    ArrayList list = new ArrayList(c);
    ArrayList<MarkedIndividual> newList=new ArrayList<MarkedIndividual>();
    int listSize=list.size();
    for(int i=0;i<listSize;i++){
      MarkedIndividual indie=(MarkedIndividual)list.get(i);
      if(indie.wasSightedInLocationCode(locCode)){newList.add(indie);}
    }
    return newList;
  }



  public Iterator getAllMarkedIndividuals(Query sharks) {
    Collection c = (Collection) (sharks.execute());
    ArrayList list = new ArrayList(c);
    //Collections.reverse(list);
    Iterator it = list.iterator();
    return it;
  }

  /**
   * Retrieves an Iterator of all the sharks in the database, ordered according to the input String
   *
   * @return an Iterator containing all of the shark objects that have been stored in the database, ordered according to the input String
   * @see shark, java.util.Iterator
   */
  public Iterator getAllMarkedIndividuals(Query sharkies, String order) {
    Map<String, Object> emptyMap = Collections.emptyMap();
    return getAllMarkedIndividuals(sharkies, order, emptyMap);
  }

  public Iterator getAllMarkedIndividuals(Query sharkies, String order, Map<String, Object> params) {
    sharkies.setOrdering(order);
    Collection c = (Collection) (sharkies.executeWithMap(params));
    ArrayList list = new ArrayList(c);
    //Collections.reverse(list);
    Iterator it = list.iterator();
    return it;
  }


  public int getNumMarkedIndividuals() {
    int num = 0;
    try {
      pm.getFetchPlan().setGroup("count");
      Query q = pm.newQuery(MarkedIndividual.class); // no filter, so all instances match
      Collection results = (Collection) q.execute();
      num = results.size();
      q.closeAll();
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      return num;
    }
    return num;
  }


  public int getNumScanTasks() {
    Extent allTasks = null;
    int num = 0;
    try {
      Query q = pm.newQuery(ScanTask.class); // no filter, so all instances match
      Collection results = (Collection) q.execute();
      num = results.size();
      q.closeAll();
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      return num;
    }
    return num;
  }

  public int getNumUnfinishedScanTasks() {
    pm.getFetchPlan().setGroup("count");
    Collection c;
    Extent encClass = getPM().getExtent(ScanTask.class, true);
    Query query = getPM().newQuery(encClass);
    String filter = "!this.isFinished";
    query.setFilter(filter);
    query.setResult("count(this)");
    try {
      return ((Long) query.execute()).intValue();
    } catch (Exception npe) {
      System.out.println("Error encountered when trying to execute shepherd.getNumUnfinishedScanTasks(). Returning an zero value.");
      npe.printStackTrace();
      return 0;
    }
  }


  public int getNumEncounters() {
    pm.getFetchPlan().setGroup("count");
    Extent encClass = pm.getExtent(Encounter.class, true);
    String filter = "this.state != \"unidentifiable\"";
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    try {
      Collection c = (Collection) (acceptedEncounters.execute());
      int num = c.size();
      acceptedEncounters.closeAll();
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      acceptedEncounters.closeAll();
      return 0;
    }
  }

  public int getNumAdoptions() {
    pm.getFetchPlan().setGroup("count");
    Extent encClass = pm.getExtent(Adoption.class, true);
    Query acceptedEncounters = pm.newQuery(encClass);
    try {
      Collection c = (Collection) (acceptedEncounters.execute());
      int num = c.size();
      acceptedEncounters.closeAll();
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      acceptedEncounters.closeAll();
      return 0;
    }
  }

  public int getNumApprovedEncounters() {
    pm.getFetchPlan().setGroup("count");
    Extent encClass = pm.getExtent(Encounter.class, true);
    String filter = "this.state == \"approved\"";
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    try {
      Collection c = (Collection) (acceptedEncounters.execute());
      int num = c.size();
      acceptedEncounters.closeAll();
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      acceptedEncounters.closeAll();
      return 0;
    }
  }

  public int getNumEncounters(String locationCode) {
    Extent encClass = pm.getExtent(Encounter.class, true);
    String filter = "this.locationID == \"" + locationCode+"\"";
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    try {
      Collection c = (Collection) (acceptedEncounters.execute());
      int num = c.size();
      acceptedEncounters.closeAll();
      acceptedEncounters = null;
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      acceptedEncounters.closeAll();
      return 0;
    }
  }

  public int getNumUnidentifiableEncountersForMarkedIndividual(String individual) {
    Extent encClass = pm.getExtent(Encounter.class, true);
    String filter = "this.state == \"unidentifiable\" && this.individualID == \"" + individual+"\"";
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    try {
      Collection c = (Collection) (acceptedEncounters.execute());
      int num = c.size();
      acceptedEncounters.closeAll();
      acceptedEncounters = null;
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      acceptedEncounters.closeAll();
      return 0;
    }
  }

  public int getNumUnidentifiableEncounters() {
    Extent encClass = pm.getExtent(Encounter.class, true);
    String filter = "this.state == \"unidentifiable\"";
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    try {
      Collection c = (Collection) (acceptedEncounters.execute());
      int num = c.size();
      acceptedEncounters.closeAll();
      acceptedEncounters = null;
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      acceptedEncounters.closeAll();
      return 0;
    }
  }

  public Vector getUnidentifiableEncountersForMarkedIndividual(String individual) {
    Extent encClass = pm.getExtent(Encounter.class, true);
    String filter = "this.state == \"unidentifiable\" && this.individualID == " + individual;
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    try {
      Collection c = (Collection) (acceptedEncounters.execute());
      Vector matches = new Vector(c);
      acceptedEncounters.closeAll();
      acceptedEncounters = null;
      return matches;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      acceptedEncounters.closeAll();
      return new Vector();
    }
  }

  public int getNumEncountersWithSpotData(boolean rightSide) {
    pm.getFetchPlan().setGroup("count");
    Extent encClass = pm.getExtent(Encounter.class, true);
    String filter = "";
    if (rightSide) {
      filter = "this.numSpotsRight > 0";
    } else {
      filter = "this.numSpotsLeft > 0";
    }
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    int num = 0;
    try {
      Collection c = (Collection) (acceptedEncounters.execute());
      Iterator it = c.iterator();

      num = c.size();
      acceptedEncounters.closeAll();
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      acceptedEncounters.closeAll();
      return 0;
    }
  }


  public int getNumRejectedEncounters() {
    Extent allEncounters = null;
    String filter = "this.state == \"unidentifiable\"";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    try {


      //acceptedEncounters.declareParameters("String rejected");
      Collection c = (Collection) (acceptedEncounters.execute());
      int num = c.size();
      acceptedEncounters.closeAll();
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      //logger.error("I could not find the number of rejected encounters in the database."+"\n"+x.getStackTrace());
      acceptedEncounters.closeAll();
      return 0;
    }
  }

  public int getNumUnapprovedEncounters() {
    String filter = "this.state == \"unapproved\"";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query unacceptedEncounters = pm.newQuery(encClass, filter);
    try {


      //acceptedEncounters.declareParameters("String rejected");
      Collection c = (Collection) (unacceptedEncounters.execute());
      int num = c.size();
      unacceptedEncounters.closeAll();
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      //logger.error("I could not find the number of rejected encounters in the database."+"\n"+x.getStackTrace());
      unacceptedEncounters.closeAll();
      return 0;
    }
  }


  public int getNumUserEncounters(String user) {
    String filter = "this.submitterID == \"" + user + "\"";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    try {
      Collection c = (Collection) (acceptedEncounters.execute());
      int num = c.size();
      acceptedEncounters.closeAll();
      return num;
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      acceptedEncounters.closeAll();
      return 0;
    }
  }

  /**
   * Returns the <i>i</i>th numbered encounter for a shark
   *
   * @param  tempShark  the shark to retrieve an encounter from
   * i			the number of the shark to get, numbered from 0...<i>n</i>
   * @return the <i>i</i>th encounter of the specified shark
   * @see MarkedIndividual
   */
  public Encounter getEncounter(MarkedIndividual tempShark, int i) {
    MarkedIndividual myShark = getMarkedIndividual(tempShark.getName());
    Encounter e = (Encounter) myShark.getEncounters().get(i);
    return e;
  }


  /**
   * Opens the database up for information retrieval, storage, and removal
   */
  public void beginDBTransaction() {
    try {
      if (pm == null || pm.isClosed()) {
        pm = pmf.getPersistenceManager();
        pm.currentTransaction().begin();
      } else if (!pm.currentTransaction().isActive()) {

        pm.currentTransaction().begin();
      }

    } catch (JDOUserException jdoe) {
      jdoe.printStackTrace();
    } catch (NullPointerException npe) {
      npe.printStackTrace();
    }
  }

  /**
   * Commits (makes permanent) any changes made to an open database
   */
  public void commitDBTransaction() {
    try {
      //System.out.println("     shepherd:"+identifyMe+" is trying to commit a transaction");
      if ((pm != null) && (pm.currentTransaction().isActive())) {

        //System.out.println("     Now commiting a transaction with pm"+(String)pm.getUserObject());
        pm.currentTransaction().commit();
        //return true;
        //System.out.println("A transaction has been successfully committed.");
      } else {
        System.out.println("You are trying to commit an inactive transaction.");
        //return false;
      }


    } catch (JDOUserException jdoe) {
      jdoe.printStackTrace();
      System.out.println("I failed to commit a transaction." + "\n" + jdoe.getStackTrace());
      //return false;
    } catch (JDOException jdoe2) {
      jdoe2.printStackTrace();
      //return false;
    }
    //added to prevent conflicting calls jah 1/19/04
    catch (NullPointerException npe) {
      System.out.println("A null pointer exception was thrown while trying to commit a transaction!");
      npe.printStackTrace();
      //return false;
    }
  }

  /**
   * Commits (makes permanent) any changes made to an open database
   */
  public void commitDBTransaction(String action) {
    commitDBTransaction();
  }


  /**
   * Closes a PersistenceManager
   */
  public void closeDBTransaction() {
    try {
      if ((pm != null) && (!pm.isClosed())) {
        pm.close();
      }
      //logger.info("A PersistenceManager has been successfully closed.");
    } catch (JDOUserException jdoe) {
      System.out.println("I hit an error trying to close a DBTransaction.");
      jdoe.printStackTrace();

      //logger.error("I failed to close a PersistenceManager."+"\n"+jdoe.getStackTrace());
    } catch (NullPointerException npe) {
      System.out.println("I hit a NullPointerException trying to close a DBTransaction.");
      npe.printStackTrace();
    }
  }


  /**
   * Undoes any changes made to an open database.
   */
  public void rollbackDBTransaction() {
    try {
      if ((pm != null) && (pm.currentTransaction().isActive())) {
        //System.out.println("     Now rollingback a transaction with pm"+(String)pm.getUserObject());
        pm.currentTransaction().rollback();
        //System.out.println("A transaction has been successfully committed.");
      } else {
        //System.out.println("You are trying to rollback an inactive transaction.");
      }

    } catch (JDOUserException jdoe) {
      jdoe.printStackTrace();
    } catch (JDOFatalUserException fdoe) {
      fdoe.printStackTrace();
    } catch (NullPointerException npe) {
      npe.printStackTrace();
    }
  }


  public Iterator getAllKeywords() {
    Extent allKeywords = null;
    Iterator it = null;
    try {
      allKeywords = pm.getExtent(Keyword.class, true);
      Query acceptedKeywords = pm.newQuery(allKeywords);
      acceptedKeywords.setOrdering("readableName descending");
      Collection c = (Collection) (acceptedKeywords.execute());
      it = c.iterator();
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      return null;
    }
    return it;
  }

  public Iterator getAllKeywords(Query acceptedKeywords) {
    Extent allKeywords = null;
    Iterator it = null;
    try {
      acceptedKeywords.setOrdering("readableName descending");
      Collection c = (Collection) (acceptedKeywords.execute());
      it = c.iterator();
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      return null;
    }
    return it;
  }


  public int getNumKeywords() {
    Extent allWords = null;
    int num = 0;
    try {
      Query q = pm.newQuery(Keyword.class); // no filter, so all instances match
      Collection results = (Collection) q.execute();
      num = results.size();
      q.closeAll();
    } catch (javax.jdo.JDOException x) {
      x.printStackTrace();
      return num;
    }
    return num;
  }


  public ArrayList<SinglePhotoVideo> getThumbnails(HttpServletRequest request, Iterator it, int startNum, int endNum, String[] keywords) {
    ArrayList<SinglePhotoVideo> thumbs = new ArrayList<SinglePhotoVideo>();
    boolean stopMe = false;
    int count = 0;
    while (it.hasNext()) {
      Encounter enc = (Encounter) it.next();
      ArrayList<SinglePhotoVideo> images=getAllSinglePhotoVideosForEncounter(enc.getCatalogNumber());

      if ((count + images.size()) >= startNum) {
        for (int i = 0; i < images.size(); i++) {
          count++;
          if ((count <= endNum) && (count >= startNum)) {
            String m_thumb = "";

            //check for video or image
            String imageName = (String) images.get(i).getFilename();

            //check if this image has one of the assigned keywords
            boolean hasKeyword = false;
            if ((keywords == null) || (keywords.length == 0)) {
              hasKeyword = true;
            } else {
              int numKeywords = keywords.length;
              for (int n = 0; n < numKeywords; n++) {
                if (!keywords[n].equals("None")) {
                  Keyword word = getKeyword(keywords[n]);

                  if (images.get(i).getKeywords().contains(word)) {


                  //if (word.isMemberOf(enc.getCatalogNumber() + "/" + imageName)) {

                    hasKeyword = true;
                    //System.out.println("member of: "+word.getReadableName());
                  }
                } else {
                  hasKeyword = true;
                }

              }

            }

			//check for specific filename conditions here
			if((request.getParameter("filenameField")!=null)&&(!request.getParameter("filenameField").equals(""))){
					String nameString=ServletUtilities.cleanFileName(ServletUtilities.preventCrossSiteScriptingAttacks(request.getParameter("filenameField").trim()));
					if(!nameString.equals(imageName)){hasKeyword=false;}
			}
      if (hasKeyword && isAcceptableVideoFile(imageName)) {
              m_thumb = "http://" + CommonConfiguration.getURLLocation(request) + "/images/video.jpg" + "BREAK" + enc.getEncounterNumber() + "BREAK" + imageName;
              //thumbs.add(m_thumb);
              thumbs.add(images.get(i));
      }
      else if (hasKeyword && isAcceptableImageFile(imageName)) {
              m_thumb = enc.getEncounterNumber() + "/" + (i + 1) + ".jpg" + "BREAK" + enc.getEncounterNumber() + "BREAK" + imageName;
              //thumbs.add(m_thumb);
              thumbs.add(images.get(i));
      }
      else {
              count--;
      }
          } else if (count > endNum) {
            stopMe = true;
          }
        }
      } //end if
      else {
        count += images.size();
      }

    }//end while
    return thumbs;
  }

  public ArrayList<SinglePhotoVideo> getMarkedIndividualThumbnails(HttpServletRequest request, Iterator<MarkedIndividual> it, int startNum, int endNum, String[] keywords) {
    ArrayList<SinglePhotoVideo> thumbs = new ArrayList<SinglePhotoVideo>();

    boolean stopMe = false;
    int count = 0;
    while (it.hasNext()&&!stopMe) {
      MarkedIndividual markie = it.next();
      Iterator allEncs = markie.getEncounters().iterator();
      while (allEncs.hasNext()&&!stopMe) {
        Encounter enc = (Encounter) allEncs.next();
        ArrayList<SinglePhotoVideo> images=getAllSinglePhotoVideosForEncounter(enc.getCatalogNumber());

        if ((count + images.size()) >= startNum) {
          for (int i = 0; i < images.size(); i++) {
            count++;
            if ((count <= endNum) && (count >= startNum)) {
              String m_thumb = "";

              //check for video or image
              String imageName = (String) images.get(i).getFilename();

              //check if this image has one of the assigned keywords
              boolean hasKeyword = false;
              if ((keywords == null) || (keywords.length == 0)) {
                hasKeyword = true;
              } else {
                int numKeywords = keywords.length;
                for (int n = 0; n < numKeywords; n++) {
                  if (!keywords[n].equals("None")) {
                    Keyword word = getKeyword(keywords[n]);

                    if (images.get(i).getKeywords().contains(word)) {


                    //if (word.isMemberOf(enc.getCatalogNumber() + "/" + imageName)) {

                      hasKeyword = true;
                      //System.out.println("member of: "+word.getReadableName());
                    }
                  } else {
                    hasKeyword = true;
                  }

                }

              }

        //check for specific filename conditions here
        if((request.getParameter("filenameField")!=null)&&(!request.getParameter("filenameField").equals(""))){
            String nameString=ServletUtilities.cleanFileName(ServletUtilities.preventCrossSiteScriptingAttacks(request.getParameter("filenameField").trim()));
            if(!nameString.equals(imageName)){hasKeyword=false;}
        }
        if (hasKeyword && isAcceptableVideoFile(imageName)) {
                m_thumb = "http://" + CommonConfiguration.getURLLocation(request) + "/images/video.jpg" + "BREAK" + enc.getEncounterNumber() + "BREAK" + imageName;
                //thumbs.add(m_thumb);
                thumbs.add(images.get(i));
        }
        else if (hasKeyword && isAcceptableImageFile(imageName)) {
                m_thumb = enc.getEncounterNumber() + "/" + (i + 1) + ".jpg" + "BREAK" + enc.getEncounterNumber() + "BREAK" + imageName;
                //thumbs.add(m_thumb);
                thumbs.add(images.get(i));
        }
        else {
                count--;
        }
            } 
            else if (count > endNum) {
              stopMe = true;
              return thumbs;
            }
          }
        } //end if
        else {
          count += images.size();
        }

      }//end while

    }//end while
    return thumbs;
  }

  public int getNumThumbnails(Iterator it, String[] keywords) {
    //Vector thumbs=new Vector();
    //boolean stopMe=false;
    int count = 0;
    while (it.hasNext()) {
      Encounter enc = (Encounter) it.next();
      for (int i = 0; i < enc.getAdditionalImageNames().size(); i++) {
        count++;
        //String m_thumb="";

        //check for video or image
        String imageName = (String) enc.getAdditionalImageNames().get(i);

        //check if this image has one of the assigned keywords
        boolean hasKeyword = false;
        if ((keywords == null) || (keywords.length == 0)) {
          hasKeyword = true;
        } else {
          int numKeywords = keywords.length;
          for (int n = 0; n < numKeywords; n++) {
            if (!keywords[n].equals("None")) {
              Keyword word = getKeyword(keywords[n]);

              //if (word.isMemberOf(enc.getCatalogNumber() + "/" + imageName)) {
              if(enc.hasKeyword(word)){
                hasKeyword = true;
                //System.out.println("member of: "+word.getReadableName());
              }
            } else {
              hasKeyword = true;
            }

          }

        }

        if (hasKeyword && isAcceptableVideoFile(imageName)) {
          //m_thumb="http://"+CommonConfiguration.getURLLocation()+"/images/video.jpg"+"BREAK"+enc.getEncounterNumber()+"BREAK"+imageName;
          //thumbs.add(m_thumb);
        } else if (hasKeyword && isAcceptableImageFile(imageName)) {
          //m_thumb=enc.getEncounterNumber()+"/"+(i+1)+".jpg"+"BREAK"+enc.getEncounterNumber()+"BREAK"+imageName;
          //thumbs.add(m_thumb);
        } else {
          count--;
        }

      }


    }//end while
    return count;
  }


  public int getNumMarkedIndividualThumbnails(Iterator<MarkedIndividual> it, String[] keywords) {
    int count = 0;
    while (it.hasNext()) {
      MarkedIndividual indie = it.next();
      Iterator allEncs = indie.getEncounters().iterator();
      while (allEncs.hasNext()) {
        Encounter enc = (Encounter) allEncs.next();
        for (int i = 0; i < enc.getAdditionalImageNames().size(); i++) {
          count++;
          //String m_thumb="";

          //check for video or image
          String imageName = (String) enc.getAdditionalImageNames().get(i);

          //check if this image has one of the assigned keywords
          boolean hasKeyword = false;
          if ((keywords == null) || (keywords.length == 0)) {
            hasKeyword = true;
          } else {
            int numKeywords = keywords.length;
            for (int n = 0; n < numKeywords; n++) {
              if (!keywords[n].equals("None")) {
                Keyword word = getKeyword(keywords[n]);
                //if (word.isMemberOf(enc.getCatalogNumber() + "/" + imageName)) {
                if(enc.hasKeyword(word)){
                  hasKeyword = true;
                  //System.out.println("member of: "+word.getReadableName());
                }
              } else {
                hasKeyword = true;
              }

            }

          }

          if (hasKeyword && isAcceptableVideoFile(imageName)) {
            //m_thumb="http://"+CommonConfiguration.getURLLocation()+"/images/video.jpg"+"BREAK"+enc.getEncounterNumber()+"BREAK"+imageName;
            //thumbs.add(m_thumb);
          } else if (hasKeyword && isAcceptableImageFile(imageName)) {
            //m_thumb=enc.getEncounterNumber()+"/"+(i+1)+".jpg"+"BREAK"+enc.getEncounterNumber()+"BREAK"+imageName;
            //thumbs.add(m_thumb);
          } else {
            count--;
          }

        }
      } //end while

    }//end while
    return count;
  }


  /**
   * Returns the number of acceptable images/videos in the enter Iterator.
   *
   * @param it The filtered iterator of encounters to count the number of images/videos in.
   * @return The number of acceptable images/videos in the Iterator of encounters.
   */
  public int getNumThumbnails(Vector it) {
    int count = 0;
    int numEncs = it.size();
    for (int f = 0; f < numEncs; f++) {
      Encounter enc = (Encounter) it.get(f);
      for (int i = 0; i < enc.getAdditionalImageNames().size(); i++) {
        count++;

        //check for video or image
        String addTextFile = (String) enc.getAdditionalImageNames().get(i);
        if ((!isAcceptableImageFile(addTextFile)) && (!isAcceptableVideoFile(addTextFile))) {
          count--;
        }
      }
    }//end while
    return count;
  }

  public boolean isAcceptableImageFile(String fileName) {
    if ((fileName.toLowerCase().indexOf(".jpg") != -1) || (fileName.toLowerCase().indexOf(".gif") != -1) || (fileName.toLowerCase().indexOf(".jpeg") != -1) || (fileName.toLowerCase().indexOf(".jpe") != -1) || (fileName.toLowerCase().indexOf(".bmp") != -1) || (fileName.toLowerCase().indexOf(".png") != -1)) {
      return true;
    }
    return false;
  }

  public boolean isAcceptableVideoFile(String fileName) {
    if ((fileName.toLowerCase().indexOf(".mov") != -1) || (fileName.toLowerCase().indexOf(".avi") != -1) || (fileName.toLowerCase().indexOf("mpg") != -1) || (fileName.toLowerCase().indexOf(".wmv") != -1) || (fileName.toLowerCase().indexOf(".mp4") != -1)|| (fileName.toLowerCase().indexOf(".flv") != -1)) {
      return true;
    }
    return false;
  }


  public Adoption getRandomAdoption() {

    //get the random number
    int numAdoptions = getNumAdoptions();
    Random ran = new Random();
    int ranNum = 0;
    if (numAdoptions > 1) {
      ranNum = ran.nextInt(numAdoptions);
    }

    //return the adoption
    int currentPosition = 0;
    Iterator it = getAllAdoptionsNoQuery();
    while (it.hasNext()) {
      Adoption ad = (Adoption) it.next();
      if (currentPosition == ranNum) {
        return ad;
      }
      currentPosition++;
    }
    return null;
  }

  public ArrayList getMarkedIndividualsByAlternateID(String altID) {
    String filter = "this.alternateid.startsWith('" + altID + "')";
    Extent encClass = pm.getExtent(MarkedIndividual.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    Collection c = (Collection) (acceptedEncounters.execute());
    ArrayList al = new ArrayList(c);
    return al;
  }

  public ArrayList getEncountersByAlternateID(String altID) {
    String filter = "this.otherCatalogNumbers.startsWith('" + altID + "')";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    Collection c = (Collection) (acceptedEncounters.execute());
    ArrayList al = new ArrayList(c);
    return al;
  }

  public ArrayList getMarkedIndividualsByNickname(String altID) {
    String filter = "this.nickName.startsWith('" + altID + "')";
    Extent encClass = pm.getExtent(MarkedIndividual.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    Collection c = (Collection) (acceptedEncounters.execute());
    ArrayList al = new ArrayList(c);
    return al;
  }

  public int getEarliestSightingYear() {
    Query q = pm.newQuery("SELECT min(year) FROM org.ecocean.Encounter where year > -1");
    return ((Integer) q.execute()).intValue();
  }

  public int getLastSightingYear() {
    Query q = pm.newQuery("SELECT max(year) FROM org.ecocean.Encounter");
    return ((Integer) q.execute()).intValue();
  }

  public int getLastMonthOfSightingYear(int yearHere) {
    Query q = pm.newQuery("SELECT max(month) FROM org.ecocean.Encounter WHERE this.year == " + yearHere);
    return ((Integer) q.execute()).intValue();
  }

  public ArrayList<String> getAllLocationIDs() {
    Query q = pm.newQuery(Encounter.class);
    q.setResult("distinct locationID");
    q.setOrdering("locationID ascending");
    Collection results = (Collection) q.execute();
    return (new ArrayList(results));
  }

  public ArrayList<String> getAllHaplotypes() {
    Query q = pm.newQuery(MitochondrialDNAAnalysis.class);
    q.setResult("distinct haplotype");
    q.setOrdering("haplotype ascending");
    Collection results = (Collection) q.execute();
    return (new ArrayList(results));
  }

  public ArrayList<String> getAllGeneticSexes() {
    Query q = pm.newQuery(SexAnalysis.class);
    q.setResult("distinct sex");
    q.setOrdering("sex ascending");
    Collection results = (Collection) q.execute();
    return (new ArrayList(results));
  }

  public ArrayList<String> getAllLoci() {
    Query q = pm.newQuery(Locus.class);
    q.setResult("distinct name");
    q.setOrdering("name ascending");
    Collection results = (Collection) q.execute();
    return (new ArrayList(results));
  }

  public ArrayList<String> getAllGenuses() {
      Query q = pm.newQuery(Encounter.class);
      q.setResult("distinct genus");
      q.setOrdering("genus ascending");
      Collection results = (Collection) q.execute();
      return (new ArrayList(results));
  }

  public ArrayList<String> getAllSpecificEpithets() {
      Query q = pm.newQuery(Encounter.class);
      q.setResult("distinct specificEpithet");
      q.setOrdering("specificEpithet ascending");
      Collection results = (Collection) q.execute();
      return (new ArrayList(results));
  }

  public ArrayList<String> getAllBehaviors() {

    Query q = pm.newQuery(Encounter.class);
    q.setResult("distinct behavior");
    q.setOrdering("behavior ascending");
    Collection results = (Collection) q.execute();
    return (new ArrayList(results));


    //temporary way
    /**
     * ArrayList<String> al=new ArrayList<String>();
     Iterator allenc=getAllEncounters();
     while(allenc.hasNext()){
     Encounter enc=(Encounter)allenc.next();
     if((enc.getBehavior()!=null)&&(!enc.getBehavior().equals(""))){
     if(!al.contains(enc.getBehavior())){
     al.add(enc.getBehavior());
     }
     }
     }
     return al;
     */
  }

  public ArrayList<String> getAllVerbatimEventDates() {
    Query q = pm.newQuery(Encounter.class);
    q.setResult("distinct verbatimEventDate");
    q.setOrdering("verbatimEventDate ascending");
    Collection results = (Collection) q.execute();
    return (new ArrayList(results));
  }

  public ArrayList<String> getAllRecordedBy() {
    Query q = pm.newQuery(Encounter.class);
    q.setResult("distinct recordedBy");
    q.setOrdering("recordedBy ascending");
    Collection results = (Collection) q.execute();
    return (new ArrayList(results));
  }

  public ArrayList<Encounter> getEncountersWithHashedEmailAddress(String hashedEmail) {
    String filter = "((this.hashedSubmitterEmail.indexOf('" + hashedEmail + "') != -1)||(this.hashedPhotographerEmail.indexOf('" + hashedEmail + "') != -1)||(this.hashedInformOthers.indexOf('" + hashedEmail + "') != -1))";
    Extent encClass = pm.getExtent(Encounter.class, true);
    Query acceptedEncounters = pm.newQuery(encClass, filter);
    Collection c = (Collection) (acceptedEncounters.execute());
    ArrayList al = new ArrayList(c);
    return al;
  }

  public ArrayList<String> getAllPatterningCodes(){
    Query q = pm.newQuery (Encounter.class);
    q.setResult ("distinct patterningCode");
    q.setOrdering("patterningCode ascending");
    Collection results = (Collection)q.execute ();
    return (new ArrayList(results));
  }

  public ArrayList<String> getAllLifeStages(){
    Query q = pm.newQuery (Encounter.class);
    q.setResult ("distinct lifeStage");
    q.setOrdering("lifeStage ascending");
    Collection results = (Collection)q.execute ();
    return (new ArrayList(results));
  }

} //end Shepherd class

