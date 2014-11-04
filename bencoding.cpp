#include <iostream>
#include <stdint.h>
#include <string>
#include <cstdlib>
#include <cstring>
#include <list>
#include <map>
#include <utility>
#include <cstdio>

#include "bencoding.h"

using namespace std;

bool BenObject::fail()
{
    return this->failFlag;
}

BenObject::~BenObject() {}

BenObject::Type BenObject::getType()
{
    return this->type;    
}

void BenObject::setType(BenObject::Type t)
{
    type = t;
}

void BenObject::setFail()
{
    failFlag = true;
}

BenObject::BenObject(): failFlag(false), type(NONE)
{

}        

BenObject* BenObject::getBenObject(const char* &str, int &length)
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

void BenInteger::parse(const char* &str, int &length)
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

BenInteger::BenInteger(const char* &str, int &length): value(0)
{
    parse(str, length);
    setType(INTEGER);
}   

void BenInteger::print(int depth)
{
    for(int i = 0; i < depth; i++)
        cout << " ";

    cout << "INTEGER: " << value << endl;
}

int64_t BenInteger::getValue()
{
    return value;
}

void BenString::parse(const char* &str, int &length)
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

BenString::BenString(const char* &str, int &length)
{
    parse(str, length);
    setType(STRING);
}   

std::string BenString::getValue()
{
    return value;
}

void BenString::print(int depth)
{
    for(int i = 0; i < depth; i++)
        cout << " ";

    cout << "STRING: " << value << endl;
}


void BenList::parse(const char* &str, int &length)
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

BenList::BenList(const char* &str, int &length)
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

BenList::~BenList()
{
    std::list<BenObject*>::iterator it;
    for(it = lst.begin(); it != lst.end(); ++it)
        delete *it; 
}  

void BenList::print(int depth)
{
    for(int i = 0; i < depth; i++)
        cout << " ";

    cout << "LIST (" << lst.size() << "): " << endl;
    std::list<BenObject*>::iterator it;
    for(it = lst.begin(); it != lst.end(); ++it)
        (*it)->print(depth+2);
}

void BenDict::parse(const char* &str, int &length)
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

BenDict::BenDict(const char* &str, int &length)
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

BenDict::~BenDict()
{
    std::list<std::pair<BenString*, BenObject*> >::iterator it;
    for(it = lst.begin(); it != lst.end(); ++it)
    {
        delete it->first;
        delete it->second;
    } 
}  

void BenDict::print(int depth)
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

