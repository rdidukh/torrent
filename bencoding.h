#ifndef BENCODING_H
#define BENCODING_H

#include <iostream>
#include <stdint.h>
#include <string>
#include <list>
#include <map>
#include <utility>

using namespace std;

class BenObject
{

public:     
    virtual void print(int)=0;

    bool fail();

    virtual ~BenObject();

    enum Type
    {
        NONE,
        INTEGER,
        STRING,
        LIST,
        DICT,
    };

    Type getType();

    static BenObject* getBenObject(const char* &str, int &length);

protected:
    void setType(BenObject::Type t);
    void setFail();
    BenObject();
private:
    bool failFlag;
    BenObject::Type type;        
};

class BenInteger: public BenObject
{
    int64_t value;

    void parse(const char* &str, int &length);
    
    public:
    BenInteger(const char* &str, int &length); 
    
    virtual void print(int depth);
    
    int64_t getValue();
};

class BenString: public BenObject
{
    string value;

    void parse(const char* &str, int &length);

    public:
    
    BenString(const char* &str, int &length);  
    
    string getValue();
    
    virtual void print(int depth);
};

class BenList: public BenObject
{
    std::list<BenObject*> lst;

    void parse(const char* &str, int &length);

    public:
    BenList(const char* &str, int &length);
    
    ~BenList();
    
    void print(int depth);    
};

class BenDict: public BenObject
{
    std::list<std::pair<BenString*, BenObject*> > lst;
    std::map<string, BenObject*> mp;

    void parse(const char* &str, int &length);

    public:
    BenDict(const char* &str, int &length);
    
    ~BenDict();
    
    void print(int depth);    
};

#endif

