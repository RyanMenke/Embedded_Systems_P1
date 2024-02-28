//“I have neither given nor received any unauthorized aid on this assignment”.

import java.io.*;
import java.util.ArrayList;


class Registers {
    //assume that the content of a register can vary between 0 – 63.
    private int R0;
    private int R1;
    private int R2;
    private int R3;
    private int R4;
    private int R5;
    private int R6;
    private int R7;
}

class DataMemory {
    //assume that the content of a DM can vary between 0 – 63.
    private int D0;
    private int D1;
    private int D2;
    private int D3;
    private int D4;
    private int D5;
    private int D6;
    private int D7;
}

class MainLoopControl {
    private ArrayList<String> finalSolutionString;

    //Container Strings for main step loop
    private ArrayList<String> INM;
    private ArrayList<String> INB;
    private ArrayList<String> AIB;
    private ArrayList<String> LIB;
    private ArrayList<String> ADB;
    private ArrayList<String> REB;
    private ArrayList<String> RGF;
    private ArrayList<String> DAM;


    public MainLoopControl(){
        finalSolutionString = new ArrayList<>();
        //initializing main loop variables
        INM = new ArrayList<>();
        INB = new ArrayList<>();
        AIB = new ArrayList<>();
        LIB = new ArrayList<>();
        ADB = new ArrayList<>();
        REB = new ArrayList<>();
        RGF = new ArrayList<>();
        DAM = new ArrayList<>();
    }

    public void initialize() {
        FileContents fileReader = new FileContents();
        ArrayList<String> instructions = fileReader.getInstructionsStored();
        ArrayList<String> registers = fileReader.getRegistersStored();
        ArrayList<String> dataMem = fileReader.getDataMemoryStored();

        this.INM = instructions;
        this.RGF = registers;
        this.DAM = dataMem;

    }

    public void mainLoopFunction() {
        initialize();
        int stepCount = 0;
        while (checkEndCondition()){
            if (stepCount != 0) {
            performLogic();
            }
            step(stepCount);

            stepCount++;
        }
        printAndFinish();
    }

    public void performLogic() {
        String storedINMResult = "";
        String storedINBResult = "";
        String storedAIBResult = "";
        String storedLIBResult = "";
        String storedADBResult = "";
        //String storedREBResult = "";
        boolean currentArithmeticOperatorIsAIB = true;
        if (!INM.isEmpty()) {
            String toTrim = INM.remove(0);
            String[] splitString = trimAndSplitString(toTrim);
            splitString[2] = ("" + retrieveIntFromRegisters(splitString[2]));
            splitString[3] = ("" + retrieveIntFromRegisters(splitString[3]));


            storedINMResult = reformatStringFromArray(splitString);
            //DO this but at the end
            //INB.add(ins);
        }
        if (!INB.isEmpty()) {
            String toTrim = INB.remove(0);
            String[] trimmedString = trimAndSplitString(toTrim);
            currentArithmeticOperatorIsAIB = isAIB(trimmedString);
            storedINBResult = reformatStringFromArray(trimmedString);
        }
        if (!AIB.isEmpty()) {
            String toTrim = AIB.remove(0);
            String[] trimmedString = trimAndSplitString(toTrim);
            storedAIBResult = "<" + trimmedString[1] + "," + checkArithmeticOperator(trimmedString) + ">";

        }
        if (!LIB.isEmpty()) {
            String toTrim = LIB.remove(0);
            String[] trimmedString = trimAndSplitString(toTrim);
            storedLIBResult = "<" + trimmedString[1] + "," + checkArithmeticOperator(trimmedString) + ">";
        }
        if (!ADB.isEmpty()) {
            //TODO: unimplemented
            String toTrim = ADB.remove(0);
            String value = getREBStringFromADB(toTrim);
            String[] trimmedString = trimAndSplitString(toTrim);
            storedADBResult = "<" + trimmedString[0] + "," + value + ">";
        }
        if (!REB.isEmpty()) {
            String toTrim = REB.remove(0);
            REBToRGF(toTrim);
        }


        //Add all values to registers at the end
        if (!storedINMResult.isEmpty()) {
            INB.add(storedINMResult);
        }
        if (!storedINBResult.isEmpty()) {
            if (currentArithmeticOperatorIsAIB) {
                AIB.add(storedINBResult);
            }
            else {
                LIB.add(storedINBResult);
            }
        }
        if (!storedADBResult.isEmpty()) {
            REB.add(storedADBResult);
        }
        if (!storedAIBResult.isEmpty()) {
            REB.add(storedAIBResult);
        }
        if (!storedLIBResult.isEmpty()) {
            ADB.add(storedLIBResult);
        }
    }

    public void REBToRGF(String REB) {
        String[] trimmedAndSplit = trimAndSplitString(REB);
        for (int i = 0; i < RGF.size(); i++) {
            String[] temp = trimAndSplitString(RGF.get(i));
            if (temp[0].equals(trimmedAndSplit[0])) {
                RGF.set(i, REB);
            }
        }
    }

    public String getREBStringFromADB(String ADB) {
        String[] trimmedAndSplit = trimAndSplitString(ADB);
        for (int i = 0; i < DAM.size(); i++) {
            String[] temp = trimAndSplitString(DAM.get(i));
            if (temp[0].equals(trimmedAndSplit[1])) {
                return temp[1];
            }
        }
        return "";
    }


    public String checkArithmeticOperator(String[] separatedIns) {
        if (separatedIns[0].equals("ADD")) {
            String destinationRegister = separatedIns[1];
            int add1 = Integer.parseInt(separatedIns[2]);
            int add2 = Integer.parseInt(separatedIns[3]);
            int finalVal = (add1 + add2) % 64;
            return "" + finalVal;

        }
        else if (separatedIns[0].equals("SUB")) {
            String destinationRegister = separatedIns[1];
            int sub1 = Integer.parseInt(separatedIns[2]);
            int sub2 = Integer.parseInt(separatedIns[3]);
            int finalVal = (sub1 - sub2);
            finalVal = (finalVal + 64) % 64;
            return "" + finalVal;
        }
        else if (separatedIns[0].equals("AND")) {
            String destinationRegister = separatedIns[1];
            int and1 = Integer.parseInt(separatedIns[2]);
            int and2 = Integer.parseInt(separatedIns[3]);
            int finalVal = (and1 & and2);
            finalVal = (finalVal + 64) % 64;
            return "" + finalVal;
        }
        else if (separatedIns[0].equals("OR")) {
            String destinationRegister = separatedIns[1];
            int or1 = Integer.parseInt(separatedIns[2]);
            int or2 = Integer.parseInt(separatedIns[3]);
            int finalVal = (or1 | or2);
            finalVal = (finalVal + 64) % 64;
            return "" + finalVal;
        }
        else if (separatedIns[0].equals("LD")) {
            String destinationRegister = separatedIns[1];
            int add1 = Integer.parseInt(separatedIns[2]);
            int add2 = Integer.parseInt(separatedIns[3]);
            int finalVal = (add1 + add2) % 8;
            return "" + finalVal;
        }
        return"";
    }

    //TODO: Remove likely
//    public int retrieveIntFromRegisters(String s) {
//        for (int i = 0; i < RGF.size(); i++) {
//            String[] temp = trimAndSplitString(RGF.get(i));
//            if (temp[0].contains(s)) {
//                return Integer.parseInt(temp[1]);
//            }
//        }
//        return 0;
//    }

    public int retrieveIntFromRegisters(String s) {
        for (int i = 0; i < RGF.size(); i++) {
            String[] temp = trimAndSplitString(RGF.get(i));
            if (temp[0].equals(s)) {
                return Integer.parseInt(temp[1]);
            }
        }
        return 0;
    }

    public boolean isAIB(String[] separatedIns) {
        if (separatedIns[0].equals("ADD")) {
            return true;
        }
        else if (separatedIns[0].equals("SUB")) {
            return true;
        }
        else if (separatedIns[0].equals("AND")) {
            return true;
        }
        else if (separatedIns[0].equals("OR")) {
            return true;
        }
        else {
            return false;
        }
    }

    public String[] trimAndSplitString(String s) {
        String trim = s.replace("<","").replace(">","");
        return trim.split(",");
    }

    public String trimString(String s) {
        return s.replace("<","").replace(">","");
    }
    public String reformatStringFromArray(String[] splitString) {
        String toBeReturned = "";
        for (int i = 0; i < splitString.length; i++) {
            if (i == 0) {
                toBeReturned += "<";
            }
            toBeReturned +=splitString[i];
            if (i != splitString.length-1) {
                toBeReturned += ",";
            }
            if (i == splitString.length-1) {
                toBeReturned += ">";
            }

        }
        return toBeReturned;
    }
    public String returnStringFromList(ArrayList<String> list) {
        String finalString = "";
        for (int i = 0; i < list.size(); i++) {
            //finalString += "<";
            finalString += list.get(i);
            //finalString += ">";
            if (i != list.size() - 1) {
                finalString += ",";
            }
        }
        return finalString;
    }

    //TODO: potentially don't need this method
    public boolean findSubstring () {
        String str = "Hello, World!";
        String substring = "World";

        int index = str.indexOf(substring); // Find the index of the substring

        if (index != -1) {
            return true;
        }
        return false;
    }


    public void step(int stepNumber) {
        //STEP 0:
        //INM:<ADD,R1,R2,R3>,<LD,R4,R2,R3>,<AND,R5,R2,R3>,<LD,R6,R2,R2>,<OR,R1,R3,R2>
        //INB:
        //AIB:
        //LIB:
        //ADB:
        //REB:
        //RGF:<R0,4>,<R1,3>,<R2,2>,<R3,1>,<R4,4>,<R5,3>,<R6,2>,<R7,1>
        //DAM:<0,2>,<1,4>,<2,6>,<3,8>,<4,10>,<5,12>,<6,14>,<7,16>
        //\n

        //A Single Step
        finalSolutionString.add("STEP " + stepNumber + ":");
        finalSolutionString.add("INM:" + returnStringFromList(INM));
        finalSolutionString.add("INB:" + returnStringFromList(INB));
        finalSolutionString.add("AIB:" + returnStringFromList(AIB));
        finalSolutionString.add("LIB:" + returnStringFromList(LIB));
        finalSolutionString.add("ADB:" + returnStringFromList(ADB));
        finalSolutionString.add("REB:" + returnStringFromList(REB));
        finalSolutionString.add("RGF:" + returnStringFromList(RGF));
        finalSolutionString.add("DAM:" + returnStringFromList(DAM));
        finalSolutionString.add("");
    }

    public boolean checkEndCondition() {
        //INM:
        //INB:
        //AIB:
        //LIB:
        //ADB:
        if (INM.isEmpty() && INB.isEmpty() && AIB.isEmpty() && LIB.isEmpty() && ADB.isEmpty() && REB.isEmpty()) {
            return false;
        }
        return true;
    }

    //TODO: Finish the final call to the printing function
    public void printAndFinish() {
        FileReaderMethods.writeOutputFile(finalSolutionString);
    }
}
class FileReaderMethods {
    public static final String instructions = "instructions.txt";
    public static final String registers = "registers.txt";
    public static final String dataMemory = "datamemory.txt";
    public static final String simulation = "simulation.txt";

    public static void readFile(String fileName, ArrayList<String> instructions) {
        //instructions = new ArrayList<>();
//        String fileName = "example.txt"; // Change this to the path of your text file

        String currentDirectory = System.getProperty("user.dir");

//        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        //TODO: TAKE AWAY THE "SRC" WHEN RUNNING OUTSIDE INTELLIJ
        try (BufferedReader br = new BufferedReader(new FileReader("src/" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Process the current line as needed
                instructions.add(line);
                //System.out.println(line); // For example, printing each line to console
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public static void writeOutputFile(ArrayList<String> allSteps) {
//        ArrayList<String> lines = new ArrayList<>();
//        lines.add("Line 1");
//        lines.add("Line 2");
//        lines.add("Line 3");

        //TODO: Remove src from path
        String fileName = "src/simulation.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            // Write lines from ArrayList to file
            int count = 0;
            for (String line : allSteps) {
                writer.write(line);
                if (count != allSteps.size()-1) {
                    writer.newLine();
                }
                count++;
            }

            System.out.println("Lines have been written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static boolean compareFiles(String filePath1, String filePath2) {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(filePath1));
             BufferedReader reader2 = new BufferedReader(new FileReader(filePath2))) {

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();

            while (line1 != null && line2 != null) {
                if (!line1.equals(line2)) {
                    System.out.println("Files are different at File 1: " + line1 + " and File 2: " + line2);
                    return false; // Lines are different
                }
                line1 = reader1.readLine();
                line2 = reader2.readLine();
            }
            // Check if both files have reached the end
            if (line1 != null || line2 != null) {
                System.out.println("Files have a different number of lines");
                System.out.println(line1);
                System.out.println(line2);
                return false; // Files have different number of lines
            }

            // Files are identical
            System.out.println("Files are Identical");
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error has occurred");
            return false; // Error occurred while reading files
        }
    }
    public static void createOutputFile() {
        //TODO: Implement this structure for the stepwise output
        //TODO: For the LD operations its not asking you to add the values at the memory locations. If it is LD R4, R3, R2. -
        //TODO: (continued) -it means Take the dataMemory value D5(3+2) and put D5 in R4
        //STEP x:
        //INM:
        //INB:
        //AIB: (Issue 1) Arithmetic operators
        //LIB: (LD) Load from data memory(Issue 2)
        //ADB: (Results of load placed here before going to REB)
        //REB: (Results of AIB go here before being placed in RGF)
        //RGF:
        //DAM:
        //(blank new line "\n")
        //STEP x+1:


        //Initial Inputs:
        //INM = instructions
        //RGF = registers
        //DAM = dataMemory

        //Everything Else is a Transition
        //INB:
        //AIB:
        //LIB:
        //ADB:
        //REB:

        //1. READ:
        //The READ transition is a slight deviation from traditional Petri net semantics since it does
        // not have any direct access to instruction tokens. Assume that it knows the top (in-order) instruction
        // in the Instruction Memory (INM). It checks for the availability of the source operands in the
        // Register File (RGF) for the top instruction token and passes them to Instruction Buffer (INB)
        // by replacing the source operands with the respective values. For example, if the top instruction
        // token in INM is <ADD, R1, R2, R3> and there are two tokens in RGF as <R2,5> and <R3,7>, then the
        // instruction token in INB would be <ADD, R1, 5, 7> once both READ and DECODE transitions are activated.
        // Both READ and DECODE transitions are executed together. Please note that when READ consumes two register
        // tokens, it also returns them to RGF in the same time step (no change in RGF due to READ).
        //
        //2. DECODE:
        //The DECODE transition consumes the top (in-order) instruction (one token) from INM and updates the values of
        // the source registers with the values from RGF (with the help of READ transition, as described above), and
        // places the modified instruction token in INB.
        //
        //3. ISSUE1:
        //The ISSUE1 transition consumes one arithmetic/logical (ADD, SUB, AND, OR) instruction token (if any) from
        // INB and places it in the Arithmetic Instruction Buffer (AIB).
        //
        //4. ISSUE2:
        //The ISSUE2 transition consumes one load (LD) instruction token (if any) from INB and places it in the
        // Load Instruction Buffer (LIB).
        //
        //5. Arithmetic Logic Unit (ALU)
        //The ALU transition performs arithmetic/logical computations as per the instruction token from AIB, and places
        // the result in the result buffer (REB). The format of the token in result buffer is same as a
        // token in RGF i.e., <destination-register-name, value>.
        //
        //6. Address Calculation (ADDR)
        //The ADDR transition performs effective (data memory) address calculation for the load instruction by
        // adding the contents of two source registers. It produces a token as <destination-register-name,
        // data memory address> and places it in the address buffer (ADB).
        //
        //7. LOAD:
        //The LOAD transition consumes a token from ADB and gets the data from the data memory for the corresponding
        // address. Assume that you will always have the data for the respective address in the data memory in the
        // same time step. It places the data value (result of load) in the result buffer (REB). The format of the
        // token in result buffer is same as a token in RGF i.e., <destination-register-name, data value>.
        //
        //8. WRITE
        //The WRITE transition transfers the result (one token) from the Result Buffer (REB) to the register file (RGF).
        // If there are more than one token in REB in a time step, the WRITE transition writes the token that belongs
        // to the in-order first instruction.
    }
}

class FileContents {
    private ArrayList<String> instructionsStored;
    private ArrayList<String> registersStored;
    private ArrayList<String> dataMemoryStored;
    private ArrayList<String> simulationStored;

    public FileContents() {
        //instantiate arraylists
        instructionsStored = new ArrayList<>();
        registersStored = new ArrayList<>();
        dataMemoryStored = new ArrayList<>();
        simulationStored = new ArrayList<>();

        //begin reading the files
        FileReaderMethods.readFile(FileReaderMethods.instructions, instructionsStored);
        FileReaderMethods.readFile(FileReaderMethods.registers, registersStored);
        FileReaderMethods.readFile(FileReaderMethods.dataMemory, dataMemoryStored);
    }

    public ArrayList<String> getInstructionsStored() {
        return instructionsStored;
    }

    public ArrayList<String> getRegistersStored() {
        return registersStored;
    }

    public ArrayList<String> getDataMemoryStored() {
        return dataMemoryStored;
    }

    public ArrayList<String> getSimulationStored() {
        return simulationStored;
    }
}
public class Psim {

    public static void main(String[] args) {
//        Please hardcode the input and output files as follows:
//        Instructions (input): instructions.txt
//        Registers (input): registers.txt
//        Data Memory (input): datamemory.txt
//        Simulation (output): simulation.txt

        FileContents f = new FileContents();
        System.out.println("\ninstructions:");
        //System.out.println(f.getInstructionsStored() + "\n");
        for (String s: f.getInstructionsStored()) {
            System.out.println(s);
            String trim = s.replace("<","").replace(">","");
            System.out.println(trim);


        }

        System.out.println("registers:");
        System.out.println(f.getRegistersStored() + "\n");


        System.out.println("dataMemory:");
        System.out.println(f.getDataMemoryStored() + "\n");

        String currentDirectory = System.getProperty("user.dir");
        System.out.println("Current relative path is: " + currentDirectory);

        System.out.println("Hello world!");

        MainLoopControl m = new MainLoopControl();
        m.mainLoopFunction();
        //FileReaderMethods.compareFiles("src/simulation.txt","src/correct_simulation.txt");
    }
}