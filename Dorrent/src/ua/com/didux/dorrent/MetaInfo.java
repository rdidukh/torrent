/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.didux.dorrent;

import java.io.InvalidClassException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
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
        private String name;
        private List<String> path;
        private byte[] md5Sum;
        private int length;
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public String getName()
        {
            return name;
        }
        
        public int getLength()
        {
            return length;
        }
        
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
        
        return new String(((BenString)benObj).getValue());
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
        
        return ((BenInteger)benObj).getInt();        
    }
    
    public int getLength() throws MetaInfoException
    {
        BenObject benObj = getInfo().get("length");
        
        if(benObj == null)
            throw new MetaInfoException("\'info.length\' not found.");
        
        if(!(benObj instanceof BenInteger))
            throw new MetaInfoException("\'info.length\' is not BenInteger.");
        
        return ((BenInteger)benObj).getInt();        
    }
    
    public String getName() throws MetaInfoException
    {
        BenObject benObj = getInfo().get("name");
        
        if(benObj == null)
            throw new MetaInfoException("\'info.name\' not found.");
        
        if(!(benObj instanceof BenString))
            throw new MetaInfoException("\'info.name\' is not BenString.");
        
        return new String(((BenString)benObj).getValue());        
    }
    
    public byte[] getPieces() throws MetaInfoException
    {
        BenObject benObj = getInfo().get("pieces");
        
        if(benObj == null)
            throw new MetaInfoException("\'info.pieces\' not found.");
        
        if(!(benObj instanceof BenString))
            throw new MetaInfoException("\'info.pieces\' is not BenString.");
        
        return ((BenString)benObj).getValue();
    }
    
    public List<File> getFileList() throws MetaInfoException
    {
        BenDictionary info = getInfo();
        List<File> files = new ArrayList<File>();
        
        BenObject fileList = info.get("files");
        
        if(fileList == null)
        {
            // Single file mode
            File file = new File();
            file.name = getName();
            file.length = getLength();
            file.path = new LinkedList<String>();
            files.add(file);
            return files;
        }
        
        if(!(fileList instanceof BenList))
            throw new MetaInfoException("\'info.files\' is not BenList.");
        
        List<BenObject> benList = ((BenList)fileList).getList();
        
        for(BenObject benObj: benList)
        {
            if(!(benObj instanceof BenDictionary))
                throw new MetaInfoException("files entry is not BenDictionary");
            
            BenDictionary bd = (BenDictionary)benObj;
            
            BenObject benLength = bd.get("length");
            
            if(benLength == null)
                throw new MetaInfoException("files entry does not hold length field");
            
            if(!(benLength instanceof BenInteger))
                throw new MetaInfoException("files entry length is not BenInteger");
            
            int length = ((BenInteger)benLength).getInt();
            
            BenObject benPath = bd.get("path");
            
            if(benPath == null)
                throw new MetaInfoException("files entry does not hold path field");
            
            if(!(benPath instanceof BenList))
                throw new MetaInfoException("files entry path is not BenList");
            
            List<BenObject> benPathList = ((BenList)benPath).getList();
            
            LinkedList<String> path = new LinkedList<String>();
            
            for(BenObject benPathEntry: benPathList)
            {
                if(!(benPathEntry instanceof BenString))
                    throw new MetaInfoException("files entry path entry is not BenString");
            
                path.add(((BenString)benPathEntry).toString());
            }
            
            
            
            File file = new File();
            file.name = path.removeLast();
            file.length = length;
            file.path = path;
            
            files.add(file);
        }
        
        
        return files;
    }
    
//    public List<File> getFileList()
//    {
//        
//        
//    
//    }
    
    public boolean isSingleFileMode() throws MetaInfoException
    {
        return getInfo().get("files") == null;
    }
    
    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();
        
        try
        {
            sw.write("name: "+getName()+"\n");
            if(this.isSingleFileMode())
                sw.write("length: "+getLength()+"\n");
            sw.write("announce: " + getAnnounce() + "\n");
            sw.write("piece length: " + getPieceLength() + "\n");
            
            List<File> files = getFileList();
            
            for(File file: files)
            {
                sw.write("file: "+file.getName()+"\n");
            }
            
        }
        catch(MetaInfoException ex)
        {
            return "Invalid meta info: " + ex.getMessage();
        }
        
        return sw.toString();
    }
 
    
    
}
