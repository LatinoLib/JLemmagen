# JLemmagen

JLemmaGen is java implmentation of {{LemmaGen|http://lemmatise.ijs.si/Software/Version3}} project.

{{LemmaGen|http://lemmatise.ijs.si/Software/Version3}} project aims at providing standardized open source multilingual platform for lemmatisation.


### Sample Usage
    Lemmatizer lm = LemmatizerFactory.getPrebuild("en");
    assert("be".equals(lm.lemmatize("are")));
