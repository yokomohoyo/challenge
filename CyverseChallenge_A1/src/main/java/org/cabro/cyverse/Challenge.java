package org.cabro.cyverse;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cabro.cyverse.challenge.a.Publisher;

/**
 * Entry point
 */
public class Challenge {
    private static int COUNT = 10;
    private static String NATS_SERVER = "nats://my-nats:4222";
    private static final Logger log = LogManager.getLogger(Challenge.class);

    public static void main(String[] args) {
        // Set up CLI
        final Options options = new Options();
        options.addOption(new Option("c", "count", true, "How many quotes to publish (Default = 10)"));
        options.addOption(new Option("n", "nats", true, "Address of NATS server to publish to (Default = " + NATS_SERVER + ")"));
        options.addOption(new Option("h", "help", false, "Show help"));

        // parse args
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("c"))
                COUNT = Integer.parseInt(cmd.getOptionValue("c"));
            if (cmd.hasOption("n"))
                NATS_SERVER = cmd.getOptionValue("n");
            if (cmd.hasOption("h")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp( "challenge", options);
                System.exit(0);
            }
        } catch (ParseException pe) {
            log.error("Invalid CLI arguments. ", pe);
        }

        // Start the publisher
        log.debug("Starting publisher loop...");
        Publisher p = new Publisher(COUNT, NATS_SERVER);
        p.start();
    }
}
