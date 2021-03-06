package org.ecocean.genetics;


import java.util.ArrayList;
import java.util.List;


import org.ecocean.DataCollectionEvent;
import javax.servlet.http.HttpServletRequest;


public class TissueSample extends DataCollectionEvent {


  private static final long serialVersionUID = -4918907304313880745L;
  private static String type="TissueSample";

  private String tissueType;
  private String preservationMethod;
  //e.g., SWFSCz ID
  private String storageLabID;
  private String sampleID;
  private String alternateSampleID;
  private List<GeneticAnalysis> analyses;


  /**
   * Empty constructor required for JDO persistence.
   * DO NOT USE
   */
  public TissueSample(){}
  
  /*
   * Required constructor for instance creation
   */
  public TissueSample(String correspondingEncounterNumber, String sampleID) {
    super(correspondingEncounterNumber, type);
    this.sampleID=sampleID;
    analyses=new ArrayList<GeneticAnalysis>();
  }
  
  public TissueSample(String correspondingEncounterNumber, String sampleID, HttpServletRequest request) {
    super(correspondingEncounterNumber, type, request);
    this.sampleID=sampleID;
    analyses=new ArrayList<GeneticAnalysis>();
  }
  
  public void addGeneticAnalysis(GeneticAnalysis ga){
    if(!analyses.contains(ga)){analyses.add(ga);}
  }
  
  public void removeGeneticAnalysis(GeneticAnalysis ga){
    if(analyses.contains(ga)){analyses.remove(ga);}
  }
  
  public void removeGeneticAnalysis(int ga){
    if(analyses.size()>ga){analyses.remove(ga);}
  }
  
  public List<GeneticAnalysis> getGeneticAnalyses(){return analyses;}

  public String getTissueType(){return tissueType;}
  public void setTissueType(String newType){this.tissueType=newType;}
  
  public String getPreservationMethod(){return preservationMethod;}
  public void setPreservationMethod(String newMethod){this.preservationMethod=newMethod;}
  
  public String getStorageLabID(){return storageLabID;}
  public void setStorageLabID(String newLab){this.storageLabID=newLab;}
  
  public String getSampleID(){return sampleID;}

  public String getAlternateSampleID(){return alternateSampleID;}
  public void setAlternateSampleID(String newID){this.alternateSampleID=newID;}

  public int getNumAnalyses(){return analyses.size();}
  
  public String getHTMLString(){
    String paramValues=super.getHTMLString();
    if((this.getAlternateSampleID()!=null)&&(!this.getAlternateSampleID().equals(""))){paramValues+="     Alternate Sample ID: "+this.getAlternateSampleID()+"<br />";}
    if((this.getPreservationMethod()!=null)&&(!this.getPreservationMethod().equals(""))){paramValues+="     Preservation method: "+this.getPreservationMethod()+"<br />";}
    if((this.getStorageLabID()!=null)&&(!this.getStorageLabID().equals(""))){paramValues+="     Storage lab ID: "+this.getStorageLabID()+"<br />";}
    if((this.getTissueType()!=null)&&(!this.getTissueType().equals(""))){paramValues+="     Tissue type: "+this.getTissueType()+"<br />";}
    return paramValues; 
  }
  
}
