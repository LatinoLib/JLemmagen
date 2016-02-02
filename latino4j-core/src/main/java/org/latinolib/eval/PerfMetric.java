package org.latinolib.eval;

/**
 * Author saxo
 */
public enum PerfMetric
{
    ACCURACY,
    ERROR,
    // special
    K_ALPHA_NOMINAL,

    // micro-averaged
    MICRO_PRECISION,
    MICRO_RECALL,
    MICRO_F1,

    // macro-averaged
    MACRO_PRECISION,
    MACRO_RECALL,
    MACRO_F1,

    ACC_STD_ERROR_CONF_90,
    ACC_STD_ERROR_CONF_95,
    ACC_STD_ERROR_CONF_99
}
