SHELL=/bin/bash

all:
	if [ ! -d bin ]; then mkdir bin; fi
	javac -d bin src/ru/itmo/golovchenko/trapezoidalmap/*.java src/ru/itmo/golovchenko/trapezoidalmapgui/*.java; \
	cd ./bin; \
	jar cfe trapezoidalmap.jar ru.itmo.golovchenko.trapezoidalmapgui.MainWindow ./ru/itmo/golovchenko/trapezoidalmap/*.class ./ru/itmo/golovchenko/trapezoidalmapgui/*.class; \

clean:
	rm -r bin
