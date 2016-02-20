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

	// to indicate if the line number to be deleted is invalid (negative
	// number/wrong format)
	static final int INVALID_LINE_NUMBER = -99;

	// to indicate the minimum file length of a file
	static final int MINIMUM_FILE_LENGTH = 4;

	// to indicate if is empty
	static final int EMPTY = 0;

	// to indicate the starting line
	static final int START_LINE = 1;

	// to indicate total lines increase by 1
	static final int INCREASE_LINE = 1;

	// to indicate total lines decrease by -1
	static final int DECREASE_LINE = -1;

	// to indicate if the file does not content any text content (i.e file is
	// empty)
	static boolean isFileEmpty = true;

	// to indicate if the delete command is valid
	static boolean isValidDelete = true;

	// to indicate the total lines in the file currently
	static int totalLines = 0;

	// to indicate the given file name
	static String givenFileName = "";

	// to indicate the updated text that will replace the text content in file
	// (for delete command)
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
	public static void addCommand(Scanner sc) throws IOException {
		String text = writeTextToFile(sc);
		showMessage("ADDED", text);
	}

	/**
	 * Check whether the argument user entered beginning is valid. If invalid,
	 * show invalid message. Otherwise, initialize the file for manipulation.
	 */
	public static void checkCorrectArguments(String[] fileName) {
		if (noArgumentsEntered(fileName)) {
			showMessage(MESSAGE_NO_ARGUMENTS, null);
			exitCommand();
		} else if (insufficentFileLength(fileName[0])) {
			showMessage(MESSAGE_INVALID_ARGUMENT, null);
			exitCommand();
		} else if (fileExtensionWrong(fileName[0])) {
			showMessage(MESSAGE_INVALID_EXTENSION, null);
			exitCommand();
		} else {
			initialiseFile(fileName[0]);
		}
	}

	/**
	 * Check if the file contains any text content. If file is empty, display
	 * empty message. Otherwise, display file content.
	 */
	public static void checkEmptyElseDisplayFileContent() throws IOException {
		checkFileIsEmpty();

		if (isFileEmpty) {
			showMessage("EMPTY", null);
		} else {
			displayFileContent();
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
			if (invalidLineNumber(lineNumber)) {
				showMessage(MESSAGE_INVALID_COMMAND, null);
				isValidDelete = false;
				return INVALID_LINE_NUMBER;
			} else {
				isValidDelete = true;
				return lineNumber;
			}
		} catch (InputMismatchException e) {
			showMessage(MESSAGE_INVALID_COMMAND, null);
			isValidDelete = false;
			return INVALID_LINE_NUMBER;
		}
	}

	/**
	 * Clear the file content and display clear message.
	 */
	public static void clearCommand() throws IOException {
		clearTextInFile();
		showMessage("CLEAR", givenFileName);
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
	public static void deleteCommand(Scanner sc) throws IOException {
		int lineNumberToDelete = checkValidLineNumber(sc);
		if (isValidDelete) {
			deleteLineAndUpdateFile(lineNumberToDelete);
		}
	}

	/**
	 * Read line by line of the file and store into a string. If the line is
	 * what the user specified to delete, exclude the line from storing into the
	 * string. Update/Replace the file with the new updated string.
	 */
	public static void deleteLineAndUpdateFile(int lineToDelete)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(givenFileName));
		int currentLine = START_LINE;
		boolean isNewFirstLineInFile = true;
		String text;
		while ((text = in.readLine()) != null) {
			if (currentLine == lineToDelete) {
				showMessage("DELETE", text);
				updateTotalLines(DECREASE_LINE);
			} else {
				storeUpdatedText(text, isNewFirstLineInFile);
				isNewFirstLineInFile = false;
			}
			currentLine++;
		}
		in.close();
		updateFile(updatedText);
	}

	/**
	 * Check if file is empty before displaying empty message or file content
	 */
	public static void displayCommand() throws IOException {
		checkEmptyElseDisplayFileContent();
	}

	/**
	 * Display the file content with the numbering at the left hand.
	 */
	public static void displayFileContent() throws IOException {
		String readText;
		int lineNo = START_LINE;
		BufferedReader in = new BufferedReader(new FileReader(givenFileName));
		while ((readText = in.readLine()) != null) {
			System.out.println(lineNo + ". " + readText);
			lineNo++;
		}
		in.close();
	}

	/**
	 * Execute the user command.
	 */
	public static void executeCommand(Scanner sc, String command)
			throws IOException {
		if (command.equalsIgnoreCase("add")) {
			addCommand(sc);
		} else if (command.equalsIgnoreCase("display")) {
			displayCommand();
		} else if (command.equalsIgnoreCase("delete")) {
			deleteCommand(sc);
		} else if (command.equalsIgnoreCase("clear")) {
			clearCommand();
		} else if (command.equalsIgnoreCase("exit")) {
			exitCommand();
		} else if (command.equalsIgnoreCase("sort")) {
			sortCommand();
		} else if (command.equalsIgnoreCase("search")) {
			searchCommand(sc);
		} else {
			invalidCommand();
		}
	}

	/**
	 * Exit the program. If the given file is empty, delete it off from local
	 * disk. Otherwise, keep it saved in local disk.
	 */
	public static void exitCommand() {
		checkFileIsEmpty();
		File file = new File(givenFileName);
		if (isFileEmpty) {
			file.delete();
		}

		System.exit(0);
	}

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
			BufferedReader in = new BufferedReader(
					new FileReader(givenFileName));
			totalLines = EMPTY;
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
	public static void invalidCommand() {
		showMessage(MESSAGE_INVALID_COMMAND, null);
	}

	/**
	 * Check if line number is within range.
	 */
	public static boolean invalidLineNumber(int lineNumber) {
		return lineNumber <= EMPTY || lineNumber > totalLines;
	}

	/**
	 * Check if the user did not entered any arguments.
	 */
	public static boolean noArgumentsEntered(String[] fileName) {
		return (fileName.length == EMPTY);
	}

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
	
	public static void searchCommand(Scanner sc) throws IOException {
		String searchedWord = trimWord(sc);
		String totalResult = performSearch(searchedWord);
		showMessage("SEARCH",totalResult);

	}

	/**
	 * Display the different kind of possible messages for user.
	 */
	public static void showMessage(String message, String item) {
		if (message.equalsIgnoreCase("ADDED")) {
			System.out.println("added to " + givenFileName + ": \"" + item + "\"");
		} else if (message.equalsIgnoreCase("CLEAR")) {
			System.out.println("all content deleted from " + givenFileName);
		} else if (message.equalsIgnoreCase("DELETE")) {
			System.out.println("deleted from " + givenFileName + ": \"" + item + "\"");
		} else if (message.equalsIgnoreCase("EMPTY")) {
			System.out.println(givenFileName + " is empty");
		} else if (message.equalsIgnoreCase("SORT")) {
			System.out.println(givenFileName + " is sorted.");
		} else if (message.equalsIgnoreCase("SEARCH")) {
			if (item.equals(Integer.toString(EMPTY))) {
				System.out.println("No such word found!");
			} else {
				System.out.println("Total results : " + item);
			}
		} else {
			System.out.println(message);
		}
	}

	/**
	 * Display the welcome messsage.
	 */
	public static void showWelcomeMessage() {
		System.out.println("Welcome to TextBuddy. " + givenFileName + " is ready for use");
	}

	public static void sortCommand() throws IOException {
		ArrayList<String> sortedList = sortFile();
		extractSortedText(sortedList);
		updateFile(updatedText);
		showMessage("SORT",givenFileName);
	}
	
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
		if (totalLines != EMPTY) {
			fw.println(text);
		}
		fw.close();
		updatedText = "";
	}

	/**
	 * Increase/decrease total lines in file currently by 'value'.
	 */
	public static void updateTotalLines(int value) {
		totalLines = totalLines + value;
	}

	public static boolean wordIsFound(String text, String searchedWord) {
		return text.toLowerCase().contains(searchedWord.toLowerCase());
	}

	/**
	 * Remove any leading/trailing whitespace of the text user entered. Add the
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
