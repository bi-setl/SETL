package core;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import helper.Methods;
import helper.Variables;

public class FileSeparation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String filePath = "I:\\Documents\\SETL\\level\\rdf\\large\\recipient_wrapper.ttl";
//		long bytesPerSplit = 102400;
//
//		testMethods(filePath, bytesPerSplit);

//		String filePath = "I:\\Documents\\SETL\\fact\\subsidy_wrapper.ttl";
//		String filePath = "C:\\Users\\Amrit\\Documents\\construct\\level\\rdf\\large\\recipient_wrapper.ttl";
//		long bytesPerSplit = 102400;
//
//		testMethods(filePath, bytesPerSplit);
//		
//		long bytesPerSplit2 = 1024000;
//
//		testMethods(filePath, bytesPerSplit2);
//		
//		long bytesPerSplit3 = 10240000;
//
//		testMethods(filePath, bytesPerSplit3);

		String filePath = "C:\\Users\\USER\\Documents\\level\\csv\\large\\recipient_wrapper.nt";

		Methods.startProcessingTime();

		splitFileByLine(filePath, 100000);

		Methods.endProcessingTime();
		
		readModel();
	}

	private static void testMethods(String filePath, long bytesPerSplit) {
		Methods.startProcessingTime();
		int numOfFiles = splitFile(filePath, bytesPerSplit);
		Methods.endProcessingTime();

		System.out.println(numOfFiles);

		Methods.startProcessingTime();
		createModelsSecond(numOfFiles);
		Methods.endProcessingTime();

		// readModel();

		System.out.println("Finished");
	}

	public static int splitFile(String filePath, long bytesPerSplit) {
		// TODO Auto-generated method stub
		File inputFile = new File(Variables.TEMP_DIR + "Splits");
		boolean status = inputFile.mkdir();

		if (status) {
			Methods.print("Directory created");
		}

		File[] listOfFiles = inputFile.listFiles();
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				boolean deleteStatus = file.delete();
			}
		}

		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(filePath, "r");

			long sourceSize = raf.length();
			int numOfFiles = (int) (sourceSize / bytesPerSplit);

			bytesPerSplit = sourceSize / numOfFiles;
			long remainingBytes = sourceSize % numOfFiles;

			int maxReadBufferSize = 8 * 1024;
			for (int destIx = 1; destIx <= numOfFiles; destIx++) {
				BufferedOutputStream bw = new BufferedOutputStream(
						new FileOutputStream(Variables.TEMP_DIR + "Splits\\split_" + destIx + ".ttl"));
				if (bytesPerSplit > maxReadBufferSize) {
					long numReads = bytesPerSplit / maxReadBufferSize;
					long numRemainingRead = bytesPerSplit % maxReadBufferSize;
					for (int i = 0; i < numReads; i++) {
						readWrite(raf, bw, maxReadBufferSize);
					}
					if (numRemainingRead > 0) {
						readWrite(raf, bw, numRemainingRead);
					}
				} else {
					readWrite(raf, bw, bytesPerSplit);
				}
				bw.close();
			}

			numOfFiles++;

			if (remainingBytes > 0) {
				BufferedOutputStream bw = new BufferedOutputStream(
						new FileOutputStream(Variables.TEMP_DIR + "Splits\\split_" + numOfFiles + ".ttl"));
				readWrite(raf, bw, remainingBytes);
				bw.close();
			}

			raf.close();

			return numOfFiles;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public static void createModels(int numOfFiles) {
		String basePath = Variables.TEMP_DIR + "Models\\";
		String fileDir = Variables.TEMP_DIR + "Splits\\";

		File baseFile = new File(basePath);
		if (baseFile.exists()) {
			File[] listOfFiles = baseFile.listFiles();
			if (listOfFiles != null) {
				for (File file : listOfFiles) {
					boolean deleteStatus = file.delete();
				}
			}
		} else {
			boolean status = baseFile.mkdir();

			if (status) {
				Methods.print("Directory created");
			}
		}

		System.out.println("All files deleted");

		String remainText = "";
		String fileString = "";

		for (int i = 1; i <= numOfFiles; i++) {
			String filePath = fileDir + "split_" + i + ".ttl";
			File splitFile = new File(filePath);

			if (splitFile.isFile()) {
//				System.out.println(splitFile.getName());

				String text = Methods.readStringFromFile(splitFile.getPath());

				if (i == 1) {
					fileString = "";

					String[] segments = text.split("\\.\\s*<");

					for (int j = 0; j < segments.length - 1; j++) {
						fileString += segments[j] + ".";

						if (j != segments.length - 2) {
							fileString += "\n\n<";
						}
					}

					if (segments[segments.length - 1].trim().length() > 0) {
						remainText += "<" + segments[segments.length - 1];
					}

//					System.out.println("Finish");
				} else if (i == numOfFiles) {
					fileString = "<";

					String[] segments = text.split("\\.\\s*<");

					if (segments[0].trim().length() > 0) {
						if (segments[0].trim().endsWith(".")) {
							remainText += segments[0];
						} else {
//							System.out.println("We are here");
							remainText += segments[0] + ".\n\n";
						}
					}

					if (segments.length > 1) {
						for (int j = 1; j < segments.length; j++) {
							if (segments[j].endsWith(".")) {
								fileString += segments[j];
							} else {
								fileString += segments[j] + ".";
							}

							if (j != segments.length - 1) {
								if (fileString.trim().length() > 0) {
									fileString += "\n\n<";
								}
							}
						}
					}

					if (fileString.equals("<")) {
						fileString = "";
					}

//					System.out.println("Finish");
				} else {
					fileString = "<";

					String[] segments = text.split("\\.\\s*<");

					if (segments[0].trim().length() > 0) {
						remainText += segments[0] + ".\n\n";
					}

					if (segments[segments.length - 1].trim().length() > 0) {
						remainText += "<" + segments[segments.length - 1];
					}

					for (int j = 1; j < segments.length - 1; j++) {
						fileString += segments[j] + ".";

						if (j != segments.length - 2) {
							fileString += "\n\n<";
						}
					}

					if (fileString.equals("<")) {
						fileString = "";
					}

//					System.out.println("Finish");
				}

				String modelPath = basePath + "model" + i + ".ttl";
				Methods.writeText(modelPath, fileString);
			}
		}

		String remainPath = basePath + "model" + (numOfFiles + 1) + ".ttl";
		Methods.writeText(remainPath, remainText);
	}

	static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
		byte[] buf = new byte[(int) numBytes];
		int val = raf.read(buf);
		if (val != -1) {
			bw.write(buf);
		}
	}

	public static void readModel() {
		String basePath = Variables.TEMP_DIR + "Models\\";

		File baseFile = new File(basePath);
		File[] listOfFiles = baseFile.listFiles();
		for (File file : listOfFiles) {
			try {
				Model model = ModelFactory.createDefaultModel();
				model.read(file.getPath());
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				System.out.println(file.getPath());

//				System.out.println(Methods.readStringFromFile(file.getPath()));
				return;
			}
		}
	}

	public static void createModelsSecond(int numOfFiles) {
		String basePath = Variables.TEMP_DIR + "Models\\";
		String fileDir = Variables.TEMP_DIR + "Splits\\";

		File baseFile = new File(basePath);
		if (baseFile.exists()) {
			File[] listOfFiles = baseFile.listFiles();
			for (File file : listOfFiles) {
				file.delete();
			}
		} else {
			baseFile.mkdir();
		}

		System.out.println("All files deleted");

		String remainText = "";

		StringBuilder stringBuilder = new StringBuilder(remainText);

		for (int i = 1; i <= 3; i++) {
			String filePath = fileDir + "split_" + i + ".ttl";

			String fullText = "";

			File file = new File(filePath);

			if (i == numOfFiles) {
				fullText = stemFromFirst(stringBuilder, filePath);
			} else if (i == 1) {
				fullText = stemFromLast(stringBuilder, file, i);
			} else {
				fullText = stemFromFirst(stringBuilder, filePath);

				String tempPath = Variables.TEMP_DIR + "remains.txt";
				Methods.writeText(tempPath, fullText);

				File savedFile = new File(tempPath);

				fullText = stemFromLast(stringBuilder, savedFile, i);
			}

			String modelPath = basePath + "model" + i + ".ttl";
			Methods.writeText(modelPath, fullText);
		}

		String modelPath = basePath + "model" + (numOfFiles + 1) + ".ttl";
		Methods.writeText(modelPath, stringBuilder.toString());
	}

	private static String stemFromLast(StringBuilder stringBuilder, File file, int numOfFiles) {
		ReversedLinesFileReader fr;
		try {
			fr = new ReversedLinesFileReader(file);

			String text = "", ch, remainderText = "";

			boolean isMatched = false;
			while ((ch = fr.readLine()) != null) {
				if (isMatched) {
					remainderText = "\n" + ch + remainderText;
				} else {
					text = "\n" + ch + text;

					String regEx = "\\.\\s*\\<";
					Pattern pattern = Pattern.compile(regEx);
					Matcher matcher = pattern.matcher(text);

					if (matcher.find()) {
						isMatched = true;
					}
				}

			}
			fr.close();

//			System.out.println("Text: " + text);

			String[] segments = text.split("\\.\\s*\\<");
			String validPart = "";
			String offsetString = "";

			if (segments[1].trim().length() > 0) {
				validPart = "<" + segments[1];
				stringBuilder.append(validPart.trim());

//				if (validPart.contains("144182")) {
//					System.out.println("Error: " + numOfFiles);
//				}

				int index = text.indexOf(validPart);
				offsetString = text.substring(0, index);
			} else {
//				System.out.println("Length 0");

				validPart = "<";
				stringBuilder.append(validPart);

//				if (validPart.contains("144182")) {
//					System.out.println("Error: " + file.getPath());
//				}

				int index = text.indexOf(validPart);
				offsetString = text.substring(0, index);

//				System.out.println("Offset String: " + offsetString);
			}

			remainderText = remainderText + offsetString.trim();
			return remainderText;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
	
	
//
//	private static String stemFromLast(StringBuilder stringBuilder, File file, int numOfFiles, boolean isNew) {
//		try {
//			FastReverseLineInputStream inputStream = new FastReverseLineInputStream(file);
//			String text = "", lineText = "", remainderText = "";
//
//			int i = 0;
//			boolean isDotFound = false;
//
//			boolean isMatched = false;
//
//			while ((i = inputStream.read()) != -1) {
//				char c = (char) i;
//
//				String string = String.valueOf(c);
//
//				if (isMatched) {
//					if (string.equals("\n")) {
//						remainderText = lineText + "\n" + remainderText;
//						lineText = "";
//					} else {
//						lineText += string;
//					}
//				} else {
//					if (string.equals("\n")) {
//						text = lineText + "\n" + text;
//						lineText = "";
//					} else {
//						if (isDotFound) {
//							if (string.matches("\\s+")) {
//								continue;
//							} else if (string.equals("<")) {
//								text = lineText + "\n" + text;
//								isMatched = true;
//								lineText = "";
//							} else {
//								lineText += string;
//								isDotFound = false;
//							}
//						} else {
//							lineText += string;
//						}
//
//						if (string.equals(".")) {
//							isDotFound = true;
//						}
//					}
//				}
//			}
//
//			inputStream.close();
//
////			System.out.println("File " + numOfFiles + ": " + text);
//
//			try {
//				String[] segments = text.split("\\.\\s*\\<");
//				String validPart = "";
//				String offsetString = "";
//				if (segments[1].trim().length() > 0) {
//					validPart = "<" + segments[1];
//					stringBuilder.append(validPart.trim());
//
//					int index = text.indexOf(validPart);
//					offsetString = text.substring(0, index);
//				} else {
//
//					validPart = "<";
//					stringBuilder.append(validPart);
//
//					int index = text.indexOf(validPart);
//					offsetString = text.substring(0, index);
//				}
//				remainderText = remainderText + offsetString.trim();
//				return remainderText;
//			} catch (Exception e) {
//				// TODO: handle exception
//
//				System.out.println("Error: " + numOfFiles + " - " + text);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return "";
//	}
//
//	
	
	private static String stemFromFirst(StringBuilder stringBuilder, String filePath) {
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));

			String text = "", s, remainderText = "";

			boolean isMatched = false;
			while ((s = bufferedReader.readLine()) != null) {
				if (isMatched) {
					remainderText = remainderText + s + "\n";
				} else {
					text = text + s + "\n";

					String regEx = "\\.\\s*\\<";
					Pattern pattern = Pattern.compile(regEx);
					Matcher matcher = pattern.matcher(text);

					if (matcher.find()) {
						isMatched = true;
					}
				}
			}

//			System.out.println("Current Text: " + text);

			String[] segments = text.split("\\.\\s*\\<");
			String validPart = "";
			String offsetString = "";

			if (segments[0].trim().length() > 0) {
//				System.out.println("Length not zero");

				if (segments[0].trim().endsWith(".")) {
					validPart = segments[0];
				} else {
					validPart = segments[0] + ".";
				}

//				System.out.println("Valid part: " + validPart);

				String addedText = validPart.trim() + "\n\n";
				stringBuilder.append(addedText);

//				if (addedText.contains("144182")) {
//					System.out.println("Error: " + filePath);
//				}

				offsetString = text.substring(validPart.length(), text.length());
			} else {
//				System.out.println("Length zero");

				validPart = ".";
				stringBuilder.append(validPart);

				int index = text.indexOf(validPart);

//				System.out.println("Index: " + index);

				offsetString = text.substring(index + 1, text.length());

//				System.out.println("Offset: " + offsetString);
			}

//			System.out.println("Offset: " + offsetString);

//			System.out.println(text);
//			System.out.println("Index: " + index + " - " + offsetString);

//			System.out.println("Text: " + text);
//			System.out.println("Valid: " + validPart);
//			System.out.println("Offset: " + offsetString);

			remainderText = offsetString.trim() + remainderText;

			bufferedReader.close();

			return remainderText;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static int splitFileByLine(String filePath, int maxLines) {
//		System.out.println("We are here: " + filePath);
		String basePath = Variables.TEMP_DIR + "Models\\";

		File baseFile = new File(basePath);
		if (baseFile.exists()) {
			File[] listOfFiles = baseFile.listFiles();
			if (listOfFiles != null) {
				for (File file : listOfFiles) {
					boolean deleteStatus = file.delete();
				}
			}
		} else {
			boolean status = baseFile.mkdir();

			if (status) {
				Methods.print("Directory created");
			}
		}

		System.out.println("All files deleted");

		File file = new File(filePath);
		BufferedReader bufferedReader = null;

		int numOfiles = 0, count = 1;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));

			String text = "", s;

			StringBuilder stringBuilder = new StringBuilder(text);

			while ((s = bufferedReader.readLine()) != null) {
//				text = text + s + "\n";
				stringBuilder.append(s);
				stringBuilder.append("\n");

				count++;

				if (count % maxLines == 0) {
					numOfiles++;

					String modelPath = basePath + "model" + numOfiles + ".nt";
					Methods.writeText(modelPath, stringBuilder.toString());

//					System.out.println(modelPath);

					stringBuilder = new StringBuilder(text);
				}
			}

			bufferedReader.close();
			
			if (stringBuilder.toString().trim().length() > 0) {
				numOfiles++;

				String modelPath = basePath + "model" + numOfiles + ".nt";
				Methods.writeText(modelPath, stringBuilder.toString());

//				System.out.println(modelPath);

				stringBuilder = new StringBuilder(text);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error: " + e.getMessage());
		}

		System.out.println("Total: " + numOfiles);

		return numOfiles;
	}
}
