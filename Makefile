JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

SRCPATH=ua/com/didux/torrent

CLASSES = \
	$(SRCPATH)/Main.java \
	$(SRCPATH)/BenObject.java

all: classes

classes: $(CLASSES:.java=.class)

clean:
	rm -rf $(SRCPATH)/*.class
