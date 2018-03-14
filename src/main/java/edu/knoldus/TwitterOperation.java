package edu.knoldus;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TwitterOperation {

    private Twitter twitter;
    private Query query;

    TwitterOperation() {

        Config config = ConfigFactory.load();
        this.twitter = new TwitterFactory().getInstance();
        this.twitter.setOAuthConsumer(config.getString("consumerKey"),
                config.getString("consumerSecret"));
        this.twitter.setOAuthAccessToken(new AccessToken(
                config.getString("accessToken"),
                config.getString("accessTokenSecret")
        ));


    }

    /**
     * @return latest post of given hashTag.
     */
    public CompletableFuture<List<Status>> getLatestPost() {
        return CompletableFuture.supplyAsync(() -> {
            List<Status> latestTweets = Collections.emptyList();
            try {
                query = new Query("#cricket");
                query.setCount(50);
                query.resultType(Query.ResultType.recent);
                latestTweets = twitter.search(query).getTweets();
            } catch (TwitterException twitterException) {
                twitterException.printStackTrace();
            }

            return latestTweets;
        });
    }

    /**
     * @return older to newer tweets.
     */
    public CompletableFuture<List<Status>> getOldToNewTweets() {
        return CompletableFuture.supplyAsync(() -> {
            List<Status> tweets = Collections.emptyList();
            try {
                query = new Query("#ipl");
                query.setCount(50);
                query.resultType(Query.ResultType.recent);
                tweets = twitter.search(query).getTweets();
                tweets.sort(Comparator.comparingLong(tweet -> tweet.getCreatedAt().getTime()));
            } catch (TwitterException twitterException) {
                twitterException.printStackTrace();
            }
            return tweets;
        });
    }

    /**
     * @return higher to lower reTweets count.
     */
    public CompletableFuture<List<String>> higherToLowerReTweetsCount() {
        return CompletableFuture.supplyAsync(() -> {
            List<Status> higherToLowTweets = Collections.emptyList();
            try {
                higherToLowTweets = twitter.getHomeTimeline().stream()
                        .sorted(Comparator.comparing(Status::getRetweetCount).reversed())
                        .collect(Collectors.toList());
            } catch (TwitterException twitterException) {
                twitterException.printStackTrace();
            }

            return higherToLowTweets;
        }).thenApply(tweets ->
                tweets.stream()
                        .map(tweet -> tweet.getRetweetCount() + " " + tweet.getText())
                        .collect(Collectors.toList()));
    }

    /**
     * @return higher to lower likes count.
     */
    public CompletableFuture<List<String>> higherToLowerLikesCount() {
        return CompletableFuture.supplyAsync(() -> {
            List<Status> higherToLowTweets = Collections.emptyList();
            try {
                higherToLowTweets = twitter.getHomeTimeline().stream()
                        .sorted(Comparator.comparing(Status::getFavoriteCount).reversed())
                        .collect(Collectors.toList());

            } catch (TwitterException twitterException) {
                twitterException.printStackTrace();
            }
            return higherToLowTweets;
        }).thenApply(tweets ->
                tweets.stream()
                        .map(tweet -> tweet.getFavoriteCount() + " " + tweet.getText())
                        .collect(Collectors.toList()));
    }

    /**
     * @return get tweets on given date only.
     */
    public CompletableFuture<List<Status>> getTweets(String date) {
        return CompletableFuture.supplyAsync(() -> {
            List<Status> tweets = Collections.emptyList();
            try {
                query = new Query("#cricket");
                query.setSince(date);
                query.setUntil(LocalDate.parse(date).plusDays(1).toString());
                tweets = twitter.search(query).getTweets();
            } catch (TwitterException twitterException) {
                twitterException.printStackTrace();
            }
            return tweets;
        });
    }

    /**
     * @param hashTag as input.
     * @return twitter streaming in interval 15 minute (***not working***).
     */
    public CompletableFuture<List<Integer>> getLikesInInterval(String hashTag) {
        List<Integer> likes = new ArrayList<>();
        return CompletableFuture.supplyAsync(() -> {
            LocalDateTime specificDate = LocalDateTime.of(2018, 2, 10, 10, 10, 30);
            try {
                int count = 1;
                while (count <= 50) {
                    query = new Query(hashTag);
                    query.setUntil(specificDate.toString());
                    List<Status> currentTweets = twitter.search(query).getTweets();
                    int currentLike = currentTweets.stream().map(x -> x.getFavoriteCount())
                            .reduce((a, b) -> a + b).get();
                    likes.add(currentLike);
                    specificDate = specificDate.plusMinutes(15);
                    System.out.println(specificDate);
                    count++;
                }
            } catch (TwitterException ie) {
                ie.printStackTrace();
            }
            return likes;

        });

    }
}
