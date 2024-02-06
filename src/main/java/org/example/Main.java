package org.example;

import java.io.IOException;
import java.net.URISyntaxException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        WeatherManager w = new WeatherManager();
        w.createEndPoint();
    }
}