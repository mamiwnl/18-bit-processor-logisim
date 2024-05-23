

import java.io.*;


public class Main {

    StringBuilder binary=new StringBuilder();
    //instruction array
    public final String[] instructionPool = {"ADD", "AND", "NAND", "NOR", "ADDI", "ANDI", "LD", "ST", "CMP", "JUMP", "JE", "JA", "JB", "JAE", "JBE"};


    public static void main(String[] args) {
        Main main =new Main();
        main.fileReader();
    }

    //reads txt file line by line
    public void fileReader(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("input.txt"));
            String line = reader.readLine();
            while (line != null) {
                //splitting line with " " and creating String array
                String[] instructions = line.split(" ");
                instructionSeparator(instructions);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //converts instructions to binary according to ISA table
    public void instructionSeparator(String[] instructions) {
        switch (instructions[0]) {

            case "ADD":
            case "AND":
            case "NAND":
            case "NOR":
            //instruction
            binary.append(instructionCodeToBinary(instructions[0]));
            //destination
            binary.append(registerToBinary(instructions[1]));
            //source 1
            binary.append(registerToBinary(instructions[2]));
            //empty bits
            binary.append("00");
            //source 2
            binary.append(registerToBinary(instructions[3]));
                break;

            case "ADDI":
            case "ANDI":
                //instruction
                binary.append(instructionCodeToBinary(instructions[0]));
                //destination
                binary.append(registerToBinary(instructions[1]));
                //source 1
                binary.append(registerToBinary(instructions[2]));
                //immidiate value
                binary.append(immediateToBinary(instructions[3]));
                break;

            case "LD":
            case "ST":
                //instruction
                binary.append(instructionCodeToBinary(instructions[0]));
                //source 1 or destination
                binary.append(registerToBinary(instructions[1]));
                //address
                binary.append(addressToBinary(instructions[2]));

                break;
            case "CMP":
                //instruction
                binary.append(instructionCodeToBinary(instructions[0]));
                //empty bits
                binary.append("000000");
                //op1
                binary.append(registerToBinary(instructions[1]));
                //op2
                binary.append(registerToBinary(instructions[2]));
                break;
            case "JUMP":
            case "JE":
            case "JA":
            case "JB":
            case "JAE":
            case "JBE":
                //instruction
                binary.append(instructionCodeToBinary(instructions[0]));
                //empty bits
                binary.append("0000");
                //address
                binary.append(addressToBinary(instructions[1]));
                break;
        }

        try {
            //If there is any error in the instruction prints Error if not prints hex value of given instruction
            if (binary.indexOf("Error") == -1) {

                // Writing to output.txt
                writeToFile("output.txt", binaryToHex(binary.toString()));
            } else {
                // Writing "Error" to output.txt
                writeToFile("output.txt", "Error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //setting empty
            binary.setLength(0);
        }
    }
    //writes txt file line by line
    private void writeToFile(String fileName, String content) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(fileName), true);
             PrintWriter printWriter = new PrintWriter(fileOutputStream)) {
            printWriter.println(content);
        }
    }

    //converts decimal value to binary in 4 bits format
    public String decimalToBinary(int decimal) {
        if (Integer.toBinaryString(decimal).length() == 1)
            return "000" + Integer.toBinaryString(decimal);
        else if (Integer.toBinaryString(decimal).length() == 2)
            return "00" + Integer.toBinaryString(decimal);
        else if (Integer.toBinaryString(decimal).length() == 3)
            return "0" + Integer.toBinaryString(decimal);
        else
            return Integer.toBinaryString(decimal) ;
    }
    //converts instruction to given binary value of instruction in ISA table
    public String instructionCodeToBinary(String instructionCode){

        for (int i=0 ; i<instructionPool.length ; i++){
            if(instructionPool[i].equals(instructionCode)) {
                return(decimalToBinary(i));
            }
        }
        return "Opcode Not Found";
    }
    //converts register to 4 bits binary number
    public String registerToBinary(String value){

        String[] values = value.split("R");
        int valueInt = Integer.parseInt(values[1]);
        if(valueInt>=0 && valueInt<=15){
            return(decimalToBinary(valueInt));
        }
        else return "Error";

    }
    //converts address to 10 bits binary value
    public String addressToBinary(String number) {
        // handling negative number
        if (number.startsWith("-")) {
            String[] unsignedNumber=number.split("-");
            //check value in range of 10 bits negative binary number
            if(Integer.parseInt(unsignedNumber[1])>512){
                System.out.println("Address overflow! Must be in [-512, +511]");
                return "Error";
            }
            //positive value of given address
            String positiveBinary = Integer.toBinaryString(Integer.parseInt(unsignedNumber[1]));
            StringBuilder paddedBinary = new StringBuilder();

            //arrange the binary number to 10 bits
            int paddingLength = 10 - positiveBinary.length();
            for (int i = 0; i < paddingLength; i++) {
                paddedBinary.append('0');
            }
            paddedBinary.append(positiveBinary);
            StringBuilder invertedBits = new StringBuilder();

            // apply 2's complement by flipping the bits and adding 1
            for (int j=0; j< paddedBinary.length(); j++) {
                invertedBits.append(paddedBinary.charAt(j) == '1' ? '0' : '1');
            }
            StringBuilder twosComplement = new StringBuilder();
            int carry = 1;
            for (int i = invertedBits.length() - 1; i >= 0; i--) {
                int bit = Character.getNumericValue(invertedBits.charAt(i)) + carry;
                twosComplement.insert(0, bit % 2);
                carry = bit / 2;
            }
            // add any remaining carry
            if (carry > 0) {
                twosComplement.insert(0, carry);
            }
            return twosComplement.toString();
        }
        //if address value is positive
        else {
             //check value in range of 10 bits positive binary number
            if(Integer.parseInt(number)>511) {
                System.out.println("Address overflow! Must be in [-512, +511]");
                return "Error";
            }
            //convert to binary and complete to 10 bits
            String binary = Integer.toBinaryString(Integer.parseInt(number));
            while (binary.length() < 10) {
                binary = "0" + binary;
            }
            return binary;
        }
    }
    //converts immediate to 6 bits binary value
    public String immediateToBinary(String number) {
        // handling negative number
        if (number.startsWith("-")) {
            //splits  -
            String[] unsignedNumber = number.split("-");
            //check value in range of 6 bits negative binary number
            if (Integer.parseInt(unsignedNumber[1]) > 32) {
                System.out.println("Immediate overflow! Must be in [-32, +31]");
                return "Error";
            }
            //positive value of given address
            String positiveBinary = Integer.toBinaryString(Integer.parseInt(unsignedNumber[1]));
            StringBuilder paddedBinary = new StringBuilder();

            // arrange the binary number to 10 bits
            int paddingLength = 6 - positiveBinary.length();
            for (int i = 0; i < paddingLength; i++) {
                paddedBinary.append('0');
            }

            paddedBinary.append(positiveBinary);
            StringBuilder invertedBits = new StringBuilder();
            // apply 2's complement by flipping the bits and adding 1
            for (int j = 0; j < paddedBinary.length(); j++) {
                invertedBits.append(paddedBinary.charAt(j) == '1' ? '0' : '1');
            }
            StringBuilder twosComplement = new StringBuilder();
            int carry = 1;
            for (int i = invertedBits.length() - 1; i >= 0; i--) {
                int bit = Character.getNumericValue(invertedBits.charAt(i)) + carry;
                twosComplement.insert(0, bit % 2);
                carry = bit / 2;
            }
            // add any remaining carry
            if (carry > 0) {
                twosComplement.insert(0, carry);
            }
            return twosComplement.toString();
        } else {
            //check value in range of 6 bits positive binary number
            if (Integer.parseInt(number) > 31) {
                System.out.println("İmmidiate overflow! Must be in [-32, +31]");
                return "Error";
            }
            //convert to binary and complete to 6 bits
            String binary = Integer.toBinaryString(Integer.parseInt(number));
            while (binary.length() < 6) {
                binary = "0" + binary;
            }
            return binary;
        }


    }

    //convert binary to hexadecimal number
    private String binaryToHex(String binary){
        return  Integer.toHexString(binaryToDecimal(binary.substring(0,2))).toUpperCase() +
                Integer.toHexString(binaryToDecimal(binary.substring(2,6))).toUpperCase() +
                Integer.toHexString(binaryToDecimal(binary.substring(6,10))).toUpperCase() +
                Integer.toHexString(binaryToDecimal(binary.substring(10,14))).toUpperCase() +
                Integer.toHexString(binaryToDecimal(binary.substring(14,18))).toUpperCase() ;

    }

    //convert binary to Decimal number
    private int binaryToDecimal(String fourBitBinary){
            int decimal=0;
        switch (fourBitBinary){
            case "00"  :
                decimal=0; break;
            case "01"  :
                decimal=1; break;
            case "10"   :
                decimal=2; break;
            case "11"   :
                decimal=3; break;
            case "0000" :
                decimal=0; break;
            case "0001" :
                decimal=1; break;
            case "0010" :
                decimal=2; break;
            case "0011" :
                decimal=3; break;
            case "0100" :
                decimal=4; break;
            case "0101" :
                decimal=5; break;
            case "0110" :
                decimal=6; break;
            case "0111" :
                decimal=7; break;
            case "1000" :
                decimal=8; break;
            case "1001" :
                decimal=9; break;
            case "1010" :
                decimal=10; break;
            case "1011" :
                decimal=11; break;
            case "1100" :
                decimal=12; break;
            case "1101" :
                decimal=13; break;
            case "1110" :
                decimal=14; break;
            case "1111" :
                decimal=15; break;
        }
        return decimal;

}



}
