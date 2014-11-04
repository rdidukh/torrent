#include <stdint.h>

static const uint32_t K[4] = {0x5a827999, 0x6ed9eba1, 0x8f1bbcdc, 0xca62c1d6};

class SHA1
{
    uint32_t h[5];

    static uint32_t ROTL(uint32_t x, unsigned b);
    static uint32_t f(unsigned t, uint32_t x, uint32_t y, uint32_t z);

    public:
    SHA1();
    
    void clear();
    void update(void* message);
    void get(void* buf);

};

SHA1::SHA1()
{
    clear();
}

void SHA1::clear()
{
    h[0] = 0x67452301;        
    h[1] = 0xefcdab89;
    h[2] = 0x98badcfe;
    h[3] = 0x10325476;
    h[4] = 0xc3d2e1f0;
}

void SHA1::update(void* message)
{
    uint32_t *M = (uint32_t *)message;       
    uint32_t W[80];    
    uint32_t a,b,c,d,e;
    
    for(int t = 0; t < 16; t++)
        W[t] = M[t];

    for(int t = 16; t < 80; t++)
        W[t] = ROTL(W[t-3]^W[t-8]^W[t-14]^W[t-16], 1);

    a = h[0];
    b = h[1];
    c = h[2];
    d = h[3];
    e = h[4];

    for(int t = 0; t < 80; t++)
    {
        uint32_t T = ROTL(a, 5) + f(t, b, c, d) + e + K[t/20] + W[t];
        e = d;
        d = c;        
        c = ROTL(b, 30);
        b = a;
        a = T;
    }

    h[0] = h[0] + a;
    h[1] = h[1] + b;
    h[2] = h[2] + c;
    h[3] = h[3] + d;
    h[4] = h[4] + e;

}

uint32_t SHA1::f(unsigned t, uint32_t x, uint32_t y, uint32_t z)
{
    if(t < 20)
        return (x&y)^((~x)&z);
    else if(t < 40)
        return x^y^z;
    else if(t < 60)
        return (x&y)^(x&z)^(y&z); 
    else if(t < 80)
        return x^y^z;
    throw "SHA1::f";   
}

uint32_t SHA1::ROTL(uint32_t x, unsigned b)
{
    return (x<<b)|(x>>(sizeof(x)*8-b));
}

void SHA1::get(void *buf)
{
    uint32_t *hash = (uint32_t *)buf;
    for(int i = 0; i < 5; i++)
        hash[i] = h[i];
}

