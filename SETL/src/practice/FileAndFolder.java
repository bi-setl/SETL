package practice;

import java.awt.EventQueue;
import java.io.File;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import core.LevelEntryNew;
import helper.Methods;

public class FileAndFolder {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String basePath = "D:\\Data\\CSV";
		File folder = new File(basePath);
		File[] listOfFiles = folder.listFiles();
		String baseName = "Census_C04";
		String baseFileName = "D:\\Data\\CSV\\" + baseName + ".csv";

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
							// baseFile.deleteOnExit();
						}

						System.out.println(listOfFiles[i].getPath());
						listOfFiles[i].renameTo(baseFile);
						System.out.println(baseFile.getPath());

						LevelEntryNew entryNew = new LevelEntryNew();

						/*
						 * entryNew.generateFactEntryFromCSV(baseFileName, "D:\\Data\\Files\\map.ttl",
						 * "D:\\Data\\Files\\bd_tbox.ttl", "D:\\Data\\Files\\prov.ttl",
						 * "D:\\Data\\Test\\Fact_" + baseName + "_TargetABox.ttl", "Space ( )");
						 */
						
						entryNew.generateFactEntryFromCSV(baseFileName, "D:\\Data\\Files\\map.ttl",
								"D:\\Data\\Files\\bd_tbox.ttl",
								"D:\\Data\\Files\\prov.ttl",
								"D:\\Data\\Test\\Fact_" + baseName + "_TargetABox.ttl", "Space ( )");
						
						
						baseFile.deleteOnExit();
					}
				}

				System.out.println("Finish");
				new Methods().showDialog("Complete");
			}
		});
	}

	public static synchronized void playSound(final String url) {
		new Thread(new Runnable() {
			// The wrapper thread is unnecessary, unless it blocks on the
			// Clip finishing; see comments.
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem
							.getAudioInputStream(FileAndFolder.class.getResourceAsStream("/path/to/sounds/" + url));
					clip.open(inputStream);
					clip.start();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
}
