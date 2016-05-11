package quickstep;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import quickstep.QuickstepContext.Mode;

import cucumber.api.cli.Main;
import cucumber.runtime.CucumberException;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;

public class CucumberRunner {

    boolean success = true;

    QuickstepContext context;

    int maxThreads = 1;
    Mode mode;
    public static String featuresLocation = "src" + File.separator + "test" + File.separator + "resources"
            + File.separator + "cucumber" + File.separator + "features";

    List<Process> processes;
    List<String> featureFiles;
    List<String> cucumberTags;

    Map<Process, Integer> processMap = new HashMap<Process, Integer>();
    int lastProcessID = 0;

    public static void main(String[] args) throws Throwable {
        // String oldWorkingDir = new java.io.File(".").getCanonicalPath());
        // System.setProperty( "user.dir", context.get )

        new CucumberRunner();
    }

    public CucumberRunner() throws Throwable {

        // Obtain test run info and display
        context = QuickstepContext.getInstance();
        maxThreads = context.getMaxThreads();

        mode = context.getMode();

        // Build feature files list
        buildFeatureFilesList();

        // Build Cucumber Tag List
        buildCucumberTagList();

        // Display test run parameters to user
        System.out.println("\n\n===========================================");
        System.out.println("Test execution parameters");
        System.out.println("===========================================");
        System.out.println("Mode:        " + mode.toString());
        System.out.println("Max threads: " + maxThreads);
        System.out.println("Browser:     " + context.getBrowserType().toString());
        System.out.println("Features:    " + featureFiles.size());
        System.out.println("Environment: " + context.getEnvironment().getId());
        System.out.println("Tags       : " + cucumberTags.toString());
        System.out.println("Context    : " + context.getClass().getCanonicalName());
        System.out.println("Working Dir: " + context.getWorkingDirectory());
        System.out.println("===========================================\n\n");

        // Save context to maintain consistency with forked processes
        // TODO: Find a better mechanism for this which preserves current
        // properties file
        context.save();

        // Extract any required resources from Quickstep
        System.out.println("Extracting resources from Quickstep");
        extractResources();
        System.out.println("Resource extraction complete. Launching tests.\n");

        // Make magic happen
        manageThreads();
    }

    private void extractResourceToFile(String resource, String path) {
        extractResourceToFile(resource, path, false);
    }

    private void extractResourceToFile(String resource, String path, boolean makeExecutable) {

        System.out.println("===> Extracting " + resource + " to " + path);

        BufferedInputStream reader = new BufferedInputStream(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(resource));

        try {

            // First check that required directory structure exists and create
            // if required
            File f = new File(path).getParentFile();

            if (!f.exists()) {
                f.mkdirs();
            }

            // Write resource to file
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(path));

            while (reader.available() > 0) {
                byte[] buffer = new byte[reader.available()];
                reader.read(buffer);
                writer.write(buffer);
            }

            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (makeExecutable) {
            // This will only work on Linux and Mac
            try {
                String cmd = "chmod +x " + path;
                System.out.println("===> " + cmd);
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void extractResources() {

        // Windows-specific resources
        if (System.getProperty("os.name").toLowerCase().contains("win")) {

            // Phantomjs
            extractResourceToFile("binaries/windows/phantomjs/phantomjs.exe", "target" + File.separator
                    + "test-classes" + File.separator + "binaries" + File.separator + "phantomjs" + File.separator
                    + "phantomjs.exe");
        }

        // Mac-specific resources
        else if (System.getProperty("os.name").toLowerCase().contains("mac os")) {

            // Phantomjs
            extractResourceToFile("binaries/mac/phantomjs/phantomjs", "target" + File.separator + "test-classes"
                    + File.separator + "binaries" + File.separator + "phantomjs" + File.separator + "phantomjs", true);
        }

        else {
            // Assume Linux OS
            // Phantomjs
            extractResourceToFile("binaries/linux/phantomjs/phantomjs", "target" + File.separator + "test-classes"
                    + File.separator + "binaries" + File.separator + "phantomjs" + File.separator + "phantomjs", true);
        }

    }

    private void buildCucumberTagList() {

        cucumberTags = new ArrayList<String>();

        if (mode == Mode.dev) {
            cucumberTags.add("@wip");
        } else if (mode == Mode.test) {
            cucumberTags.add("~@new");
            cucumberTags.add("~@manual");
            cucumberTags.add("~@to_automate");
            cucumberTags.add("~@wip");
            cucumberTags.add("~@broken");
            // cucumberTags.add("~@sample");
        } else if (mode == Mode.custom) {
            // Do nothing. Custom tags will be used as passed in via -Dtags=....
        }

        String[] customTags = context.getTags();
        for (String tag : customTags) {
            cucumberTags.add(tag);
        }
    }

    private void buildFeatureFilesList() throws Throwable {

        featureFiles = new ArrayList<String>();

        // Recursively list all files in featuresLocation
        File featuresDirectory = new File(context.getAbsolutePath(featuresLocation));
        Collection<File> allFiles = null;

        try {
            System.out.println("Looking for feature files in " + featuresDirectory.getAbsolutePath());
            allFiles = FileUtils.listFiles(featuresDirectory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

            // Filter out files which are not feature files
            for (File f : allFiles) {
                if (f.getAbsolutePath().endsWith(".feature")) {
                    featureFiles.add(f.getAbsolutePath());
                }
            }
        } catch (IllegalArgumentException iae) {

            if (iae.getMessage().contains("is not a directory")) {
                throw new Exception(
                        "\n*****\n* Error: No feature files found.  Please place your feature files in a sub-directory of "
                                + featuresDirectory.getAbsolutePath() + "\n*****\n");
            }

        }

    }

    private void killAllProcesses() {
        for (Process p : processes) {
            p.destroy();
        }
    }

    private void singleThreadExecution() throws Throwable {

        long startTime = System.currentTimeMillis();

        System.out.println("Running all features in " + context.getAbsolutePath(featuresLocation));

        List<String> args = new ArrayList<String>();
        args.add(context.getAbsolutePath(featuresLocation));
        args.add("--glue");
        args.add("com.nature.quickstep");

        for (String tag : cucumberTags) {
            args.add("--tags");
            args.add(tag);
        }

        args.add("--format");
        args.add("json:" + context.getAbsolutePath("target/cucumber-report/report" + ".json"));

        args.add("--format");
        args.add("html:" + context.getAbsolutePath("target/cucumber-report/html"));

        cucumber.runtime.Runtime runtime = null;

        try {
            // Main.main(args.toArray(new String[0]));

            // In the case of single-threaded execution, we re-implement code
            // from cucumber.api.cli.Main.main(String) because that method
            // currently uses System.exit() to terminate in this is not always
            // desirable for us. The following code is copied directly from
            // https://github.com/cucumber/cucumber-jvm/blob/master/core/src/main/java/cucumber/api/cli/Main.java
            // but the System.exit() command is not used.

            // IMPORTANT NOTE: Check this code when upgrading to new versions of
            // Cucumber
        	
        	//below mentioned 3 files are commented by Ravinder Kumar

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            //RuntimeOptions runtimeOptions = new RuntimeOptions(System.getProperties(), args.toArray(new String[0]));
            //runtime = new cucumber.runtime.Runtime(new MultiLoader(classLoader), classLoader, runtimeOptions);
            //runtime.writeStepdefsJson();
            //runtime.run();

            // Since, we do not use System.exit() now, we need to run shutdown
            // hooks manually
            context.runShutdownHooks();

        } catch (CucumberException ce) {
            if (ce.getMessage().contains("None of the features at")) {
                System.out.println("None of the features matched the filters provided.");
            } else {
                ce.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();

        }

        // Check exit status
        if (runtime != null && runtime.exitStatus() != 0) {

            List<Throwable> errors = runtime.getErrors();
            for (Throwable error : errors) {
                throw error;
            }
        }

        System.out.println("\n\n===========================================");
        System.out.println("Total System Test Duration: " + (System.currentTimeMillis() - startTime) / 1000
                + " seconds");
        System.out.println("===========================================\n\n");

    }

    private void manageThreads() throws Throwable {

        if (maxThreads == 1) {
            singleThreadExecution();
        } else {
            multiThreadExecution();

            if (!success) {
                throw new Exception("Cucumber errors detected.");
            }
        }
    }

    public void multiThreadExecution() {

        long startTime = System.currentTimeMillis();

        // Install a shutdown hook to kill all processes in case of a forced
        // shutdown
        context.addShutdownHook(new Thread() {
            @Override
            public void run() {
                killAllProcesses();
            }
        });

        processes = new ArrayList<Process>();

        while (!featureFiles.isEmpty()) {

            // Check if a free thread exists
            if (processes.size() < maxThreads) {
                startProcess(featureFiles.get(0));

                try {
                    // Allow 1 second before starting the next process in order
                    // to mitigate risk of multiple processes trying to start a
                    // browser at exactly the same time
                    Thread.sleep(1000);
                } catch (Exception e) {
                }

                featureFiles.remove(0);
            } else {

                // Wait for a free thread
                try {
                    Thread.sleep(1000);
                    for (Process process : processes) {

                        try {
                            int exitValue = process.exitValue();

                            // Check if there were any failures
                            success = success & (exitValue == 0);

                            dumpProcessOutput(process);
                            processes.remove(process);
                            break;
                        } catch (IllegalThreadStateException itse) {
                            // Thread is still alive
                        }

                    }
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

        // Wait for all threads to finish
        while (!processes.isEmpty()) {
            try {
                Thread.sleep(1000);
                for (Process process : processes) {
                    try {
                        int exitValue = process.exitValue();

                        // Check if there were any failures and update build
                        // success flag
                        success = success & (exitValue == 0);

                        dumpProcessOutput(process);
                        processes.remove(process);
                        break;
                    } catch (IllegalThreadStateException itse) {
                        // Thread is still alive
                    }

                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        System.out.println("\n\n===========================================");
        System.out.println("Total System Test Duration: " + (System.currentTimeMillis() - startTime) / 1000
                + " seconds");
        System.out.println("===========================================\n\n");

    }

    private void startProcess(String featureFile) {

        lastProcessID++;
        int processID = lastProcessID;

        featureFile = new File(featureFile).getAbsolutePath();

        System.out.println("Running " + featureFile + " (Process ID: " + lastProcessID + ")");

        // Build Classpath

        StringBuffer sb = new StringBuffer();

        URL[] urls = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();

        for (URL url : urls) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparator);
            }

            try {
                sb.append(new File(url.toURI()).getAbsolutePath());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        String classpath = sb.toString();

        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";

        List<String> args = new ArrayList<String>();

        args.add(path);
        args.add("-cp");
        args.add(classpath);
        args.add(Main.class.getName());
        args.add(featureFile);
        args.add("--glue");
        args.add("com.nature.quickstep");

        for (String tag : cucumberTags) {
            args.add("--tags");
            args.add(tag);
        }

        args.add("--format");
        args.add("json:" + context.getAbsolutePath("target/cucumber-report/report-" + processID + ".json"));

        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process;

        try {
            process = processBuilder.start();
            processMap.put(process, new Integer(processID));
            processes.add(process);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dumpProcessOutput(Process p) {
        System.out.println("\n\n===========================================");
        System.out.println("Process output stream dump for process with ID " + processMap.get(p));
        System.out.println("===========================================");
        System.out.println(getProcessOutput(p));
        System.out.println("===========================================");
        System.out.println("Process error stream dump for process with ID " + processMap.get(p));
        System.out.println("===========================================");
        System.out.println(getProcessErrors(p));
        System.out.println("===========================================\n\n");

    }

    public String getProcessOutput(Process p) {

        StringBuffer result = new StringBuffer();

        InputStream is = p.getInputStream();

        try {
            while (is.available() > 0) {
                result.append((char) is.read());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();

    }

    public String getProcessErrors(Process p) {

        StringBuffer result = new StringBuffer();

        InputStream is = p.getErrorStream();

        try {
            while (is.available() > 0) {
                result.append((char) is.read());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();

    }
}
