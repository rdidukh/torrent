#include <iostream>
#include <stdint.h>
#include <string>
#include <cstdlib>
#include <cstring>
#include <list>
#include <map>
#include <utility>
#include <cstdlib>
#include <cstdio>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>

using namespace std;

class BenObject
{

public:     
    virtual void print(int)=0;

    bool fail()
    {
        return failFlag;
    }

    virtual ~BenObject() {}

    enum Type
    {
        NONE,
        INTEGER,
        STRING,
        LIST,
        DICT,
    };

    Type getType()
    {
        return type;    
    }

protected:
    
    void setType(BenObject::Type t)
    {
        type = t;
    }

    void setFail()
    {
        failFlag = true;
    }

    BenObject(): failFlag(false), type(NONE)
    {
    
    }  
private:
    bool failFlag;
    BenObject::Type type;        
};

BenObject* getBenObject(const char* &str, int &length);

class BenInteger: public BenObject
{
    int64_t value;

    void parse(const char* &str, int &length)
    {
        if(length < 1) return setFail();    
        if(*str != 'i') return setFail();

        str++;
        length--;
        
        if(length < 1) return setFail();
        
        int sign = 1;
        
        if(*str == '-')
        {
            str++;
            length--;
            sign = -1;
        }
        
        if(length < 1) return setFail();
        
        while(*str != 'e')
        {        
            if(*str < '0' || *str > '9') return setFail();
        
            value *= 10;
            value += (*str-'0');
            str++;
            length--;
            if(length < 1) return setFail();
        }
        
        value *= sign;
        
        str++;
        length--;
    }
    
    public:
    BenInteger(const char* &str, int &length): value(0)
    {
        parse(str, length);
        setType(INTEGER);
    }   
    
    virtual void print(int depth)
    {
        for(int i = 0; i < depth; i++)
            cout << " ";
    
        cout << "INTEGER: " << value << endl;
    }
    
    int64_t getValue()
    {
        return value;
    }
};

class BenString: public BenObject
{
    string value;

    void parse(const char* &str, int &length)
    {    
        if(length < 1) return setFail();
        if(*str < '0' || *str > '9') return setFail();
    
        int strLen = 0;
        
        while(*str != ':')
        {
            strLen *= 10;
            strLen += *str-'0';
            str++;
            length--; 
            if(length < 1) return setFail();
        }
 
        str++;
        length--;
        
        if(length < strLen) return setFail();
        
        value = std::string(str, strLen);  
        
        str += strLen;
        length -= strLen;  
    }

    public:
    
    BenString(const char* &str, int &length)
    {
        parse(str, length);
        setType(STRING);
    }   
    
    string getValue()
    {
        return value;
    }
    
    virtual void print(int depth)
    {
        for(int i = 0; i < depth; i++)
            cout << " ";
    
        cout << "STRING: " << value << endl;
    }
};

class BenList: public BenObject
{
    std::list<BenObject*> lst;

    void parse(const char* &str, int &length)
    {
        if(length < 1) return setFail();
        if(*str != 'l') return setFail();
        
        str++;
        length--;
        if(length < 1) return setFail();
        
        while(*str != 'e')
        {
            BenObject* bt = getBenObject(str, length);
        
            if(bt == NULL) return setFail();
            
            lst.push_back(bt);
            if(length < 1) return setFail();
        }
        
        str++;
        length--;
    }

    public:
    BenList(const char* &str, int &length)
    {
        parse(str, length);
        setType(LIST);
        std::list<BenObject*>::iterator it;
        if(fail())
        {
            for(it = lst.begin(); it != lst.end(); ++it)
                delete *it;   
            lst.clear();
        }
    }
    
    ~BenList()
    {
        std::list<BenObject*>::iterator it;
        for(it = lst.begin(); it != lst.end(); ++it)
            delete *it; 
    }  
    
    void print(int depth)
    {
        for(int i = 0; i < depth; i++)
            cout << " ";
    
        cout << "LIST (" << lst.size() << "): " << endl;
        std::list<BenObject*>::iterator it;
        for(it = lst.begin(); it != lst.end(); ++it)
            (*it)->print(depth+2);
    }
    
};

class BenDict: public BenObject
{
    std::list<std::pair<BenString*, BenObject*> > lst;
    std::map<string, BenObject*> mp;

    void parse(const char* &str, int &length)
    {
        if(length < 1) return setFail();
        if(*str != 'd') return setFail();
        
        str++;
        length--;
        if(length < 1) return setFail();
        
        while(*str != 'e')
        {
            BenObject* key = getBenObject(str, length);
            if(key == NULL) return setFail();
            if(key->getType() != STRING)
            {
                delete key;
                return setFail();
            }
            
            BenObject* value = getBenObject(str, length);
            if(value == NULL) return setFail();
            if(value->fail())
            {
                delete key;
                delete value;
                return setFail();
            }
            
            lst.push_back(std::make_pair(dynamic_cast<BenString*>(key), value));
            mp[dynamic_cast<BenString*>(key)->getValue()] = value;
            
            if(length < 1) return setFail();
        }
        
        str++;
        length--;
    }

    public:
    BenDict(const char* &str, int &length)
    {
        parse(str, length);
        setType(DICT);
        
        if(fail())
        {
            std::list<std::pair<BenString*, BenObject*> >::iterator it;
            for(it = lst.begin(); it != lst.end(); ++it)
            {
                delete it->first;
                delete it->second;
            }   
            lst.clear();
        }
    }
    
    ~BenDict()
    {
        std::list<std::pair<BenString*, BenObject*> >::iterator it;
        for(it = lst.begin(); it != lst.end(); ++it)
        {
            delete it->first;
            delete it->second;
        } 
    }  
    
    void print(int depth)
    {
        for(int i = 0; i < depth; i++)
            cout << " ";
    
        cout << "DICT (" << lst.size() << "): " << endl;
        std::list<std::pair<BenString*, BenObject*> >::iterator it;
        for(it = lst.begin(); it != lst.end(); ++it)
        {
            it->first->print(depth+2);
            it->second->print(depth+2);
            cout << endl;
        }
    }
    
};

BenObject* getBenObject(const char* &str, int &length)
{
    BenObject* bt = NULL;

    if(*str == 'i')
    {
        bt = new BenInteger(str, length);
    }
    else if(*str == 'l')
    {
        bt = new BenList(str, length);
    }
    else if(*str == 'd')
    {
        bt = new BenDict(str, length);
    }
    else if(*str >= '0' && *str <= '9')
    {
        bt = new BenString(str, length);
    }
    
    if(bt && bt->fail())
    {
        delete bt;
        bt = NULL;
    }
    
    return bt;
} 


int main(int argc, char*argv[])
{
    int fd = open(argv[1], O_RDONLY);

    if(fd < 0)
    {
        perror("open");
        exit(1);
    }

//    off_t lseek(int fd, off_t offset, int whence);

    off_t size = lseek(fd, 0, SEEK_END);

    if(size == (off_t)-1)
    {
        perror("lseek");
        close(fd);
        exit(1);
    }
    
    int length = (int)size;
    void* handle = (void *)mmap(NULL, length, PROT_READ, MAP_PRIVATE, fd, 0);      
    if(handle == NULL)
    {
        perror("mmap");
        close(fd);
        exit(1);
    }

    const char *str = (char *)handle;

    BenObject * bt = getBenObject(str, length);
    
    if(!bt) 
    {
        cout << "FAIL" << endl;
        return 1;   
    }
    bt->print(0);

    delete bt;

    munmap(handle, size);
    close(fd);

    return 0;
}

