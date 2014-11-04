#include <iostream>
#include <cstdio>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>
#include <cstdlib>
#include "bencoding.h"

using namespace std;

int main(int argc, char*argv[])
{
    int fd = open(argv[1], O_RDONLY);

    if(fd < 0)
    {
        perror("open");
        exit(1);
    }

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

    BenObject * bt = BenObject::getBenObject(str, length);
    
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

