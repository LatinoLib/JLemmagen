# JLemmagen

JLemmaGen is java implmentation of LemmaGen project.

LemmaGen project aims at providing standardized open source multilingual platform for lemmatisation. We started this work as a result of lack of high quality lemmatiser for Slovene language. Currently we have, not only the lemmatiser for Slovene, but also for 11 other European languages and the system which is able to learn lemmatisation rules for new languages by providing it with existing wordform-lemma pair examples. 


### Sample Usage
    Lemmatizer lm = LemmatizerFactory.getPrebuild("en");
    assert("be".equals(lm.lemmatize("are")));