package ua.com.didux.torrent;

import java.io.ByteArrayInputStream;

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

    public static BenObject getBenObject(ByteArrayInputStream input)
    {
        if(input.available() < 1) 
            throw new IllegalArgumentException("[ 1 ]");

        char ch = (char)input.read();

        switch(ch)
        {
            case 'i':
                return new BenInteger(input);
            default:
                throw new IllegalArgumentException("[ 2 ]");
        }            
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

