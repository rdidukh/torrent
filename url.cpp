#include <string>
using namespace std;

class URL
{
    bool failFlag;
    int port;
    string protocol;
    string address;
    string query;
    string url;
    

    void setFail();
    void parse(const string & str);

    public:
    URL(const string &str);
    bool fail();
    

};

URL::URL(const string& str): port(-1), url(str)
{
   parse(str);
}

void URL::parse(const string &str)
{
    int len = str.length();
    int i;

    for(i = 0; i < len && str[i] != ':'; i++)
        i++;

    protocol = str.substr(0, i);

    if(i+2 > len && str[i+1] != '/' && str[i+2] != '/') return setFail();

    int start = i+3;    

    while(i < len && str[i] != ':' && str[i] != '/') i++;

    address = str.substr(start, i-start);    

    if(i == len) return;

    if(str[i] == ':')
    {
        i++;
        if(i >= len) return setFail();        
        if(str[i] < '0' || str[i] > '9') return setFail();        

        port = 0;
        while(i < len && str[i] != '/')
        {
            port *= 10;
            port += str[i]-'0';
            i++;
        }
    }

    if(str[i] != '/') return setFail();

    query = str.substr(i);
}

void URL::setFail()
{
    failFlag = true;
}

bool URL::fail()
{
    return failFlag;
}



