import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * Galactic Synopsis:
 *
 * This program is an implementation of The Merchant's Guide To The Galaxy.  It
 * assists the merchant user to perform intergalactic transactions in an 
 * accurate, efficient, and user-friendly manner by:
 *
 * - Allowing the merchant to provide the transactional specifications as well
 *   as the transactions themselves in plain English
 * - Accepting from the merchant a text file that contains the transactional
 *   specifications and the transactions that he needs answers for
 * 
 *
 * System Requirement:  
 *
 *             JDK 1.6+   (Tested: 1.6 and 1.7)
 *
 * * Usage:
 *
 *     To build: 
 *             javac -cp . Galactic.java
 *
 *     To run: 
 *             java -cp . Galactic
 * 
 *     [ To run with finest/full tracing on:  
 *                     java -cp . -Ddebug Galactic ]
 *
 *     Sample Input File:
 *             default_test_input.txt
 *
 *
 * ** Usage Note: 
 *    
 *    When no argument is given to the runtime, the program will read in the
 *    default file, default_test_input.txt, which contains a set of definitions
 *    and specifications, which will then be followed by a set of transactions 
 *    that this program will perform its calculations on.  The answers will be
 *    provided on standard output (stdout).
 *
 *
 * *** Disclaimer :) ***
 *    This program has been written under tight time constraints based on the
 *    specifications as outlined in the coding assignment.  There is still
 *    plenty of room for improvements, and it is the wish of the author
 *    to be able to further enhance it to achieve a higher level of 
 *    robustness.  As such, the processing is written to meet the specific
 *    rules and requirements as defined in the sample input and output
 *    files.  Any changes that will alter the current rules in the sample
 *    input test file may yield undesirable result. 
 * 
 */

public class Galactic {

    private final static Logger _log = Logger.getLogger(Galactic.class.getName());

    public static final String DEFAULT_INPUT_FILE = "default_test_input.txt";
    
    public static HashMap<String,Integer> valueTable = new HashMap();
    
    static {
        valueTable.put("I", 1);
        valueTable.put("V", 5);
        valueTable.put("X", 10);
        valueTable.put("L", 50);
        valueTable.put("C", 100);
        valueTable.put("D", 500);
        valueTable.put("M", 1000);
    }

    static final String ROMAN_NUMERAL_PATTERN = "\\b(M{0,3})(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})\\b";

    static final String SPECIFICATION_UNIT_MAPPING_PATTERN = "^[(glob)|(prok)|(pish)|(tegj)]+( is )[IVXLCDM]$";  /** e.g. glob is I */

    static final String SPECIFICATION_CREDITS_SAMPLE_PATTERN = "^[(glob\\s){0,2}|(prok\\s){0,2}|(pish\\s){0,2}|(tegj\\s){0,2}]+(Silver|Gold|Iron)\\s(is)\\s(\\d)+\\s(Credits)$"; // e.g. glob glob Silver is 34 Credits

    static final String QUESTION_UNIT_VALUE_PATTERN = "^(how much is)\\s[(glob\\s)|(prok\\s)|(pish\\s)|(tegj\\s)]+(\\s\\?)$"; /* e.g. how much is pish tegj glob glob ? */

    static final String QUESTION_TOTAL_CREDITS_PATTERN = "^(how many Credits is)\\s[(glob\\s)|(prok\\s)|(pish\\s)|(tegj\\s)]+(Silver|Gold|Iron)(\\s\\?)$"; /** e.g. how many Credits is glob prok Silver ? */
    

    public String filename = null;
    public HashMap<String,String> unitMap = new HashMap();
    public HashMap<String,Float> metalPerUnitCostMap = new HashMap();


    /**
     * Galactic: the constructor
     */
    public Galactic() {

        if (null != System.getProperty("debug")) {
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.FINEST);
            _log.setLevel(Level.FINEST);
            _log.addHandler(handler);
        } else {
            _log.setLevel(Level.INFO);
        }

    }
            
    /**
     * Name of input file
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }


    /**
     * Reads the input file
     */
    public void processFile() {

        File f = new File(getFilename());
        if (!f.exists() || !f.isFile()) {
            _log.severe("ERROR: Invalid test input file (" +getFilename()+ ")");
            return;
        }
        
        BufferedReader inbuf = null;
        try {
            inbuf = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String theLine = null;
            while (null != (theLine = inbuf.readLine())) {

                processLine(theLine);

            }
        } catch (FileNotFoundException fnfe) {  //should not reach this - so just in case
            _log.severe("ERROR: Invalid test input file (" +getFilename()+ ")");
            fnfe.printStackTrace();
        } catch (IOException ie) {
            _log.severe("ERROR: File IO Exception!\n");
            ie.printStackTrace();
        } catch (Exception e) {
            _log.severe("ERROR: File processing encounters an Exception!\n");
            e.printStackTrace();
        } finally {
            if (null != inbuf) {
                try {
                    inbuf.close();
                } catch (Exception e) {
                    _log.severe("ERROR: BufferedReader close!\n");
                }
            }
        }

    }

    /**
     * Processes the instruction as provided in the line
     */
    public void processLine(String theLine) {
        
        theLine = theLine.trim();

        if (theLine.matches(SPECIFICATION_UNIT_MAPPING_PATTERN)) {
            _log.finest("Line - Specification of Unit Mapping:\t" + theLine);
            processUnitMapping(theLine);
        } else if (theLine.matches(SPECIFICATION_CREDITS_SAMPLE_PATTERN)) {
            _log.finest("Line - Specification of Credit Sample:\t" + theLine);
            processCreditsSample(theLine);
        } else if (theLine.matches(QUESTION_UNIT_VALUE_PATTERN)) {
            _log.finest("Line - Question of Unit Value:\t" + theLine);
            processUnitValue(theLine);
        } else if (theLine.matches(QUESTION_TOTAL_CREDITS_PATTERN)) {
            _log.finest("Line - Question of Total Credits:\t" + theLine);
            processTotalCredits(theLine);

        } else {
            System.out.println("I have no idea what you are talking about");
        }        

    }
    
    /**
     * Specification: <Unit> is <Roman numeral symbol>
     *
     * Method: Build the Unit Mapping table
     */
    void processUnitMapping(String theLine) {
        String[] tokens = theLine.split("\\s+");
        if (tokens.length == 3) {
            unitMap.put(tokens[0], tokens[2]);
        }
    }
    
    /**
     * Specification sample: <Roman numeral symbol 1> <Roman numeral symbol 2> <Metal> is <Number> Credits
     * 
     * Method: Build the Metal Per-Unit Cost Mapping table
     */
    void processCreditsSample(String theLine) {
        String[] tokens = theLine.split("\\s+");
        
        String romanNum = unitMap.get(tokens[0]) + unitMap.get(tokens[1]);
        int numUnits = convertRomanToArabic(romanNum);

        String metal = tokens[2];
        String credits = tokens[4];
        float perUnitCost = Float.parseFloat(credits) / numUnits;
        metalPerUnitCostMap.put(metal, Float.valueOf(perUnitCost));
    }

    /**
     * Question: how much is <Unit 1> <Unit 2> <Unit 3> <Unit 4> ?
     *
     * Method: Calculate the Arabic numberic equivalent value of the given Roman Numerals as expressed in Galactic units
     */
    void processUnitValue(String theLine) {
        String[] tokens = theLine.split("\\s+");

        StringBuilder romanNumBuf = new StringBuilder();
        StringBuilder unitBuf = new StringBuilder();
        for (int i=3; i < (tokens.length-1); i++) {
            romanNumBuf.append(unitMap.get(tokens[i]));
            unitBuf.append(tokens[i]);
            unitBuf.append(" ");
        }
        
        System.out.println(unitBuf.toString() + "is " + convertRomanToArabic(romanNumBuf.toString()));
    }        
                           
    /**
     * Question: how many Credits is <Unit 1> <Unit 2> <Metal> ?
     *
     * Method: Calculate the Total Number of Credits for the given Galactic units of the Metal
     */
    void processTotalCredits(String theLine) {
        String[] tokens = theLine.split("\\s+");

        StringBuilder romanNumBuf = new StringBuilder();
        StringBuilder unitBuf = new StringBuilder();
        for (int i=4; i<6; i++) {
            romanNumBuf.append(unitMap.get(tokens[i]));
            unitBuf.append(tokens[i]);
            unitBuf.append(" ");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(unitBuf).append(tokens[6]).append(" is ");

        int numUnits = convertRomanToArabic(romanNumBuf.toString());
        float metalPerUnitCost = (metalPerUnitCostMap.get(tokens[6])).floatValue();
        float totalCredits = numUnits * metalPerUnitCost;

        sb.append(String.format("%.0f", Float.valueOf(totalCredits)));
        sb.append(" Credits");
        
        System.out.println(sb.toString());
    }


    /**
     * Converts the Roman numerals to its equivalent Arabic value
     */
    public static int convertRomanToArabic(String romanVal) {
        
        if (null != romanVal) {

            _log.finest("Input - Roman Value: " + romanVal);
            
            romanVal = romanVal.toUpperCase();

            if (romanVal.matches(ROMAN_NUMERAL_PATTERN)) {
                _log.finest("Arabic equivalent value: " + calResultString(romanVal));
            } else {
                _log.severe("ERROR: Invalid input value!\n");
            }
            
        } else {
            _log.severe("ERROR: Invalid NULL input value!\n");
        }

        return Integer.parseInt(calResultString(romanVal));

    } 

    /**
     * Helper method to 'calculate' the Arabic value and returns as String
     */
    protected static String calResultString(String romanVal) {

        char[] chars = romanVal.toCharArray();
        int calResult = 0;
        for (int i=0; i<chars.length; i++) {
            
            if ( !(i == chars.length - 1 ) ) {
                if ( (('C' == chars[i]) && ('M' == chars[i+1])) ||
                     (('C' == chars[i]) && ('D' == chars[i+1])) ||
                     (('X' == chars[i]) && ('C' == chars[i+1])) ||
                     (('X' == chars[i]) && ('L' == chars[i+1])) ||
                     (('I' == chars[i]) && ('X' == chars[i+1])) ||
                     (('I' == chars[i]) && ('V' == chars[i+1])) ) {

                    calResult = calResult + getIntVal(chars[i+1]) - getIntVal(chars[i]);
                    i++;
                } else {

                    calResult += getIntVal(chars[i]);
                }
            } else {
                calResult += getIntVal(chars[i]);
            }
        }

        return String.valueOf(calResult);
    }

    static int getIntVal(char c) {
        return valueTable.get(String.valueOf(c)).intValue();
    }
    

    /**
     * Prints the usage
     */
    public static void printUsage() {

        StringBuilder sb = new StringBuilder();

        try {
            sb.append("***** Usage *****\n");
            sb.append("Galactic ");
            sb.append("[Optional Test file (Default: default_test_input.txt)]\n");
     
            _log.info(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }        



    /**
     * main()
     */
    public static void main(String argv[]) {
        
        if (argv.length > 0 && ("-h".equals(argv[0]) || "-help".equals(argv[0]))) {
            printUsage();
            System.exit(0);
        } 

        Galactic rt = new Galactic();

        if (argv.length > 0) {
            rt.setFilename(argv[0]);
        } else {
            rt.setFilename(DEFAULT_INPUT_FILE);
        }

        rt.processFile();
    }

}
