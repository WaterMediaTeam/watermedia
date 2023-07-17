package me.srrapero720.watermedia;

import twitter4j.*;

public class TwitterVideoURL {
    public static void main(String[] args) {
        // Configura las credenciales de la API de Twitter
        String consumerKey = "TU_CONSUMER_KEY";
        String consumerSecret = "TU_CONSUMER_SECRET";
        String accessToken = "TU_ACCESS_TOKEN";
        String accessTokenSecret = "TU_ACCESS_TOKEN_SECRET";

        // Crea una instancia de TwitterFactory con las credenciales
        TwitterFactory twitterFactory = new TwitterFactory();
        Twitter twitter = twitterFactory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));

        // ID del tweet a buscar
        long tweetId = 1678735680791257088L;

        try {
            // Obtiene el tweet usando su ID
            Status status = twitter.showStatus(tweetId);

            // Comprueba si hay un video adjunto
            MediaEntity[] mediaEntities = status.getMediaEntities();
            for (MediaEntity mediaEntity : mediaEntities) {
                if (mediaEntity.getType().equals("video")) {
                    // Obtiene la URL del video
                    String videoURL = mediaEntity.getVideoVariants()[0].getUrl();
                    System.out.println("URL del video: " + videoURL);
                    break;
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
