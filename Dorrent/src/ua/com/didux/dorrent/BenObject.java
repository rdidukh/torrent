package ua.com.didux.dorrent;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.LinkedList;

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

    protected static BenObject getBenObject(ByteArrayInputStream input, char ch)
    {
        if(ch == 'i')
            return new BenInteger(input);
        else if(ch >= '0' && ch <= '9')
            return new BenString(input, ch);
        else if(ch == 'l')
            return new BenList(input);    

        throw new IllegalArgumentException("[ 1 ]");
    }

    public static BenObject getBenObject(ByteArrayInputStream input)
    {
        if(input.available() < 1) 
            throw new IllegalArgumentException("[ 1 ]");

        char ch = (char)input.read();

        return getBenObject(input, ch);
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

    private void parse(ByteArrayInputStream input)
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

    public int getValue()
    {
        return value;
    }

    public BenInteger(ByteArrayInputStream input)
    {
        super(Type.INTEGER);
        parse(input);
    }
        
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

    private void parse(ByteArrayInputStream input, char firstChar)
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

    public String getValue()
    {
        return value;
    }

    public BenString(ByteArrayInputStream input, char firstChar)
    {
        super(Type.STRING);
        parse(input, firstChar);
    }
        
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

    private void parse(ByteArrayInputStream input)
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

    public BenList(ByteArrayInputStream input)
    {
        super(Type.LIST);
        parse(input);
    }
        
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

