package org.watermedia;

import java.net.URI;

public class Testo {
    public static void main(String[] args) {
        WaterMedia.LOGGER.info("ne");

        URI u = URI.create("water://local.wm/");
        WaterMedia.LOGGER.info("AUTHORITY: {} || HOST: {} || PROTOCOL: {}", u.getAuthority(), u.getHost(), u.getScheme());

//        String[] arr1 = new String[] { "value-1", "value-2", "value-3" };
//        String[] arr2 = new String[] { "extra-1", "extra-2", "extra-3" };
//        System.out.println(Arrays.toString(arr1));
//        System.out.println(Arrays.toString(arr2));
//
//        String[] combined = DataTool.data(arr1, arr2);
//        System.out.println(Arrays.toString(combined));


    }
}