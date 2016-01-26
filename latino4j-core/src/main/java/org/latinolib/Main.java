package org.latinolib;

import java.io.IOException;
import static org.latinolib.Language.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(new PorterStemmer().getStem("friendships"));
        System.out.println(EN.getLemmatizer().getStem("trilogies"));
        System.out.println(SL.getLemmatizer().getStem("trilogije"));
    }
}
