# JLemmagen

JLemmaGen is java implmentation of [LemmaGen][lemmagen] project.

[LemmaGen][lemmagen] project aims at providing standardized open source multilingual platform for lemmatisation.


### Sample Usage
    Lemmatizer lm = LemmatizerFactory.getPrebuild("en");
    assert("be".equals(lm.lemmatize("are")));

[lemmagen]: http://lemmatise.ijs.si/Software/Version3