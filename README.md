# Progetto di LFT

### Introduction
This repository hosts a project develeoped by [Federico Giacardi](https://github.com/Giacca01), [Michael Urru](https://github.com/michaelurru), and [Stefano Fontana](https://github.com/SteuFonta01) as part of their [Formal Languages and Compilers course](http://laurea.educ.di.unito.it/index.php/offerta-formativa/insegnamenti/laurea/insegnamenti-coorti-triennale/scheda-insegnamento?cod=MFN0603&codA=&year=2021&orienta=U), attended during the second year of their Computer Science degree at the Univerisity of Turin.

### Content
The project is made of several separated, yet interacting, blocks, that can be blanded together to create all the elements (lexer, parser, ....) of a translation pipeline.
The aim of the latter is to build a working compiler for a simple [programming language](/assets/TargetProgrammingLanguage.pdf) developed by [our teachers](http://laurea.educ.di.unito.it/index.php/offerta-formativa/insegnamenti/laurea/insegnamenti-coorti-triennale/scheda-insegnamento?cod=MFN0603&codA=&year=2021&orienta=U).
The modules, listed in order of development, that compose the project are:

    1. Automata: ten different automata are made available. 
    The most interesting ones are the 1.2 and 1.9, that are used in our lexer.

    2. Lexer: Java implementation of a lexer that takes a byte stream as input
    and splits it into tokens of the target language, that will later be used by 
    the parser.

    3. Parser: To be implemented....