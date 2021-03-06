package org.ecocean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ecocean.genetics.TissueSample;

public class SinglePhotoVideo extends DataCollectionEvent {


  private static final long serialVersionUID = 7999349137348568641L;
  private String filename;
  private String fullFileSystemPath;
  
  /*
  private String thumbnailFilename;
  private String thumbnailFullFileSystemPath;
  */
  
  private static String type="SinglePhotoVideo";
  private String copyrightOwner;
  private String copyrightStatement;
  private List<Keyword> keywords;

  /**
   * Empty constructor required for JDO persistence
   */
  public SinglePhotoVideo(){}
  
  /*
   * Required constructor for instance creation
   */
  public SinglePhotoVideo(String correspondingEncounterNumber, String filename, String fullFileSystemPath) {
    super(correspondingEncounterNumber, type);
    this.filename=filename;
    this.fullFileSystemPath=fullFileSystemPath;
  }
  
  public SinglePhotoVideo(String correspondingEncounterNumber, File file) {
    super(correspondingEncounterNumber, type);
    this.filename=file.getName();
    this.fullFileSystemPath=file.getAbsolutePath();
  }
  
  /**
   * Returns the photo or video represented by this object as a java.io.File
   * This is a convenience method.
   * @return java.io.File
   */
  public File getFile(){
    if(fullFileSystemPath!=null){
        return (new File(fullFileSystemPath));
    }
    else{return null;}
  }
  
  /*
  public File getThumbnailFile(){
    if(thumbnailFullFileSystemPath!=null){
        return (new File(thumbnailFullFileSystemPath));
    }
    else{return null;}
  }
  */
  
  public String getFilename(){return filename;}
  public void setFilename(String newName){this.filename=newName;}
  
  public String getFullFileSystemPath(){return fullFileSystemPath;}
  public void setFullFileSystemPath(String newPath){this.fullFileSystemPath=newPath;}
  
  public String getCopyrightOwner(){return copyrightOwner;}
  public void setCopyrightOwner(String owner){copyrightOwner=owner;}
  
  public String getCopyrightStatement(){return copyrightStatement;}
  public void setCopyrightStatement(String statement){copyrightStatement=statement;}
  
  
  //public String getThumbnailFilename(){return (this.getDataCollectionEventID()+".jpg");}
  
  /*
  public void setThumbnailFilename(String newName){this.thumbnailFilename=newName;}
  
  public String getThumbnailFullFileSystemPath(){return thumbnailFullFileSystemPath;}
  public void setThumbnailFullFileSystemPath(String newPath){this.thumbnailFullFileSystemPath=newPath;}
  */
  
  public void addKeyword(Keyword dce){
    if(keywords==null){keywords=new ArrayList<Keyword>();}
    if(!keywords.contains(dce)){keywords.add(dce);}
  }
  public void removeKeyword(int num){keywords.remove(num);}
  public List<Keyword> getKeywords(){return keywords;}
  public void removeKeyword(Keyword num){keywords.remove(num);}
  
  
  
}
