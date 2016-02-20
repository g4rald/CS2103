import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

public class TextBuddyTest {

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
		assertTrue(TextBuddy.fileExtensionWrong(".xyz"));
		assertTrue(TextBuddy.fileExtensionWrong("$2adasd"));

		assertFalse(TextBuddy.fileExtensionWrong(".tXt"));
		assertFalse(TextBuddy.fileExtensionWrong(".TXT"));
		assertFalse(TextBuddy.fileExtensionWrong(".txt"));
		assertFalse(TextBuddy.fileExtensionWrong(".TxT"));
	}

	@Test
	public void testFileLength() {
		assertTrue(TextBuddy.insufficentFileLength("txt"));
		assertTrue(TextBuddy.insufficentFileLength(""));
		assertTrue(TextBuddy.insufficentFileLength("1$@"));
		assertTrue(TextBuddy.insufficentFileLength("%@#$"));

		assertFalse(TextBuddy.insufficentFileLength("abc.txt"));
		assertFalse(TextBuddy.insufficentFileLength("zxcsdasd"));
		assertFalse(TextBuddy.insufficentFileLength("ab@@s"));
		assertFalse(TextBuddy.insufficentFileLength("abcd.lol"));
	}

	@Test
	public void testInvalidLineNumber() {
		TextBuddy.totalLines = 100;

		assertTrue(TextBuddy.invalidLineNumber(-99));
		assertTrue(TextBuddy.invalidLineNumber(0));
		assertTrue(TextBuddy.invalidLineNumber(101));

		assertFalse(TextBuddy.invalidLineNumber(100));
		assertFalse(TextBuddy.invalidLineNumber(50));
		assertFalse(TextBuddy.invalidLineNumber(1));
	}

	@Test
	public void testTextTrim() throws IOException {
		Scanner sc = new Scanner("HAHA   \n    LOL\n    ABC     \n     KAPPPA");
		TextBuddy.givenFileName = "abc.txt";
		assertEquals("HAHA", TextBuddy.writeTextToFile(sc));
		assertEquals("LOL", TextBuddy.writeTextToFile(sc));
		assertEquals("ABC", TextBuddy.writeTextToFile(sc));
		TextBuddy.clearCommand();
	}	
}
