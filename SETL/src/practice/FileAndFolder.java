package practice;

import java.awt.EventQueue;
import java.io.File;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import core.LevelEntryNew;
import core.RDFWrapper;
import helper.Methods;

public class FileAndFolder {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String basePath = "C:\\Users\\Amrit\\Documents\\Census";
		File folder = new File(basePath);
		File[] listOfFiles = folder.listFiles();
		String baseName = "Census02_sex";
		String baseFileName = basePath + "\\" + baseName + ".csv";

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < listOfFiles.length; i++) {
				// for (int i = 0; i < 1; i++) {
					if (listOfFiles[i].isFile()) {
						File baseFile = new File(baseFileName);
						if (baseFile.exists()) {
							String tempString = basePath + "\\" + baseName + "_" + i + ".csv";
							File tempFile = new File(tempString);
							baseFile.renameTo(tempFile);
							baseFile.deleteOnExit();
						}

						System.out.println(listOfFiles[i].getPath());
						listOfFiles[i].renameTo(baseFile);
						System.out.println(baseFile.getPath());

						/*
						 * LevelEntryNew entryNew = new LevelEntryNew();
						 * entryNew.generateLevelEntryFromCSV(baseFileName,
						 * "C:\\Users\\Amrit\\Documents\\map.ttl",
						 * "C:\\Users\\Amrit\\Documents\\bd_tbox.ttl",
						 * "C:\\Users\\Amrit\\Documents\\Census\\prov.ttl",
						 * "C:\\Users\\Amrit\\Documents\\Level_" + baseName + "_TargetABox.ttl",
						 * "Space ( )");
						 */
						
						/*
						 * RDFWrapper rdfWrapper = new RDFWrapper(); rdfWrapper.parseCSV(baseFileName,
						 * "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\191022_102457_TargetABox.ttl",
						 * "http://linked-statistics-bd.org/2011/data",
						 * "CONCAT(admUnitFiveId,residence,ageGroup)", "Space ( )");
						 */
						
						CSVABox csvaBox = new CSVABox();
						String resultString = csvaBox.parseCSV(baseFileName,
								"http://linked-statistics-bd.org/2011/data",
								"Space ( )",
								"Expression",
								"CONCAT(admUnitFiveId,residence,sex)",
								"TTL",
								"C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\source_aboxes\\" + baseName + ".ttl");
						System.out.println(resultString);
					}
				}

				System.out.println("Finish");
//				playSound();
				new Methods().showDialog("Complete");
			}
		});
	}

	public static synchronized void playSound() {
		try {
			File file = new File("C:\\Users\\Amrit\\Downloads\\tone.wav");
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
			clip.open(inputStream);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
