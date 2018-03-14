package edu.knoldus;

final class TwitterApplication {

    public static void main(String[] args) {

        TwitterOperation twitterOperation = new TwitterOperation();
        twitterOperation.getLatestPost().thenAccept(System.out::println);
        twitterOperation.getOldToNewTweets().thenAccept(System.out::println);
        twitterOperation.higherToLowerReTweetsCount().thenAccept(System.out::println);
        twitterOperation.higherToLowerLikesCount().thenAccept(System.out::println);
        twitterOperation.getTweets("2018-03-11").thenAccept(System.out::println);
        twitterOperation.getLikesInInterval("#ipl").thenAccept(System.out::println);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
