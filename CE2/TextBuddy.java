import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * TextBuddy <CS2103 CE1>
 * 
 * TextBuddy is a CLI Java program that can be used to manipulate text in file.
 * 
 * This program will create a text file with given file name entered by user.
 * The user can begin manipulating the file with commands. The program will save
 * the given text file as long as the file contains text content. Upon exiting
 * the program, the given text file will be deleted if there are no text content
 * within the file (i.e, file is empty). Otherwise, the given text file is
 * saved.
 * 
 * User can enter the following commands : 
 * "add <text>" - add text content into the file 
 * "display" - display the file content 
 * "delete <number>" - deletes the text at the specified line entered by user 
 * "clear" - clears the file content in file 
 * "sort" - to sort lines alphabetically. 
 * "search" - search for a word in the file and return the lines containing that word. 
 * "exit" - exits the program
 * 
 * ============== Assumptions ============== 
 * User will ALWAYS exit the program by typing command : exit
 * 
 * @author GERALD THAM WAI KIT
 *
 */
public class TextBuddy {

	// list of invalid messages to be printed by program
	static final String MESSAGE_CORRECT_USAGE = "\nCorrect usage : java TextBuddy <filename>.txt";
	static final String MESSAGE_INVALID_COMMAND = "Invalid command! Please retry.";
	static final String MESSAGE_INVALID_ARGUMENT = "Invalid argument entered." + MESSAGE_CORRECT_USAGE;
	static final String MESSAGE_INVALID_EXTENSION = "Invalid file extension entered." + MESSAGE_CORRECT_USAGE;
	static final String MESSAGE_NO_ARGUMENTS = "No arguments entered. Please enter argument." + MESSAGE_CORRECT_USAGE;

	// list of parameters for file
	static final int INVALID_LINE_NUMBER = -99;
	static final int MINIMUM_FILE_LENGTH = 4;
	static final int EMPTY = 0;
	static final int START_LINE = 1;
	static final int INCREASE_LINE = 1;
	static final int DECREASE_LINE = -1;
	static final String SUCCESS = "";
	static boolean isFileEmpty = true;
	static boolean isValidDelete = true;
	static int totalLinesInFile = 0;

	// to indicate the given file name
	static String givenFileName = "";

	// updated text that will replace the text content in file
	static String updatedText = "";

	static Scanner sc = new Scanner(System.in);

	/**
	 * The program will check if arguments entered by user is valid. If so,
	 * welcome the user and start the program for user to input user commands.
	 */
	public static void main(String[] args) throws IOException {
		checkCorrectArguments(args);
		showWelcomeMessage();
		startProgram();
	}
	
	/**
	 * Add the text user entered into the file and display the added message.
	 */
	public static String addCommand(Scanner sc) throws IOException {
		String text = writeTextToFile(sc);
		return printMessage(messageType("ADDED", text));
	}
	
	/**
	 * Check whether the argument user entered beginning is valid. If invalid,
	 * show invalid message. Otherwise, initialize the file for manipulation.
	 */
	public static void checkCorrectArguments(String[] fileName) {
		if (noArgumentsEntered(fileName)) {
			printMessage(messageType(MESSAGE_NO_ARGUMENTS, null));
			exitCommand();
		} else if (insufficentFileLength(fileName[0])) {
			printMessage(messageType(MESSAGE_INVALID_ARGUMENT, null));
			exitCommand();
		} else if (fileExtensionWrong(fileName[0])) {
			printMessage(messageType(MESSAGE_INVALID_EXTENSION, null));
			exitCommand();
		} else {
			initialiseFile(fileName[0]);
		}
	}

	/**
	 * Check if the file contains any text content. If file is empty, display
	 * empty message. Otherwise, display file content.
	 */
	public static String checkEmptyElseDisplayFileContent() throws IOException {
		checkFileIsEmpty();

		if (isFileEmpty) {
			return printMessage(messageType("EMPTY", null));
		} else {
			return displayFileContent();
		}
	}

	/**
	 * To indicate if file does not contain any text content (i.e empty).
	 */
	public static void checkFileIsEmpty() {
		File file = new File(givenFileName);
		if (file.length() == EMPTY) {
			isFileEmpty = true;
		} else {
			isFileEmpty = false;
		}
	}

	/**
	 * Update the total lines in file first. Check if the user entered a valid
	 * line number (between 1 to total_lines, both inclusive).
	 */
	public static int checkValidLineNumber(Scanner sc) throws IOException {
		getTotalLinesInFile();

		try {
			int lineNumber = sc.nextInt();
			if (isInvalidLineNumber(lineNumber)) {
				printMessage(messageType(MESSAGE_INVALID_COMMAND, null));
				isValidDelete = false;
				return INVALID_LINE_NUMBER;
			} else {
				isValidDelete = true;
				return lineNumber;
			}
		} catch (InputMismatchException e) {
			printMessage(messageType(MESSAGE_INVALID_COMMAND, null));
			isValidDelete = false;
			return INVALID_LINE_NUMBER;
		}
	}

	/**
	 * Clear the file content and display clear message.
	 */
	public static String clearCommand() throws IOException {
		clearTextInFile();
		return printMessage(messageType("CLEAR", givenFileName));
	}

	/**
	 * Clear the file content
	 */
	public static void clearTextInFile() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(givenFileName);
		writer.print("");
		writer.close();
	}

	/**
	 * Check if line to delete is a valid line. If so, delete the line and
	 * update the new file content. Otherwise, display invalid message.
	 */
	public static String deleteCommand(Scanner sc) throws IOException {
		int lineNumberToDelete = checkValidLineNumber(sc);
		if (isValidDelete) {
			return deleteLineAndUpdateFile(lineNumberToDelete);
		}
		else {
			return printMessage(messageType(MESSAGE_INVALID_COMMAND, null));
		}
	}

	/**
	 * Read the file, delete/ignore the line indicated by user,
	 * and update the file.
	 */
	public static String deleteLineAndUpdateFile(int lineToDelete)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(givenFileName));
		int currentLine = START_LINE;
		boolean isNewFirstLineInFile = true;
		String text;
		String deletedText = "";
		while ((text = in.readLine()) != null) {
			if (currentLine == lineToDelete) {
				deletedText = text;
				updateTotalLines(DECREASE_LINE);
			} else {
				storeUpdatedText(text, isNewFirstLineInFile);
				isNewFirstLineInFile = false;
			}
			currentLine++;
		}
		in.close();
		updateFile(updatedText);
		return printMessage(messageType("DELETE", deletedText));
	}

	/**
	 * Check if file is empty before displaying empty message or file content
	 */
	public static String displayCommand() throws IOException {
		return checkEmptyElseDisplayFileContent();
	}

	/**
	 * Display the file content with the numbering at the left hand.
	 */
	public static String displayFileContent() throws IOException {
		String readText;
		int lineNumber = START_LINE;
		BufferedReader in = new BufferedReader(new FileReader(givenFileName));
		while ((readText = in.readLine()) != null) {
			System.out.println(lineNumber + ". " + readText);
			lineNumber++;
		}
		in.close();
		return SUCCESS;
	}

	/**
	 * Execute the user command.
	 */
	public static String executeCommand(Scanner sc, String command)
			throws IOException {
		if (command.equalsIgnoreCase("add")) {
			return addCommand(sc);
		} else if (command.equalsIgnoreCase("display")) {
			return displayCommand();
		} else if (command.equalsIgnoreCase("delete")) {
			return deleteCommand(sc);
		} else if (command.equalsIgnoreCase("clear")) {
			return clearCommand();
		} else if (command.equalsIgnoreCase("exit")) {
			return exitCommand();
		} else if (command.equalsIgnoreCase("sort")) {
			return sortCommand();
		} else if (command.equalsIgnoreCase("search")) {
			return searchCommand(sc);
		} else {
			return invalidCommand();
		}
	}

	/**
	 * Exit the program. If the given file is empty, delete it off from local
	 * disk. Otherwise, keep it saved in local disk.
	 */
	public static String exitCommand() {
		checkFileIsEmpty();
		File file = new File(givenFileName);
		if (isFileEmpty) {
			file.delete();
		}

		System.exit(0);
		return SUCCESS;
	}

	/**
	 * Extract the data in the sorted arraylist.
	 */
	public static void extractSortedText(ArrayList<String> sortedList) {
		for (int i = 0; i < sortedList.size(); i++) {
			if (i == 0) {
				storeUpdatedText(sortedList.get(i), true);
			} else {
				storeUpdatedText(sortedList.get(i), false);
			}
		}
	}

	/**
	 * Check if the file name does not end with ".txt".
	 */
	public static boolean fileExtensionWrong(String fileName) {
		return !(fileName.substring(
				((fileName.length()) - MINIMUM_FILE_LENGTH), fileName.length())
				.equalsIgnoreCase(".txt"));
	}

	/**
	 * Get command from user.
	 */
	public static String getCommand(Scanner sc) {
		System.out.print("command : ");
		String operation = sc.next();
		return operation;
	}

	/**
	 * Check whether the file is empty before getting the updated total lines in
	 * file.
	 */
	public static void getTotalLinesInFile() throws IOException {
		checkFileIsEmpty();
		if (!isFileEmpty) {
			BufferedReader in = new BufferedReader(new FileReader(givenFileName));
			totalLinesInFile = EMPTY;
			while (in.readLine() != null) {
				updateTotalLines(INCREASE_LINE);
			}
			in.close();
		}
	}

	/**
	 * Initialize the file name for usage.
	 */
	public static void initialiseFile(String fileName) {
		givenFileName = fileName;
	}

	/**
	 * Check if the given file name is not minimum 4 character long.
	 */
	public static boolean insufficentFileLength(String fileName) {
		return (fileName.length() <= MINIMUM_FILE_LENGTH);
	}

	/**
	 * Display invalid message if user entered an invalid command.
	 */
	public static String invalidCommand() {
		return printMessage(messageType(MESSAGE_INVALID_COMMAND, null));
	}

	/**
	 * Check if line number is within range.
	 */
	public static boolean isInvalidLineNumber(int lineNumber) {
		return lineNumber <= EMPTY || lineNumber > totalLinesInFile;
	}

	/**
	 * Display the different kind of possible messages types for user.
	 */
	public static String messageType(String message, String item) {
		if (message.equalsIgnoreCase("ADDED")) {
			return "added to " + givenFileName + ": \"" + item + "\"";
		} else if (message.equalsIgnoreCase("CLEAR")) {
			return "all content deleted from " + givenFileName;
		} else if (message.equalsIgnoreCase("DELETE")) {
			return "deleted from " + givenFileName + ": \"" + item + "\"";
		} else if (message.equalsIgnoreCase("EMPTY")) {
			return givenFileName + " is empty";
		} else if (message.equalsIgnoreCase("SORT")) {
			return givenFileName + " is sorted.";
		} else if (message.equalsIgnoreCase("SEARCH")) {
			if (item.equals(Integer.toString(EMPTY))) {
				return "No such word found!";
			} else {
				return "Total results : " + item;
			}
		} else {
			return message;
		}
	}
	
	/**
	 * Check if the user did not entered any arguments.
	 */
	public static boolean noArgumentsEntered(String[] fileName) {
		return (fileName.length == EMPTY);
	}
	/**
	 * Performs a search of the given word and display lines containing it
	 */
	public static String performSearch(String searchedWord) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(givenFileName));
		String readText;
		int lineNo = START_LINE;
		int totalResult = EMPTY;
		while ((readText = in.readLine()) != null) {
			if (wordIsFound(readText,searchedWord)) {
				System.out.println(lineNo + ". " + readText);
				totalResult++;
				lineNo++;
			}
		}
		in.close();
		return Integer.toString(totalResult);
	}
	
	/**
	 * Print the given message tpye
	 */
	public static String printMessage(String message) {
		System.out.println(message);
		return message;
	}

	/**
	 * Get user searched word, perform search and display results
	 */
	public static String searchCommand(Scanner sc) throws IOException {
		String searchedWord = trimWord(sc);
		String totalResult = performSearch(searchedWord);
		return printMessage(messageType("SEARCH",totalResult));
	}

	/**
	 * Display the welcome messsage.
	 */
	public static void showWelcomeMessage() {
		System.out.println("Welcome to TextBuddy. " + givenFileName + " is ready for use");
	}

	/**
	 * Get the data in file and put in arraylist for sorting, 
	 * extract the sorted data, update the file and display message.
	 */
	public static String sortCommand() throws IOException {
		ArrayList<String> sortedList = sortFile();
		extractSortedText(sortedList);
		updateFile(updatedText);
		return printMessage(messageType("SORT",givenFileName));
	}
	
	/**
	 * Get data in file for sort and return a sorted arraylist.
	 */
	public static ArrayList<String> sortFile() throws IOException {
		String readText;
		ArrayList<String> toBeSortedList = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new FileReader(givenFileName));
		while ((readText = in.readLine()) != null) {
			toBeSortedList.add(readText);
		}
		Collections.sort(toBeSortedList, String.CASE_INSENSITIVE_ORDER);
		in.close();
		return toBeSortedList;
	}

	/**
	 * Constantly get commands from user and execute it correctly.
	 */
	public static void startProgram() throws IOException {
		while (true) {
			String userCommand = getCommand(sc);
			executeCommand(sc, userCommand);
		}
	}

	/**
	 * Store the new updated string which is used in updating the file content.
	 */
	public static void storeUpdatedText(String text, boolean isNewFirstLineInFile) {
		if (isNewFirstLineInFile) {
			updatedText += text;
		} else {
			updatedText += "\n" + text;
		}
	}
	
	/**
	 * Trim the given word.
	 */
	public static String trimWord(Scanner sc) {
		String word = sc.nextLine();
		String trimmedWord = word.trim();
		return trimmedWord;
	}
	
	/**
	 * Replace the file content with the updated string.
	 */
	public static void updateFile(String text) throws IOException {
		getTotalLinesInFile();
		PrintWriter fw = new PrintWriter(givenFileName);
		if (totalLinesInFile != EMPTY) {
			fw.println(text);
		}
		fw.close();
		updatedText = "";
	}

	/**
	 * Increase/decrease total lines in file currently by 'value'.
	 */
	public static void updateTotalLines(int value) {
		totalLinesInFile = totalLinesInFile + value;
	}

	/**
	 * Indicate if the searched word is found.
	 */
	public static boolean wordIsFound(String text, String searchedWord) {
		return text.toLowerCase().contains(searchedWord.toLowerCase());
	}

	/**
	 * Remove leading/trailing whitespace text. Add the
	 * text into file and update total lines in file.
	 */
	public static String writeTextToFile(Scanner sc) throws IOException {
		String textToBeWritten = trimWord(sc);
		PrintWriter fw = new PrintWriter(new BufferedWriter(new FileWriter(givenFileName, true)));
		fw.println(textToBeWritten);
		fw.close();
		updateTotalLines(INCREASE_LINE);
		return textToBeWritten;
	}

}
