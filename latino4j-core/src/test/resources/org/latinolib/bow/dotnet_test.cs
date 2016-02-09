using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Latino;
using Latino.TextMining;
using Newtonsoft.Json;

namespace LatinoTester
{
    class Program
    {

        const string Corpus = "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?. On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains.";
        const string Document = "know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there These cases are perfectly simple and easy to distinguish. In a free hour, when our power principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures";
        //const string Document = "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";

        // ReSharper disable InconsistentNaming
        public enum TestCase
        {
            testWordWeightType_TERM_FREQ,
            testWordWeightType_TF_IDF,
            testWordWeightType_LOG_DF_TF_IDF,
            testNGramLen,
            testMinWordFreq,
            testNormalizeVectors,
            testKeepWordForms,
            testInitializeLargeScale,
            testLemmatizer,
            testStopWords
        }
        // ReSharper restore InconsistentNaming

        static void Main(string[] args)
        {
            string[] documents = Corpus.Split(new[] { '.' }, StringSplitOptions.RemoveEmptyEntries);
            var results = new Dictionary<string, SparseVector<double>>();
            foreach (TestCase testCase in Enum.GetValues(typeof(TestCase)).Cast<TestCase>())
            {
                var bow = new BowSpace
                {
                    Tokenizer = new SimpleTokenizer
                    {
                        MinTokenLen = 2,
                        Type = TokenizerType.AllChars
                    },
                    StopWords = null,
                    Stemmer = null,
                    MaxNGramLen = 2,
                    MinWordFreq = 1,
                    WordWeightType = WordWeightType.TermFreq,
                    NormalizeVectors = true,
                    KeepWordForms = false
                };
                switch (testCase)
                {
                    case TestCase.testWordWeightType_TERM_FREQ:
                        break;
                    case TestCase.testWordWeightType_TF_IDF:
                        bow.WordWeightType = WordWeightType.TfIdf;
                        break;
                    case TestCase.testWordWeightType_LOG_DF_TF_IDF:
                        bow.WordWeightType = WordWeightType.LogDfTfIdf;
                        break;
                    case TestCase.testNGramLen:
                        bow.MaxNGramLen = 5;
                        break;
                    case TestCase.testMinWordFreq:
                        bow.MinWordFreq = 3;
                        break;
                    case TestCase.testNormalizeVectors:
                        bow.NormalizeVectors = false;
                        break;
                    case TestCase.testKeepWordForms:
                        bow.KeepWordForms = true;
                        break;
                    case TestCase.testLemmatizer:
                        bow.Stemmer = new Lemmatizer(Language.English);
                        break;
                    case TestCase.testStopWords:
                        bow.Stemmer = new Lemmatizer(Language.English);
                        bow.StopWords = StopWords.EnglishStopWords;
                        break;
                }
                bow.Initialize(documents, testCase == TestCase.testInitializeLargeScale);
                SparseVector<double> vector = bow.ProcessDocument(Document);

                results.Add(testCase.ToString(), vector);
            }

            File.WriteAllText("vec.json", JsonConvert.SerializeObject(results));
            Console.WriteLine(results);
        }
    }
}
