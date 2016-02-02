package org.latinolib.eval;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * Author saxo
 */
public class StdErrTables
{
    public static double getProb(double zScore)
    {
        return getProbFromZeroToZ(zScore) * 2;
    }

    public static double getZScore(double prob)
    {
        return getZScoreOfProbFromZeroToZ(prob / 2);
    }

    public static double getProbFromZeroToZ(double zScore)
    {
        Preconditions.checkArgument(zScore >= 0);
        int idx = Math.abs(Collections.binarySearch(PAIRS_BY_SCORE, ScoreProbPair.byScore(zScore, Double.NaN)));
        return idx < PAIRS_BY_SCORE.size() ? PAIRS_BY_SCORE.get(idx).getProbability() : 0.49999;
    }

    public static double getZScoreOfProbFromZeroToZ(double probFromZeroToZ)
    {
        Preconditions.checkArgument(probFromZeroToZ >= 0);
        if (PAIRS_BY_PROB == null)
        {
            PAIRS_BY_PROB = Lists.newArrayList();
            for (ScoreProbPair pair : PAIRS_BY_SCORE) {
                PAIRS_BY_PROB.add(ScoreProbPair.byProb(pair.getScore(), pair.getProbability()));
            }
            Collections.sort(PAIRS_BY_PROB);
        }
        int idx = Math.abs(Collections.binarySearch(PAIRS_BY_PROB, ScoreProbPair.byProb(Double.NaN, probFromZeroToZ)));
        return PAIRS_BY_PROB.get(idx).getScore();
    }

    // probability that a statistic is between 0 (the mean) and Z
    // data source: https://en.wikipedia.org/wiki/Standard_normal_table
    private static List<ScoreProbPair> PAIRS_BY_PROB;
    private static final List<ScoreProbPair> PAIRS_BY_SCORE = Lists.newArrayList(
        ScoreProbPair.byScore(0, 0.00000), ScoreProbPair.byScore(0.01, 0.00399),
        ScoreProbPair.byScore(0.02, 0.00798), ScoreProbPair.byScore(0.03, 0.01197),
        ScoreProbPair.byScore(0.04, 0.01595), ScoreProbPair.byScore(0.05, 0.01994),
        ScoreProbPair.byScore(0.06, 0.02392), ScoreProbPair.byScore(0.07, 0.02790),
        ScoreProbPair.byScore(0.08, 0.03188), ScoreProbPair.byScore(0.09, 0.03586),
        ScoreProbPair.byScore(0.1, 0.03980), ScoreProbPair.byScore(0.11, 0.04380),
        ScoreProbPair.byScore(0.12, 0.04776), ScoreProbPair.byScore(0.13, 0.05172),
        ScoreProbPair.byScore(0.14, 0.05567), ScoreProbPair.byScore(0.15, 0.05966),
        ScoreProbPair.byScore(0.16, 0.06360), ScoreProbPair.byScore(0.17, 0.06749),
        ScoreProbPair.byScore(0.18, 0.07142), ScoreProbPair.byScore(0.19, 0.07535),
        ScoreProbPair.byScore(0.2, 0.07930), ScoreProbPair.byScore(0.21, 0.08317),
        ScoreProbPair.byScore(0.22, 0.08706), ScoreProbPair.byScore(0.23, 0.09095),
        ScoreProbPair.byScore(0.24, 0.09483), ScoreProbPair.byScore(0.25, 0.09871),
        ScoreProbPair.byScore(0.26, 0.10257), ScoreProbPair.byScore(0.27, 0.10642),
        ScoreProbPair.byScore(0.28, 0.11026), ScoreProbPair.byScore(0.29, 0.11409),
        ScoreProbPair.byScore(0.3, 0.11791), ScoreProbPair.byScore(0.31, 0.12172),
        ScoreProbPair.byScore(0.32, 0.12552), ScoreProbPair.byScore(0.33, 0.12930),
        ScoreProbPair.byScore(0.34, 0.13307), ScoreProbPair.byScore(0.35, 0.13683),
        ScoreProbPair.byScore(0.36, 0.14058), ScoreProbPair.byScore(0.37, 0.14431),
        ScoreProbPair.byScore(0.38, 0.14803), ScoreProbPair.byScore(0.39, 0.15173),
        ScoreProbPair.byScore(0.4, 0.15542), ScoreProbPair.byScore(0.41, 0.15910),
        ScoreProbPair.byScore(0.42, 0.16276), ScoreProbPair.byScore(0.43, 0.16640),
        ScoreProbPair.byScore(0.44, 0.17003), ScoreProbPair.byScore(0.45, 0.17364),
        ScoreProbPair.byScore(0.46, 0.17724), ScoreProbPair.byScore(0.47, 0.18082),
        ScoreProbPair.byScore(0.48, 0.18439), ScoreProbPair.byScore(0.49, 0.18793),
        ScoreProbPair.byScore(0.5, 0.19146), ScoreProbPair.byScore(0.51, 0.19497),
        ScoreProbPair.byScore(0.52, 0.19847), ScoreProbPair.byScore(0.53, 0.20194),
        ScoreProbPair.byScore(0.54, 0.20540), ScoreProbPair.byScore(0.55, 0.20884),
        ScoreProbPair.byScore(0.56, 0.21226), ScoreProbPair.byScore(0.57, 0.21566),
        ScoreProbPair.byScore(0.58, 0.21904), ScoreProbPair.byScore(0.59, 0.22240),
        ScoreProbPair.byScore(0.6, 0.22575), ScoreProbPair.byScore(0.61, 0.22907),
        ScoreProbPair.byScore(0.62, 0.23237), ScoreProbPair.byScore(0.63, 0.23565),
        ScoreProbPair.byScore(0.64, 0.23891), ScoreProbPair.byScore(0.65, 0.24215),
        ScoreProbPair.byScore(0.66, 0.24537), ScoreProbPair.byScore(0.67, 0.24857),
        ScoreProbPair.byScore(0.68, 0.25175), ScoreProbPair.byScore(0.69, 0.25490),
        ScoreProbPair.byScore(0.7, 0.25804), ScoreProbPair.byScore(0.71, 0.26115),
        ScoreProbPair.byScore(0.72, 0.26424), ScoreProbPair.byScore(0.73, 0.26730),
        ScoreProbPair.byScore(0.74, 0.27035), ScoreProbPair.byScore(0.75, 0.27337),
        ScoreProbPair.byScore(0.76, 0.27637), ScoreProbPair.byScore(0.77, 0.27935),
        ScoreProbPair.byScore(0.78, 0.28230), ScoreProbPair.byScore(0.79, 0.28524),
        ScoreProbPair.byScore(0.8, 0.28814), ScoreProbPair.byScore(0.81, 0.29103),
        ScoreProbPair.byScore(0.82, 0.29389), ScoreProbPair.byScore(0.83, 0.29673),
        ScoreProbPair.byScore(0.84, 0.29955), ScoreProbPair.byScore(0.85, 0.30234),
        ScoreProbPair.byScore(0.86, 0.30511), ScoreProbPair.byScore(0.87, 0.30785),
        ScoreProbPair.byScore(0.88, 0.31057), ScoreProbPair.byScore(0.89, 0.31327),
        ScoreProbPair.byScore(0.9, 0.31594), ScoreProbPair.byScore(0.91, 0.31859),
        ScoreProbPair.byScore(0.92, 0.32121), ScoreProbPair.byScore(0.93, 0.32381),
        ScoreProbPair.byScore(0.94, 0.32639), ScoreProbPair.byScore(0.95, 0.32894),
        ScoreProbPair.byScore(0.96, 0.33147), ScoreProbPair.byScore(0.97, 0.33398),
        ScoreProbPair.byScore(0.98, 0.33646), ScoreProbPair.byScore(0.99, 0.33891),
        ScoreProbPair.byScore(1, 0.34134), ScoreProbPair.byScore(1.01, 0.34375),
        ScoreProbPair.byScore(1.02, 0.34614), ScoreProbPair.byScore(1.03, 0.34849),
        ScoreProbPair.byScore(1.04, 0.35083), ScoreProbPair.byScore(1.05, 0.35314),
        ScoreProbPair.byScore(1.06, 0.35543), ScoreProbPair.byScore(1.07, 0.35769),
        ScoreProbPair.byScore(1.08, 0.35993), ScoreProbPair.byScore(1.09, 0.36214),
        ScoreProbPair.byScore(1.1, 0.36433), ScoreProbPair.byScore(1.11, 0.36650),
        ScoreProbPair.byScore(1.12, 0.36864), ScoreProbPair.byScore(1.13, 0.37076),
        ScoreProbPair.byScore(1.14, 0.37286), ScoreProbPair.byScore(1.15, 0.37493),
        ScoreProbPair.byScore(1.16, 0.37698), ScoreProbPair.byScore(1.17, 0.37900),
        ScoreProbPair.byScore(1.18, 0.38100), ScoreProbPair.byScore(1.19, 0.38298),
        ScoreProbPair.byScore(1.2, 0.38493), ScoreProbPair.byScore(1.21, 0.38686),
        ScoreProbPair.byScore(1.22, 0.38877), ScoreProbPair.byScore(1.23, 0.39065),
        ScoreProbPair.byScore(1.24, 0.39251), ScoreProbPair.byScore(1.25, 0.39435),
        ScoreProbPair.byScore(1.26, 0.39617), ScoreProbPair.byScore(1.27, 0.39796),
        ScoreProbPair.byScore(1.28, 0.39973), ScoreProbPair.byScore(1.29, 0.40147),
        ScoreProbPair.byScore(1.3, 0.40320), ScoreProbPair.byScore(1.31, 0.40490),
        ScoreProbPair.byScore(1.32, 0.40658), ScoreProbPair.byScore(1.33, 0.40824),
        ScoreProbPair.byScore(1.34, 0.40988), ScoreProbPair.byScore(1.35, 0.41149),
        ScoreProbPair.byScore(1.36, 0.41308), ScoreProbPair.byScore(1.37, 0.41466),
        ScoreProbPair.byScore(1.38, 0.41621), ScoreProbPair.byScore(1.39, 0.41774),
        ScoreProbPair.byScore(1.4, 0.41924), ScoreProbPair.byScore(1.41, 0.42073),
        ScoreProbPair.byScore(1.42, 0.42220), ScoreProbPair.byScore(1.43, 0.42364),
        ScoreProbPair.byScore(1.44, 0.42507), ScoreProbPair.byScore(1.45, 0.42647),
        ScoreProbPair.byScore(1.46, 0.42785), ScoreProbPair.byScore(1.47, 0.42922),
        ScoreProbPair.byScore(1.48, 0.43056), ScoreProbPair.byScore(1.49, 0.43189),
        ScoreProbPair.byScore(1.5, 0.43319), ScoreProbPair.byScore(1.51, 0.43448),
        ScoreProbPair.byScore(1.52, 0.43574), ScoreProbPair.byScore(1.53, 0.43699),
        ScoreProbPair.byScore(1.54, 0.43822), ScoreProbPair.byScore(1.55, 0.43943),
        ScoreProbPair.byScore(1.56, 0.44062), ScoreProbPair.byScore(1.57, 0.44179),
        ScoreProbPair.byScore(1.58, 0.44295), ScoreProbPair.byScore(1.59, 0.44408),
        ScoreProbPair.byScore(1.6, 0.44520), ScoreProbPair.byScore(1.61, 0.44630),
        ScoreProbPair.byScore(1.62, 0.44738), ScoreProbPair.byScore(1.63, 0.44845),
        ScoreProbPair.byScore(1.64, 0.44950), ScoreProbPair.byScore(1.65, 0.45053),
        ScoreProbPair.byScore(1.66, 0.45154), ScoreProbPair.byScore(1.67, 0.45254),
        ScoreProbPair.byScore(1.68, 0.45352), ScoreProbPair.byScore(1.69, 0.45449),
        ScoreProbPair.byScore(1.7, 0.45543), ScoreProbPair.byScore(1.71, 0.45637),
        ScoreProbPair.byScore(1.72, 0.45728), ScoreProbPair.byScore(1.73, 0.45818),
        ScoreProbPair.byScore(1.74, 0.45907), ScoreProbPair.byScore(1.75, 0.45994),
        ScoreProbPair.byScore(1.76, 0.46080), ScoreProbPair.byScore(1.77, 0.46164),
        ScoreProbPair.byScore(1.78, 0.46246), ScoreProbPair.byScore(1.79, 0.46327),
        ScoreProbPair.byScore(1.8, 0.46407), ScoreProbPair.byScore(1.81, 0.46485),
        ScoreProbPair.byScore(1.82, 0.46562), ScoreProbPair.byScore(1.83, 0.46638),
        ScoreProbPair.byScore(1.84, 0.46712), ScoreProbPair.byScore(1.85, 0.46784),
        ScoreProbPair.byScore(1.86, 0.46856), ScoreProbPair.byScore(1.87, 0.46926),
        ScoreProbPair.byScore(1.88, 0.46995), ScoreProbPair.byScore(1.89, 0.47062),
        ScoreProbPair.byScore(1.9, 0.47128), ScoreProbPair.byScore(1.91, 0.47193),
        ScoreProbPair.byScore(1.92, 0.47257), ScoreProbPair.byScore(1.93, 0.47320),
        ScoreProbPair.byScore(1.94, 0.47381), ScoreProbPair.byScore(1.95, 0.47441),
        ScoreProbPair.byScore(1.96, 0.47500), ScoreProbPair.byScore(1.97, 0.47558),
        ScoreProbPair.byScore(1.98, 0.47615), ScoreProbPair.byScore(1.99, 0.47670),
        ScoreProbPair.byScore(2, 0.47725), ScoreProbPair.byScore(2.01, 0.47778),
        ScoreProbPair.byScore(2.02, 0.47831), ScoreProbPair.byScore(2.03, 0.47882),
        ScoreProbPair.byScore(2.04, 0.47932), ScoreProbPair.byScore(2.05, 0.47982),
        ScoreProbPair.byScore(2.06, 0.48030), ScoreProbPair.byScore(2.07, 0.48077),
        ScoreProbPair.byScore(2.08, 0.48124), ScoreProbPair.byScore(2.09, 0.48169),
        ScoreProbPair.byScore(2.1, 0.48214), ScoreProbPair.byScore(2.11, 0.48257),
        ScoreProbPair.byScore(2.12, 0.48300), ScoreProbPair.byScore(2.13, 0.48341),
        ScoreProbPair.byScore(2.14, 0.48382), ScoreProbPair.byScore(2.15, 0.48422),
        ScoreProbPair.byScore(2.16, 0.48461), ScoreProbPair.byScore(2.17, 0.48500),
        ScoreProbPair.byScore(2.18, 0.48537), ScoreProbPair.byScore(2.19, 0.48574),
        ScoreProbPair.byScore(2.2, 0.48610), ScoreProbPair.byScore(2.21, 0.48645),
        ScoreProbPair.byScore(2.22, 0.48679), ScoreProbPair.byScore(2.23, 0.48713),
        ScoreProbPair.byScore(2.24, 0.48745), ScoreProbPair.byScore(2.25, 0.48778),
        ScoreProbPair.byScore(2.26, 0.48809), ScoreProbPair.byScore(2.27, 0.48840),
        ScoreProbPair.byScore(2.28, 0.48870), ScoreProbPair.byScore(2.29, 0.48899),
        ScoreProbPair.byScore(2.3, 0.48928), ScoreProbPair.byScore(2.31, 0.48956),
        ScoreProbPair.byScore(2.32, 0.48983), ScoreProbPair.byScore(2.33, 0.49010),
        ScoreProbPair.byScore(2.34, 0.49036), ScoreProbPair.byScore(2.35, 0.49061),
        ScoreProbPair.byScore(2.36, 0.49086), ScoreProbPair.byScore(2.37, 0.49111),
        ScoreProbPair.byScore(2.38, 0.49134), ScoreProbPair.byScore(2.39, 0.49158),
        ScoreProbPair.byScore(2.4, 0.49180), ScoreProbPair.byScore(2.41, 0.49202),
        ScoreProbPair.byScore(2.42, 0.49224), ScoreProbPair.byScore(2.43, 0.49245),
        ScoreProbPair.byScore(2.44, 0.49266), ScoreProbPair.byScore(2.45, 0.49286),
        ScoreProbPair.byScore(2.46, 0.49305), ScoreProbPair.byScore(2.47, 0.49324),
        ScoreProbPair.byScore(2.48, 0.49343), ScoreProbPair.byScore(2.49, 0.49361),
        ScoreProbPair.byScore(2.5, 0.49379), ScoreProbPair.byScore(2.51, 0.49396),
        ScoreProbPair.byScore(2.52, 0.49413), ScoreProbPair.byScore(2.53, 0.49430),
        ScoreProbPair.byScore(2.54, 0.49446), ScoreProbPair.byScore(2.55, 0.49461),
        ScoreProbPair.byScore(2.56, 0.49477), ScoreProbPair.byScore(2.57, 0.49492),
        ScoreProbPair.byScore(2.58, 0.49506), ScoreProbPair.byScore(2.59, 0.49520),
        ScoreProbPair.byScore(2.6, 0.49534), ScoreProbPair.byScore(2.61, 0.49547),
        ScoreProbPair.byScore(2.62, 0.49560), ScoreProbPair.byScore(2.63, 0.49573),
        ScoreProbPair.byScore(2.64, 0.49585), ScoreProbPair.byScore(2.65, 0.49598),
        ScoreProbPair.byScore(2.66, 0.49609), ScoreProbPair.byScore(2.67, 0.49621),
        ScoreProbPair.byScore(2.68, 0.49632), ScoreProbPair.byScore(2.69, 0.49643),
        ScoreProbPair.byScore(2.7, 0.49653), ScoreProbPair.byScore(2.71, 0.49664),
        ScoreProbPair.byScore(2.72, 0.49674), ScoreProbPair.byScore(2.73, 0.49683),
        ScoreProbPair.byScore(2.74, 0.49693), ScoreProbPair.byScore(2.75, 0.49702),
        ScoreProbPair.byScore(2.76, 0.49711), ScoreProbPair.byScore(2.77, 0.49720),
        ScoreProbPair.byScore(2.78, 0.49728), ScoreProbPair.byScore(2.79, 0.49736),
        ScoreProbPair.byScore(2.8, 0.49744), ScoreProbPair.byScore(2.81, 0.49752),
        ScoreProbPair.byScore(2.82, 0.49760), ScoreProbPair.byScore(2.83, 0.49767),
        ScoreProbPair.byScore(2.84, 0.49774), ScoreProbPair.byScore(2.85, 0.49781),
        ScoreProbPair.byScore(2.86, 0.49788), ScoreProbPair.byScore(2.87, 0.49795),
        ScoreProbPair.byScore(2.88, 0.49801), ScoreProbPair.byScore(2.89, 0.49807),
        ScoreProbPair.byScore(2.9, 0.49813), ScoreProbPair.byScore(2.91, 0.49819),
        ScoreProbPair.byScore(2.92, 0.49825), ScoreProbPair.byScore(2.93, 0.49831),
        ScoreProbPair.byScore(2.94, 0.49836), ScoreProbPair.byScore(2.95, 0.49841),
        ScoreProbPair.byScore(2.96, 0.49846), ScoreProbPair.byScore(2.97, 0.49851),
        ScoreProbPair.byScore(2.98, 0.49856), ScoreProbPair.byScore(2.99, 0.49861),
        ScoreProbPair.byScore(3, 0.49865), ScoreProbPair.byScore(3.01, 0.49869),
        ScoreProbPair.byScore(3.02, 0.49874), ScoreProbPair.byScore(3.03, 0.49878),
        ScoreProbPair.byScore(3.04, 0.49882), ScoreProbPair.byScore(3.05, 0.49886),
        ScoreProbPair.byScore(3.06, 0.49889), ScoreProbPair.byScore(3.07, 0.49893),
        ScoreProbPair.byScore(3.08, 0.49896), ScoreProbPair.byScore(3.09, 0.49900));

    private static class ScoreProbPair implements Comparable<ScoreProbPair>
    {
        private final double score;
        private final double probability;
        private final boolean byScore;

        private ScoreProbPair(double score, double probability, boolean byScore) {
            this.score = score;
            this.probability = probability;
            this.byScore = byScore;
        }

        public static ScoreProbPair byScore(double score, double probability) {
            return new ScoreProbPair(score, probability, true);
        }

        public static ScoreProbPair byProb(double score, double probability) {
            return new ScoreProbPair(score, probability, false);
        }
        
        public double getScore() {
            return score;
        }

        public double getProbability() {
            return probability;
        }

        @Override
        public int compareTo(ScoreProbPair o) {
            return byScore
                ? Double.compare(score, o.score)
                : Double.compare(probability, o.probability);
        }
    }
}
