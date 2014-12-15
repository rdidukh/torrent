package ua.com.didux.dorrent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract public class BenObject
{
    public enum Type
    {
        NONE,
        INTEGER,
        STRING,
        LIST,
        DICTIONARY
    }    

    Type type;

    protected BenObject(Type t)
    {
        type = t;
    }

    protected static BenObject getBenObject(InputStream input, char ch)
    {
        try
        {
        
            if(ch == 'i')
                return new BenInteger(input);
            else if(ch >= '0' && ch <= '9')
                return new BenString(input, ch);
            else if(ch == 'l')
                return new BenList(input);    
            else if(ch == 'd')
                return new BenDictionary(input);
        
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new IllegalArgumentException("[ 1 ]");
        }
        
        throw new IllegalArgumentException("[ 2 ]");
    }

    public static BenObject getBenObject(InputStream input) throws IOException
    {
        if(input.available() < 1) 
            throw new IllegalArgumentException("[ 1 ]");

        char ch = (char)input.read();

        
        return getBenObject(input, ch);
    }

    public Object getValue() 
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    public Type getType()
    {
        return type;
    }

    abstract public void print(int tab);
}


class BenInteger extends BenObject
{
    int value;

    private void parse(InputStream input) throws IOException
    {
        char ch = 'i';

        if(input.available() < 1) 
            throw new IllegalArgumentException("[ 1 ]");

        value = 0;

        while(input.available() > 0)
        {
            ch = (char)input.read();

            if(ch == 'e') break;

            if(ch < '0' || ch > '9')
                throw new IllegalArgumentException("[ 2 ]");                
            
            value *= 10;
            value += ch-'0';
        }

        if(ch != 'e') 
            throw new IllegalArgumentException("[ 3 ]");   
    }

    @Override
    public Integer getValue()
    {
        return value;
    }

    public BenInteger(InputStream input) throws IOException
    {
        super(Type.INTEGER);
        parse(input);
    }
        
    @Override
    public void print(int tab)
    {
        for(int i = 0; i < tab; i++)
            System.out.print(' ');
    
        System.out.println("INTEGER: "+value);
    }
}


class BenString extends BenObject
{
    String value;

    private void parse(InputStream input, char firstChar) throws IOException
    {
        if(firstChar < '0' || firstChar > '9')
            throw new IllegalArgumentException("[ 1 ]");

        char ch = firstChar;
        int length = firstChar-'0';

        while(input.available() > 0)
        {
            ch = (char)input.read();

            if(ch == ':') break;

            if(ch < '0' || ch > '9')
                throw new IllegalArgumentException("[ 2 ]");                
            
            length *= 10;
            length += ch-'0';
        }

        if(ch != ':') 
            throw new IllegalArgumentException("[ 3 ]");   

        if(input.available() < length)
            throw new IllegalArgumentException("[ 4 ]"); 

        byte[] bytes = new byte[length];
                
        input.read(bytes, 0, length);

        value = new String(bytes);        
    }

    @Override
    public String getValue()
    {
        return value;
    }

    public BenString(InputStream input, char firstChar) throws IOException
    {
        super(Type.STRING);
        parse(input, firstChar);
    }
        
    @Override
    public void print(int tab)
    {
        for(int i = 0; i < tab; i++)
            System.out.print(' ');
    
        System.out.println("STRING("+value.length()+ "): \'"+value+"\'");
    }
}

class BenList extends BenObject
{
    List<BenObject> list = new LinkedList<BenObject>();

    private void parse(InputStream input) throws IOException
    {
        char ch = 'l';

        if(input.available() < 1) 
            throw new IllegalArgumentException("[ 1 ]");

        while(input.available() > 0)
        {
            ch = (char)input.read();

            if(ch == 'e') break;

            list.add(BenObject.getBenObject(input, ch));
        }

        if(ch != 'e') 
            throw new IllegalArgumentException("[ 3 ]");   
    }

    public BenList(InputStream input) throws IOException
    {
        super(BenObject.Type.LIST);
        parse(input);
    }
        
    @Override
    public void print(int tab)
    {
        for(int i = 0; i < tab; i++)
            System.out.print(' ');
    
        System.out.println("LIST(" + list.size() + "): ");
        
        for(BenObject element : list)
        {
            element.print(tab+2);
        }
    }

}


class BenDictionary extends BenObject
{
    List<BenObject> keys = new ArrayList<BenObject>();
    List<BenObject> values = new ArrayList<BenObject>();
    Map<String, BenObject> strings = new HashMap<String, BenObject>();
    
    private void parse(InputStream input) throws IOException
    {
        char ch = 'd';

        if(input.available() < 1) 
            throw new IllegalArgumentException("[ 1 ]");

        while(input.available() > 0)
        {
            ch = (char)input.read();

            if(ch == 'e') break;

            BenObject key = BenObject.getBenObject(input, ch);
            
            if(key.getType() != BenObject.Type.STRING)
                throw new IllegalArgumentException("[ 2 ]");
            
            if(input.available() <= 0)
                throw new IllegalArgumentException("[ 3 ]");
                
            ch = (char)input.read();
            
            BenObject value = BenObject.getBenObject(input, ch);
            
            keys.add(key);
            values.add(value);
            strings.put((String)key.getValue(), value);
        }

        if(ch != 'e') 
            throw new IllegalArgumentException("[ 4 ]");   
    }

    
    public BenDictionary(InputStream input) throws IOException
    {
        super(BenObject.Type.DICTIONARY);
        parse(input);
    }
    
    
    @Override
    public void print(int tab)
    {
        for(int i = 0; i < tab; i++)
            System.out.print(' ');
    
        System.out.println("DICTIONARY(" + keys.size() + "): ");
        
        for(int i = 0; i < keys.size(); i++)
        {
            keys.get(i).print(tab+2);
            values.get(i).print(tab+2);
        }
    }
}