/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.didux.dorrent;

import java.io.InvalidClassException;
import java.io.StringWriter;
import java.util.List;

class MetaInfoException extends Exception
{    
    MetaInfoException(String message)
    {
        super(message);
    }
}

/**
 *
 * @author Roma
 */
public class MetaInfo 
{     
    private BenDictionary benDict;
    //private BenDictionary info;
  //  private BenList announceList;
  //  private BenString comment;
    
    public class File
    {
        String path;
        byte[] md5Sum;
        int lenght;
    }
    
    public MetaInfo(BenDictionary bd)
    {
        benDict = bd;  
    }
    
    public String getAnnounce() throws MetaInfoException
    {
        BenObject benObj = benDict.get("announce");
        
        if(benObj == null)
            throw new MetaInfoException("\'announce\' not found.");
        
        if(!(benObj instanceof BenString))
            throw new MetaInfoException("\'announce\' is not BenString.");
        
        return ((BenString)benObj).getValue();
    }
   
    
    private BenDictionary getInfo() throws MetaInfoException
    {
        BenObject info = benDict.get("info");
        if(info == null)
            throw new MetaInfoException("\'info\' not found.");
        
        if(!(info instanceof BenDictionary))
            throw new MetaInfoException("\'info\' not BenDictionary.");  
        
        return (BenDictionary)info;
    }
    
    public int getPieceLength() throws MetaInfoException
    {
        BenObject benObj = getInfo().get("piece length");
        
        if(benObj == null)
            throw new MetaInfoException("\'piece length\' not found.");
        
        if(!(benObj instanceof BenInteger))
            throw new MetaInfoException("\'piece length\' is not BenInteger.");
        
        return ((BenInteger)benObj).getValue().intValue();        
    }
    
    public String getName() throws MetaInfoException
    {
        BenObject benObj = getInfo().get("name");
        
        if(benObj == null)
            throw new MetaInfoException("\'info.name\' not found.");
        
        if(!(benObj instanceof BenString))
            throw new MetaInfoException("\'info.name\' is not BenString.");
        
        return ((BenString)benObj).getValue();        
    }
    
//    public List<File> getFileList()
//    {
//        
//        
//    
//    }
    
    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();
        
        try
        {
            sw.write("name: "+getName()+"\n");
            sw.write("announce: " + getAnnounce() + "\n");
            sw.write("piece length: " + getPieceLength() + "\n");
        }
        catch(MetaInfoException ex)
        {
            return "Invalid meta info: " + ex.getMessage();
        }
        
        return sw.toString();
    }
 
    
    
}
