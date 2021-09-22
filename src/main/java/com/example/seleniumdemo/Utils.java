package com.example.seleniumdemo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Map;

public final class Utils {

    public static final String TPOT_LOGIN = "nativeEmail";
    public static final String TPOT_PASS = "nativePass";

    public static final String TABLEAU_LOGIN = "tableauEmail";
    public static final String TABLEAU_PASS = "tableauPass";

    public static void parseArguments(String[] args, Map<String, String> params) {
        Options seleniumDemoOptions = new Options();

        Option nativeEmail = new Option("nativeEmail", true, "Platform email");
        nativeEmail.setArgs(1);
        seleniumDemoOptions.addOption(nativeEmail);
        Option nativePassword = new Option("nativePass", true, "Platform password");
        nativePassword.setArgs(1);
        seleniumDemoOptions.addOption(nativePassword);
        Option tableauEmail = new Option("tableauEmail", true, "Tableau email");
        tableauEmail.setArgs(1);
        seleniumDemoOptions.addOption(tableauEmail);
        Option tableauPassword = new Option("tableauPass", true, "Tableau password");
        tableauPassword.setArgs(1);
        seleniumDemoOptions.addOption(tableauPassword);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(seleniumDemoOptions, args);
            if (commandLine.hasOption("nativeEmail")) {
                params.put("nativeEmail", commandLine.getOptionValues("nativeEmail")[0]);
            }
            if (commandLine.hasOption("nativePass")) {
                params.put("nativePass", commandLine.getOptionValues("nativePass")[0]);
            }
            if (commandLine.hasOption("tableauEmail")) {
                params.put("tableauEmail", commandLine.getOptionValues("tableauEmail")[0]);
            }
            if (commandLine.hasOption("tableauPass")) {
                params.put("tableauPass", commandLine.getOptionValues("tableauPass")[0]);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
