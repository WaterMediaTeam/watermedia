package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.url.UrlAPI;
import me.srrapero720.watermedia.api.url.fixers.URLFixer;

public class FixerTester {
    public static void main(String[] args) {
        try {
            UrlAPI.init(null);
        } catch (Exception e) {}

        URLFixer.Result result = UrlAPI.fixURL("https://imgur.com/t/rick_roll/kGy6J8J");
        System.out.println("Result is: " + result);

        result = UrlAPI.fixURL("https://imgur.com/4JMVauZ");
        System.out.println("Result is: " + result);

        result = UrlAPI.fixURL("https://imgur.com/a/TIiiR21");
        System.out.println("Result is: " + result);

        result = UrlAPI.fixURL("https://imgur.com/t/art/gm4Q7kN");
        System.out.println("Result is: " + result);

        System.exit(0);
    }
}