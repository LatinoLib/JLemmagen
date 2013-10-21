# JLemmagen

JLemmaGen is java implmentation of [LemmaGen][lemmagen] project.

[LemmaGen][lemmagen] project aims at providing standardized open source multilingual platform for lemmatisation.

Project contains 2 libraries:

*    **lemmagen.jar** - implementation of lemmatizer and API for building own lemmatizers
*    **lemmagen-lang.jar** - prebuilded lemmatizers from [Multext Eastern dictionaries][multeast]

### Sample Usage
    Lemmatizer lm = LemmatizerFactory.getPrebuild("mlteast-en");
    assert("be".equals(lm.lemmatize("are")));

### Maven
Repository:

    <repository>
        <id>jlemmagen-snapshots</id>
        <name>JLemmaGen snaphsot repository</name>
        <url>https://mvn.datalan.sk/maven2/libs-snapshots-local/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
        <layout>default</layout>
    </repository>

Dependency:

    <dependency>
        <groupId>eu.hlavki.text</groupId>
        <artifactId>lemmagen-lang</groupId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

[lemmagen]: http://lemmatise.ijs.si/Software/Version3
[multeast]: http://nl.ijs.si/ME/V4/
