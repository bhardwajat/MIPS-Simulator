package cda5155;
/* On my honor, I have neither given nor
received unauthorized aid on this assignment */

import java.io.*;
import java.util.*;

public class MIPSsim {
	public static Integer beginDataAddress;
	
	 public static int       numInst;        //Number of processed instructions
	 public static boolean   isStalled;        //Is the pipeline stalled?
	 public static boolean   notFetch;        //Missed branch prediction => flush
	 public static int       PC;             //Program Counter of the Simulator
	 public static int       clockCycle; 
	 public int n = 256;
	 try {
	 PrintStream fileSimulation = new PrintStream(new File("simulation.txt"));
	 }
	 catch(FileNotFoundException ex) {
		 ex.printStackTrace();
	 }
	 
	 public static List<String> originalQ = new ArrayList<>();
	 public static LinkedList<String> fetchQ = new LinkedList<String>();
	 public static LinkedList<String> preIssueQ = new LinkedList<String>();
	 public static LinkedList<String> preALU1Q = new LinkedList<String>();
	 public static LinkedList<String> preALU2Q = new LinkedList<String>();
	 public static LinkedList<String> postALUQ = new LinkedList<String>();
	 public static LinkedList<String> preMEMQ = new LinkedList<String>();
	 public static LinkedList<String> postMEMQ = new LinkedList<String>();
	 public static HashSet<String> preDestSet = new HashSet<>();//Stores the destination registers in the preIssue stage of pipeline 
	 public static HashSet<String> preSourceSet = new HashSet<>();//Stores the source registers in the preIssue stage of pipeline
	 public static HashSet<String> postDestSet = new HashSet<>();//Stores the destination registers for issued instructions
	 public static HashSet<String> postSourceSet = new HashSet<>();//Stores the source registers for issued instructions
	 
	 public static Map<Integer, Integer> hm = new HashMap<Integer, Integer>();//for storing values in the registers
	 public static Map<Integer, String> map = new TreeMap<Integer, String>();//for storing input string in the addresses
	 public static TreeMap<String, String> tm = new TreeMap<String, String>();//Instructions as keys and opcodes as their values
	 static { hm.put(0, 0);
	 hm.put(1, 0);hm.put(2, 0);hm.put(3, 0);hm.put(4, 0);hm.put(5, 0);hm.put(6, 0);hm.put(7, 0);
	 hm.put(8, 0);hm.put(9, 0);hm.put(10, 0);hm.put(11, 0);hm.put(12, 0);hm.put(13, 0);hm.put(14, 0);hm.put(15, 0);
	 hm.put(16, 0);hm.put(17, 0);hm.put(18, 0);hm.put(19, 0);hm.put(20, 0);hm.put(21, 0);hm.put(22, 0);hm.put(23, 0);
	 hm.put(24, 0);hm.put(25, 0);hm.put(26, 0);hm.put(27, 0);hm.put(28, 0);hm.put(29, 0);hm.put(30, 0);hm.put(31, 0);}
	public static void main(String[] args) throws FileNotFoundException, IOException {
		ArrayList<String> a = new ArrayList<>();
		File file = new File("sample2.txt");
		Scanner sc = new Scanner(file);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			a.add(line);
		}
		sc.close();
//		PrintStream System.out = new PrintStream(new File("disassembly.txt"));
		PrintStream fileSimulation = new PrintStream(new File("simulation.txt"));
		int i = 0;
		MIPSsim sim = new MIPSsim();
//		Map<Integer, Integer> hm = new HashMap<>();//for storing values in the registers
//		Map<Integer, String> map = new TreeMap<Integer, String>();//for storing input string in the addresses
//		hm.put(0, 0);hm.put(1, 0);hm.put(2, 0);hm.put(3, 0);hm.put(4, 0);hm.put(5, 0);hm.put(6, 0);hm.put(7, 0);
//		hm.put(8, 0);hm.put(9, 0);hm.put(10, 0);hm.put(11, 0);hm.put(12, 0);hm.put(13, 0);hm.put(14, 0);hm.put(15, 0);
//		hm.put(16, 0);hm.put(17, 0);hm.put(18, 0);hm.put(19, 0);hm.put(20, 0);hm.put(21, 0);hm.put(22, 0);hm.put(23, 0);
//		hm.put(24, 0);hm.put(25, 0);hm.put(26, 0);hm.put(27, 0);hm.put(28, 0);hm.put(29, 0);hm.put(30, 0);hm.put(31, 0);
		/* Code for the disassemble text */
		int n = 256;
		while(i <= a.size()-1) {
			String s = a.get(i);
			/* CATEGORY 2 - DISASSEMBLY TEXT */
			if (s.charAt(0) == '1' && s.charAt(1) == '1') {
				if (s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '0') {
					String st = findAllRegisters(s);
				//	System.out.println(s + "\t" + n + "\t" + "ADD " + st);//ADD
					originalQ.add("ADD " + st);
					tm.put("ADD " + st, s);
					map.put(n, s);
				}else if (s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '1') {
					String st = findAllRegisters(s);
				//	System.out.println(s + "\t" + n + "\t" + "SUB " + st);//SUB
					originalQ.add("SUB " + st);
					tm.put("SUB " + st, s);
					map.put(n, s);
				}else if (s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '0') {
					String st = findAllRegisters(s);
				//	System.out.println(s + "\t" + n + "\t" + "MUL " + st);//MUL
					originalQ.add("MUL " + st);
					tm.put("MUL " + st, s);
					map.put(n, s);
				}else if (s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '1') {
					String st = findAllRegisters(s);
				//	System.out.println(s + "\t" + n + "\t" + "AND " + st);//AND
					originalQ.add("AND " + st);
					tm.put("AND " + st, s);
					map.put(n, s);
				}else if (s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '0' && s.charAt(5) == '0') {
					String st = findAllRegisters(s);
				//	System.out.println(s + "\t" + n + "\t" + "OR " + st);//OR
					originalQ.add("OR " + st);
					tm.put("OR " + st, s);
					map.put(n, s);
				}else if (s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '0' && s.charAt(5) == '1') {
					String st = findAllRegisters(s);
				//	System.out.println(s + "\t" + n + "\t" + "XOR" + st);//XOR
					originalQ.add("XOR " + st);
					tm.put("XOR " + st, s);
					map.put(n, s);
				}else if (s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '1' && s.charAt(5) == '0') {
					String st = findAllRegisters(s);
				//	System.out.println(s + "\t" + n + "\t" + "NOR " + st);//NOR
					originalQ.add("NOR " + st);
					tm.put("NOR " + st, s);
					map.put(n, s);
				}else if (s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '1' && s.charAt(5) == '1') {
					String st = findAllRegisters(s);
				//	System.out.println(s + "\t" + n + "\t" + "SLT " + st);//SLT
					originalQ.add("SLT " + st);
					tm.put("SLT " + st, s);
					map.put(n, s);
				}else if (s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '0') {
					String rs = ""; String rt = "";
					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
					rs = findRegister(sb.toString());
					sb.setLength(0);
					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
					rt = findRegister(sb.toString());
					sb.setLength(0);
					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
					int d = findDecimal(sb.toString());
				//	System.out.println(s + "\t" + n + "\t" + "ADDI " + rt + ", " + rs + ", " + "#" + d);//ADDI
					originalQ.add("ADDI " + rt + ", " + rs + ", " + "#" + d);
					tm.put("ADDI " + rt + ", " + rs + ", " + "#" + d, s);
					map.put(n, s);
				}else if (s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '1') {
					String rs = ""; String rt = "";
					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
					rs = findRegister(sb.toString());
					sb.setLength(0);
					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
					rt = findRegister(sb.toString());
					sb.setLength(0);
					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
					int d = findDecimal(sb.toString());
				//	System.out.println(s + "\t" + n + "\t" + "ANDI " + rt + ", " + rs + ", " + "#" + d);//ANDI
					originalQ.add("ANDI " + rt + ", " + rs + ", " + "#" + d);
					tm.put("ANDI " + rt + ", " + rs + ", " + "#" + d, s);
					map.put(n, s);
				}else if (s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '0') {
					String rs = ""; String rt = "";
					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
					rs = findRegister(sb.toString());
					sb.setLength(0);
					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
					rt = findRegister(sb.toString());
					sb.setLength(0);
					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
					int d = findDecimal(sb.toString());
				//	System.out.println(s + "\t" + n + "\t" + "OR " + rt + ", " + rs + ", " + "#" + d);//ORI
					originalQ.add("OR " + rt + ", " + rs + ", " + "#" + d);
					tm.put("OR " + rt + ", " + rs + ", " + "#" + d, s);
					map.put(n, s);
				}else if (s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '1') {
					String rs = ""; String rt = "";
					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
					rs = findRegister(sb.toString());
					sb.setLength(0);
					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
					rt = findRegister(sb.toString());
					sb.setLength(0);
					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
					int d = findDecimal(sb.toString());
				//	System.out.println(s + "\t" + n + "\t" + "XORI " + rt + ", " + rs + ", " + "#" + d);//XORI
					originalQ.add("XORI " + rt + ", " + rs + ", " + "#" + d);
					tm.put("XORI " + rt + ", " + rs + ", " + "#" + d, s);
					map.put(n, s);
				}
				i++;n+=4;
			  /* CATEGORY 1 - DISASSEMBLY TEXT */
			} else if (s.charAt(0) == '0' && s.charAt(1) == '1'
					&& !(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '0' && s.charAt(5) == '1')) {
				if (s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '0') {
					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10)).append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15)).append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
					int d = findDecimal(sb.toString());
				//	System.out.println(s + "\t" + n + "\t" + "J " +  "#" + d*4);//J
					originalQ.add("J " +  "#" + d*4);
					tm.put("J " +  "#" + d*4, s);
					map.put(n, s);
					}else if(s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '1') {
						String rs = "";
						StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
						rs = findRegister(sb.toString());
						sb.setLength(0);
					//	System.out.println(s + "\t" + n + "\t" + "JR " + rs);//JR
						originalQ.add("JR " + rs);
						tm.put("JR " + rs, s);
						map.put(n, s);
					}else if(s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '0') {
						String rs = ""; String rt = "";
						StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
						rs = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
						rt = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
						int d = findDecimal(sb.toString());
					//	System.out.println(s + "\t" + n + "\t" + "BEQ " + rs + ", " + rt + ", " + "#" + d*4);//BEQ
						originalQ.add("BEQ " + rs + ", " + rt + ", " + "#" + d*4);
						tm.put("BEQ " + rs + ", " + rt + ", " + "#" + d*4, s);
						map.put(n, s);
					}else if(s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '1') {
						String rs = "";
						StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
						rs = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
						int d = findDecimal(sb.toString());
					//	System.out.println(s + "\t" + n + "\t" + "BLTZ " + rs + ", " + "#" + d*4);//BLTZ
						originalQ.add("BLTZ " + rs + ", " + "#" + d*4);
						tm.put("BLTZ " + rs + ", " + "#" + d*4, s);
						map.put(n, s);
					}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '0' && s.charAt(5) == '0') {
						String rs = "";
						StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
						rs = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
						int d= findDecimal(sb.toString());
					//	System.out.println(s + "\t" + n + "\t" + "BGTZ " + rs + ", " + "#" + d*4);//BGTZ
						originalQ.add("BGTZ " + rs + ", " + "#" + d*4);
						tm.put("BGTZ " + rs + ", " + "#" + d*4, s);
						map.put(n, s);
					}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '1' && s.charAt(5) == '0') {
						String base = ""; String rt = "";
						StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
						base = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
						rt = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
						int d = findDecimal(sb.toString());
					//	System.out.println(s + "\t" + n + "\t" + "SW "+ rt + ", " + d + "(" + base + ")");//SW
						originalQ.add("SW "+ rt + ", " + d + "(" + base + ")");
						tm.put("SW "+ rt + ", " + d + "(" + base + ")", s);
						map.put(n, s);
					}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '1' && s.charAt(5) == '1') {
						String base = ""; String rt = "";
						StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
						base = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
						rt = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
						int d = findDecimal(sb.toString());
					//	System.out.println(s + "\t" + n + "\t" + "LW "+ rt + ", " + d + "(" + base + ")");//LW
						originalQ.add("LW "+ rt + ", " + d + "(" + base + ")");
						tm.put("LW "+ rt + ", " + d + "(" + base + ")", s);
						map.put(n, s);
					}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '0') {
						String rd = ""; String rt = "";
						StringBuilder sb = new StringBuilder();
						sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
						rt = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
						rd = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25));
						int d = findDecimal(sb.toString());
					//	System.out.println(s + "\t" + n + "\t" + "SLL " + rd + ", " + rt + ", " + "#" + d);//SLL
						originalQ.add("SLL " + rd + ", " + rt + ", " + "#" + d);
						tm.put("SLL " + rd + ", " + rt + ", " + "#" + d, s);
						map.put(n, s);
					}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '1') {
						String rd = ""; String rt = "";
						StringBuilder sb = new StringBuilder();
						sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
						rt = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
						rd = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25));
						int d = findDecimal(sb.toString());
					//	System.out.println(s + "\t" + n + "\t" + "SRL " + rd + ", " + rt + ", " + "#" + d);//SRL
						originalQ.add("SRL " + rd + ", " + rt + ", " + "#" + d);
						tm.put("SRL " + rd + ", " + rt + ", " + "#" + d, s);
						map.put(n, s);
					}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '0') {
						String rd = ""; String rt = "";
						StringBuilder sb = new StringBuilder();
						sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
						rt = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
						rd = findRegister(sb.toString());
						sb.setLength(0);
						sb = sb.append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25));
						int d = findDecimal(sb.toString());
					//	System.out.println(s + "\t" + n + "\t" + "SRA " + rd + ", " + rt + ", " + "#" + d);//SRA
						originalQ.add("SRA " + rd + ", " + rt + ", " + "#" + d);
						tm.put("SRA " + rd + ", " + rt + ", " + "#" + d, s);
						map.put(n, s);
					}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '1') {
					//	System.out.println(s + "\t" + n + "\t" + "NOP");
						originalQ.add("NOP");
						tm.put("NOP", s);
						map.put(n, s);
					}
				i++;n += 4;
			} else if (s.charAt(0) == '0' && s.charAt(1) == '1' && s.charAt(2) == '0' && s.charAt(3) == '1'
					&& s.charAt(4) == '0' && s.charAt(5) == '1') {
			//	System.out.println(s + "\t" + n + "\t" + "BREAK");
				originalQ.add("BREAK");
				tm.put("BREAK", s);
				map.put(n, s);
				beginDataAddress = n;
				i++;n += 4;
				while (i <= a.size() - 1) {
					s = a.get(i);
					if(s.charAt(0) == '1') {
						int bits = (int) Long.parseLong(s, 2);
					//	System.out.println(s + "\t" + n + "\t" + bits);
						map.put(n, Integer.toString(bits));
					}else if(s.charAt(0) == '0') {
						int bits = findDecimal(s);
					//	System.out.println(s + "\t" + n + "\t" + bits);
						map.put(n, Integer.toString(bits));
					}
					i++; n+= 4;
				}
			}
		}
		
		
//		/* Code for the simulation trace */
//		int n2 = 256; int cycleCount = 0;
//		loop: while(n2 <= beginDataAddress+1) {
//			String s = map.get(n2);
//			//CATEGORY 2 - SIMULATION TRACE
//			if (s.charAt(0) == '1' && s.charAt(1) == '1') {
//				if (s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					hm.put(nrd, hm.get(nrs)+hm.get(nrt));
//					cycleCount+=1;
//					System.out.println("--------------------");
//					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "ADD R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt);//ADD
//					printValues(hm, map, beginDataAddress);//ADD
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					hm.put(nrd, hm.get(nrs)-hm.get(nrt));
//					cycleCount+=1;
//					tm.put("SUB R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt, s);
//	//				System.out.println("--------------------");
//		//			System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "SUB R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt);//SUB
//			//		printValues(hm, map, beginDataAddress);//SUB
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					hm.put(nrd, hm.get(nrs)*hm.get(nrt));
//					cycleCount += 1;
//					tm.put("MUL R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "MUL R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt);//MUL
////					printValues(hm, map, beginDataAddress);//MUL
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					hm.put(nrd, hm.get(nrs) & hm.get(nrt));
//					cycleCount += 1;
//					tm.put("AND R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "AND R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt);//BITWISE AND
////					printValues(hm, map, beginDataAddress);//BITWISE AND
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '0' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					hm.put(nrd, hm.get(nrs) | hm.get(nrt));
//					cycleCount += 1;
//					tm.put("OR R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "OR R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt);//BITWISE OR
////					printValues(hm, map, beginDataAddress);//BITWISE OR
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '0' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					hm.put(nrd, hm.get(nrs) ^ hm.get(nrt));
//					cycleCount += 1;
//					tm.put("XOR R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "XOR R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt);//BITWISE XOR
////					printValues(hm, map, beginDataAddress);//BITWISE XOR
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '1' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					hm.put(nrd, ~(hm.get(nrs) | hm.get(nrt)));
//					cycleCount += 1;
//					tm.put("NOR R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "NOR R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt);//BITWISE NOR
////					printValues(hm, map, beginDataAddress);//BITWISE NOR
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '1' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					if(hm.get(nrs) < hm.get(nrt)) {
//						hm.put(nrd, 1);
//					}else {
//						hm.put(nrd, 0);
//					}
//					cycleCount += 1;
//					tm.put("SLT R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "SLT R" + nrd + ", " + "R" + nrs + ", " + "R" + nrt);//SLT
////					printValues(hm, map, beginDataAddress);//BITWISE SLT
//				}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//					int imd = findDecimal(sb.toString());
//					hm.put(nrt, hm.get(nrs) + imd);
//					cycleCount += 1;
//					System.out.println("--------------------");
//					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "ADDI R" + nrt + ", " + "R" + nrs + ", " + "#" + imd);//ADD IMMEDIATE
//					printValues(hm, map, beginDataAddress);//ADD IMMEDIATE
//				}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//					int imd = findDecimal(sb.toString());
//					hm.put(nrt, hm.get(nrs) & imd);
//					cycleCount += 1;
//					tm.put("ANDI R" + nrt + ", " + "R" + nrs + ", " + "#" + imd, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "ANDI R" + nrt + ", " + "R" + nrs + ", " + "#" + imd);//AND IMMEDIATE
////					printValues(hm, map, beginDataAddress);//AND IMMEDIATE
//				}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//					int imd = findDecimal(sb.toString());
//					hm.put(nrt, hm.get(nrs) | imd);
//					cycleCount += 1;
//					tm.put("ORI R" + nrt + ", " + "R" + nrs + ", " + "#" + imd, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "ORI R" + nrt + ", " + "R" + nrs + ", " + "#" + imd);//OR IMMEDIATE
////					printValues(hm, map, beginDataAddress);//OR IMMEDIATE
//				}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//					int imd = findDecimal(sb.toString());
//					hm.put(nrt, hm.get(nrs) ^ imd);
//					cycleCount += 1;
//					tm.put("XORI R" + nrt + ", " + "R" + nrs + ", " + "#" + imd, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "XORI R" + nrt + ", " + "R" + nrs + ", " + "#" + imd);//XOR IMMEDIATE
////					printValues(hm, map, beginDataAddress);//XOR IMMEDIATE
//				} n2 += 4;
//				//CATEGORY 1 - SIMULATION TRACE
//		}else if (s.charAt(0) == '0' && s.charAt(1) == '1'
//				&& !(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '0' && s.charAt(5) == '1')) {
//			if (s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '0') {
//				StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10)).append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15)).append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//				int d = findDecimal(sb.toString());
//				int d4 = d * 4;
//				cycleCount += 1;
//				if(map.containsKey(d4)) {
//					tm.put("J " +  "#" + d*4, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "J " +  "#" + d*4);//just for checking
////					printValues(hm, map, beginDataAddress);
//					n2 = d4;
//					continue loop;
//				}else {
//					tm.put("J " +  "#" + d*4, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "J " +  "#" + d4);//J
////				printValues(hm, map, beginDataAddress);//J
//				}
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					cycleCount += 1;
//					if(hm.containsKey(nrs)) {
//						tm.put("JR R" + hm.get(nrs), s);
////						System.out.println("--------------------");
////						System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "JR R" + hm.get(nrs));//just for checking
////						printValues(hm, map, beginDataAddress);
//						n2 = hm.get(nrs);
//						continue loop;
//					}else {
//						tm.put("JR R" + hm.get(nrs), s);
////						System.out.println("--------------------");
////						System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "JR R" + hm.get(nrs));//JR
////					printValues(hm, map, beginDataAddress);//JR
//					}
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//					int d = findDecimal(sb.toString());
//					int d4 = d * 4;
//					cycleCount += 1;
//					if(hm.get(nrs) == hm.get(nrt)) {
//						if(map.containsKey(n2+d4+4)) {
//							tm.put("BEQ R" + nrs + ", " + "R" + nrt + ", " + "#" + d4, s);
////							System.out.println("--------------------");
////							System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BEQ R" + nrs + ", " + "R" + nrt + ", " + "#" + d4);//just for checking
////							printValues(hm, map, beginDataAddress);
//							n2 = n2 + d4 + 4;
//							continue loop;
//						}else {
//							tm.put("BEQ R" + nrs + ", " + "R" + nrt + ", " + "#" + d4, s);
////							System.out.println("--------------------");
////							System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BEQ R" + nrs + ", " + "R" + nrt + ", " + "#" + d4);
////							printValues(hm, map, beginDataAddress);//BEQ
//							break;
//						}
//					}
//					tm.put("BEQ R" + nrs + ", " + "R" + nrt + ", " + "#" + d4, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BEQ R" + nrs + ", " + "R" + nrt + ", " + "#" + d4);//BEQ
////					printValues(hm, map, beginDataAddress);//BEQ
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//					int d = findDecimal(sb.toString());
//					int d4 = d * 4;
//					cycleCount += 1;
//					if(hm.get(nrs) < 0) {
//						if(map.containsKey(n2+d4+4)) {
//							tm.put("BLTZ R" + nrs + ", " + "#" + d4, s);
////							System.out.println("--------------------");
////							System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BLTZ R" + nrs + ", " + "#" + d4);//just for checking
////							printValues(hm, map, beginDataAddress);
//							n2 = n2 + d4 + 4;
//							continue loop;
//						}else {
//							tm.put("BLTZ R" + nrs + ", " + "#" + d4, s);
////							System.out.println("--------------------");
////							System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BLTZ R" + nrs + ", " + "#" + d4);
////							printValues(hm, map, beginDataAddress);//BLTZ
//							break;
//						}
//					}
//					tm.put("BLTZ R" + nrs + ", " + "#" + d4, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BLTZ R" + nrs + ", " + "#" + d4);//BLTZ
////					printValues(hm, map, beginDataAddress);//BLTZ
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '0' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//					int d = findDecimal(sb.toString());
//					int d4 = d * 4;
//					cycleCount += 1;
//					if(hm.get(nrs) > 0) {
//						if(map.containsKey(n2+d4+4)) {
//							tm.put("BGTZ R" + nrs + ", " + "#" + d4, s);
////							System.out.println("--------------------");
////							System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BGTZ R" + nrs + ", " + "#" + d4);//just for checking
////							printValues(hm, map, beginDataAddress);
//							n2 = n2 + d4 + 4;
//							continue loop;
//						}else {
//							tm.put("BGTZ R" + nrs + ", " + "#" + d4, s);
////							System.out.println("--------------------");
////							System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BGTZ R" + nrs + ", " + "#" + d4);
////							printValues(hm, map, beginDataAddress);//BGTZ
//							break;
//						}
//					}
//					tm.put("BGTZ R" + nrs + ", " + "#" + d4, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BGTZ R" + nrs + ", " + "#" + d4);//BGTZ
////					printValues(hm, map, beginDataAddress);//BGTZ
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '1' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nbase = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//					int d = findDecimal(sb.toString());//offset
//					int newAddress = hm.get(nbase) + d;
//					map.put(newAddress, Integer.toString(hm.get(nrt)));
//					cycleCount += 1;
//					tm.put("SW R"+ nrt + ", " + d + "(" + "R" + nbase + ")", s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "SW R"+ nrt + ", " + d + "(" + "R" + nbase + ")");//SW
////					printValues(hm, map, beginDataAddress);//SW
//				}else if(s.charAt(2) == '0' && s.charAt(3) == '1' && s.charAt(4) == '1' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
//					int nbase = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20)).append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25)).append(s.charAt(26)).append(s.charAt(27)).append(s.charAt(28)).append(s.charAt(29)).append(s.charAt(30)).append(s.charAt(31));
//					int d = findDecimal(sb.toString());
//					int newAddress = d + hm.get(nbase);
//					hm.put(nrt, Integer.parseInt(map.get(newAddress)));
//					cycleCount +=1;
//					tm.put("LW R"+ nrt + ", " + d + "(" + "R" + nbase + ")", s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "LW R"+ nrt + ", " + d + "(" + "R" + nbase + ")");//LW
////					printValues(hm, map, beginDataAddress);//LW
//				}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder();
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25));
//					int d = findDecimal(sb.toString());
//					int changeValue = hm.get(nrt) << d;
//					hm.put(nrd, changeValue);
//					cycleCount += 1;
//					tm.put("SLL R" + nrd + ", " + "R" + nrt + ", " + "#" + d, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "SLL R" + nrd + ", " + "R" + nrt + ", " + "#" + d);//SLL
////					printValues(hm, map, beginDataAddress);//SLL
//				}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '0' && s.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder();
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25));
//					int d = findDecimal(sb.toString());
//					int changeValue = hm.get(nrt) >> d;
//					hm.put(nrd, changeValue);
//					cycleCount+=1;
//					tm.put("SRL R" + nrd + ", " + "R" + nrt + ", " + "#" + d, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "SRL R" + nrd + ", " + "R" + nrt + ", " + "#" + d);//SRL
////					printValues(hm, map, beginDataAddress);//SRL
//				}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder();
//					sb = sb.append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
//					int nrt = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
//					int nrd = findDecimal(sb.toString());
//					sb.setLength(0);
//					sb = sb.append(s.charAt(21)).append(s.charAt(22)).append(s.charAt(23)).append(s.charAt(24)).append(s.charAt(25));
//					int d = findDecimal(sb.toString());
//					int changeValue = hm.get(nrt) >> d;
//					hm.put(nrd, changeValue);
//					cycleCount += 1;
//					tm.put("SRA R" + nrd + ", " + "R" + nrt + ", " + "#" + d, s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "SRA R" + nrd + ", " + "R" + nrt + ", " + "#" + d);//SRA
////					printValues(hm, map, beginDataAddress);//SRA
//				}else if(s.charAt(2) == '1' && s.charAt(3) == '0' && s.charAt(4) == '1' && s.charAt(5) == '1') {
//					cycleCount += 1;
//					tm.put("NOP", s);
////					System.out.println("--------------------");
////					System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "NOP");
////					printValues(hm, map, beginDataAddress);
//				}
//			n2 += 4;
//		}else if (s.charAt(0) == '0' && s.charAt(1) == '1' && s.charAt(2) == '0' && s.charAt(3) == '1'
//				&& s.charAt(4) == '0' && s.charAt(5) == '1') {
//			cycleCount += 1;
//			tm.put("BREAK", s);
////			System.out.println("--------------------");
////			System.out.println("Cycle:" + cycleCount + "\t" + n2 + "\t" + "BREAK");
////			printValues(hm, map, beginDataAddress);
//			n2 += 4;
//		}
//		}
		sim.runSim();
	}

	
	public void runSim() {
		
		clockCycle = 7;
		isStalled = false;
		notFetch = false;
		preIssueQ.add(0, "");preIssueQ.add(1, "");preIssueQ.add(2, "");preIssueQ.add(3, "");
		preALU2Q.add("");preALU2Q.add("");
		postALUQ.add(0, "");fetchQ.add("");fetchQ.add("");
		
		while(clockCycle != 8) {
			
			writeBackStage();
			postALUStage();
			preALUStage();
			preIssueStage();
//			ifUnitStage();
			printValues(hm, map, beginDataAddress, fileSimulation);
			clockCycle++;
		}
	}
	
	
	
//	public void ifUnitStage() {
//		if(!notFetch) {
//			if(!isStalled && preIssueQ.size() <= 4) {
//				fetchQ.add(originalQ.get(0));
//				originalQ.remove(0);
//				String s1 = tm.get(fetchQ.get(0));
//				if (s1.charAt(0) == '0' && s1.charAt(1) == '1' && s1.charAt(2) == '0' && s1.charAt(3) == '1'
//						&& s1.charAt(4) == '0' && s1.charAt(5) == '1') {
//					notFetch = true; //BREAK instruction
//				}
//				else if(s1.charAt(0) == '0' && s1.charAt(1) == '1' && s1.charAt(2) == '0' && s1.charAt(3) == '0'
//						&& s1.charAt(4) == '0' && s1.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s1.charAt(6)).append(s1.charAt(7)).append(s1.charAt(8)).append(s1.charAt(9)).append(s1.charAt(10)).append(s1.charAt(11)).append(s1.charAt(12)).append(s1.charAt(13)).append(s1.charAt(14)).append(s1.charAt(15)).append(s1.charAt(16)).append(s1.charAt(17)).append(s1.charAt(18)).append(s1.charAt(19)).append(s1.charAt(20)).append(s1.charAt(21)).append(s1.charAt(22)).append(s1.charAt(23)).append(s1.charAt(24)).append(s1.charAt(25)).append(s1.charAt(26)).append(s1.charAt(27)).append(s1.charAt(28)).append(s1.charAt(29)).append(s1.charAt(30)).append(s1.charAt(31));
//					int d = findDecimal(sb.toString());
//					sb.setLength(0);
//					n = d * 4;//J instruction
//				}
//				else if(s1.charAt(0) == '0' && s1.charAt(1) == '1' && s1.charAt(2) == '0' && s1.charAt(3) == '0' 
//						&& s1.charAt(4) == '0' && s1.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s1.charAt(6)).append(s1.charAt(7)).append(s1.charAt(8)).append(s1.charAt(9)).append(s1.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					n = hm.get(nrs);//JR instruction maybe wrong
//				}
//				else if(s1.charAt(0) == '0' && s1.charAt(1) == '1' && s1.charAt(1) == '0' && s1.charAt(1) == '0' 
//						&& s1.charAt(4) == '1' && s1.charAt(5) == '0') {
//					StringBuilder sb1 = new StringBuilder().append(s1.charAt(6)).append(s1.charAt(7)).append(s1.charAt(8)).append(s1.charAt(9)).append(s1.charAt(10));
//					int nrs = findDecimal(sb1.toString());
//					StringBuilder sb2 = new StringBuilder().append(s1.charAt(11)).append(s1.charAt(12)).append(s1.charAt(13)).append(s1.charAt(14)).append(s1.charAt(15));
//					int nrt = findDecimal(sb2.toString());
//					StringBuilder sb = new StringBuilder().append(s1.charAt(16)).append(s1.charAt(17)).append(s1.charAt(18)).append(s1.charAt(19)).append(s1.charAt(20)).append(s1.charAt(21)).append(s1.charAt(22)).append(s1.charAt(23)).append(s1.charAt(24)).append(s1.charAt(25)).append(s1.charAt(26)).append(s1.charAt(27)).append(s1.charAt(28)).append(s1.charAt(29)).append(s1.charAt(30)).append(s1.charAt(31));
//					int d = findDecimal(sb.toString());
//					int d4 = d * 4;
//					if(!(registerSet.contains(sb1.toString()) || registerSet.contains(sb2.toString()))) {
//						if(hm.get(nrs) == hm.get(nrt)) {
//							n = n + d4 + 4;
//						}else {
//							fetchQ.add(1, fetchQ.get(0));//BEQ instruction executed instruction
//							fetchQ.set(0, "");//not sure
//						}
//					}else {
//						isStalled = true; //registers busy
//					}
//				}
//				else if(s1.charAt(0) == '0' && s1.charAt(1) == '1' && s1.charAt(2) == '0' && s1.charAt(3) == '0' 
//						&& s1.charAt(4) == '1' && s1.charAt(5) == '1') {
//					StringBuilder sb1 = new StringBuilder().append(s1.charAt(6)).append(s1.charAt(7)).append(s1.charAt(8)).append(s1.charAt(9)).append(s1.charAt(10));
//					int nrs = findDecimal(sb1.toString());
//					sb1.setLength(0);
//					StringBuilder sb2 = new StringBuilder().append(s1.charAt(16)).append(s1.charAt(17)).append(s1.charAt(18)).append(s1.charAt(19)).append(s1.charAt(20)).append(s1.charAt(21)).append(s1.charAt(22)).append(s1.charAt(23)).append(s1.charAt(24)).append(s1.charAt(25)).append(s1.charAt(26)).append(s1.charAt(27)).append(s1.charAt(28)).append(s1.charAt(29)).append(s1.charAt(30)).append(s1.charAt(31));
//					int d = findDecimal(sb2.toString());
//					int d4 = d * 4;
//					if(!registerSet.contains(sb1.toString())) {
//						if(hm.get(nrs) < 0) {
//							n = n + d4 + 4;
//						}else {
//							fetchQ.add(1, fetchQ.get(0));//BLTZ instruction executed instruction
//							fetchQ.set(0, "");
//						}
//					}
//					else {
//							isStalled = true; //registers busy
//						}
//						}
//				else if(s1.charAt(0) == '0' && s1.charAt(1) == '1' && s1.charAt(2) == '0' && s1.charAt(3) == '0' 
//						&& s1.charAt(4) == '1' && s1.charAt(5) == '1') {
//					StringBuilder sb1 = new StringBuilder().append(s1.charAt(6)).append(s1.charAt(7)).append(s1.charAt(8)).append(s1.charAt(9)).append(s1.charAt(10));
//					int nrs = findDecimal(sb1.toString());
//					sb1.setLength(0);
//					StringBuilder sb2 = new StringBuilder().append(s1.charAt(16)).append(s1.charAt(17)).append(s1.charAt(18)).append(s1.charAt(19)).append(s1.charAt(20)).append(s1.charAt(21)).append(s1.charAt(22)).append(s1.charAt(23)).append(s1.charAt(24)).append(s1.charAt(25)).append(s1.charAt(26)).append(s1.charAt(27)).append(s1.charAt(28)).append(s1.charAt(29)).append(s1.charAt(30)).append(s1.charAt(31));
//					int d = findDecimal(sb2.toString());
//					int d4 = d * 4;
//					if(!registerSet.contains(sb1.toString())) {
//						if(hm.get(nrs) > 0) {
//							n = n + d4 + 4;
//						}else {
//							fetchQ.add(1, fetchQ.get(0));//BGTZ instruction executed instruction
//							fetchQ.set(0, "");
//						}
//					}
//					else {
//							isStalled = true; //registers busy
//						}
//						}
//				else {
//					preIssueQ.add(fetchQ.get(0));//Adding non-branch instructions
//					n += 4;
//				}
//				fetchQ.add(originalQ.get(0));
//				originalQ.remove(0);
//				String s2 = tm.get(fetchQ.get(1));//fetching 2nd instruction
//				if (s2.charAt(0) == '0' && s2.charAt(1) == '1' && s2.charAt(2) == '0' && s2.charAt(3) == '1'
//						&& s2.charAt(4) == '0' && s2.charAt(5) == '1') {
//					notFetch = true; //BREAK instruction
//				}
//				else if(s2.charAt(0) == '0' && s2.charAt(1) == '1' && s2.charAt(2) == '0' && s2.charAt(3) == '0'
//						&& s2.charAt(4) == '0' && s2.charAt(5) == '0') {
//					StringBuilder sb = new StringBuilder().append(s2.charAt(6)).append(s2.charAt(7)).append(s2.charAt(8)).append(s2.charAt(9)).append(s2.charAt(10)).append(s2.charAt(11)).append(s2.charAt(12)).append(s2.charAt(13)).append(s2.charAt(14)).append(s2.charAt(15)).append(s2.charAt(16)).append(s2.charAt(17)).append(s2.charAt(18)).append(s2.charAt(19)).append(s2.charAt(20)).append(s2.charAt(21)).append(s2.charAt(22)).append(s2.charAt(23)).append(s2.charAt(24)).append(s2.charAt(25)).append(s2.charAt(26)).append(s2.charAt(27)).append(s2.charAt(28)).append(s2.charAt(29)).append(s2.charAt(30)).append(s2.charAt(31));
//					int d = findDecimal(sb.toString());
//					sb.setLength(0);
//					n = d * 4;//J instruction
//				}
//				else if(s2.charAt(0) == '0' && s2.charAt(1) == '1' && s2.charAt(2) == '0' && s2.charAt(3) == '0' 
//						&& s2.charAt(4) == '0' && s2.charAt(5) == '1') {
//					StringBuilder sb = new StringBuilder().append(s2.charAt(6)).append(s2.charAt(7)).append(s2.charAt(8)).append(s2.charAt(9)).append(s2.charAt(10));
//					int nrs = findDecimal(sb.toString());
//					sb.setLength(0);
//					n = hm.get(nrs);//JR instruction maybe wrong
//				}
//				else if(s2.charAt(0) == '0' && s2.charAt(1) == '1' && s2.charAt(1) == '0' && s2.charAt(1) == '0' 
//						&& s2.charAt(4) == '1' && s2.charAt(5) == '0') {
//					StringBuilder sb1 = new StringBuilder().append(s2.charAt(6)).append(s2.charAt(7)).append(s2.charAt(8)).append(s2.charAt(9)).append(s2.charAt(10));
//					int nrs = findDecimal(sb1.toString());
//					StringBuilder sb2 = new StringBuilder().append(s2.charAt(11)).append(s2.charAt(12)).append(s2.charAt(13)).append(s2.charAt(14)).append(s2.charAt(15));
//					int nrt = findDecimal(sb2.toString());
//					StringBuilder sb = new StringBuilder().append(s2.charAt(16)).append(s2.charAt(17)).append(s2.charAt(18)).append(s2.charAt(19)).append(s2.charAt(20)).append(s2.charAt(21)).append(s2.charAt(22)).append(s2.charAt(23)).append(s2.charAt(24)).append(s2.charAt(25)).append(s2.charAt(26)).append(s2.charAt(27)).append(s2.charAt(28)).append(s2.charAt(29)).append(s2.charAt(30)).append(s2.charAt(31));
//					int d = findDecimal(sb.toString());
//					int d4 = d * 4;
//					if(!(registerSet.contains(sb1.toString()) || registerSet.contains(sb2.toString()))) {
//						if(hm.get(nrs) == hm.get(nrt)) {
//							n = n + d4 + 4;
//						}else {
//						//	fetchQ.add(1, fetchQ.get(0));//BEQ instruction executed instruction
//							fetchQ.set(1, "");//not sure
//						}
//					}else {
//						isStalled = true; //registers busy
//					}
//				}
//				else if(s2.charAt(0) == '0' && s2.charAt(1) == '1' && s2.charAt(2) == '0' && s2.charAt(3) == '0' 
//						&& s2.charAt(4) == '1' && s2.charAt(5) == '1') {
//					StringBuilder sb1 = new StringBuilder().append(s2.charAt(6)).append(s2.charAt(7)).append(s2.charAt(8)).append(s2.charAt(9)).append(s2.charAt(10));
//					int nrs = findDecimal(sb1.toString());
//					sb1.setLength(0);
//					StringBuilder sb2 = new StringBuilder().append(s2.charAt(16)).append(s2.charAt(17)).append(s2.charAt(18)).append(s2.charAt(19)).append(s2.charAt(20)).append(s2.charAt(21)).append(s2.charAt(22)).append(s2.charAt(23)).append(s2.charAt(24)).append(s2.charAt(25)).append(s2.charAt(26)).append(s2.charAt(27)).append(s2.charAt(28)).append(s2.charAt(29)).append(s2.charAt(30)).append(s2.charAt(31));
//					int d = findDecimal(sb2.toString());
//					int d4 = d * 4;
//					if(!registerSet.contains(sb1.toString())) {
//						if(hm.get(nrs) < 0) {
//							n = n + d4 + 4;
//						}else {
//						//	fetchQ.add(1, fetchQ.get(0));//BLTZ instruction executed instruction
//							fetchQ.set(1, "");
//						}
//					}
//					else {
//							isStalled = true; //registers busy
//						}
//						}
//				else if(s2.charAt(0) == '0' && s2.charAt(1) == '1' && s2.charAt(2) == '0' && s2.charAt(3) == '0' 
//						&& s2.charAt(4) == '1' && s2.charAt(5) == '1') {
//					StringBuilder sb1 = new StringBuilder().append(s2.charAt(6)).append(s2.charAt(7)).append(s2.charAt(8)).append(s2.charAt(9)).append(s2.charAt(10));
//					int nrs = findDecimal(sb1.toString());
//					sb1.setLength(0);
//					StringBuilder sb2 = new StringBuilder().append(s2.charAt(16)).append(s2.charAt(17)).append(s2.charAt(18)).append(s2.charAt(19)).append(s2.charAt(20)).append(s2.charAt(21)).append(s2.charAt(22)).append(s2.charAt(23)).append(s2.charAt(24)).append(s2.charAt(25)).append(s2.charAt(26)).append(s2.charAt(27)).append(s2.charAt(28)).append(s2.charAt(29)).append(s2.charAt(30)).append(s2.charAt(31));
//					int d = findDecimal(sb2.toString());
//					int d4 = d * 4;
//					if(!registerSet.contains(sb1.toString())) {
//						if(hm.get(nrs) > 0) {
//							n = n + d4 + 4;
//						}else {
//						//	fetchQ.add(1, fetchQ.get(0));//BGTZ instruction executed instruction
//							fetchQ.set(1, "");
//						}
//					}
//					else {
//							isStalled = true; //registers busy
//						}
//						}
//				else {
//					preIssueQ.add(fetchQ.get(0));
//					n += 4;
//				}
//	}

				
//				for(int i = 0; i < originalQ.size(); i++) {
//					System.out.println(originalQ.get(i));
//				}
			
//	if(preIssueQ.size() <= 4) {
//		for(int i = 0; i < preIssueQ.size(); i++) {
//			String s1 = tm.get(preIssueQ.get(i));
//		}
//	}
/*---------------------- PRE-ISSUE STAGE-------------------------*/	
	public void preIssueStage() {
		originalQ.remove(0);originalQ.remove(0);
		preIssueQ.set(0, originalQ.get(1));
		preIssueQ.set(1, originalQ.get(2));
		for(int i = 0; i < preIssueQ.size(); i++) {
			if(tm.containsKey(preIssueQ.get(i))) {
			String s = tm.get(preIssueQ.get(i));
			if(s.charAt(0) == '0' && s.charAt(1) == '1' && s.charAt(2) == '0' && s.charAt(3) == '1'
					&& s.charAt(4) == '1' && s.charAt(5) == '1') {
				StringBuilder sb = new StringBuilder().append(s.charAt(11)).append(s.charAt(12)).append(s.charAt(13)).append(s.charAt(14)).append(s.charAt(15));
				preDestSet.add(sb.toString());//Added to preIssue destination registers
				sb.setLength(0);//LW
				sb = new StringBuilder().append(s.charAt(6)).append(s.charAt(7)).append(s.charAt(8)).append(s.charAt(9)).append(s.charAt(10));
				preSourceSet.add(sb.toString());//Added to preIssue source registers
				sb.setLength(0);//LW
			}
			else if(s.charAt(0) == '0' && s.charAt(1) == '1' && s.charAt(2) == '1' && s.charAt(3) == '0'
					&& s.charAt(4) == '0' && s.charAt(5) == '0') {
				StringBuilder sb = new StringBuilder().append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
				preDestSet.add(sb.toString());//Added to preIssue destination registers
				sb.setLength(0);//SLL instruction
				sb = new StringBuilder().append(s.charAt(16)).append(s.charAt(17)).append(s.charAt(18)).append(s.charAt(19)).append(s.charAt(20));
				preSourceSet.add(sb.toString());//Added to preIssue source registers
				sb.setLength(0);//SLL instruction
			}
		}
		}
		
//		 Iterator<String> itr = registerSet.iterator();
//	        while(itr.hasNext()){
//	            System.out.println(itr.next());
//	        }
//		for(int i = 0; i < originalQ.size(); i++) {
//			for (int key : map.keySet()) {
//		        if (map.get(key) == tm.get(originalQ.get(i))) {
//		            System.out.println(originalQ.get(i) + " " + key);
//		        }
//		    }
//		}
		
	}
	
	public void preALUStage() {
		String s = tm.get(preIssueQ.get(0));
		
		preALU2Q.set(0, preIssueQ.get(0));
		
	}
	
	public void postALUStage() {
		
	}
	
	public void writeBackStage() {
		
	}
	
	
	public static String findAllRegisters(String s3) {
		String rs = ""; String rt = ""; String rd = "";
		StringBuilder sb = new StringBuilder().append(s3.charAt(6)).append(s3.charAt(7)).append(s3.charAt(8)).append(s3.charAt(9)).append(s3.charAt(10));
		rs = findRegister(sb.toString());
		sb.setLength(0);
		sb = sb.append(s3.charAt(11)).append(s3.charAt(12)).append(s3.charAt(13)).append(s3.charAt(14)).append(s3.charAt(15));
		rt = findRegister(sb.toString());
		sb.setLength(0);
		sb = sb.append(s3.charAt(16)).append(s3.charAt(17)).append(s3.charAt(18)).append(s3.charAt(19)).append(s3.charAt(20));
		rd = findRegister(sb.toString());
		return rd+", "+rs+", "+rt;
	}

	public static String findRegister(String input) {
		int result = Integer.parseInt(input);
		int decimal = 0;
		int p = 0;
		while (result != 0) {
			decimal += ((result % 10) * Math.pow(2, p));
			result = result / 10;
			p++;
		}
		return "R"+decimal;

	}
	
	public static int findDecimal(String input) {
		int result = Integer.parseInt(input);
		int decimal = 0;
		int p = 0;
		while (result != 0) {
			decimal += ((result % 10) * Math.pow(2, p));
			result = result / 10;
			p++;
		}
		return decimal;

	}
	
	public static void printValues(Map<Integer, Integer> hm, Map<Integer, String> map, int beginDataAddress, File fileSimulation) {
		System.out.println("");
		System.out.println("--------------------");
		System.out.println("Cycle: " + clockCycle);
		System.out.println("");
		System.out.println("IF Unit:");
		System.out.println("\t" + "Waiting Instruction:" + " " + fetchQ.get(0));
		System.out.println("\t" + "Executed Instruction:" + " " + fetchQ.get(1));
		System.out.println("Pre-Issue Queue:");
		for (int i = 0; i < preIssueQ.size(); i++) {
            System.out.println("\t" + "Entry "+ i + ": " + preIssueQ.get(i));
        }
		System.out.println("Pre-ALU1 Queue:");
		System.out.println("\t" + "Entry 0: ");
		System.out.println("\t" + "Entry 1: ");
		System.out.println("Pre-MEM Queue:");
		System.out.println("Post-MEM Queue:");
		System.out.println("Pre-ALU2 Queue:");
		System.out.println("\t" + "Entry 0: " + preALU2Q.get(0));
		System.out.println("\t" + "Entry 1: " + preALU2Q.get(1));
		System.out.println("Post-ALU2 Queue: " + postALUQ.get(0));
		System.out.println("Registers");
		System.out.println("R00:" + "\t" + hm.get(0) + "\t" + hm.get(1) + "\t" + hm.get(2) + "\t" + hm.get(3) + "\t" + hm.get(4) + "\t" + hm.get(5) + "\t" + hm.get(6) + "\t" + hm.get(7));
		System.out.println("R08:" + "\t" + hm.get(8) + "\t" + hm.get(9) + "\t" + hm.get(10) + "\t" + hm.get(11) + "\t" + hm.get(12) + "\t" + hm.get(13) + "\t" + hm.get(14) + "\t" + hm.get(15));
		System.out.println("R16:" + "\t" + hm.get(16) + "\t" + hm.get(17) + "\t" + hm.get(18) + "\t" + hm.get(19) + "\t" + hm.get(20) + "\t" + hm.get(21) + "\t" + hm.get(22) + "\t" + hm.get(23));
		System.out.println("R24:" + "\t" + hm.get(24) + "\t" + hm.get(25) + "\t" + hm.get(26) + "\t" + hm.get(27) + "\t" + hm.get(28) + "\t" + hm.get(29) + "\t" + hm.get(30) + "\t" + hm.get(31));
		System.out.println("");
		System.out.println("Data");
		int i = beginDataAddress;//336
		int j = 256 + (map.size()-1)*4;//432
		int x = i + 4;
		System.out.print(x + ":" + "\t");
		while(x <= j) {
			for(int a = 0; a < 8; a++) {
				System.out.print(map.get(x+a*4) + "\t");
			}
			System.out.print("\n");
			x += 32;
			if(x <= j)
				System.out.print(x + ":" + "\t");
		}
		System.out.println("");
	}
}