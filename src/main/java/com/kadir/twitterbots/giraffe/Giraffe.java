package com.kadir.twitterbots.giraffe;

import com.kadir.twitterbots.giraffe.exporter.Exporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterException;

import java.io.IOException;

/**
 * @author akadir
 * Date: 15/01/2019
 * Time: 21:28
 */
public class Giraffe {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) throws InterruptedException {
        Giraffe giraffe = new Giraffe();
        giraffe.start();
    }

    private void start() throws InterruptedException {
        try {
            Exporter exporter = new Exporter();
            exporter.authenticate();
            exporter.run();
        } catch (IOException | TwitterException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            throw e;
        }
    }

}
