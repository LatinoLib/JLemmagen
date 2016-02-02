package org.latinolib.eval;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Author saxo
 */
public class StdErrTablesTest
{
    @Test
    public void testGetProb() throws Exception {
        assertEquals(0.95, StdErrTables.getProb(1.96), 0.0001);
    }

    @Test
    public void testGetZScore() throws Exception {
        assertEquals(1.96, StdErrTables.getZScore(0.95), 0.0001);
    }
}