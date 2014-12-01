package ua.com.didux.torrent;

import java.io.ByteArrayInputStream;

public class Main
{
    public static void main(String[] args)
    {        
        
        byte[] bytes = args[0].getBytes();

        BenObject ben = BenObject.getBenObject(new ByteArrayInputStream(bytes));

        ben.print(0);
    }
}

