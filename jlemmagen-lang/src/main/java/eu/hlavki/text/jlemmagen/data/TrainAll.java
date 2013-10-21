/*
 * Copyright (C) 2013 Michal Hlavac <hlavki@hlavki.eu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.hlavki.text.jlemmagen.data;

import eu.hlavki.text.lemmagen.LemmatizerFactory;
import eu.hlavki.text.lemmagen.impl.DefaultLemmatizer;
import eu.hlavki.text.lemmagen.impl.LemmatizerSettings;
import eu.hlavki.text.lemmagen.impl.LemmatizerSettings.MsdConsideration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Michal Hlavac <hlavki@hlavki.eu>
 */
public final class TrainAll {

    public static void main(String[] args) throws Exception {
        File dir = new File(".");
        if (dir.isDirectory()) {
            LemmatizerSettings settings = new LemmatizerSettings();
            settings.setUseFromInRules(false);
            settings.setMsdConsider(MsdConsideration.IGNORE);
            settings.setMaxRulesPerNode(0);
            settings.setBuildFrontLemmatizer(true);
            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".tbl")) {
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new FileReader(file));
                        System.out.println("Reading " + file.getName());
                        DefaultLemmatizer lm = new DefaultLemmatizer(br, "WLM", settings);
                        System.out.println("Building " + file.getName());
                        lm.buildModel();
                        String filename = file.getName().substring(0, file.getName().lastIndexOf("."));
                        System.out.println("Saving to " + filename + ".lem");
                        LemmatizerFactory.saveToFile(lm, new File(filename + ".lem"));
                    } finally {
                        try {
                            if (br != null) br.close();
                        } catch (IOException e) {
                            System.out.println("Cant close stream");
                        }
                    }
                }
            }
        } else {
            System.out.println(dir.getAbsolutePath() + " is not directory");
        }
    }
}
