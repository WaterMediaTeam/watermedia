package me.srrapero720.watermedia.compat;

import me.srrapero720.watermedia.api.WaterMediaAPI;

public class Checker {
    public static void main(String ...args) {
        var url = WaterMediaAPI.urlPatch("https://1drv.ms/i/s!AoHO9f0CSCtxozX0Y6Noqo7Dg8CR?e=zPU5Ba");
        System.out.println(url);

        url = WaterMediaAPI.urlPatch("https://www.youtube.com/watch?v=hsXeFqj5p7Q&list=RDYIC1aK6CleE&index=6&ab_channel=Diversity");
        System.out.println(url);

        url = WaterMediaAPI.urlPatch("https://www.twitch.tv/lilousurtwitch/clip/UnsightlyGorgeousTruffleBrainSlug-pkyHV-eBlMh9l0Wz");
        System.out.println(url);

        url = WaterMediaAPI.urlPatch("https://www.twitch.tv/videos/1859049751");
        System.out.println(url);

        url = WaterMediaAPI.urlPatch("https://drive.google.com/file/d/1dW33Z_btpX1mR4vbSM2KFJbwNYfPP1Hu/view?usp=drive_link");
        System.out.println(url);


        // These URL may no works

    }
}