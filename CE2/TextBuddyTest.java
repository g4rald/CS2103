import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TextBuddyTest {

	@Before 
	public void initializeFile() throws IOException {
		TextBuddy.givenFileName = "abc.txt";
		TextBuddy.clearCommand();
	}
	
	@Test
	public void testArgumentsEntered() {
		String[] nullArgument = new String[0];
		String[] args = new String[10];
		args[0] = "abc";

		assertTrue(TextBuddy.noArgumentsEntered(nullArgument));

		assertFalse(TextBuddy.noArgumentsEntered(args));
	}

	@Test
	public void testFileExtension() {
		assertTrue(TextBuddy.fileExtensionWrong(".lopo"));
		assertTrue(TextBuddy.fileExtensionWrong(".abc"));
		assertFalse(TextBuddy.fileExtensionWrong(".tXt"));
		assertFalse(TextBuddy.fileExtensionWrong(".TXT"));
	}

	@Test
	public void testFileLength() {
		assertTrue(TextBuddy.insufficentFileLength("txt"));
		assertTrue(TextBuddy.insufficentFileLength(""));
		assertFalse(TextBuddy.insufficentFileLength("abc.txt"));
		assertFalse(TextBuddy.insufficentFileLength("zxcsdasd"));
	}

	@Test
	public void testInvalidLineNumber() {
		TextBuddy.totalLinesInFile = 100;
		assertTrue(TextBuddy.isInvalidLineNumber(-99));
		assertTrue(TextBuddy.isInvalidLineNumber(0));
		assertFalse(TextBuddy.isInvalidLineNumber(50));
		assertFalse(TextBuddy.isInvalidLineNumber(1));
	}
	
	@Test
	public void testAddCommand() throws IOException {
		String command1 = "add";
		Scanner sc = new Scanner("stupid dog");
		String expected = "added to " + TextBuddy.givenFileName + ": \"" + "stupid dog" + "\"";
		assertEquals(expected,TextBuddy.executeCommand(sc, command1));
	}
	
	@Test
	public void testClearCommand() throws IOException {
		String command1 = "clear";
		Scanner sc = new Scanner(System.in);
		String expected = "all content deleted from " + TextBuddy.givenFileName;
		assertEquals(expected,TextBuddy.executeCommand(sc, command1));
	}
	
	@Test
	public void testDeleteCommand() throws IOException {
		Scanner sc1 = new Scanner ("haha");
		Scanner sc2 = new Scanner ("lol");
		TextBuddy.writeTextToFile(sc1);
		TextBuddy.writeTextToFile(sc2);
		String command1 = "delete";
		Scanner sc3 = new Scanner("2");
		String expected = "deleted from " + TextBuddy.givenFileName + ": \"" + "lol" + "\"";
		assertEquals(expected,TextBuddy.executeCommand(sc3, command1));
	}
	
	@Test
	public void testDisplayCommand() throws IOException {
		Scanner sc1 = new Scanner ("haha");
		Scanner sc2 = new Scanner ("lol");
		TextBuddy.writeTextToFile(sc1);
		TextBuddy.writeTextToFile(sc2);
		String command1 = "display";
		Scanner sc = new Scanner(System.in);
		String expected = "1. haha\n2. lol";
		assertEquals(expected,TextBuddy.executeCommand(sc, command1));
	}
	
	@Test
	public void testSearchCommand() throws IOException {
		Scanner sc1 = new Scanner ("haha");
		Scanner sc2 = new Scanner ("lol");
		TextBuddy.writeTextToFile(sc1);
		TextBuddy.writeTextToFile(sc2);
		String command1 = "search";
		Scanner sc3 = new Scanner("ah");
		String expected = "Total results : 1";
		assertEquals(expected,TextBuddy.executeCommand(sc3, command1));
	}
	
	@Test
	public void testSortCommand() throws IOException {
		Scanner sc1 = new Scanner ("lol");
		Scanner sc2 = new Scanner ("haha");
		TextBuddy.writeTextToFile(sc1);
		TextBuddy.writeTextToFile(sc2);
		TextBuddy.sortCommand();
		Scanner sc = new Scanner(System.in);
		String command1 = "display";
		String expected = "1. haha\n2. lol";
		assertEquals(expected,TextBuddy.executeCommand(sc, command1));
	}
		
	@Test
	public void testInvalidCommand() throws IOException {
		Scanner sc = new Scanner(System.in);
		String command = "zxcas";
		String expected = "Invalid command! Please retry.";
		assertEquals(expected,TextBuddy.executeCommand(sc, command));
	}
	
	@Test
	public void testDisplayEmpty() throws IOException {
		String command1 = "display";
		Scanner sc = new Scanner(System.in);
		String expected = TextBuddy.givenFileName + " is empty";
		assertEquals(expected,TextBuddy.executeCommand(sc, command1));
	}
	
	@Test
	public void testSearchNoResults() throws IOException {
		Scanner sc1 = new Scanner ("haha");
		Scanner sc2 = new Scanner ("lol");
		TextBuddy.writeTextToFile(sc1);
		TextBuddy.writeTextToFile(sc2);
		String command1 = "search";
		Scanner sc3 = new Scanner("bbb");
		String expected = "No such word found!";
		assertEquals(expected,TextBuddy.executeCommand(sc3, command1));
	}
	
	@Test
	public void testDeleteInvalid() throws IOException {
		String command1 = "add";
		Scanner sc = new Scanner("stupid dog");
		TextBuddy.executeCommand(sc, command1);
		String command2 = "delete";
		Scanner sc3 = new Scanner("2");
		String expected = "Invalid command! Please retry.";
		assertEquals(expected,TextBuddy.executeCommand(sc3, command2));
	}
	
	@After
	public void deleteFile() throws IOException {
		File file = new File("abc.txt");
		TextBuddy.clearCommand();
		file.delete();
	}

}
