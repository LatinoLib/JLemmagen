// This file was generated automatically by the Snowball to Java compiler

package org.tartarus.snowball.ext;

import org.tartarus.snowball.MyAmong;

 /**
  * This class was automatically generated by a Snowball to Java compiler 
  * It implements the stemming algorithm defined by a snowball script.
  */

public class swedishStemmer extends org.tartarus.snowball.SnowballStemmer {

private static final long serialVersionUID = 1L;

        //private final static swedishStemmer methodObject = new swedishStemmer ();

                private final static MyAmong a_0[] = {
                    new MyAmong ( "a", -1, 1 ),
                    new MyAmong ( "arna", 0, 1 ),
                    new MyAmong ( "erna", 0, 1 ),
                    new MyAmong ( "heterna", 2, 1 ),
                    new MyAmong ( "orna", 0, 1 ),
                    new MyAmong ( "ad", -1, 1 ),
                    new MyAmong ( "e", -1, 1 ),
                    new MyAmong ( "ade", 6, 1 ),
                    new MyAmong ( "ande", 6, 1 ),
                    new MyAmong ( "arne", 6, 1 ),
                    new MyAmong ( "are", 6, 1 ),
                    new MyAmong ( "aste", 6, 1 ),
                    new MyAmong ( "en", -1, 1 ),
                    new MyAmong ( "anden", 12, 1 ),
                    new MyAmong ( "aren", 12, 1 ),
                    new MyAmong ( "heten", 12, 1 ),
                    new MyAmong ( "ern", -1, 1 ),
                    new MyAmong ( "ar", -1, 1 ),
                    new MyAmong ( "er", -1, 1 ),
                    new MyAmong ( "heter", 18, 1 ),
                    new MyAmong ( "or", -1, 1 ),
                    new MyAmong ( "s", -1, 2 ),
                    new MyAmong ( "as", 21, 1 ),
                    new MyAmong ( "arnas", 22, 1 ),
                    new MyAmong ( "ernas", 22, 1 ),
                    new MyAmong ( "ornas", 22, 1 ),
                    new MyAmong ( "es", 21, 1 ),
                    new MyAmong ( "ades", 26, 1 ),
                    new MyAmong ( "andes", 26, 1 ),
                    new MyAmong ( "ens", 21, 1 ),
                    new MyAmong ( "arens", 29, 1 ),
                    new MyAmong ( "hetens", 29, 1 ),
                    new MyAmong ( "erns", 21, 1 ),
                    new MyAmong ( "at", -1, 1 ),
                    new MyAmong ( "andet", -1, 1 ),
                    new MyAmong ( "het", -1, 1 ),
                    new MyAmong ( "ast", -1, 1 )
                };

                private final static MyAmong a_1[] = {
                    new MyAmong ( "dd", -1, -1 ),
                    new MyAmong ( "gd", -1, -1 ),
                    new MyAmong ( "nn", -1, -1 ),
                    new MyAmong ( "dt", -1, -1 ),
                    new MyAmong ( "gt", -1, -1 ),
                    new MyAmong ( "kt", -1, -1 ),
                    new MyAmong ( "tt", -1, -1 )
                };

                private final static MyAmong a_2[] = {
                    new MyAmong ( "ig", -1, 1 ),
                    new MyAmong ( "lig", 0, 1 ),
                    new MyAmong ( "els", -1, 1 ),
                    new MyAmong ( "fullt", -1, 3 ),
                    new MyAmong ( "l\u00F6st", -1, 2 )
                };

                private static final char g_v[] = {17, 65, 16, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 32 };

                private static final char g_s_ending[] = {119, 127, 149 };

        private int I_x;
        private int I_p1;

                private void copy_from(swedishStemmer other) {
                    I_x = other.I_x;
                    I_p1 = other.I_p1;
                    super.copy_from(other);
                }

                private boolean r_mark_regions() {
            int v_1;
            int v_2;
                    // (, line 26
                    I_p1 = limit;
                    // test, line 29
                    v_1 = cursor;
                    // (, line 29
                    // hop, line 29
                    {
                        int c = cursor + 3;
                        if (0 > c || c > limit)
                        {
                            return false;
                        }
                        cursor = c;
                    }
                    // setmark x, line 29
                    I_x = cursor;
                    cursor = v_1;
                    // goto, line 30
                    golab0: while(true)
                    {
                        v_2 = cursor;
                        lab1: do {
                            if (!(in_grouping(g_v, 97, 246)))
                            {
                                break lab1;
                            }
                            cursor = v_2;
                            break golab0;
                        } while (false);
                        cursor = v_2;
                        if (cursor >= limit)
                        {
                            return false;
                        }
                        cursor++;
                    }
                    // gopast, line 30
                    golab2: while(true)
                    {
                        lab3: do {
                            if (!(out_grouping(g_v, 97, 246)))
                            {
                                break lab3;
                            }
                            break golab2;
                        } while (false);
                        if (cursor >= limit)
                        {
                            return false;
                        }
                        cursor++;
                    }
                    // setmark p1, line 30
                    I_p1 = cursor;
                    // try, line 31
                    lab4: do {
                        // (, line 31
                        if (!(I_p1 < I_x))
                        {
                            break lab4;
                        }
                        I_p1 = I_x;
                    } while (false);
                    return true;
                }

                private boolean r_main_suffix() {
            int among_var;
            int v_1;
            int v_2;
                    // (, line 36
                    // setlimit, line 37
                    v_1 = limit - cursor;
                    // tomark, line 37
                    if (cursor < I_p1)
                    {
                        return false;
                    }
                    cursor = I_p1;
                    v_2 = limit_backward;
                    limit_backward = cursor;
                    cursor = limit - v_1;
                    // (, line 37
                    // [, line 37
                    ket = cursor;
                    // substring, line 37
                    among_var = find_among_b(a_0, 37);
                    if (among_var == 0)
                    {
                        limit_backward = v_2;
                        return false;
                    }
                    // ], line 37
                    bra = cursor;
                    limit_backward = v_2;
                    switch(among_var) {
                        case 0:
                            return false;
                        case 1:
                            // (, line 44
                            // delete, line 44
                            slice_del();
                            break;
                        case 2:
                            // (, line 46
                            if (!(in_grouping_b(g_s_ending, 98, 121)))
                            {
                                return false;
                            }
                            // delete, line 46
                            slice_del();
                            break;
                    }
                    return true;
                }

                private boolean r_consonant_pair() {
            int v_1;
            int v_2;
            int v_3;
                    // setlimit, line 50
                    v_1 = limit - cursor;
                    // tomark, line 50
                    if (cursor < I_p1)
                    {
                        return false;
                    }
                    cursor = I_p1;
                    v_2 = limit_backward;
                    limit_backward = cursor;
                    cursor = limit - v_1;
                    // (, line 50
                    // and, line 52
                    v_3 = limit - cursor;
                    // among, line 51
                    if (find_among_b(a_1, 7) == 0)
                    {
                        limit_backward = v_2;
                        return false;
                    }
                    cursor = limit - v_3;
                    // (, line 52
                    // [, line 52
                    ket = cursor;
                    // next, line 52
                    if (cursor <= limit_backward)
                    {
                        limit_backward = v_2;
                        return false;
                    }
                    cursor--;
                    // ], line 52
                    bra = cursor;
                    // delete, line 52
                    slice_del();
                    limit_backward = v_2;
                    return true;
                }

                private boolean r_other_suffix() {
            int among_var;
            int v_1;
            int v_2;
                    // setlimit, line 55
                    v_1 = limit - cursor;
                    // tomark, line 55
                    if (cursor < I_p1)
                    {
                        return false;
                    }
                    cursor = I_p1;
                    v_2 = limit_backward;
                    limit_backward = cursor;
                    cursor = limit - v_1;
                    // (, line 55
                    // [, line 56
                    ket = cursor;
                    // substring, line 56
                    among_var = find_among_b(a_2, 5);
                    if (among_var == 0)
                    {
                        limit_backward = v_2;
                        return false;
                    }
                    // ], line 56
                    bra = cursor;
                    switch(among_var) {
                        case 0:
                            limit_backward = v_2;
                            return false;
                        case 1:
                            // (, line 57
                            // delete, line 57
                            slice_del();
                            break;
                        case 2:
                            // (, line 58
                            // <-, line 58
                            slice_from("l\u00F6s");
                            break;
                        case 3:
                            // (, line 59
                            // <-, line 59
                            slice_from("full");
                            break;
                    }
                    limit_backward = v_2;
                    return true;
                }

                public boolean stem() {
            int v_1;
            int v_2;
            int v_3;
            int v_4;
                    // (, line 64
                    // do, line 66
                    v_1 = cursor;
                    lab0: do {
                        // call mark_regions, line 66
                        if (!r_mark_regions())
                        {
                            break lab0;
                        }
                    } while (false);
                    cursor = v_1;
                    // backwards, line 67
                    limit_backward = cursor; cursor = limit;
                    // (, line 67
                    // do, line 68
                    v_2 = limit - cursor;
                    lab1: do {
                        // call main_suffix, line 68
                        if (!r_main_suffix())
                        {
                            break lab1;
                        }
                    } while (false);
                    cursor = limit - v_2;
                    // do, line 69
                    v_3 = limit - cursor;
                    lab2: do {
                        // call consonant_pair, line 69
                        if (!r_consonant_pair())
                        {
                            break lab2;
                        }
                    } while (false);
                    cursor = limit - v_3;
                    // do, line 70
                    v_4 = limit - cursor;
                    lab3: do {
                        // call other_suffix, line 70
                        if (!r_other_suffix())
                        {
                            break lab3;
                        }
                    } while (false);
                    cursor = limit - v_4;
                    cursor = limit_backward;                    return true;
                }

        public boolean equals( Object o ) {
            return o instanceof swedishStemmer;
        }

        public int hashCode() {
            return swedishStemmer.class.getName().hashCode();
        }



}

