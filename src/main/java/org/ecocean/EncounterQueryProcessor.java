package org.ecocean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;

import org.ecocean.Util.MeasurementDesc;
import org.ecocean.servlet.ServletUtilities;

public class EncounterQueryProcessor {

  private static final String SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE = "SELECT FROM org.ecocean.Encounter WHERE ";

  public static String queryStringBuilder(HttpServletRequest request, StringBuffer prettyPrint, Map<String, Object> paramMap){
    String filter= SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE;
    String jdoqlVariableDeclaration = "";
    String parameterDeclaration = "";
    Shepherd myShepherd=new Shepherd();

  //filter for location------------------------------------------
    if((request.getParameter("locationField")!=null)&&(!request.getParameter("locationField").equals(""))) {
      String locString=request.getParameter("locationField").toLowerCase().replaceAll("%20", " ").trim();
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){
        filter+="(verbatimLocality.toLowerCase().indexOf('"+locString+"') != -1)";
      }
      else{filter+=" && (verbatimLocality.toLowerCase().indexOf('"+locString+"') != -1)";}
      prettyPrint.append("locationField contains \""+locString+"\".<br />");
    }
    //end location filter--------------------------------------------------------------------------------------

    
    //filter for resighted encounter------------------------------------------
    if(request.getParameter("resightOnly")!=null) {
      //String locString=request.getParameter("locationField").toLowerCase().replaceAll("%20", " ").trim();
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){
        filter+="(individualID != \"Unassigned\")";
      }
      else{filter+=" && (individualID != \"Unassigned\")";}
      prettyPrint.append("Identified and resighted.<br />");
    }
    //end resighted filter--------------------------------------------------------------------------------------


/**    
  //filter for unidentifiable encounters------------------------------------------
    if(request.getParameter("unidentifiable")==null) {
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="!unidentifiable";}
      else{filter+=" && !unidentifiable";}
      prettyPrint.append("Not identifiable.<br />");
    }
    //-----------------------------------------------------

    //---filter out approved
    if((request.getParameter("approved")==null)&&(request.getParameter("unapproved")!=null)) {
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="!approved";}
      else{filter+=" && !approved";}
      prettyPrint.append("Not approved.<br />");
    }
    //----------------------------

    //---filter out unapproved
    if((request.getParameter("unapproved")==null)&&(request.getParameter("approved")!=null)) {
      
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="approved";}
      else{filter+=" && approved";}
      prettyPrint.append("Not unapproved.<br />");
    }
    //----------------------------

    //---filter out unapproved and unapproved, leaving only unidentifiable
    if((request.getParameter("unapproved")==null)&&(request.getParameter("approved")==null)&&(request.getParameter("unidentifiable")!=null)) {
      
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="unidentifiable";}
      else{filter+=" && unidentifiable";}
      prettyPrint.append("Not unapproved.<br />");
    }
    //----------------------------

*/

    //------------------------------------------------------------------
    //locationID filters-------------------------------------------------
    String[] locCodes=request.getParameterValues("locationCodeField");
    if((locCodes!=null)&&(!locCodes[0].equals("None"))){
          prettyPrint.append("locationCodeField is one of the following: ");
          int kwLength=locCodes.length;
            String locIDFilter="(";
            for(int kwIter=0;kwIter<kwLength;kwIter++) {
              String kwParam=locCodes[kwIter].replaceAll("%20", " ").trim();
              if(!kwParam.equals("")){
                if(locIDFilter.equals("(")){
                  locIDFilter+=" locationID == \""+kwParam+"\"";
                }
                else{
                  locIDFilter+=" || locationID == \""+kwParam+"\"";
                }
                prettyPrint.append(kwParam+" ");
              }
            }
            locIDFilter+=" )";
            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=locIDFilter;}
            else{filter+=(" && "+locIDFilter);}
            prettyPrint.append("<br />");
    }
    //end locationID filters-----------------------------------------------

    
    //------------------------------------------------------------------
    //state filters-------------------------------------------------
    String[] states=request.getParameterValues("state");
    if((states!=null)&&(!states[0].equals("None"))){
          prettyPrint.append("State is one of the following: ");
          int kwLength=states.length;
            String locIDFilter="(";
            for(int kwIter=0;kwIter<kwLength;kwIter++) {
              String kwParam=states[kwIter].replaceAll("%20", " ").trim();
              if(!kwParam.equals("")){
                if(locIDFilter.equals("(")){
                  locIDFilter+=" state == \""+kwParam+"\"";
                }
                else{
                  locIDFilter+=" || state == \""+kwParam+"\"";
                }
                prettyPrint.append(kwParam+" ");
              }
            }
            locIDFilter+=" )";
            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=locIDFilter;}
            else{filter+=(" && "+locIDFilter);}
            prettyPrint.append("<br />");
    }
    //end locationID filters-----------------------------------------------

    
    
  //------------------------------------------------------------------
    //patterningCode filters-------------------------------------------------
    String[] patterningCodes=request.getParameterValues("patterningCode");
    if((patterningCodes!=null)&&(!patterningCodes[0].equals("None"))){
          prettyPrint.append("Color code is one of the following: ");
          int kwLength=patterningCodes.length;
            String patterningCodeFilter="(";
            for(int kwIter=0;kwIter<kwLength;kwIter++) {

              String kwParam=patterningCodes[kwIter].replaceAll("%20", " ").trim();
              if(!kwParam.equals("")){
                if(patterningCodeFilter.equals("(")){
                  patterningCodeFilter+=" patterningCode == \""+kwParam+"\"";
                }
                else{

                  patterningCodeFilter+=" || patterningCode == \""+kwParam+"\"";
                }
                prettyPrint.append(kwParam+" ");
              }
            }
            patterningCodeFilter+=" )";


            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=patterningCodeFilter;}
            else{filter+=(" && "+patterningCodeFilter);}

            prettyPrint.append("<br />");
    }
    //end patterningCode filters-----------------------------------------------

    //------------------------------------------------------------------
    //behavior filters-------------------------------------------------
    String[] behaviors=request.getParameterValues("behaviorField");
    if((behaviors!=null)&&(!behaviors[0].equals("None"))){
          prettyPrint.append("behaviorField is one of the following: ");
          int kwLength=behaviors.length;
            String locIDFilter="(";
            for(int kwIter=0;kwIter<kwLength;kwIter++) {
              String kwParam=behaviors[kwIter].replaceAll("%20", " ").trim();
              if(!kwParam.equals("")){
                if(locIDFilter.equals("(")){
                  locIDFilter+=" behavior == \""+kwParam+"\"";
                }
                else{
                  locIDFilter+=" || behavior == \""+kwParam+"\"";
                }
                prettyPrint.append(kwParam+" ");
              }
            }
            locIDFilter+=" )";
            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=locIDFilter;}
            else{filter+=(" && "+locIDFilter);}
            prettyPrint.append("<br />");
    }
    //end behavior filters-----------------------------------------------
    //------------------------------------------------------------------
    
    //Tag Filters--------------------------------------------------------
    
    StringBuilder metalTagFilter = new StringBuilder();
    Enumeration<String> parameterNames = request.getParameterNames();
    int metalTagsInQuery = 0;
    while (parameterNames.hasMoreElements()) {
      String parameterName = parameterNames.nextElement();
      final String metalTagPrefix = "metalTag(";
      if (parameterName.startsWith(metalTagPrefix)) {
        String metalTagLocation = parameterName.substring(metalTagPrefix.length(), parameterName.lastIndexOf(')'));
        String value = request.getParameter(parameterName);
        if (value != null && value.trim().length() > 0) {
          prettyPrint.append("metal tag ");
          prettyPrint.append(metalTagLocation);
          prettyPrint.append(" is ");
          prettyPrint.append(value);
          prettyPrint.append("<br/>");
          String metalTagVar = "metalTag" + metalTagsInQuery++;
          metalTagFilter.append("(metalTags.contains(" + metalTagVar + ") && ");
          metalTagFilter.append(metalTagVar + ".location == " + Util.quote(metalTagLocation));
          String jdoParam = "tagNumber" + metalTagsInQuery;
          metalTagFilter.append(" && " + metalTagVar + ".tagNumber == " + jdoParam + ")");
          paramMap.put(jdoParam, value);
          parameterDeclaration = updateParametersDeclaration(parameterDeclaration, "String " + jdoParam);
        }
      }
    }
    if (metalTagFilter.length() > 0) {
      if (!filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)) {
        filter += " && ";
      }
      filter += metalTagFilter.toString();
      for (int i = 0; i < metalTagsInQuery; i++) {
        updateJdoqlVariableDeclaration(jdoqlVariableDeclaration, "org.ecocean.tag.MetalTag metalTag" + i);
      }
    }

    // We don't do metal tags (above) in processTagFilters because of the dependency on jdoqlVariableDeclaration
    String tagFilters = processTagFilters(request, prettyPrint);
    if (tagFilters.length() > 0) {
      if (!filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)) {
        filter += " && ";
      }
      filter += tagFilters.toString();
    }
    //end tag filters----------------------------------------------------
   
    //lifeStage filters-------------------------------------------------
    String[] stages=request.getParameterValues("lifeStageField");
    if((stages!=null)&&(!stages[0].equals("None"))&&(!stages[0].equals(""))){
          prettyPrint.append("lifeStage is one of the following: ");
          int kwLength=stages.length;
            String stageFilter="(";
            for(int kwIter=0;kwIter<kwLength;kwIter++) {
              String kwParam=stages[kwIter].replaceAll("%20", " ").trim();
              if(!kwParam.equals("")){
                if(stageFilter.equals("(")){
                  stageFilter+=" lifeStage == \""+kwParam+"\"";
                }
                else{
                  stageFilter+=" || lifeStage == \""+kwParam+"\"";
                }
                prettyPrint.append(kwParam+" ");
              }
            }
            stageFilter+=" ) ";
            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=stageFilter;}
            else{filter+=(" && "+stageFilter);}
            prettyPrint.append("<br />");
    }
    // Measurement filters-----------------------------------------------
    List<MeasurementDesc> measurementDescs = Util.findMeasurementDescs("us");
    String measurementPrefix = "measurement";
    StringBuilder measurementFilter = new StringBuilder(); //"( collectedData.contains(measurement) && (");
    boolean atLeastOneMeasurement = false;
    int measurementsInQuery = 0;
    for (MeasurementDesc measurementDesc : measurementDescs) {
      String valueParamName= measurementPrefix + measurementDesc.getType() + "(value)";
      String value = request.getParameter(valueParamName);
      if (value != null) {
        value = value.trim();
        if ( value.length() > 0) {
          String operatorParamName = measurementPrefix + measurementDesc.getType() + "(operator)";
          String operatorParamValue = request.getParameter(operatorParamName);
          if (operatorParamValue == null) {
            operatorParamValue = "";
          }
          String operator = null;
          if ("gt".equals(operatorParamValue)) {
            operator = ">";
          }
          else if ( "lt".equals(operatorParamValue)) {
            operator = "<";
          }
          else if ("eq".equals(operatorParamValue)) {
            operator = "==";
          }
          if (operator != null) {
            prettyPrint.append(measurementDesc.getUnitsLabel());
            prettyPrint.append(" is ");
            prettyPrint.append(operator);
            prettyPrint.append(value);
            prettyPrint.append("<br/>");
            if (atLeastOneMeasurement) {
              measurementFilter.append("&&");
            }
            String measurementVar = "measurement" + measurementsInQuery++;
            measurementFilter.append("(measurements.contains(" + measurementVar + ") && ");
            measurementFilter.append( measurementVar + ".value " + operator + " " + value);
            measurementFilter.append(" && " + measurementVar + ".type == ");
            measurementFilter.append("\"" + measurementDesc.getType() + "\")");
            atLeastOneMeasurement = true;
          }
        }
      }
    }
    if (atLeastOneMeasurement) {
      if(jdoqlVariableDeclaration.length() > 0){
        jdoqlVariableDeclaration += ";";
      }
      else {
        jdoqlVariableDeclaration=" VARIABLES ";
      }
      for (int i = 0; i < measurementsInQuery; i++) {
        if (i > 0) {
          jdoqlVariableDeclaration += "; ";
        }
        jdoqlVariableDeclaration += " org.ecocean.Measurement measurement" + i;
      }
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){
        filter+= measurementFilter.toString();
      }
      else{
        filter+=(" && "+ measurementFilter.toString());
      }
    }
    // end measurement filters
    //end behavior filters-----------------------------------------------






    //------------------------------------------------------------------
    //verbatimEventDate filters-------------------------------------------------
    String[] verbatimEventDates=request.getParameterValues("verbatimEventDateField");
    if((verbatimEventDates!=null)&&(!verbatimEventDates[0].equals("None"))){
          prettyPrint.append("verbatimEventDateField is one of the following: ");
          int kwLength=verbatimEventDates.length;
            String locIDFilter="(";
            for(int kwIter=0;kwIter<kwLength;kwIter++) {

              String kwParam=verbatimEventDates[kwIter].replaceAll("%20", " ").trim();
              if(!kwParam.equals("")){
                if(locIDFilter.equals("(")){
                  locIDFilter+=" verbatimEventDate == \""+kwParam+"\"";
                }
                else{
                  locIDFilter+=" || verbatimEventDate == \""+kwParam+"\"";
                }
                prettyPrint.append(kwParam+" ");
              }
            }
            locIDFilter+=" )";
            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=locIDFilter;}
            else{filter+=(" && "+locIDFilter);}
            prettyPrint.append("<br />");
    }
    //end verbatimEventDate filters-----------------------------------------------

    //------------------------------------------------------------------
    //hasTissueSample filters-------------------------------------------------
    if(request.getParameter("hasTissueSample")!=null){
          prettyPrint.append("Has tissue sample.");

            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="tissueSamples.contains(dce)";}
            else if (filter.indexOf("tissueSamples.contains(dce)")==-1){filter+=(" && tissueSamples.contains(dce) ");}

            prettyPrint.append("<br />");
            if(jdoqlVariableDeclaration.equals("")){jdoqlVariableDeclaration=" VARIABLES org.ecocean.genetics.TissueSample dce";}
            else if(!jdoqlVariableDeclaration.contains("org.ecocean.genetics.TissueSample dce")){jdoqlVariableDeclaration+=";org.ecocean.genetics.TissueSample dce";}
            
    }
    //end hasTissueSample filters-----------------------------------------------

    //------------------------------------------------------------------
    //hasPhoto filters-------------------------------------------------
    if(request.getParameter("hasPhoto")!=null){
          prettyPrint.append("Has at least one photo.");

            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="images.contains(dce15)";}
            else if (filter.indexOf("images.contains(dce15)")==-1){filter+=(" && images.contains(dce15) ");}

            prettyPrint.append("<br />");
            if(jdoqlVariableDeclaration.equals("")){jdoqlVariableDeclaration=" VARIABLES org.ecocean.SinglePhotoVideo dce15";}
            else if(!jdoqlVariableDeclaration.contains("org.ecocean.SinglePhotoVideo dce15")){jdoqlVariableDeclaration+=";org.ecocean.SinglePhotoVideo dce15";}
            
    }
    //end hasPhoto filters-----------------------------------------------

    
    
    //------------------------------------------------------------------
    //keyword filters-------------------------------------------------
    String[] keywords=request.getParameterValues("keyword");
    if((keywords!=null)&&(!keywords[0].equals("None"))){
          prettyPrint.append("Photo/video keyword is one of the following: ");
          int kwLength=keywords.length;
            String locIDFilter="(";
            for(int kwIter=0;kwIter<kwLength;kwIter++) {

              String kwParam=keywords[kwIter].replaceAll("%20", " ").trim();
              if(!kwParam.equals("")){
                if(locIDFilter.equals("(")){
                  locIDFilter+=" word.indexname == \""+kwParam+"\" ";
                }
                else{
                  locIDFilter+=" || word.indexname == \""+kwParam+"\" ";
                }
                prettyPrint.append(kwParam+" ");
              }
            }
            locIDFilter+=" )";
            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="images.contains(photo) && photo.keywords.contains(word) && "+locIDFilter;}
            else{
              if(filter.indexOf("images.contains(photo)")==-1){filter+=" && images.contains(photo)";}
             
              if(filter.indexOf("photo.keywords.contains(word)")==-1){filter+=" && photo.keywords.contains(word)";}
              filter+=(" && "+locIDFilter);
            }

            prettyPrint.append("<br />");
            if(jdoqlVariableDeclaration.equals("")){jdoqlVariableDeclaration=" VARIABLES org.ecocean.SinglePhotoVideo photo;org.ecocean.Keyword word";}
            else{ 
              if(!jdoqlVariableDeclaration.contains("org.ecocean.SinglePhotoVideo photo")){jdoqlVariableDeclaration+=";org.ecocean.SinglePhotoVideo photo";}
              if(!jdoqlVariableDeclaration.contains("org.ecocean.Keyword word")){jdoqlVariableDeclaration+=";org.ecocean.Keyword word";}
            }
         
      }
  
    //end photo keyword filters-----------------------------------------------

    
    
    //------------------------------------------------------------------
    //ms markers filters-------------------------------------------------
      myShepherd.beginDBTransaction();  
      ArrayList<String> markers=myShepherd.getAllLoci();
        int numMarkers=markers.size();
        String theseMarkers="";
        boolean hasMarkers=false;
        for(int h=0;h<numMarkers;h++){
          
            String marker=markers.get(h);
            if(request.getParameter(marker)!=null){
              hasMarkers=true;
              String locIDFilter="(";
              locIDFilter+=" "+marker+".name == \""+marker+"\" ";
              locIDFilter+=" )";

              
              int alleleNum=0;
              boolean hasMoreAlleles=true;
              while(hasMoreAlleles){
                
                if(request.getParameter((marker+"_alleleValue"+alleleNum))!=null){
                  try{
                    Integer thisInt=new Integer(request.getParameter((marker+"_alleleValue"+alleleNum)));
                    Integer relaxValue=new Integer(request.getParameter("alleleRelaxValue"));
                    Integer upperValue=thisInt+relaxValue;
                    Integer lowerValue=thisInt-relaxValue;
                    locIDFilter+=(" && ("+marker+".allele"+alleleNum+" >= "+lowerValue+")"+" && ("+marker+".allele"+alleleNum+" <= "+upperValue+")");
                    
                  }
                  catch(Exception e){
                    hasMoreAlleles=false;
                  }
                }
                else{
                  hasMoreAlleles=false;
                }
                alleleNum++;
              }
              
              
              theseMarkers+=(marker+" ");
            
        
              if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="tissueSamples.contains(dce) && dce.analyses.contains(msanalysis) && msanalysis.loci.contains("+marker+") && "+locIDFilter;}
              else{
                if(filter.indexOf("tissueSamples.contains(dce)")==-1){filter+=" && tissueSamples.contains(dce)";}
                if(filter.indexOf("dce.analyses.contains(analysis)")==-1){filter+=" && dce.analyses.contains(msanalysis)";}
                if(filter.indexOf("msanalysis.loci.contains("+marker+")")==-1){filter+=" && msanalysis.loci.contains("+marker+")";}
              
                filter+=(" && "+locIDFilter);
              }

            
              if(jdoqlVariableDeclaration.equals("")){jdoqlVariableDeclaration=" VARIABLES org.ecocean.genetics.TissueSample dce;org.ecocean.genetics.MicrosatelliteMarkersAnalysis msanalysis;org.ecocean.genetics.Locus "+marker;}
              else{ 
                if(!jdoqlVariableDeclaration.contains("org.ecocean.genetics.TissueSample dce")){jdoqlVariableDeclaration+=";org.ecocean.genetics.TissueSample dce";}
                if(!jdoqlVariableDeclaration.contains("org.ecocean.genetics.MitochondrialDNAAnalysis analysis")){jdoqlVariableDeclaration+=";org.ecocean.genetics.MicrosatelliteMarkersAnalysis msanalysis";}
                if(!jdoqlVariableDeclaration.contains("org.ecocean.genetics.Locus "+marker)){jdoqlVariableDeclaration+=";org.ecocean.genetics.Locus "+marker;}
              
              }
            }
        }
        if(hasMarkers){
          prettyPrint.append("Microsatellite marker is one of the following: ");
          theseMarkers+="<br />";
          prettyPrint.append(theseMarkers);
        }
        myShepherd.rollbackDBTransaction();
        myShepherd.closeDBTransaction();
    //end ms markers filters-----------------------------------------------

    

    //filter for alternate ID------------------------------------------
    if((request.getParameter("alternateIDField")!=null)&&(!request.getParameter("alternateIDField").equals(""))) {
      String altID=request.getParameter("alternateIDField").replaceAll("%20", " ").trim();
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="otherCatalogNumbers.startsWith('"+altID+"')";}
      else{filter+=" && otherCatalogNumbers.startsWith('"+altID+"')";}
      prettyPrint.append("alternateIDField starts with \""+altID+"\".<br />");
    }
    
    //------------------------------------------------------------------
    //haplotype filters-------------------------------------------------
    String[] haplotypes=request.getParameterValues("haplotypeField");
    if((haplotypes!=null)&&(!haplotypes[0].equals("None"))){
          prettyPrint.append("Haplotype is one of the following: ");
          int kwLength=haplotypes.length;
            String locIDFilter="(";
            for(int kwIter=0;kwIter<kwLength;kwIter++) {

              String kwParam=haplotypes[kwIter].replaceAll("%20", " ").trim();
              if(!kwParam.equals("")){
                if(locIDFilter.equals("(")){
                  locIDFilter+=" analysis.haplotype == \""+kwParam+"\" ";
                }
                else{
                  locIDFilter+=" || analysis.haplotype == \""+kwParam+"\" ";
                }
                prettyPrint.append(kwParam+" ");
              }
            }
            locIDFilter+=" )";
            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="tissueSamples.contains(dce) && dce.analyses.contains(analysis) && "+locIDFilter;}
            else{
              if(filter.indexOf("tissueSamples.contains(dce)")==-1){filter+=" && tissueSamples.contains(dce)";}
             
              if(filter.indexOf("dce.analyses.contains(analysis)")==-1){filter+=" && dce.analyses.contains(analysis)";}
              filter+=(" && "+locIDFilter);
            }

            prettyPrint.append("<br />");
            if(jdoqlVariableDeclaration.equals("")){jdoqlVariableDeclaration=" VARIABLES org.ecocean.genetics.TissueSample dce;org.ecocean.genetics.MitochondrialDNAAnalysis analysis";}
            else{ 
              if(!jdoqlVariableDeclaration.contains("org.ecocean.genetics.TissueSample dce")){jdoqlVariableDeclaration+=";org.ecocean.genetics.TissueSample dce";}
              if(!jdoqlVariableDeclaration.contains("org.ecocean.genetics.MitochondrialDNAAnalysis analysis")){jdoqlVariableDeclaration+=";org.ecocean.genetics.MitochondrialDNAAnalysis analysis";}
              
            }
         
      }
  
    //end haplotype filters-----------------------------------------------

    
    //------------------------------------------------------------------
    //genetic sex filters-------------------------------------------------
    String[] genSexes=request.getParameterValues("geneticSexField");
    if((genSexes!=null)&&(!genSexes[0].equals("None"))){
          prettyPrint.append("Genetic determination of sex is one of the following: ");
          int kwLength=genSexes.length;
            String locIDFilter="(";
            for(int kwIter=0;kwIter<kwLength;kwIter++) {

              String kwParam=genSexes[kwIter].replaceAll("%20", " ").trim();
              if(!kwParam.equals("")){
                if(locIDFilter.equals("(")){
                  locIDFilter+=" sexanalysis.sex == \""+kwParam+"\" ";
                }
                else{
                  locIDFilter+=" || sexanalysis.sex == \""+kwParam+"\" ";
                }
                prettyPrint.append(kwParam+" ");
              }
            }
            locIDFilter+=" )";
            if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="tissueSamples.contains(dce9) && dce9.analyses.contains(sexanalysis) && "+locIDFilter;}
            else{
              if(filter.indexOf("tissueSamples.contains(dce9)")==-1){filter+=" && tissueSamples.contains(dce9)";}
             
              if(filter.indexOf("dce9.analyses.contains(sexanalysis)")==-1){filter+=" && dce9.analyses.contains(sexanalysis)";}
              filter+=(" && "+locIDFilter);
            }

            prettyPrint.append("<br />");
            if(jdoqlVariableDeclaration.equals("")){jdoqlVariableDeclaration=" VARIABLES org.ecocean.genetics.TissueSample dce9;org.ecocean.genetics.SexAnalysis sexanalysis";}
            else{ 
              if(!jdoqlVariableDeclaration.contains("org.ecocean.genetics.TissueSample dce9")){jdoqlVariableDeclaration+=";org.ecocean.genetics.TissueSample dce9";}
              if(!jdoqlVariableDeclaration.contains("org.ecocean.genetics.SexAnalysis analysis")){jdoqlVariableDeclaration+=";org.ecocean.genetics.SexAnalysis sexanalysis";}
              
            }
         
      }
  
    //end genetic sex filters-----------------------------------------------



    //filter for genus------------------------------------------
	    if((request.getParameter("genusField")!=null)&&(!request.getParameter("genusField").equals(""))) {
	      String genusSpecies=request.getParameter("genusField").replaceAll("%20", " ").trim();
	      String genus="";
		  String specificEpithet = "";

		  //now we have to break apart genus species
		  StringTokenizer tokenizer=new StringTokenizer(genusSpecies," ");
		  		if(tokenizer.countTokens()==2){

					genus=tokenizer.nextToken();
					specificEpithet=tokenizer.nextToken();

					if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="genus == '"+genus+"' ";}
					else{filter+=" && genus == '"+genus+"' ";}

					filter+=" && specificEpithet == '"+specificEpithet+"' ";

	      			prettyPrint.append("genus and species are \""+genusSpecies+"\".<br />");

		        }

    }



    //filter for identificationRemarks------------------------------------------
    if((request.getParameter("identificationRemarksField")!=null)&&(!request.getParameter("identificationRemarksField").equals(""))) {
      String idRemarks=request.getParameter("identificationRemarksField").trim();
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="identificationRemarks.startsWith('"+idRemarks+"')";}
      else{filter+=" && identificationRemarks.startsWith('"+idRemarks+"')";}
      prettyPrint.append("identificationRemarks starts with \""+idRemarks+"\".<br />");

    }

    /**
    //filter for behavior------------------------------------------
    if((request.getParameter("behaviorField")!=null)&&(!request.getParameter("behaviorField").equals(""))) {
      String behString=request.getParameter("behaviorField").toLowerCase().replaceAll("%20", " ").trim();
      if(filter.equals("")){filter="behavior.toLowerCase().indexOf('"+behString+"') != -1";}
      else{filter+=" && behavior.toLowerCase().indexOf('"+behString+"') != -1";}
      prettyPrint.append("behaviorField contains \""+behString+"\".<br />");
    }
    //end behavior filter--------------------------------------------------------------------------------------
     */

    //filter by alive/dead status------------------------------------------
    if(request.getParameter("alive")==null) {
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="!livingStatus.startsWith('alive')";}
      else{filter+=" && !livingStatus.startsWith('alive')";}
      prettyPrint.append("Alive.<br />");
    }
    if(request.getParameter("dead")==null) {
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+="!livingStatus.startsWith('dead')";}
      else{filter+=" && !livingStatus.startsWith('dead')";}
      prettyPrint.append("Dead.<br />");
    }
    //filter by alive/dead status--------------------------------------------------------------------------------------

    //submitter or photographer name filter------------------------------------------
    if((request.getParameter("nameField")!=null)&&(!request.getParameter("nameField").equals(""))) {
      String nameString=request.getParameter("nameField").replaceAll("%20"," ").toLowerCase().trim();
      String filterString="((recordedBy.toLowerCase().indexOf('"+nameString+"') != -1)||(submitterEmail.toLowerCase().indexOf('"+nameString+"') != -1)||(photographerName.toLowerCase().indexOf('"+nameString+"') != -1)||(photographerEmail.toLowerCase().indexOf('"+nameString+"') != -1))";
      if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=filterString;}
      else{filter+=(" && "+filterString);}
      prettyPrint.append("nameField contains: \""+nameString+"\"<br />");
    }
    //end name and email filter--------------------------------------------------------------------------------------




/*
This code is no longer necessary with Charles Overbeck's new multi-measurement feature.

    //filter for length------------------------------------------
    if((request.getParameter("selectLength")!=null)&&(request.getParameter("lengthField")!=null)&&(!request.getParameter("lengthField").equals("skip"))&&(!request.getParameter("selectLength").equals(""))) {

      String size=request.getParameter("lengthField").trim();

      if(request.getParameter("selectLength").equals("gt")) {
        String filterString="size > "+size;
        if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=filterString;}
        else{filter+=(" && "+filterString);}
        prettyPrint.append("selectLength is > "+size+".<br />");
      }
      else if(request.getParameter("selectLength").equals("lt")) {
        String filterString="size < "+size;
        if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=filterString;}
        else{filter+=(" && "+filterString);}
        prettyPrint.append("selectLength is < "+size+".<br />");
      }
      else if(request.getParameter("selectLength").equals("eq")) {
        String filterString="size == "+size;
        if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){filter+=filterString;}
        else{filter+=(" && "+filterString);}
        prettyPrint.append("selectLength is = "+size+".<br />");
      }
    }
*/


    //start date filter----------------------------
    if((request.getParameter("day1")!=null)&&(request.getParameter("month1")!=null)&&(request.getParameter("year1")!=null)&&(request.getParameter("day2")!=null)&&(request.getParameter("month2")!=null)&&(request.getParameter("year2")!=null)) {
      try{

    //get our date values
    int day1=(new Integer(request.getParameter("day1"))).intValue();
    int day2=(new Integer(request.getParameter("day2"))).intValue();
    int month1=(new Integer(request.getParameter("month1"))).intValue();
    int month2=(new Integer(request.getParameter("month2"))).intValue();
    int year1=(new Integer(request.getParameter("year1"))).intValue();
    int year2=(new Integer(request.getParameter("year2"))).intValue();

    prettyPrint.append("Dates between: "+year1+"-"+month1+"-"+day1+" and "+year2+"-"+month2+"-"+day2+"<br />");

    //order our values
    int minYear=year1;
    int minMonth=month1;
    int minDay=day1;
    int maxYear=year2;
    int maxMonth=month2;
    int maxDay=day2;
    if(year1>year2) {
      minDay=day2;
      minMonth=month2;
      minYear=year2;
      maxDay=day1;
      maxMonth=month1;
      maxYear=year1;
    }
    else if(year1==year2) {
      if(month1>month2) {
        minDay=day2;
        minMonth=month2;
        minYear=year2;
        maxDay=day1;
        maxMonth=month1;
        maxYear=year1;
      }
      else if(month1==month2) {
        if(day1>day2) {
          minDay=day2;
          minMonth=month2;
          minYear=year2;
          maxDay=day1;
          maxMonth=month1;
          maxYear=year1;
        }
      }
    }

    GregorianCalendar gcMin=new GregorianCalendar(minYear, (minMonth-1), minDay, 0, 0);
    GregorianCalendar gcMax=new GregorianCalendar(maxYear, (maxMonth-1), maxDay, 23, 59);

    if(filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)){
      filter+="((dateInMilliseconds >= "+gcMin.getTimeInMillis()+") && (dateInMilliseconds <= "+gcMax.getTimeInMillis()+"))";
    }
    else{filter+=" && ((dateInMilliseconds >= "+gcMin.getTimeInMillis()+") && (dateInMilliseconds <= "+gcMax.getTimeInMillis()+"))";
    }

      } catch(NumberFormatException nfe) {
    //do nothing, just skip on
    nfe.printStackTrace();
      }
    }
    
    String releaseDateFromStr = request.getParameter("releaseDateFrom");
    String releaseDateToStr = request.getParameter("releaseDateTo");
    String pattern = CommonConfiguration.getProperty("releaseDateFormat");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    if (releaseDateFromStr != null && releaseDateFromStr.trim().length() > 0) {
      try {
        Date releaseDateFrom = simpleDateFormat.parse(releaseDateFromStr);
        if (!filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)) {
          filter += " && ";
        }
        filter += "(releaseDate >= releaseDateFrom)";
        parameterDeclaration = updateParametersDeclaration(parameterDeclaration, "java.util.Date releaseDateFrom");
        paramMap.put("releaseDateFrom", releaseDateFrom);
        prettyPrint.append("release date >= " + simpleDateFormat.format(releaseDateFrom));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (releaseDateToStr != null && releaseDateToStr.trim().length() > 0) {
      try {
        Date releaseDateTo = simpleDateFormat.parse(releaseDateToStr);
        if (!filter.equals(SELECT_FROM_ORG_ECOCEAN_ENCOUNTER_WHERE)) {
          filter += " && ";
        }
        filter += "(releaseDate <= releaseDateTo)";
        parameterDeclaration = updateParametersDeclaration(parameterDeclaration, "java.util.Date releaseDateTo");
        paramMap.put("releaseDateTo", releaseDateTo);
        prettyPrint.append("releaseDate <= " + simpleDateFormat.format(releaseDateTo));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


    

    //end date filter ----------------------------------------

    //------------------------------------------------------------------
    //GPS filters-------------------------------------------------

    if((request.getParameter("ne_lat")!=null)&&(!request.getParameter("ne_lat").equals(""))) {
      if((request.getParameter("ne_long")!=null)&&(!request.getParameter("ne_long").equals(""))) {
        if((request.getParameter("sw_lat")!=null)&&(!request.getParameter("sw_lat").equals(""))) {
          if((request.getParameter("sw_long")!=null)&&(!request.getParameter("sw_long").equals(""))) {




                try{

                  String thisLocalFilter="(";

                  double ne_lat=(new Double(request.getParameter("ne_lat"))).doubleValue();
                  double ne_long = (new Double(request.getParameter("ne_long"))).doubleValue();
                  double sw_lat = (new Double(request.getParameter("sw_lat"))).doubleValue();
                  double sw_long=(new Double(request.getParameter("sw_long"))).doubleValue();

                  if((sw_long>0)&&(ne_long<0)){
                    //if(!((encLat<=ne_lat)&&(encLat>=sw_lat)&&((encLong<=ne_long)||(encLong>=sw_long)))){

                      //process lats
                      thisLocalFilter+="(decimalLatitude <= "+request.getParameter("ne_lat")+") && (decimalLatitude >= "+request.getParameter("sw_lat")+")";

                      //process longs
                      thisLocalFilter+=" && ((decimalLongitude <= "+request.getParameter("ne_long")+") || (decimalLongitude >= "+request.getParameter("sw_long")+"))";



                    //}
                  }
                  else{
                    //if(!((encLat<=ne_lat)&&(encLat>=sw_lat)&&(encLong<=ne_long)&&(encLong>=sw_long))){

                    //process lats
                    thisLocalFilter+="(decimalLatitude <= "+request.getParameter("ne_lat")+") && (decimalLatitude >= "+request.getParameter("sw_lat")+")";

                    //process longs
                    thisLocalFilter+=" && (decimalLongitude <= "+request.getParameter("ne_long")+") && (decimalLongitude >= "+request.getParameter("sw_long")+")";



                    //}
                  }

                  thisLocalFilter+=" )";
                  if(filter.equals("")){filter=thisLocalFilter;}
                  else{filter+=" && "+thisLocalFilter;}

                  prettyPrint.append("GPS Boundary NE: \""+request.getParameter("ne_lat")+", "+request.getParameter("ne_long")+"\".<br />");
                  prettyPrint.append("GPS Boundary SW: \""+request.getParameter("sw_lat")+", "+request.getParameter("sw_long")+"\".<br />");



                }

                catch(Exception ee){

                  System.out.println("Exception when trying to process lat and long data in EncounterQueryProcessor!");
                  ee.printStackTrace();

                }








          }
        }
      }
    }


    //end GPS filters-----------------------------------------------

    filter+=jdoqlVariableDeclaration;
    
    filter += parameterDeclaration;

    return filter;

  }

  public static EncounterQueryResult processQuery(Shepherd myShepherd, HttpServletRequest request, String order){

    Vector<Encounter> rEncounters=new Vector<Encounter>();
    Iterator<Encounter> allEncounters;


    //Extent<Encounter> encClass=myShepherd.getPM().getExtent(Encounter.class, true);
    //Query query=myShepherd.getPM().newQuery(encClass);
    //if(!order.equals("")){query.setOrdering(order);}


    String filter="";
    StringBuffer prettyPrint=new StringBuffer("");
    
    Map<String,Object> paramMap = new HashMap<String, Object>();

    filter=queryStringBuilder(request, prettyPrint, paramMap);

    Query query=myShepherd.getPM().newQuery(filter);

    if(!filter.trim().equals("")){
        //filter="("+filter+")";
        //query.setFilter(filter);
        allEncounters=myShepherd.getAllEncounters(query, paramMap);
    }
    else{
      allEncounters=myShepherd.getAllEncountersNoFilter();
    }



    System.out.println("Final filter: "+filter);
    //allEncounters=myShepherd.getAllEncountersNoQuery();

    if(allEncounters!=null){
      while (allEncounters.hasNext()) {
        Encounter temp_enc=(Encounter)allEncounters.next();
        rEncounters.add(temp_enc);
      }
    }




  //filter for encounters of MarkedIndividuals that have been resighted------------------------------------------
    if((request.getParameter("resightOnly")!=null)&&(request.getParameter("numResights")!=null)) {
      int numResights=1;

      try{
        numResights=(new Integer(request.getParameter("numResights"))).intValue();
        prettyPrint.append("numResights is > "+numResights+".<br />");
        }
      catch(NumberFormatException nfe) {nfe.printStackTrace();}

      for(int q=0;q<rEncounters.size();q++) {
        Encounter rEnc=(Encounter)rEncounters.get(q);
        //if(rEnc.isAssignedToMarkedIndividual().equals("Unassigned")){
          //rEncounters.remove(q);
          //q--;
        //}
        //else{
          MarkedIndividual s=myShepherd.getMarkedIndividual(rEnc.isAssignedToMarkedIndividual());
          if(s.totalEncounters()<numResights) {
            rEncounters.remove(q);
            q--;
          }
        //}
      }
    }
  //end if resightOnly--------------------------------------------------------------------------------------



    /**
  //filter for vessel------------------------------------------
  if((request.getParameter("vesselField")!=null)&&(!request.getParameter("vesselField").equals(""))) {
    String vesString=request.getParameter("vesselField");
    for(int q=0;q<rEncounters.size();q++) {
        Encounter rEnc=(Encounter)rEncounters.get(q);
        if((rEnc.getDynamicPropertyValue("Vessel")==null)||(rEnc.getDynamicPropertyValue("Vessel").toLowerCase().indexOf(vesString.toLowerCase())==-1)){
          rEncounters.remove(q);
          q--;
          }
      }
      prettyPrint.append("vesselField is "+vesString+".<br />");
  }
  //end vessel filter--------------------------------------------------------------------------------------
*/


/*
  //keyword filters-------------------------------------------------
  String[] keywords=request.getParameterValues("keyword");
  if((keywords!=null)&&(!keywords[0].equals("None"))){

      prettyPrint.append("Keywords: ");
      int kwLength=keywords.length;
      for(int y=0;y<kwLength;y++){
        String kwParam=keywords[y];
        prettyPrint.append(kwParam+" ");
      }

      for(int q=0;q<rEncounters.size();q++) {
          Encounter tShark=(Encounter)rEncounters.get(q);
          boolean hasNeededKeyword=false;
          for(int kwIter=0;kwIter<kwLength;kwIter++) {
            String kwParam=keywords[kwIter];
            if(myShepherd.isKeyword(kwParam)) {
              Keyword word=myShepherd.getKeyword(kwParam);
              //if(word.isMemberOf(tShark)) {
                hasNeededKeyword=true;

              }
            } //end if isKeyword
          }
          if(!hasNeededKeyword){
            rEncounters.remove(q);
            q--;
          }


      } //end for
      prettyPrint.append("<br />");

  }
  //end keyword filters-----------------------------------------------
*/

  	//start photo filename filtering
  	    if((request.getParameter("filenameField")!=null)&&(!request.getParameter("filenameField").equals(""))) {

  	      //clean the input string to create its equivalent as if it had been submitted through the web form
  	      String nameString=ServletUtilities.cleanFileName(ServletUtilities.preventCrossSiteScriptingAttacks(request.getParameter("filenameField").trim()));

  	           for(int q=0;q<rEncounters.size();q++) {
		         Encounter rEnc=(Encounter)rEncounters.get(q);
		         int numPhotos=rEnc.getAdditionalImageNames().size();
		         boolean hasPhoto=false;
		         for(int i=0;i<numPhotos;i++){
					 String thisPhoto=(String)rEnc.getAdditionalImageNames().get(i);
					 if(thisPhoto.equals(nameString)){hasPhoto=true;}
				}
		         if(!hasPhoto){
		           rEncounters.remove(q);
		           q--;
		         }
               }
  	      prettyPrint.append("filenameField contains: \""+nameString+"\"<br />");
      }

	//end photo filename filtering


    return (new EncounterQueryResult(rEncounters,filter,prettyPrint.toString()));

  }


  private static String processTagFilters(HttpServletRequest request, StringBuffer prettyPrint) {
    StringBuilder sb = new StringBuilder();
    sb.append(processAcousticTagFilter(request, prettyPrint));
    String satelliteTagFilter = processSatelliteTagFilter(request, prettyPrint);
    if (satelliteTagFilter.length() > 0) {
      if (sb.length() > 0) {
        sb.append(" && ");
      }
      sb.append(satelliteTagFilter);
    }
    return sb.toString();
  }

  
  private static String processSatelliteTagFilter(HttpServletRequest request,
      StringBuffer prettyPrint) {
    StringBuilder sb = new StringBuilder();
    String name = request.getParameter("satelliteTagName");
    if (name != null && name.length() > 0 && !"None".equals(name)) {
      prettyPrint.append("satellite tag name is: ");
      prettyPrint.append(name);
      prettyPrint.append("<br/>");
      sb.append('(');
      sb.append("satelliteTag.name == ");
      sb.append(Util.quote(name));
      sb.append(')');
    }
    String serialNumber = request.getParameter("satelliteTagSerial");
    if (serialNumber != null && serialNumber.length() > 0) {
      prettyPrint.append("satellite tag serial is: ");
      prettyPrint.append(serialNumber);
      prettyPrint.append("<br/>");
      if (sb.length() > 0) {
        sb.append(" && ");
      }
      sb.append('(');
      sb.append("satelliteTag.serialNumber == ");
      sb.append(Util.quote(serialNumber));
      sb.append(')');
    }
    String argosPttNumber = request.getParameter("satelliteTagArgosPttNumber");
    if (argosPttNumber != null && argosPttNumber.length() > 0) {
      prettyPrint.append("satellite tag Argos PTT Number is: ");
      prettyPrint.append(argosPttNumber);
      prettyPrint.append("<br/>");
      if (sb.length() > 0) {
        sb.append(" && ");
      }
      sb.append('(');
      sb.append("satelliteTag.argosPttNumber == ");
      sb.append(Util.quote(argosPttNumber));
      sb.append(')');
    }
    return sb.toString();
  }

  private static String processAcousticTagFilter(HttpServletRequest request,
      StringBuffer prettyPrint) {
    StringBuilder tagFilter = new StringBuilder();
    String acousticTagSerial = request.getParameter("acousticTagSerial");
    if (acousticTagSerial != null && acousticTagSerial.length() > 0) {
      prettyPrint.append("acoustic tag serial number is: ");
      prettyPrint.append(acousticTagSerial);
      prettyPrint.append("<br/>");
      tagFilter.append('(');
      tagFilter.append("acousticTag.serialNumber == ");
      tagFilter.append(Util.quote(acousticTagSerial));
      tagFilter.append(')');
    }
    String acousticTagId = request.getParameter("acousticTagId");
    if (acousticTagId != null && acousticTagId.length() > 0) {
      prettyPrint.append("acoustic tag id is: ");
      prettyPrint.append(acousticTagId);
      prettyPrint.append("<br/>");
      if (tagFilter.length() > 0) {
        tagFilter.append(" && ");
      }
      tagFilter.append('(');
      tagFilter.append("acousticTag.idNumber == ");
      tagFilter.append(Util.quote(acousticTagId));
      tagFilter.append(')');
    }
    return tagFilter.toString();
  }
  
  private static String updateJdoqlVariableDeclaration(String jdoqlVariableDeclaration, String typeAndVariable) {
    StringBuilder sb = new StringBuilder(jdoqlVariableDeclaration);
    if (jdoqlVariableDeclaration.length() == 0) {
      sb.append(" VARIABLES ");
      sb.append(typeAndVariable);
    }
    else {
      if (!jdoqlVariableDeclaration.contains(typeAndVariable)) {
        sb.append("; ");
        sb.append(typeAndVariable);
      }
    }
    return sb.toString();
  }
  
  private static String updateParametersDeclaration(
      String parameterDeclaration, String typeAndVariable) {
    StringBuilder sb = new StringBuilder(parameterDeclaration);
    if (parameterDeclaration.length() == 0) {
      sb.append(" PARAMETERS ");
    }
    else {
      sb.append(", ");
    }
    sb.append(typeAndVariable);
    return sb.toString();
  }

  

}
